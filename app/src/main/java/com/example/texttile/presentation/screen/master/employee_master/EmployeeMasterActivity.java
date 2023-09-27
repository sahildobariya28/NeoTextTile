package com.example.texttile.presentation.screen.master.employee_master;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.texttile.R;
import com.example.texttile.databinding.ActivityEmployeeMasterBinding;
import com.example.texttile.presentation.ui.activity.ImagePreviewActivity;
import com.example.texttile.data.dao.DAOEmployeeMaster;
import com.example.texttile.data.model.EmployeeMasterModel;
import com.example.texttile.presentation.ui.lib.snackUtil.CustomSnackUtil;
import com.example.texttile.presentation.ui.util.DialogUtil;

import java.util.ArrayList;
import java.util.HashMap;

public class EmployeeMasterActivity extends AppCompatActivity {

    private final int PICK_PHOTO_REQUEST = 111, PICK_ID_PHOTO_REQUEST = 222;
    String tracker;
    EmployeeMasterModel employeeMasterModel;

    public Uri photo_path, id_path;
    DAOEmployeeMaster dao;

    ActivityEmployeeMasterBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEmployeeMasterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dao = new DAOEmployeeMaster(this);

        initToolbar();
        tracker = (getIntent().getStringExtra("tracker") != null) ? getIntent().getStringExtra("tracker") : "add";
        employeeMasterModel = (tracker.equals("edit") && getIntent().getSerializableExtra("employee_data") != null) ? (EmployeeMasterModel) getIntent().getSerializableExtra("employee_data") : new EmployeeMasterModel();

        if (tracker != null && tracker.equals("edit")) {
            if (employeeMasterModel != null) {
                binding.imgPreview.setVisibility(View.VISIBLE);
                binding.imgIdPreview.setVisibility(View.VISIBLE);
                binding.btnUploadPhoto.setVisibility(View.GONE);
                binding.btnUploadId.setVisibility(View.GONE);
                binding.btnAdd.setText("Update");

                binding.editEmployeeName.setText(String.valueOf(employeeMasterModel.getEmployee_name()));
                binding.editPhoneNo.setText(String.valueOf(employeeMasterModel.getPhone_no()));
                binding.editAlternativePhoneNo.setText(String.valueOf(employeeMasterModel.getAlter_phone_no()));
                binding.editAccountNo.setText(String.valueOf(employeeMasterModel.getAccount_no()));
                binding.editIfscCode.setText(String.valueOf(employeeMasterModel.getIfsc_code()));
                binding.editBankName.setText(String.valueOf(employeeMasterModel.getBank_name()));
                binding.editBankHolderName.setText(String.valueOf(employeeMasterModel.getBank_holder_name()));

                Glide.with(this).load(employeeMasterModel.getEmployee_photo()).placeholder(R.drawable.no_image).into(binding.imgPhoto);
                Glide.with(this).load(employeeMasterModel.getId_proof()).placeholder(R.drawable.no_image).into(binding.imgId);
            }
        } else {
            if (employeeMasterModel != null) {
                binding.editEmployeeName.requestFocus();
            }
        }

        binding.btnAdd.setOnClickListener(view -> addArtist());

