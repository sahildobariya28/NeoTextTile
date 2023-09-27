package com.example.texttile.presentation.screen.master.design_master.dialog;

import static com.example.texttile.presentation.ui.util.PermissionUtil.isInsertPermissionGranted;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Guideline;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.texttile.R;
import com.example.texttile.databinding.DesignMasterDialogBinding;
import com.example.texttile.presentation.ui.activity.ImagePreviewActivity;
import com.example.texttile.presentation.screen.master.design_master.adapter.FAdapter;
import com.example.texttile.presentation.screen.master.design_master.adapter.JalaSelectedListAdapter;
import com.example.texttile.presentation.ui.dialog.JalaDialog;
import com.example.texttile.data.dao.DAODesignMaster;
import com.example.texttile.data.dao.DAOJalaMaster;
import com.example.texttile.data.dao.DAOShadeMaster;
import com.example.texttile.presentation.ui.interfaces.AddOnSearchedItemSelectListener;
import com.example.texttile.data.model.JalaMasterModel;
import com.example.texttile.data.model.JalaWithFileModel;
import com.example.texttile.data.model.ShadeMasterModel;
import com.example.texttile.presentation.ui.lib.snackUtil.CustomSnackUtil;
import com.example.texttile.presentation.ui.util.DialogUtil;
import com.example.texttile.presentation.ui.util.KeyboardUtils;
import com.example.texttile.presentation.ui.util.ListShowDialog;
import com.example.texttile.presentation.ui.util.PermissionState;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.annotation.Nullable;

public class DesignMasterDialogFragment extends DialogFragment implements AddOnSearchedItemSelectListener, JalaSelectedListAdapter.OnClickImageSelectButton {

    public final int STEP1 = 1;
    public final int STEP2 = 2;
    public final int STEP3 = 3;
    public int CURRENT_STEP = 1;
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

    DesignMasterDialogBinding binding;
    DesignMasterDialogViewModel viewModel;

    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(getActivity());
        binding = DesignMasterDialogBinding.inflate(getLayoutInflater());
        viewModel = new ViewModelProvider(this, new DesignMasterDialogViewModelFactory(getArguments().getString("tracker"), getActivity())).get(DesignMasterDialogViewModel.class);
        binding.setDesignModel(viewModel);

        dialog.getWindow().setWindowAnimations(R.style.DialogAnimation);
        dialog.setContentView(binding.getRoot());
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Guideline guideline3 = dialog.findViewById(R.id.guideline3);
        Guideline guideline4 = dialog.findViewById(R.id.guideline4);

        binding.editDesignNo.setText(getArguments().getString("design_no"));

        KeyboardUtils.addKeyboardToggleListener(getActivity(), new KeyboardUtils.SoftKeyboardToggleListener() {
            @Override
            public void onToggleSoftKeyboard(boolean isVisible) {
                if (isVisible) {
                    guideline3.setGuidelinePercent(0.10f);
                    guideline4.setGuidelinePercent(0.90f);
                } else {
                    guideline3.setGuidelinePercent(0.25f);
                    guideline4.setGuidelinePercent(0.75f);
                }
            }
        });

        dao = new DAODesignMaster(getActivity());
        daoJalaMaster = new DAOJalaMaster(getActivity());

        initData();
        setUpStep1();

