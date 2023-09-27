package com.example.texttile.presentation.screen.master.design_master.activity.design_master;

import static com.example.texttile.presentation.ui.util.PermissionUtil.isInsertPermissionGranted;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.texttile.R;
import com.example.texttile.databinding.ActivityDesignMasterBinding;
import com.example.texttile.presentation.ui.activity.ImagePreviewActivity;
import com.example.texttile.presentation.screen.master.design_master.adapter.FAdapter;
import com.example.texttile.presentation.screen.master.design_master.adapter.JalaSelectedListAdapter;
import com.example.texttile.presentation.ui.dialog.JalaDialog;
import com.example.texttile.data.dao.DAODesignMaster;
import com.example.texttile.data.dao.DAOJalaMaster;
import com.example.texttile.data.dao.DAOShadeMaster;
import com.example.texttile.presentation.ui.interfaces.AddOnSearchedItemSelectListener;
import com.example.texttile.data.model.DesignMasterModel;
import com.example.texttile.data.model.JalaMasterModel;
import com.example.texttile.data.model.JalaWithFileModel;
import com.example.texttile.data.model.ShadeMasterModel;
import com.example.texttile.presentation.ui.lib.snackUtil.CustomSnackUtil;
import com.example.texttile.presentation.ui.util.DialogUtil;
import com.example.texttile.presentation.ui.util.ListShowDialog;
import com.example.texttile.presentation.ui.util.PermissionState;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.annotation.Nullable;

public class DesignMasterActivity extends AppCompatActivity implements JalaSelectedListAdapter.OnClickImageSelectButton, AddOnSearchedItemSelectListener {

    public final int STEP1 = 1;
    public final int STEP2 = 2;
    public final int STEP3 = 3;
    public int CURRENT_STEP = 1;
    String tracker;
    DesignMasterModel duplicateDesignMasterModel = new DesignMasterModel();
    ArrayList<JalaWithFileModel> duplicateJalaWithFileModelList = new ArrayList<>();
    ArrayList<JalaWithFileModel> jalaWithFileModelList = new ArrayList<>();
    ArrayList<String> jala_list = new ArrayList<>();
    JalaSelectedListAdapter jalaSelectedListAdapter;
    int pos = 0;
    int position = 9999;
    FAdapter fAdapter;
    ArrayList<String> fileUriList = new ArrayList<>();
    DAODesignMaster dao;
    DAOJalaMaster daoJalaMaster;

    ImageView img_photo;
    LinearLayout btn_upload_photo;
    ConstraintLayout img_preview;

    ListShowDialog jalaListDialog;
    final int JALA_REQUEST = 1;

    ActivityDesignMasterBinding binding;
    DesignMasterViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDesignMasterBinding.inflate(getLayoutInflater());
        viewModel = new ViewModelProvider(this, new DesignMasterViewModelFactory(getIntent().getStringExtra("tracker"), this)).get(DesignMasterViewModel.class);
        binding.setDesignModel(viewModel);
        setContentView(binding.getRoot());

        dao = new DAODesignMaster(this);
        daoJalaMaster = new DAOJalaMaster(this);

        initToolbar();
        initData();
        setUpStep1();

        binding.textJalaName.setOnClickListener(view -> {
            if (jalaListDialog != null) {
                jalaListDialog.show_dialog();
            } else {
                jalaListDialog = new ListShowDialog(this, jala_list, this, JALA_REQUEST);
                jalaListDialog.show_dialog();
            }
        });