        binding.imgPreview.setOnClickListener(view -> {
            startActivity(new Intent(this, ImagePreviewActivity.class).putExtra("tracker", tracker).putExtra("img_uri", employeeMasterModel.getEmployee_photo() != null ? employeeMasterModel.getEmployee_photo() : photo_path.toString()));
        });
        binding.imgIdPreview.setOnClickListener(view -> {
            startActivity(new Intent(this, ImagePreviewActivity.class).putExtra("tracker", tracker).putExtra("img_uri", employeeMasterModel.getId_proof() != null ? employeeMasterModel.getId_proof() : id_path.toString()));
        });
        binding.btnUploadPhoto.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Image from here..."), PICK_PHOTO_REQUEST);
        });
        binding.btnUploadId.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Image from here..."), PICK_ID_PHOTO_REQUEST);
        });
        binding.btnPhotoDelete.setOnClickListener(view -> {
            employeeMasterModel.setEmployee_photo(null);
            binding.btnUploadPhoto.setVisibility(View.VISIBLE);
            binding.imgPreview.setVisibility(View.GONE);
            binding.imgPhoto.setImageBitmap(null);
        });
        binding.btnIdDelete.setOnClickListener(view -> {
            employeeMasterModel.setId_proof(null);
            binding.btnUploadId.setVisibility(View.VISIBLE);
            binding.imgIdPreview.setVisibility(View.GONE);
            binding.imgId.setImageBitmap(null);
        });
    }

    private void selectImage(int requestCode) {

        // Defining Implicit Intent to mobile gallery

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_PHOTO_REQUEST && resultCode == -1 && data != null && data.getData() != null) {

            photo_path = data.getData();
            if (tracker.equals("edit")) {
                DialogUtil.showProgressDialog(EmployeeMasterActivity.this);
                dao.uploadPhoto(employeeMasterModel.getId(), data.getData()).addOnSuccessListener(runnable -> {
                    dao.getDownloadUri(employeeMasterModel.getId()).addOnSuccessListener(uri -> {
                        binding.btnUploadPhoto.setVisibility(View.GONE);
                        binding.imgPreview.setVisibility(View.VISIBLE);
                        Glide.with(EmployeeMasterActivity.this).load(photo_path).placeholder(getResources().getDrawable(R.drawable.no_image)).into(binding.imgPhoto);
                        employeeMasterModel.setEmployee_photo(uri.toString());
                        DialogUtil.showSuccessDialog(EmployeeMasterActivity.this, false);
                    }).addOnFailureListener(runnable1 -> {
                        DialogUtil.showErrorDialog(EmployeeMasterActivity.this);
                    });
                }).addOnFailureListener(runnable -> {
                    DialogUtil.showErrorDialog(EmployeeMasterActivity.this);
                });
            } else {

                binding.btnUploadPhoto.setVisibility(View.GONE);
                binding.imgPreview.setVisibility(View.VISIBLE);
                Glide.with(this).load(photo_path).placeholder(getResources().getDrawable(R.drawable.no_image)).into(binding.imgPhoto);
            }


        } else if (requestCode == PICK_ID_PHOTO_REQUEST && resultCode == -1 && data != null && data.getData() != null) {
            id_path = data.getData();
            if (tracker.equals("edit")) {
                DialogUtil.showProgressDialog(this);
                dao.uploadIdPhoto(employeeMasterModel.getId(), data.getData()).addOnSuccessListener(runnable -> {
                    dao.getIdDownloadUri(employeeMasterModel.getId()).addOnSuccessListener(uri -> {
                        binding.btnUploadId.setVisibility(View.GONE);
                        binding.imgIdPreview.setVisibility(View.VISIBLE);
                        Glide.with(this).load(id_path).placeholder(getResources().getDrawable(R.drawable.no_image)).into(binding.imgId);
                        employeeMasterModel.setId_proof(uri.toString());
                        DialogUtil.showSuccessDialog(this, false);
                    }).addOnFailureListener(runnable1 -> {
                        DialogUtil.showErrorDialog(this);
                    });
                }).addOnFailureListener(runnable -> {
                    DialogUtil.showErrorDialog(this);
                });
            } else {

                binding.btnUploadId.setVisibility(View.GONE);
                binding.imgIdPreview.setVisibility(View.VISIBLE);
                Glide.with(this).load(id_path).placeholder(getResources().getDrawable(R.drawable.no_image)).into(binding.imgId);
            }
        }
    }


    private void addArtist() {
        String employee_name = binding.editEmployeeName.getText().toString().trim();
        String phone_no = binding.editPhoneNo.getText().toString().trim();
        String alternative_phone_no = binding.editAlternativePhoneNo.getText().toString().trim();
        String account_no = binding.editAccountNo.getText().toString().trim();
        String ifsc_code = binding.editIfscCode.getText().toString().trim();
        String bank_name = binding.editBankName.getText().toString().trim();
        String bank_holder_name = binding.editBankHolderName.getText().toString().trim();

        if (TextUtils.isEmpty(employee_name)) {
            new CustomSnackUtil().showSnack(this, "Please enter a Employee name", R.drawable.error_msg_icon);
        } else if (TextUtils.isEmpty(phone_no)) {
            new CustomSnackUtil().showSnack(this, "Please enter a Phone No", R.drawable.error_msg_icon);
        } else if (TextUtils.isEmpty(alternative_phone_no)) {
            new CustomSnackUtil().showSnack(this, "Please enter a Alternative Phone No", R.drawable.error_msg_icon);
        } else if (TextUtils.isEmpty(account_no)) {
            new CustomSnackUtil().showSnack(this, "Please enter a Account No", R.drawable.error_msg_icon);
        } else if (TextUtils.isEmpty(ifsc_code)) {
            new CustomSnackUtil().showSnack(this, "Please enter a IFSC Code", R.drawable.error_msg_icon);
        } else if (TextUtils.isEmpty(bank_name)) {
            new CustomSnackUtil().showSnack(this, "Please enter a Bank Name", R.drawable.error_msg_icon);
        } else if (TextUtils.isEmpty(bank_holder_name)) {
            new CustomSnackUtil().showSnack(this, "Please enter a Bank Holder Name", R.drawable.error_msg_icon);
        } else {

            String id = dao.getNewId();
            DialogUtil.showProgressDialog(this);
            if (tracker.equals("edit")) {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("employee_name", employee_name);
                hashMap.put("employee_photo", employeeMasterModel.getEmployee_photo());
                hashMap.put("id_proof", employeeMasterModel.getId_proof());
                hashMap.put("phone_no", phone_no);
                hashMap.put("alter_phone_no", alternative_phone_no);
                hashMap.put("account_no", account_no);
                hashMap.put("ifsc_code", ifsc_code);
                hashMap.put("bank_name", bank_name);
                hashMap.put("bank_holder_name", bank_holder_name);
                hashMap.put("employee_allot_status", employeeMasterModel.getEmployee_allot_status());
                hashMap.put("machine_name", employeeMasterModel.getMachine_name());
//
                dao.update(employeeMasterModel.getId(), hashMap).addOnSuccessListener(runnable -> {
                    DialogUtil.showUpdateSuccessDialog(this, true);
                });
            } else {
                if (photo_path != null && id_path != null) {
                    dao.uploadPhoto(id, photo_path).addOnSuccessListener(runnable -> {
                        dao.getDownloadUri(id).addOnSuccessListener(photo_uri -> {
                            dao.uploadIdPhoto(id, id_path).addOnSuccessListener(runnable1 -> {
                                dao.getIdDownloadUri(id).addOnSuccessListener(id_uri -> {

                                    employeeMasterModel.setId(id);
                                    employeeMasterModel.setEmployee_name(employee_name);
                                    employeeMasterModel.setEmployee_photo(photo_uri.toString());
                                    employeeMasterModel.setId_proof(id_uri.toString());
                                    employeeMasterModel.setPhone_no(phone_no);
                                    employeeMasterModel.setAlter_phone_no(alternative_phone_no);
                                    employeeMasterModel.setAccount_no(account_no);
                                    employeeMasterModel.setIfsc_code(ifsc_code);
                                    employeeMasterModel.setBank_name(bank_name);
                                    employeeMasterModel.setBank_holder_name(bank_holder_name);
                                    ArrayList<String> machineName = new ArrayList<>();
                                    machineName.add("null");
                                    employeeMasterModel.setMachine_name(machineName);

                                    dao.insert(id, employeeMasterModel).addOnSuccessListener(runnable10 -> {
                                        DialogUtil.showSuccessDialog(this, true);
                                    });
                                }).addOnFailureListener(runnable2 -> {
                                    DialogUtil.showErrorDialog(this);
                                });

                            }).addOnFailureListener(runnable3 -> {
                                DialogUtil.showErrorDialog(this);
                            });
                        }).addOnFailureListener(runnable1 -> {
                            DialogUtil.showErrorDialog(this);
                        });

                    }).addOnFailureListener(runnable -> {
                        DialogUtil.showErrorDialog(this);
                    });
                } else {
                    new CustomSnackUtil().showSnack(this, "Please Select Image", R.drawable.error_msg_icon);
                }
            }
        }
    }



    public void initToolbar() {
        binding.include.textTitle.setText("Employee Master");
        binding.include.btnBack.setOnClickListener(view -> {
            onBackPressed();
        });
    }


}