        binding.textJalaName.setOnClickListener(view -> {
            if (jalaListDialog != null) {
                jalaListDialog.show_dialog();
            } else {
                jalaListDialog = new ListShowDialog(getActivity(), jala_list, this, JALA_REQUEST);
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
            DatePickerDialog mDatePicker = new DatePickerDialog(getContext(), date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
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
                        new CustomSnackUtil().showSnack(getActivity(), "Design No is Empty!", R.drawable.error_msg_icon);
                    } else if (TextUtils.isEmpty(viewModel.designMasterLiveData.getValue().getDate())) {
                        new CustomSnackUtil().showSnack(getActivity(), "Date is Empty!", R.drawable.error_msg_icon);
                    } else if (TextUtils.isEmpty(String.valueOf(viewModel.designMasterLiveData.getValue().getReed()))) {
                        new CustomSnackUtil().showSnack(getActivity(), "Reed is Empty!", R.drawable.error_msg_icon);
                    } else if (TextUtils.isEmpty(String.valueOf(viewModel.designMasterLiveData.getValue().getBase_pick()))) {
                        new CustomSnackUtil().showSnack(getActivity(), "Base Pick is Empty!", R.drawable.error_msg_icon);
                    } else if (TextUtils.isEmpty(String.valueOf(viewModel.designMasterLiveData.getValue().getBase_card()))) {
                        new CustomSnackUtil().showSnack(getActivity(), "Base Card is Empty!", R.drawable.error_msg_icon);
                    } else if (TextUtils.isEmpty(String.valueOf(viewModel.designMasterLiveData.getValue().getTotal_card()))) {
                        new CustomSnackUtil().showSnack(getActivity(), "Total Card is Empty!", R.drawable.error_msg_icon);
                    } else if (TextUtils.isEmpty(String.valueOf(viewModel.designMasterLiveData.getValue().getAvg_pick()))) {
                        new CustomSnackUtil().showSnack(getActivity(), "Avg Pick is Empty!", R.drawable.error_msg_icon);
                    } else {
                        setUpStep2();
                    }
                    break;
                case STEP2:

                    if (viewModel.designMasterLiveData.getValue().getF_list().size() <= 8) {
                        boolean is_valid = true;
                        for (int i = 0; i < viewModel.designMasterLiveData.getValue().getF_list().size(); i++) {

                            if (viewModel.designMasterLiveData.getValue().getF_list().get(i) == null) {
                                new CustomSnackUtil().showSnack(getActivity(), "Please Select at least 1 Jala!", R.drawable.error_msg_icon);
                                is_valid = false;
                                break;
                            }
                        }
                        if (is_valid) {
                            if (TextUtils.isEmpty(binding.editTotalLen.getText())) {
                                new CustomSnackUtil().showSnack(getActivity(), "Total Length is Empty!", R.drawable.error_msg_icon);
                            } else if (TextUtils.isEmpty(binding.editSampleCardLen1.getText())) {
                                new CustomSnackUtil().showSnack(getActivity(), "Sample Card Starting is Empty!", R.drawable.error_msg_icon);
                            } else if (TextUtils.isEmpty(binding.editSampleCardLen2.getText())) {
                                new CustomSnackUtil().showSnack(getActivity(), "Sample card Ending is Empty!", R.drawable.error_msg_icon);
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
                        new CustomSnackUtil().showSnack(getActivity(), "Please Select at least 1 Jala!", R.drawable.error_msg_icon);
                    } else if (!isValidList()) {
                        new CustomSnackUtil().showSnack(getActivity(), "Please Select all Design File!", R.drawable.error_msg_icon);
                    } else {
                        if (jalaWithFileModelList != null) {
                            ArrayList<String> arrayList = new ArrayList<>();
                            for (JalaWithFileModel jalaWithFileModel : jalaWithFileModelList) {
//                                if (!tracker.equals("edit")) {
                                    arrayList.add(jalaWithFileModel.getFile_uri());
//                                }
                            }
                            viewModel.designMasterLiveData.getValue().setJalaWithFileModelList(jalaWithFileModelList);
                            this.fileUriList = arrayList;

                            addArtist(false);
                        }
                    }
                    break;
            }
        });


        binding.btnIdea.setOnClickListener(view -> {
            float totalLenOfMeter = (float) ((viewModel.designMasterLiveData.getValue().getTotal_card() / viewModel.designMasterLiveData.getValue().getAvg_pick()) / 39.37);
            binding.editTotalLen.setText(totalLenOfMeter + "/mtr");
        });
        return dialog;
    }

    public void addArtist(boolean isCreateNew) {

        String id = dao.getNewId();
        DialogUtil.showProgressDialog(getActivity());
            uploadFileList(fileUriList.get(pos), id);
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
                                        jalaListDialog.dismiss_dialog();
                                        DialogUtil.showSuccessDialog(getActivity(), false);
                                    });
                                } else {
                                    new CustomSnackUtil().showSnack(getActivity(), "Jala List is Empty!", R.drawable.error_msg_icon);
                                }
                            }
                        }).addOnFailureListener(runnable2 -> {
                            DialogUtil.showErrorDialog(getActivity());
                        });

                    }).addOnFailureListener(runnable3 -> {
                        DialogUtil.showErrorDialog(getActivity());
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
                            jalaListDialog.dismiss_dialog();
                            DialogUtil.showSuccessDialog(getActivity(), false);
                        });
                    } else {
                        new CustomSnackUtil().showSnack(getActivity(), "Jala List is Empty!", R.drawable.error_msg_icon);
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
        jalaSelectedListAdapter = new JalaSelectedListAdapter(jalaWithFileModelList, getContext(), this);
        binding.recyclerViewJala.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewJala.setAdapter(jalaSelectedListAdapter);
        viewVisibilityChanger(STEP3);
    }

    public void viewVisibilityChanger(int step) {
        CURRENT_STEP = step;


        switch (step) {
            case STEP1:
                seekTrack(1);
                binding.btnBack.setVisibility(View.GONE);
                binding.viewDesign1.setVisibility(View.VISIBLE);
                binding.viewDesign2.setVisibility(View.GONE);
                binding.viewDesign3.setVisibility(View.GONE);
                break;
            case STEP2:
                seekTrack(2);
                binding.btnBack.setVisibility(View.VISIBLE);
                binding.viewDesign1.setVisibility(View.GONE);
                binding.viewDesign2.setVisibility(View.VISIBLE);
                binding.viewDesign3.setVisibility(View.GONE);
                break;
            case STEP3:
                seekTrack(3);
                binding.btnBack.setVisibility(View.VISIBLE);
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

                jalaWithFileModelList.get(position).setFile_uri(data.getData().toString());
                jalaWithFileModelList.get(position).setFirebaseURI(false);
                jalaWithFileModelList.get(position).setReady(true);
                jalaSelectedListAdapter.notifyDataSetChanged();

        } else if (requestCode == 222 && resultCode == -1 && data != null && data.getData() != null) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());
                btn_upload_photo.setVisibility(View.GONE);
                img_preview.setVisibility(View.VISIBLE);
                img_photo.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void initData() {
        getJalaList();

            viewModel.designMasterLiveData.getValue().setDate(new SimpleDateFormat("dd/MM/yy").format(new Date().getTime()));
            if (viewModel.designMasterLiveData.getValue().getF_list() != null) {
                binding.recyclerViewFlist.setAdapter(fAdapter = new FAdapter(viewModel.designMasterLiveData.getValue().getF_list()));
                fAdapter.notifyDataSetChanged();
            } else {
                viewModel.designMasterLiveData.getValue().getF_list().add(null);
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
                jalaListDialog = new ListShowDialog(getActivity(), jala_list, DesignMasterDialogFragment.this, JALA_REQUEST);
            }
        });
    }

    public void seekTrack(int i) {
        binding.dot1.setImageResource(R.drawable.circle_border);
        binding.dot2.setImageResource(R.drawable.circle_border);
        binding.dot3.setImageResource(R.drawable.circle_border);
        binding.dot1.setColorFilter(ContextCompat.getColor(getContext(), R.color.unSelected_btn), android.graphics.PorterDuff.Mode.SRC_IN);
        binding.dot2.setColorFilter(ContextCompat.getColor(getContext(), R.color.unSelected_btn), android.graphics.PorterDuff.Mode.SRC_IN);
        binding.dot3.setColorFilter(ContextCompat.getColor(getContext(), R.color.unSelected_btn), android.graphics.PorterDuff.Mode.SRC_IN);
        binding.line1.setColorFilter(ContextCompat.getColor(getContext(), R.color.unSelected_btn), android.graphics.PorterDuff.Mode.SRC_IN);
        binding.line2.setColorFilter(ContextCompat.getColor(getContext(), R.color.unSelected_btn), android.graphics.PorterDuff.Mode.SRC_IN);
        binding.textStep1.setTextColor(ContextCompat.getColor(getContext(), R.color.unSelected_btn));
        binding.textStep2.setTextColor(ContextCompat.getColor(getContext(), R.color.unSelected_btn));
        binding.textStep3.setTextColor(ContextCompat.getColor(getContext(), R.color.unSelected_btn));

        if (i == 1) {
            binding.dot1.setImageResource(R.drawable.circle);
            binding.dot1.setColorFilter(ContextCompat.getColor(getContext(), R.color.theme), android.graphics.PorterDuff.Mode.SRC_IN);
            binding.textStep1.setTextColor(ContextCompat.getColor(getContext(), R.color.theme));
        } else if (i == 2) {
            seekTrack(1);
            binding.dot2.setImageResource(R.drawable.circle);
            binding.dot2.setColorFilter(ContextCompat.getColor(getContext(), R.color.theme), android.graphics.PorterDuff.Mode.SRC_IN);
            binding.line1.setColorFilter(ContextCompat.getColor(getContext(), R.color.theme), android.graphics.PorterDuff.Mode.SRC_IN);
            binding.textStep2.setTextColor(ContextCompat.getColor(getContext(), R.color.theme));
        } else if (i == 3) {
            seekTrack(2);
            binding.dot3.setImageResource(R.drawable.circle);
            binding.dot3.setColorFilter(ContextCompat.getColor(getContext(), R.color.theme), android.graphics.PorterDuff.Mode.SRC_IN);
            binding.line2.setColorFilter(ContextCompat.getColor(getContext(), R.color.theme), android.graphics.PorterDuff.Mode.SRC_IN);
            binding.textStep3.setTextColor(ContextCompat.getColor(getContext(), R.color.theme));
        } else {
            binding.dot1.setImageResource(R.drawable.circle);
            binding.dot1.setColorFilter(ContextCompat.getColor(getContext(), R.color.unSelected_btn), android.graphics.PorterDuff.Mode.SRC_IN);
            binding.dot2.setColorFilter(ContextCompat.getColor(getContext(), R.color.unSelected_btn), android.graphics.PorterDuff.Mode.SRC_IN);
            binding.dot3.setColorFilter(ContextCompat.getColor(getContext(), R.color.unSelected_btn), android.graphics.PorterDuff.Mode.SRC_IN);
            binding.line1.setColorFilter(ContextCompat.getColor(getContext(), R.color.unSelected_btn), android.graphics.PorterDuff.Mode.SRC_IN);
            binding.line2.setColorFilter(ContextCompat.getColor(getContext(), R.color.unSelected_btn), android.graphics.PorterDuff.Mode.SRC_IN);
            binding.textStep1.setTextColor(ContextCompat.getColor(getContext(), R.color.unSelected_btn));
            binding.textStep2.setTextColor(ContextCompat.getColor(getContext(), R.color.unSelected_btn));
            binding.textStep3.setTextColor(ContextCompat.getColor(getContext(), R.color.unSelected_btn));
        }
    }

    @Override
    public void onAddImage(int position) {
        if (jalaWithFileModelList.get(position).isReady()) {
            startActivity(new Intent(getActivity(), ImagePreviewActivity.class).putExtra("img_uri", jalaWithFileModelList.get(position).getFile_uri()).putExtra("tracker", "add"));
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

            jalaWithFileModelList.set(position, new JalaWithFileModel(jalaWithFileModelList.get(position).getJala_name(), false));
            jalaSelectedListAdapter.notifyDataSetChanged();

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
                jalaDialog.show(getChildFragmentManager(), "My Fragment");
            } else {
                DialogUtil.showAccessDeniedDialog(getActivity());
            }
        }
    }

    @Override
    public void onMultiSelected(ArrayList<String> shadeList, int request_code) {

        if (shadeList.size() == 0) {
            addArtist(false);
        } else {
            ArrayList<ShadeMasterModel> selectedShadeMasterModels = new ArrayList<>();
            DAOShadeMaster daoShadeMaster = new DAOShadeMaster(getActivity());
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