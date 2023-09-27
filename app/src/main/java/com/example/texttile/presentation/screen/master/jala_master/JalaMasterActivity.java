package com.example.texttile.presentation.screen.master.jala_master;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.texttile.R;
import com.example.texttile.databinding.ActivityJalaMasterBinding;
import com.example.texttile.presentation.ui.activity.ImagePreviewActivity;
import com.example.texttile.data.dao.DAOJalaMaster;
import com.example.texttile.data.model.JalaMasterModel;
import com.example.texttile.presentation.ui.lib.snackUtil.CustomSnackUtil;
import com.example.texttile.presentation.ui.util.DialogUtil;

import java.util.HashMap;

public class JalaMasterActivity extends AppCompatActivity {

    private final int PICK_IMAGE_REQUEST = 1000;
    String tracker;
    JalaMasterModel jalaMasterModel;
    Uri mImageUri;
    DAOJalaMaster dao;

    ActivityJalaMasterBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityJalaMasterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dao = new DAOJalaMaster(this);

        initToolbar();

        tracker = (getIntent().getStringExtra("tracker") != null) ? getIntent().getStringExtra("tracker") : "add";
        jalaMasterModel = (tracker.equals("edit") && getIntent().getSerializableExtra("jala_data") != null) ? (JalaMasterModel) getIntent().getSerializableExtra("jala_data") : new JalaMasterModel();

        if (tracker != null && tracker.equals("edit")) {
            if (jalaMasterModel != null) {
                binding.imgPreview.setVisibility(View.VISIBLE);
                binding.btnUploadPhoto.setVisibility(View.GONE);
                binding.btnAdd.setText("Update");

                binding.editJalaName.setText(String.valueOf(jalaMasterModel.getJala_name()));
                Glide.with(this).load(jalaMasterModel.getSelect_photo()).placeholder(R.drawable.no_image).into(binding.imgPhoto);
            }
        } else {
            if (jalaMasterModel != null) {
                binding.editJalaName.requestFocus();
            }
        }

        binding.imgPreview.setOnClickListener(view -> {
            startActivity(new Intent(this, ImagePreviewActivity.class).putExtra("tracker", tracker).putExtra("img_uri", jalaMasterModel.getSelect_photo() != null ? jalaMasterModel.getSelect_photo() : mImageUri.toString()));
        });

        binding.btnAdd.setOnClickListener(view -> addArtist());

        binding.btnUploadPhoto.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Image from here..."), PICK_IMAGE_REQUEST);
        });

        binding.btnDelete.setOnClickListener(view -> {
            mImageUri = null;
            binding.btnUploadPhoto.setVisibility(View.VISIBLE);
            binding.imgPreview.setVisibility(View.GONE);
            binding.imgPhoto.setImageBitmap(null);
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            mImageUri = data.getData();
            if (tracker.equals("edit")) {
                DialogUtil.showProgressDialog(this);
                dao.uploadImage(jalaMasterModel.getId(), data.getData()).addOnSuccessListener(runnable -> {
                    dao.getDownloadUri(jalaMasterModel.getId()).addOnSuccessListener(uri -> {
                        binding.btnUploadPhoto.setVisibility(View.GONE);
                        binding.imgPreview.setVisibility(View.VISIBLE);
                        Glide.with(this).load(mImageUri).placeholder(getResources().getDrawable(R.drawable.no_image)).into(binding.imgPhoto);
                        jalaMasterModel.setSelect_photo(uri.toString());
                        DialogUtil.showSuccessDialog(this, false);
                    }).addOnFailureListener(runnable1 -> {
                        DialogUtil.showErrorDialog(this);
                    });
                }).addOnFailureListener(runnable -> {
                    DialogUtil.showErrorDialog(this);
                });
            } else {
                binding.btnUploadPhoto.setVisibility(View.GONE);
                binding.imgPreview.setVisibility(View.VISIBLE);
                Glide.with(this).load(mImageUri).placeholder(getResources().getDrawable(R.drawable.no_image)).into(binding.imgPhoto);
            }


        }
    }

    private void addArtist() {
        String jala = binding.editJalaName.getText().toString().trim();

        if (TextUtils.isEmpty(jala)) {
            new CustomSnackUtil().showSnack(this, "Please enter a Jala Name", R.drawable.error_msg_icon);
        } else {

            String id = dao.getNewId();
            DialogUtil.showProgressDialog(this);
            if (tracker.equals("edit")) {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("jala_name", jala);
                hashMap.put("select_photo", jalaMasterModel.getSelect_photo());
                dao.update(jalaMasterModel.getId(), hashMap).addOnSuccessListener(runnable -> {
                    DialogUtil.showUpdateSuccessDialog(this, true);
                });
            } else {
                if (mImageUri != null) {
                    dao.uploadImage(id, mImageUri).addOnSuccessListener(runnable -> {
                        dao.getDownloadUri(id).addOnSuccessListener(uri -> {
                            jalaMasterModel.setId(id);
                            jalaMasterModel.setJala_name(jala);
                            jalaMasterModel.setSelect_photo(uri.toString());
                            dao.insert(id, jalaMasterModel).addOnSuccessListener(runnable1 -> {
                                DialogUtil.showSuccessDialog(this, true);
                            });
                        }).addOnFailureListener(runnable1 -> {
                            DialogUtil.showErrorDialog(this);
                        });

                    }).addOnFailureListener(runnable -> {
                        DialogUtil.showErrorDialog(this);
                    });
                }else {
                    new CustomSnackUtil().showSnack(this, "Please Select Image", R.drawable.error_msg_icon);
                }
            }
        }
    }

    public void initToolbar() {
        binding.include.textTitle.setText("Jala Master");
        binding.include.btnBack.setOnClickListener(view -> {
            onBackPressed();
        });
    }
}