        binding.editDate.setOnClickListener(view -> {
            final Calendar myCalendar = Calendar.getInstance();
            DatePickerDialog.OnDateSetListener date = (view1, year, month, day) -> {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, day);
                String myFormat = "dd/MM/yy";
                SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.US);
                binding.editDate.setText(dateFormat.format(myCalendar.getTime()));
            };
            DatePickerDialog mDatePicker = new DatePickerDialog(this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
            mDatePicker.getDatePicker().setMaxDate(System.currentTimeMillis());
            mDatePicker.show();
        });
        binding.btnBack.setOnClickListener(view -> {
            if (CURRENT_STEP == 2) {
                setUpStep1();
            } else if (CURRENT_STEP == 3) {
                setUpStep2();
            }
        });
        binding.btnNext.setOnClickListener(view -> {

            switch (CURRENT_STEP) {

                case STEP1:
                    if (TextUtils.isEmpty(viewModel.designMasterLiveData.getValue().getDesign_no())) {
                        new CustomSnackUtil().showSnack(this, "Design No is Empty!", R.drawable.error_msg_icon);
                    } else if (TextUtils.isEmpty(viewModel.designMasterLiveData.getValue().getDate())) {
                        new CustomSnackUtil().showSnack(this, "Date is Empty!", R.drawable.error_msg_icon);
                    } else if (TextUtils.isEmpty(String.valueOf(viewModel.designMasterLiveData.getValue().getReed()))) {
                        new CustomSnackUtil().showSnack(this, "Reed is Empty!", R.drawable.error_msg_icon);
                    } else if (TextUtils.isEmpty(String.valueOf(viewModel.designMasterLiveData.getValue().getBase_pick()))) {
                        new CustomSnackUtil().showSnack(this, "Base Pick is Empty!", R.drawable.error_msg_icon);
                    } else if (TextUtils.isEmpty(String.valueOf(viewModel.designMasterLiveData.getValue().getBase_card()))) {
                        new CustomSnackUtil().showSnack(this, "Base Card is Empty!", R.drawable.error_msg_icon);
                    } else if (TextUtils.isEmpty(String.valueOf(viewModel.designMasterLiveData.getValue().getTotal_card()))) {
                        new CustomSnackUtil().showSnack(this, "Total Card is Empty!", R.drawable.error_msg_icon);
                    } else if (TextUtils.isEmpty(String.valueOf(viewModel.designMasterLiveData.getValue().getAvg_pick()))) {
                        new CustomSnackUtil().showSnack(this, "Avg Pick is Empty!", R.drawable.error_msg_icon);
                    } else {
                        setUpStep2();
                    }
                    break;
                case STEP2:

                    if (viewModel.designMasterLiveData.getValue().getF_list().size() <= 8) {
                        boolean is_valid = true;
                        for (int i = 0; i < viewModel.designMasterLiveData.getValue().getF_list().size(); i++) {

                            if (viewModel.designMasterLiveData.getValue().getF_list().get(i) == null) {
                                new CustomSnackUtil().showSnack(this, "Please Select at least 1 Jala!", R.drawable.error_msg_icon);
                                is_valid = false;
                                break;
                            }
                        }
                        if (is_valid) {
                            if (TextUtils.isEmpty(binding.editTotalLen.getText())) {
                                new CustomSnackUtil().showSnack(this, "Total Length is Empty!", R.drawable.error_msg_icon);
                            } else if (TextUtils.isEmpty(binding.editSampleCardLen1.getText())) {
                                new CustomSnackUtil().showSnack(this, "Sample Card Starting is Empty!", R.drawable.error_msg_icon);
                            } else if (TextUtils.isEmpty(binding.editSampleCardLen2.getText())) {
                                new CustomSnackUtil().showSnack(this, "Sample card Ending is Empty!", R.drawable.error_msg_icon);
                            } else {
                                if (viewModel.designMasterLiveData.getValue() != null) {
                                    ArrayList<Integer> flist = new ArrayList<>();
                                    for (int i = 0; i < viewModel.designMasterLiveData.getValue().getF_list().size(); i++) {
                                        flist.add(viewModel.designMasterLiveData.getValue().getF_list().get(i));
                                    }

                                    viewModel.designMasterLiveData.getValue().setF_list(flist);
                                    viewModel.designMasterLiveData.getValue().setSample_card_len(binding.editSampleCardLen1.getText().toString() + "-" + binding.editSampleCardLen2.getText().toString());
                                    if (binding.editTotalLen.getText().toString().contains("/mtr")) {
                                        viewModel.designMasterLiveData.getValue().setTotal_len(Float.parseFloat(binding.editTotalLen.getText().toString().replace("/mtr", "")));
                                    }
                                    setUpStep3();
                                }
                            }
                        }
                    }
                    break;
                case STEP3:
                    if (jalaWithFileModelList.size() == 0) {
                        new CustomSnackUtil().showSnack(this, "Please Select at least 1 Jala!", R.drawable.error_msg_icon);
                    } else if (!isValidList()) {
                        new CustomSnackUtil().showSnack(this, "Please Select all Design File!", R.drawable.error_msg_icon);
                    } else {
                        if (jalaWithFileModelList != null) {
                            ArrayList<String> arrayList = new ArrayList<>();
                            for (JalaWithFileModel jalaWithFileModel : jalaWithFileModelList) {
                                if (!tracker.equals("edit")) {
                                    arrayList.add(jalaWithFileModel.getFile_uri());
                                }
                            }
                            viewModel.designMasterLiveData.getValue().setJalaWithFileModelList(jalaWithFileModelList);
                            this.fileUriList = arrayList;

                            addArtist(false);
                        }
                    }
                    break;
            }
        });

        binding.btnCreate.setOnClickListener(view -> {
            if (tracker.equals("edit")) {
                if (CURRENT_STEP == STEP3) {
                    if (viewModel.designMasterLiveData.getValue().getF_list().size() <= 8) {
                        boolean is_valid = true;
                        for (int i = 0; i < viewModel.designMasterLiveData.getValue().getF_list().size(); i++) {

                            if (viewModel.designMasterLiveData.getValue().getF_list().get(i) == null) {
                                new CustomSnackUtil().showSnack(this, "Please Select at least 1 Jala!", R.drawable.error_msg_icon);
                                is_valid = false;
                                break;
                            }
                        }
                        if (is_valid) {
                            if (TextUtils.isEmpty(binding.editTotalLen.getText())) {
                                new CustomSnackUtil().showSnack(this, "Total Length is Empty!", R.drawable.error_msg_icon);
                            } else if (TextUtils.isEmpty(binding.editSampleCardLen1.getText())) {
                                new CustomSnackUtil().showSnack(this, "Sample Card Starting is Empty!", R.drawable.error_msg_icon);
                            } else if (TextUtils.isEmpty(binding.editSampleCardLen2.getText())) {
                                new CustomSnackUtil().showSnack(this, "Sample card Ending is Empty!", R.drawable.error_msg_icon);
                            } else {
                                if (viewModel.designMasterLiveData.getValue() != null) {
                                    ArrayList<Integer> flist = new ArrayList<>();
                                    for (int i = 0; i < viewModel.designMasterLiveData.getValue().getF_list().size(); i++) {
                                        flist.add(viewModel.designMasterLiveData.getValue().getF_list().get(i));
                                    }
                                    viewModel.designMasterLiveData.getValue().setF_list(flist);
                                    viewModel.designMasterLiveData.getValue().setSample_card_len(binding.editSampleCardLen1.getText().toString() + "-" + binding.editSampleCardLen2.getText().toString());
                                    viewModel.designMasterLiveData.getValue().setTotal_len(Float.parseFloat(binding.editTotalLen.getText().toString().replace("/mtr", "")));
                                    addArtist(true);
                                }
                            }
                        }
                    }
                } else {
                    new CustomSnackUtil().showSnack(this, "Something Want Wrong", R.drawable.icon_warning);
                }
            }
        });


        binding.btnIdea.setOnClickListener(view -> {
            float totalLenOfMeter = (float) ((viewModel.designMasterLiveData.getValue().getTotal_card() / viewModel.designMasterLiveData.getValue().getAvg_pick()) / 39.37);
            binding.editTotalLen.setText(totalLenOfMeter + "/mtr");
        });

    }

    public void addArtist(boolean isCreateNew) {

        String id = dao.getNewId();
        DialogUtil.showProgressDialog(this);
        if (tracker.equals("edit")) {
            if (isCreateNew) {
                duplicateDesignMasterModel = new DesignMasterModel();
                for (int i = 0; i < viewModel.designMasterLiveData.getValue().getJalaWithFileModelList().size(); i++) {
                    duplicateFileList(id, viewModel.designMasterLiveData.getValue().getJalaWithFileModelList().get(pos).getFile_uri(), i);
                }
            } else {
                dao.update((String) viewModel.designMasterLiveData.getValue().getDesignMasterHashMap().get("id"), viewModel.designMasterLiveData.getValue().getDesignMasterHashMap()).addOnSuccessListener(runnable -> {
                    DialogUtil.showUpdateSuccessDialog(this, true);
                });
            }

        } else {
            uploadFileList(fileUriList.get(pos), id);
        }
    }

    public void duplicateFileList(String id, String uri, int dupPos) {
        if (viewModel.designMasterLiveData.getValue().getJalaWithFileModelList().get(dupPos).isFirebaseURI()) {


            StorageReference originalRef = FirebaseStorage.getInstance().getReferenceFromUrl(uri);
            StorageReference duplicateRef = FirebaseStorage.getInstance().getReference().child("design").child(id).child(viewModel.designMasterLiveData.getValue().getJalaWithFileModelList().get(dupPos).getJala_name());


            originalRef.getBytes(Long.MAX_VALUE).addOnSuccessListener(bytes -> {
                duplicateRef.putBytes(bytes).addOnSuccessListener(runnable -> {
                    FirebaseStorage.getInstance().getReference().child("design").child(id).child(viewModel.designMasterLiveData.getValue().getJalaWithFileModelList().get(dupPos).getJala_name()).getDownloadUrl().addOnSuccessListener(uri1 -> {


                        if (duplicateJalaWithFileModelList.size() < viewModel.designMasterLiveData.getValue().getJalaWithFileModelList().size()) {
                            duplicateJalaWithFileModelList.add(new JalaWithFileModel(viewModel.designMasterLiveData.getValue().getJalaWithFileModelList().get(dupPos).getJala_name(), uri1.toString(), true, true));
                            if (duplicateJalaWithFileModelList.size() == viewModel.designMasterLiveData.getValue().getJalaWithFileModelList().size()) {

                                duplicateDesignMasterModel.setId(id);
                                duplicateDesignMasterModel.setDesign_no(viewModel.designMasterLiveData.getValue().getDesign_no());
                                duplicateDesignMasterModel.setDate(viewModel.designMasterLiveData.getValue().getDate());
                                duplicateDesignMasterModel.setReed(viewModel.designMasterLiveData.getValue().getReed());
                                duplicateDesignMasterModel.setBase_pick(viewModel.designMasterLiveData.getValue().getBase_pick());
                                duplicateDesignMasterModel.setBase_card(viewModel.designMasterLiveData.getValue().getBase_card());
                                duplicateDesignMasterModel.setTotal_card(viewModel.designMasterLiveData.getValue().getTotal_card());
                                duplicateDesignMasterModel.setAvg_pick(viewModel.designMasterLiveData.getValue().getAvg_pick());
                                duplicateDesignMasterModel.setType(viewModel.designMasterLiveData.getValue().getType());
                                duplicateDesignMasterModel.setF_list(viewModel.designMasterLiveData.getValue().getF_list());
                                duplicateDesignMasterModel.setTotal_len(viewModel.designMasterLiveData.getValue().getTotal_len());
                                duplicateDesignMasterModel.setSample_card_len(viewModel.designMasterLiveData.getValue().getSample_card_len());
                                duplicateDesignMasterModel.setJalaWithFileModelList(duplicateJalaWithFileModelList);
                                dao.insert(id, viewModel.designMasterLiveData.getValue()).addOnSuccessListener(runnable1 -> {
                                    DialogUtil.showSuccessDialog(DesignMasterActivity.this, true);
                                });
                            }
                        }


                    });
                });
            }).addOnFailureListener(exception -> {

            });

        } else {
            if (pos < (fileUriList.size() - 1)) {
                uploadFileList(fileUriList.get(pos++), id);
            }
        }
    }

    public void uploadFileList(String uploadString, String id) {
        if (!viewModel.designMasterLiveData.getValue().getJalaWithFileModelList().get(pos).isFirebaseURI()) {
            if (uploadString != null) {
                Uri uploadURI = Uri.parse(uploadString);
                if (uploadURI != null) {


                    dao.uploadPhoto(id, viewModel.designMasterLiveData.getValue().getJalaWithFileModelList().get(pos).getJala_name(), uploadURI).addOnSuccessListener(runnable1 -> {
                        dao.getDownloadUri(id, viewModel.designMasterLiveData.getValue().getJalaWithFileModelList().get(pos).getJala_name()).addOnSuccessListener(uri -> {

                            viewModel.designMasterLiveData.getValue().getJalaWithFileModelList().get(pos).setFile_uri(uri.toString());
                            viewModel.designMasterLiveData.getValue().getJalaWithFileModelList().get(pos).setFirebaseURI(true);
                            if (pos < (fileUriList.size() - 1)) {
                                uploadFileList((String) fileUriList.get(pos++), id);
                            } else {
                                if (viewModel.designMasterLiveData.getValue().getJalaWithFileModelList() != null) {
                                    viewModel.designMasterLiveData.getValue().setId(id);
                                    dao.insert(id, viewModel.designMasterLiveData.getValue()).addOnSuccessListener(runnable -> {
                                        DialogUtil.showSuccessDialog(this, true);
                                    });
                                } else {
                                    new CustomSnackUtil().showSnack(this, "Jala List is Empty!", R.drawable.error_msg_icon);
                                }
                            }
                        }).addOnFailureListener(runnable2 -> {
                            DialogUtil.showErrorDialog(this);
                        });

                    }).addOnFailureListener(runnable3 -> {
                        DialogUtil.showErrorDialog(this);
                    });

                }
            } else {
                viewModel.designMasterLiveData.getValue().getJalaWithFileModelList().get(pos).setFirebaseURI(false);
                if (pos < (fileUriList.size() - 1)) {
                    uploadFileList(fileUriList.get(pos++), id);
                } else {
                    if (viewModel.designMasterLiveData.getValue().getJalaWithFileModelList() != null) {
                        viewModel.designMasterLiveData.getValue().setId(id);
                        dao.insert(id, viewModel.designMasterLiveData.getValue()).addOnSuccessListener(runnable -> {
                            DialogUtil.showSuccessDialog(this, true);
                        });
                    } else {
                        new CustomSnackUtil().showSnack(this, "Jala List is Empty!", R.drawable.error_msg_icon);
                    }
                }
            }
        } else {
            if (pos < (fileUriList.size() - 1)) {
                uploadFileList(fileUriList.get(pos++), id);
            }
        }
    }

    public boolean isValidList() {
        boolean bool = true;
//        for (int i = 0; i < jalaWithFileModelList.size(); i++) {
//            if (!jalaWithFileModelList.get(i).isReady()) {
//                bool = false;
//                return bool;
//            }
//        }
        return bool;
    }

    public void setUpStep1() {
        viewVisibilityChanger(STEP1);
    }

    public void setUpStep2() {

        float totalLenOfMeter = (float) ((viewModel.designMasterLiveData.getValue().getTotal_card() / viewModel.designMasterLiveData.getValue().getAvg_pick()) / 39.37);
        binding.editTotalLen.setText(totalLenOfMeter + "/mtr");
        viewVisibilityChanger(STEP2);
    }

    public void setUpStep3() {
        if (viewModel.designMasterLiveData.getValue().getJalaWithFileModelList() != null) {
            this.jalaWithFileModelList = viewModel.designMasterLiveData.getValue().getJalaWithFileModelList();
        }
        jalaSelectedListAdapter = new JalaSelectedListAdapter(jalaWithFileModelList, this, this);
        binding.recyclerViewJala.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerViewJala.setAdapter(jalaSelectedListAdapter);
        viewVisibilityChanger(STEP3);
    }

    public void viewVisibilityChanger(int step) {
        CURRENT_STEP = step;


        switch (step) {
            case STEP1:
                seekTrack(1);
                binding.btnBack.setVisibility(View.GONE);
                binding.btnCreate.setVisibility(View.GONE);
                binding.viewDesign1.setVisibility(View.VISIBLE);
                binding.viewDesign2.setVisibility(View.GONE);
                binding.viewDesign3.setVisibility(View.GONE);
                break;
            case STEP2:
                seekTrack(2);
                binding.btnBack.setVisibility(View.VISIBLE);
                binding.btnCreate.setVisibility(View.GONE);
                binding.viewDesign1.setVisibility(View.GONE);
                binding.viewDesign2.setVisibility(View.VISIBLE);
                binding.viewDesign3.setVisibility(View.GONE);
                break;
            case STEP3:
                seekTrack(3);
                binding.btnBack.setVisibility(View.VISIBLE);
                if (tracker.equals("edit")) binding.btnCreate.setVisibility(View.VISIBLE);
                binding.viewDesign1.setVisibility(View.GONE);
                binding.viewDesign2.setVisibility(View.GONE);
                binding.viewDesign3.setVisibility(View.VISIBLE);
                break;
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000 && resultCode == -1 && data != null && data.getData() != null) {

            jalaWithFileModelList.get(position).setFile_uri(data.getData().toString());
            jalaWithFileModelList.get(position).setFirebaseURI(false);
            jalaWithFileModelList.get(position).setReady(true);
            jalaSelectedListAdapter.notifyDataSetChanged();
        } else if (requestCode == 1001 && resultCode == -1 && data != null && data.getData() != null) {

            if (tracker.equals("edit")) {
                jalaWithFileModelList.get(position).setFile_uri(data.getData().toString());
                jalaWithFileModelList.get(position).setFirebaseURI(false);
                jalaWithFileModelList.get(position).setReady(true);
                jalaSelectedListAdapter.notifyDataSetChanged();
                uploadSingleFile(data.getData().toString(), viewModel.designMasterLiveData.getValue().getId(), new Date().toString());

            } else {
                jalaWithFileModelList.get(position).setFile_uri(data.getData().toString());
                jalaWithFileModelList.get(position).setFirebaseURI(false);
                jalaWithFileModelList.get(position).setReady(true);
                jalaSelectedListAdapter.notifyDataSetChanged();
            }

        } else if (requestCode == 222 && resultCode == -1 && data != null && data.getData() != null) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                btn_upload_photo.setVisibility(View.GONE);
                img_preview.setVisibility(View.VISIBLE);
                img_photo.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void uploadSingleFile(String uploadString, String id, String unique_name) {


        Uri uploadURI = Uri.parse(uploadString);
        if (uploadURI != null) {
            DialogUtil.showProgressDialog(this);
            dao.uploadPhoto(unique_name, viewModel.designMasterLiveData.getValue().getJalaWithFileModelList().get(pos).getJala_name(), uploadURI).addOnSuccessListener(runnable1 -> {
                dao.getDownloadUri(unique_name, viewModel.designMasterLiveData.getValue().getJalaWithFileModelList().get(pos).getJala_name()).addOnSuccessListener(uri -> {

                    viewModel.designMasterLiveData.getValue().getJalaWithFileModelList().get(position).setFile_uri(uri.toString());
                    viewModel.designMasterLiveData.getValue().getJalaWithFileModelList().get(position).setFirebaseURI(true);

                    if (viewModel.designMasterLiveData.getValue().getJalaWithFileModelList() != null) {
                        dao.insert(viewModel.designMasterLiveData.getValue().getId(), viewModel.designMasterLiveData.getValue()).addOnSuccessListener(runnable -> {
                            DialogUtil.showSuccessDialog(this, false);
                        });
                    } else {
                        new CustomSnackUtil().showSnack(this, "Jala List is Empty!", R.drawable.error_msg_icon);
                    }

                }).addOnFailureListener(runnable2 -> {
                    DialogUtil.showErrorDialog(this);
                });

            }).addOnFailureListener(runnable3 -> {
                DialogUtil.showErrorDialog(this);
            });
        }
    }

    public void initData() {
        tracker = (getIntent().getStringExtra("tracker") != null) ? getIntent().getStringExtra("tracker") : "add";
        getJalaList();



        if (tracker != null && tracker.equals("edit")) {
            if (getIntent().getSerializableExtra("design_data") != null) {
                viewModel.designMasterLiveData.setValue((DesignMasterModel) getIntent().getSerializableExtra("design_data"));
                binding.recyclerViewFlist.setAdapter(fAdapter = new FAdapter(viewModel.designMasterLiveData.getValue().getF_list()));
                fAdapter.notifyDataSetChanged();
            }
        } else {
            viewModel.designMasterLiveData.getValue().setDate(new SimpleDateFormat("dd/MM/yy").format(new Date().getTime()));
            if (viewModel.designMasterLiveData.getValue().getF_list() != null) {
                binding.recyclerViewFlist.setAdapter(fAdapter = new FAdapter(viewModel.designMasterLiveData.getValue().getF_list()));
                fAdapter.notifyDataSetChanged();
            } else {
                viewModel.designMasterLiveData.getValue().getF_list().add(null);
            }
        }
    }

    private void getJalaList() {

        daoJalaMaster.getJalaMasterList("");
        daoJalaMaster.getJalaMasterListLiveData().observe(this, jalaMasterModels -> {
            jala_list.clear();
            for (JalaMasterModel jalaMasterModel : jalaMasterModels) {
                jala_list.add(jalaMasterModel.getJala_name());
            }
            if (jalaListDialog != null) {
                jalaListDialog.update_list(jala_list);
            } else {
                jalaListDialog = new ListShowDialog(DesignMasterActivity.this, jala_list, DesignMasterActivity.this, JALA_REQUEST);
            }
        });
    }


    public void initToolbar() {
        binding.include.textTitle.setText("Design Master");
        binding.include.btnBack.setOnClickListener(view -> {
            this.onBackPressed();
        });
    }

    public void seekTrack(int i) {
        binding.dot1.setImageResource(R.drawable.circle_border);
        binding.dot2.setImageResource(R.drawable.circle_border);
        binding.dot3.setImageResource(R.drawable.circle_border);
        binding.dot1.setColorFilter(ContextCompat.getColor(this, R.color.unSelected_btn), android.graphics.PorterDuff.Mode.SRC_IN);
        binding.dot2.setColorFilter(ContextCompat.getColor(this, R.color.unSelected_btn), android.graphics.PorterDuff.Mode.SRC_IN);
        binding.dot3.setColorFilter(ContextCompat.getColor(this, R.color.unSelected_btn), android.graphics.PorterDuff.Mode.SRC_IN);
        binding.line1.setColorFilter(ContextCompat.getColor(this, R.color.unSelected_btn), android.graphics.PorterDuff.Mode.SRC_IN);
        binding.line2.setColorFilter(ContextCompat.getColor(this, R.color.unSelected_btn), android.graphics.PorterDuff.Mode.SRC_IN);
        binding.textStep1.setTextColor(ContextCompat.getColor(this, R.color.unSelected_btn));
        binding.textStep2.setTextColor(ContextCompat.getColor(this, R.color.unSelected_btn));
        binding.textStep3.setTextColor(ContextCompat.getColor(this, R.color.unSelected_btn));

        if (i == 1) {
            binding.dot1.setImageResource(R.drawable.circle);
            binding.dot1.setColorFilter(ContextCompat.getColor(this, R.color.theme), android.graphics.PorterDuff.Mode.SRC_IN);
            binding.textStep1.setTextColor(ContextCompat.getColor(this, R.color.theme));
        } else if (i == 2) {
            seekTrack(1);
            binding.dot2.setImageResource(R.drawable.circle);
            binding.dot2.setColorFilter(ContextCompat.getColor(this, R.color.theme), android.graphics.PorterDuff.Mode.SRC_IN);
            binding.line1.setColorFilter(ContextCompat.getColor(this, R.color.theme), android.graphics.PorterDuff.Mode.SRC_IN);
            binding.textStep2.setTextColor(ContextCompat.getColor(this, R.color.theme));
        } else if (i == 3) {
            seekTrack(2);
            binding.dot3.setImageResource(R.drawable.circle);
            binding.dot3.setColorFilter(ContextCompat.getColor(this, R.color.theme), android.graphics.PorterDuff.Mode.SRC_IN);
            binding.line2.setColorFilter(ContextCompat.getColor(this, R.color.theme), android.graphics.PorterDuff.Mode.SRC_IN);
            binding.textStep3.setTextColor(ContextCompat.getColor(this, R.color.theme));
        } else {
            binding.dot1.setImageResource(R.drawable.circle);
            binding.dot1.setColorFilter(ContextCompat.getColor(this, R.color.unSelected_btn), android.graphics.PorterDuff.Mode.SRC_IN);
            binding.dot2.setColorFilter(ContextCompat.getColor(this, R.color.unSelected_btn), android.graphics.PorterDuff.Mode.SRC_IN);
            binding.dot3.setColorFilter(ContextCompat.getColor(this, R.color.unSelected_btn), android.graphics.PorterDuff.Mode.SRC_IN);
            binding.line1.setColorFilter(ContextCompat.getColor(this, R.color.unSelected_btn), android.graphics.PorterDuff.Mode.SRC_IN);
            binding.line2.setColorFilter(ContextCompat.getColor(this, R.color.unSelected_btn), android.graphics.PorterDuff.Mode.SRC_IN);
            binding.textStep1.setTextColor(ContextCompat.getColor(this, R.color.unSelected_btn));
            binding.textStep2.setTextColor(ContextCompat.getColor(this, R.color.unSelected_btn));
            binding.textStep3.setTextColor(ContextCompat.getColor(this, R.color.unSelected_btn));
        }
    }

    @Override
    public void onAddImage(int position) {
        if (jalaWithFileModelList.get(position).isReady()) {
            startActivity(new Intent(this, ImagePreviewActivity.class).putExtra("img_uri", jalaWithFileModelList.get(position).getFile_uri()).putExtra("tracker", tracker));
        } else {

            this.position = position;

            Intent intent = new Intent();
            intent.setType("application/octet-stream");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Image from here..."), 1001);

        }
    }

    @Override
    public void onDeleteImage(int position) {
        if (tracker.equals("edit")) {

            StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(jalaWithFileModelList.get(position).getFile_uri());
            storageReference.delete().addOnSuccessListener(runnable -> {

                jalaWithFileModelList.set(position, new JalaWithFileModel(jalaWithFileModelList.get(position).getJala_name(), false));

                new DAODesignMaster(this).getReference().document(viewModel.designMasterLiveData.getValue().getId())
                        .update("jalaWithFileModelList", jalaWithFileModelList)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // update successful
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@Nullable Exception e) {
                                // handle error
                            }
                        });

//                new DAODesignMaster(this).getReference().child(designMasterModel.getId()).child("jalaWithFileModelList").setValue(jalaWithFileModelList);
                jalaSelectedListAdapter.notifyDataSetChanged();

            });


        } else {

            jalaWithFileModelList.set(position, new JalaWithFileModel(jalaWithFileModelList.get(position).getJala_name(), false));
            jalaSelectedListAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onSelected(int position, String name, int request_code) {
        if (request_code == JALA_REQUEST) {
            jalaWithFileModelList.add(new JalaWithFileModel(name, false));
            jalaSelectedListAdapter.notifyDataSetChanged();

        }
    }

    @Override
    public void onAddButtonClicked(int position, String name, int request_code) {
        if (request_code == JALA_REQUEST) {

            if (isInsertPermissionGranted(PermissionState.JALA_MASTER.getValue())) {
                JalaDialog jalaDialog = new JalaDialog();
                Bundle bundle = new Bundle();
                bundle.putString("jala_name", name);
                jalaDialog.setArguments(bundle);
                jalaDialog.show(getSupportFragmentManager(), "My Fragment");
            } else {
                DialogUtil.showAccessDeniedDialog(this);
            }
        }
    }

    @Override
    public void onMultiSelected(ArrayList<String> shadeList, int request_code) {

        if (shadeList.size() == 0) {
            addArtist(false);
        } else {
            ArrayList<ShadeMasterModel> selectedShadeMasterModels = new ArrayList<>();
            DAOShadeMaster daoShadeMaster = new DAOShadeMaster(this);
            daoShadeMaster.getReference().get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                    for (int i = 0; i < shadeList.size(); i++) {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            ShadeMasterModel shadeMasterModel = document.toObject(ShadeMasterModel.class);
                            if (shadeMasterModel.getShade_no().equals(shadeList.get(i))) {
                                selectedShadeMasterModels.add(shadeMasterModel);
                                shadeMasterModel.setDesign_no(viewModel.designMasterLiveData.getValue().getDesign_no());
                                daoShadeMaster.getReference().add(shadeMasterModel).addOnSuccessListener(runnable -> {

                                });
                                break;
                            }
                        }
                    }
                    addArtist(false);
                    Log.d("fjsdfjkbn23ui", "onMultiSelected: " + selectedShadeMasterModels.size());
                }
            });
        }


    }
}