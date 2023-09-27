package com.example.texttile.presentation.ui.dialog;

import static android.app.Activity.RESULT_OK;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.example.texttile.R;
import com.example.texttile.databinding.JalaDialogBinding;
import com.example.texttile.presentation.ui.activity.ImagePreviewActivity;
import com.example.texttile.data.dao.DAOJalaMaster;
import com.example.texttile.data.model.JalaMasterModel;
import com.example.texttile.presentation.ui.lib.snackUtil.CustomSnackUtil;
import com.example.texttile.presentation.ui.util.DialogUtil;
import com.example.texttile.presentation.ui.util.KeyboardUtils;

public class JalaDialog extends DialogFragment {

    private final int PICK_IMAGE_REQUEST = 1000;
    JalaMasterModel jalaMasterModel = new JalaMasterModel();
    Uri mImageUri;
    DAOJalaMaster dao;
    JalaDialogBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dao = new DAOJalaMaster(getActivity());
    }

    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(getActivity());
        binding = JalaDialogBinding.inflate(getLayoutInflater());
        dialog.getWindow().setWindowAnimations(R.style.DialogAnimation);
        dialog.setContentView(binding.getRoot());
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        binding.editJalaName.setText(getArguments().getString("jala_name"));

        KeyboardUtils.addKeyboardToggleListener(getActivity(), new KeyboardUtils.SoftKeyboardToggleListener() {
            @Override
            public void onToggleSoftKeyboard(boolean isVisible) {
                if (isVisible) {
                    binding.guideline3.setGuidelinePercent(0.10f);
                    binding.guideline4.setGuidelinePercent(0.90f);
                } else {
                    binding.guideline3.setGuidelinePercent(0.25f);
                    binding.guideline4.setGuidelinePercent(0.75f);
                }
            }
        });

        binding.btnClose.setOnClickListener(view -> {
            dialog.dismiss();
        });

        binding.imgPreview.setOnClickListener(view -> {
            startActivity(new Intent(getContext(), ImagePreviewActivity.class).putExtra("tracker", "add").putExtra("img_uri", jalaMasterModel.getSelect_photo() != null ? jalaMasterModel.getSelect_photo() : mImageUri.toString()));
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

        return dialog;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            mImageUri = data.getData();

                binding.btnUploadPhoto.setVisibility(View.GONE);
                binding.imgPreview.setVisibility(View.VISIBLE);
                Glide.with(getContext()).load(mImageUri).placeholder(getResources().getDrawable(R.drawable.no_image)).into(binding.imgPhoto);
        }
    }

    private void addArtist() {
        String jala = binding.editJalaName.getText().toString().trim();

        if (TextUtils.isEmpty(jala)) {
            new CustomSnackUtil().showSnack(getActivity(), "Please enter a Jala Name", R.drawable.error_msg_icon);
        } else {

            String id = dao.getNewId();
            DialogUtil.showProgressDialog(getActivity());

                if (mImageUri != null) {
                    dao.uploadImage(id, mImageUri).addOnSuccessListener(runnable -> {
                        dao.getDownloadUri(id).addOnSuccessListener(uri -> {
                            jalaMasterModel.setId(id);
                            jalaMasterModel.setJala_name(jala);
                            jalaMasterModel.setSelect_photo(uri.toString());
                            dao.insert(id, jalaMasterModel).addOnSuccessListener(runnable1 -> {
                                dismiss();
                                DialogUtil.showSuccessDialog(getActivity(), false);
                            });
                        }).addOnFailureListener(runnable1 -> {
                            DialogUtil.showErrorDialog(getActivity());
                        });

                    }).addOnFailureListener(runnable -> {
                        DialogUtil.showErrorDialog(getActivity());
                    });
                }else {
                    new CustomSnackUtil().showSnack(getActivity(), "Please Select Image", R.drawable.error_msg_icon);
                }
        }
    }
}