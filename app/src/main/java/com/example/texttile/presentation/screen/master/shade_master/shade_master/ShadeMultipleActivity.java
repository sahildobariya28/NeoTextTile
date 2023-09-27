package com.example.texttile.presentation.screen.master.shade_master.shade_master;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.texttile.R;
import com.example.texttile.databinding.ActivityShadeMultipleBinding;
import com.example.texttile.data.dao.DAODesignMaster;
import com.example.texttile.data.dao.DAOShadeMaster;
import com.example.texttile.data.model.ShadeMasterModel;
import com.example.texttile.presentation.ui.lib.snackUtil.CustomSnackUtil;
import com.example.texttile.presentation.ui.util.DialogUtil;
import com.example.texttile.presentation.ui.util.ListShowDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;

import javax.annotation.Nonnull;

public class ShadeMultipleActivity extends AppCompatActivity {

    ActivityShadeMultipleBinding binding;
    ShadeMultipleViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityShadeMultipleBinding.inflate(getLayoutInflater());
        viewModel = new ViewModelProvider(this, new ShadeMultipleViewModelFactory(this, getSupportFragmentManager())).get(ShadeMultipleViewModel.class);
        setContentView(binding.getRoot());

        initToolbar();

        binding.btnSingleItem.setOnClickListener(view -> {
            viewModel.addItemState.setValue(viewModel.SINGLE_ITEM);
        });

        binding.btnMultipleItem.setOnClickListener(view -> {
            viewModel.addItemState.setValue(viewModel.MULTIPLE_ITEM);
        });

        viewModel.addItemState.observe(this, addItemState -> {
            switch (addItemState) {
                case 1:
                    binding.btnSingleItem.setBackgroundColor(getColor(R.color.selected_btn));
                    binding.btnMultipleItem.setBackgroundColor(getColor(R.color.white_theme_extra_light));
                    binding.btnSingleItem.setTextColor(getColor(R.color.white));
                    binding.btnMultipleItem.setTextColor(getColor(R.color.white_text_light));
                    binding.singleItemLayout.singleItemContainer.setVisibility(View.VISIBLE);
                    binding.multipleItemLayout.multipleItemContainer.setVisibility(View.GONE);

                    viewModel.fAdapter = new FAdapter(getSupportFragmentManager(), this, viewModel);
                    binding.singleItemLayout.recyclerView.setAdapter(viewModel.fAdapter);
                    break;
                case 2:
                    binding.btnSingleItem.setBackgroundColor(getColor(R.color.white_theme_extra_light));
                    binding.btnMultipleItem.setBackgroundColor(getColor(R.color.selected_btn));
                    binding.btnSingleItem.setTextColor(getColor(R.color.white_text_light));
                    binding.btnMultipleItem.setTextColor(getColor(R.color.white));
                    binding.singleItemLayout.singleItemContainer.setVisibility(View.GONE);
                    binding.multipleItemLayout.multipleItemContainer.setVisibility(View.VISIBLE);
                    break;
            }
        });


        if (viewModel.tracker != null && viewModel.tracker.equals("edit")) {
            if (viewModel.shadeMasterModel != null) {
                binding.btnAdd.setText("Update");

                viewModel.adapterSize = viewModel.shadeMasterModel.getF_list().size();
                viewModel.list.getValue().addAll(viewModel.shadeMasterModel.getF_list());
                viewModel.fAdapter.notifyDataSetChanged();

                binding.singleItemLayout.autoDesignNo.setText(String.valueOf(viewModel.shadeMasterModel.getDesign_no()));
                binding.singleItemLayout.editShadeNo.setText(String.valueOf(viewModel.shadeMasterModel.getShade_no()));

            }
        } else {
            if (viewModel.shadeMasterModel != null) {
                for (int j = 0; j < viewModel.designMasterModelsList.getValue().size(); j++) {
                    if (viewModel.designMasterModelsList.getValue().get(j).getDesign_no().equals(binding.singleItemLayout.autoDesignNo.getText().toString().trim())) {
                        viewModel.adapterSize = viewModel.designMasterModelsList.getValue().get(j).getF_list().size();
                        viewModel.list.getValue().clear();
                        for (int i = 0; i < viewModel.adapterSize; i++) {
                            viewModel.list.getValue().add(null);
                            viewModel.fAdapter.notifyDataSetChanged();
                        }
                    }
                }
                binding.singleItemLayout.autoDesignNo.requestFocus();
            }
        }

        binding.singleItemLayout.autoWrapColor.setOnClickListener(view -> {
            if (viewModel.warpListDialog != null) {
                viewModel.warpListDialog.show_dialog();
            } else {
                viewModel.warpListDialog = new ListShowDialog(this, viewModel.warpColorList.getValue(), viewModel.getSelectDialogRef(), viewModel.WARP_REQUEST);
                viewModel.warpListDialog.show_dialog();
            }
        });
        binding.singleItemLayout.autoDesignNo.setOnClickListener(view -> {
            if (viewModel.designListDialog != null) {
                viewModel.designListDialog.show_dialog();
            } else {
                viewModel.designListDialog = new ListShowDialog(this, viewModel.design_no_list.getValue(), viewModel.getSelectDialogRef(), viewModel.DESIGN_REQUEST);
                viewModel.designListDialog.show_dialog();
            }
        });
        binding.multipleItemLayout.autoCopyDesignNoTo.setOnClickListener(view -> {
            if (viewModel.designCopyToDialog != null) {
                viewModel.designCopyToDialog.show_dialog();
            } else {
                viewModel.designCopyToDialog = new ListShowDialog(this, viewModel.design_no_list.getValue(), viewModel.getSelectDialogRef(), viewModel.DESIGN_REQUEST_COPY_TO);
                viewModel.designCopyToDialog.show_dialog();
            }
        });
        binding.multipleItemLayout.autoCopyDesignNoFrom.setOnClickListener(view -> {
            if (viewModel.designCopyFromDialog != null) {
                viewModel.designCopyFromDialog.show_dialog();
            } else {
                viewModel.designCopyFromDialog = new ListShowDialog(this, viewModel.design_no_list.getValue(), viewModel.getSelectDialogRef(), viewModel.DESIGN_REQUEST_COPY_FROM);
                viewModel.designCopyFromDialog.show_dialog();
            }
        });

        binding.btnAdd.setOnClickListener(view -> {

            addArtist();
        });

        viewModel.autoDesignNo.observe(this, text -> {
            binding.singleItemLayout.autoDesignNo.setText(text);
        });
        viewModel.textCopyToDesign.observe(this, text -> {
            binding.multipleItemLayout.autoCopyDesignNoTo.setText(text);
        });
        viewModel.textCopyFromDesign.observe(this, text -> {
            binding.multipleItemLayout.autoCopyDesignNoFrom.setText(text);
        });

        viewModel.editWrapColor.observe(this, text -> {
            binding.singleItemLayout.autoWrapColor.setText(text);
        });

    }


    private void addArtist() {

        if (viewModel.addItemState.getValue() == viewModel.SINGLE_ITEM) {
            String design_no = binding.singleItemLayout.autoDesignNo.getText().toString().trim();
            String shade_no = binding.singleItemLayout.editShadeNo.getText().toString().trim();
            String wrap_color = binding.singleItemLayout.autoWrapColor.getText().toString().trim();

            boolean is_valid = true;
            for (int i = 0; i < viewModel.list.getValue().size(); i++) {
                if (TextUtils.isEmpty(viewModel.list.getValue().get(i))) {
                    new CustomSnackUtil().showSnack(this, "Please Enter Jala" + i, R.drawable.icon_warning);
                    is_valid = false;
                    break;
                }
            }
            if (is_valid) {
                if (TextUtils.isEmpty(shade_no)) {
                    new CustomSnackUtil().showSnack(this, "Please enter a Shade no", R.drawable.icon_warning);
                } else if (TextUtils.isEmpty(design_no)) {
                    new CustomSnackUtil().showSnack(this, "Please enter a Design no", R.drawable.icon_warning);
                } else if (TextUtils.isEmpty(wrap_color)) {
                    new CustomSnackUtil().showSnack(this, "Please enter a Wrap color", R.drawable.icon_warning);
                } else {


                    new DAODesignMaster(ShadeMultipleActivity.this).getReference().whereEqualTo("design_no", design_no).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@Nonnull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                QuerySnapshot snapshot = task.getResult();
                                if (snapshot.isEmpty()) {
                                    new CustomSnackUtil().showSnack(ShadeMultipleActivity.this, "Warp Color not match", R.drawable.error_msg_icon);
                                    return;
                                }
                                String id = viewModel.dao.getId();
                                DialogUtil.showProgressDialog(ShadeMultipleActivity.this);
                                if (viewModel.tracker.equals("edit")) {
                                    HashMap<String, Object> hashMap = new HashMap<>();
                                    hashMap.put("shade_no", shade_no);
                                    hashMap.put("design_no", design_no);
                                    hashMap.put("wrap_color", wrap_color);
                                    hashMap.put("f_list", viewModel.list.getValue());

                                    viewModel.dao.update(viewModel.shadeMasterModel.getId(), hashMap).addOnSuccessListener(runnable -> {
                                        DialogUtil.showUpdateSuccessDialog(ShadeMultipleActivity.this, true);
                                    });
                                } else {
                                    viewModel.shadeMasterModel.setId(id);
                                    viewModel.shadeMasterModel.setShade_no(shade_no);
                                    viewModel.shadeMasterModel.setDesign_no(design_no);
                                    viewModel.shadeMasterModel.setWrap_color(wrap_color);
                                    viewModel.shadeMasterModel.setF_list(viewModel.list.getValue());

                                    viewModel.dao.insert(id, viewModel.shadeMasterModel).addOnSuccessListener(runnable -> {
                                        DialogUtil.showSuccessDialog(ShadeMultipleActivity.this, true);
                                    });
                                }
                            } else {
                                Log.d("TAG", "Error getting documents: ", task.getException());
                            }
                        }
                    });
                }
            }
        } else if (viewModel.addItemState.getValue() == viewModel.MULTIPLE_ITEM) {
            binding.multipleItemLayout.shadeGeneratedList.setText("");
            String copy_design_no_to = binding.multipleItemLayout.autoCopyDesignNoTo.getText().toString().trim();
            String design_no_from = binding.multipleItemLayout.autoCopyDesignNoFrom.getText().toString().trim();
            boolean is_valid = true;

            for (int i = 0; i < viewModel.list.getValue().size(); i++) {
                if (TextUtils.isEmpty(viewModel.list.getValue().get(i))) {
                    new CustomSnackUtil().showSnack(this, "Please Enter Jala" + i, R.drawable.icon_warning);
                    is_valid = false;
                    break;
                }
            }

            if (is_valid) {
                if (TextUtils.isEmpty(design_no_from)) {
                    new CustomSnackUtil().showSnack(this, "Please enter a Design no", R.drawable.icon_warning);
                } else if (TextUtils.isEmpty(copy_design_no_to)) {
                    new CustomSnackUtil().showSnack(this, "Please enter a Copy Shade no", R.drawable.icon_warning);
                } else {
                    DialogUtil.showProgressDialog(this);
                    new DAOShadeMaster(this)
                            .getReference()
                            .get()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    QuerySnapshot snapshot = task.getResult();
                                    if (snapshot != null && !snapshot.isEmpty()) {
                                        for (QueryDocumentSnapshot documentSnapshot : snapshot) {
                                            ShadeMasterModel shadeMasterModel1 = documentSnapshot.toObject(ShadeMasterModel.class);
                                            if (shadeMasterModel1.getDesign_no().equals(copy_design_no_to)) {
                                                Log.d("dsfjklsdhkjf345", "addArtist: " + shadeMasterModel1.getShade_no() + " design no :" + shadeMasterModel1.getDesign_no());
                                                String text = binding.multipleItemLayout.shadeGeneratedList.getText().toString() + "shade No = " + shadeMasterModel1.getShade_no() + ", design No = " + design_no_from + "\n";
                                                binding.multipleItemLayout.shadeGeneratedList.setText(text);

                                                String id = viewModel.dao.getId();
                                                shadeMasterModel1.setId(id);
                                                shadeMasterModel1.setDesign_no(binding.multipleItemLayout.autoCopyDesignNoFrom.getText().toString());
                                                viewModel.requestInsertDataCount.setValue(viewModel.requestInsertDataCount.getValue() + 1);
                                                viewModel.dao.insert(id, shadeMasterModel1).addOnSuccessListener(runnable -> {
                                                    viewModel.responseInsertDataCount.setValue(viewModel.responseInsertDataCount.getValue() + 1);
                                                    if (viewModel.requestInsertDataCount.getValue() == viewModel.responseInsertDataCount.getValue()) {
                                                        DialogUtil.hideProgressDialog();
                                                        DialogUtil.showSuccessDialog(this, true);
                                                        viewModel.requestInsertDataCount.setValue(0);
                                                        viewModel.responseInsertDataCount.setValue(0);
                                                    }
                                                });
                                            }
                                        }
                                    }
                                } else {
                                    // Handle the error
                                    Exception exception = task.getException();
                                    if (exception != null) {
                                        // Handle the error
                                        Log.e("TAG", "Error getting documents: " + exception.getMessage());
                                    }
                                }
                            });

                }
            }
        }


//        String design_no = binding.autoDesignNo.getText().toString().trim();
//        String shade_no = binding.editShadeNo.getText().toString().trim();
//        String wrap_color = "";
//
//        boolean is_valid = true;
//        for (int i = 0; i < viewModel.list.getValue().size(); i++) {
//            if (TextUtils.isEmpty(viewModel.list.getValue().get(i))) {
//                new CustomSnackUtil().showSnack(this, "Please Enter Jala" + i, R.drawable.icon_warning);
//                is_valid = false;
//                break;
//            }
//        }
//        if (is_valid) {
//            if (TextUtils.isEmpty(shade_no)) {
//                new CustomSnackUtil().showSnack(this, "Please enter a Shade no", R.drawable.icon_warning);
//            } else if (TextUtils.isEmpty(design_no)) {
//                new CustomSnackUtil().showSnack(this, "Please enter a Design no", R.drawable.icon_warning);
//            } else if (TextUtils.isEmpty(wrap_color)) {
//                new CustomSnackUtil().showSnack(this, "Please enter a Wrap color", R.drawable.icon_warning);
//            } else {
//
//                new DAODesignMaster(ShadeMultipleActivity.this).getReference().whereEqualTo("design_no", design_no).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@Nonnull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            QuerySnapshot snapshot = task.getResult();
//                            if (snapshot.isEmpty()) {
//                                new CustomSnackUtil().showSnack(ShadeMultipleActivity.this, "Warp Color not match", R.drawable.error_msg_icon);
//                                return;
//                            }
//                            String id = viewModel.dao.getId();
//                            DialogUtil.showProgressDialog(ShadeMultipleActivity.this);
//                            if (viewModel.tracker.equals("edit")) {
//                                HashMap<String, Object> hashMap = new HashMap<>();
//                                hashMap.put("shade_no", shade_no);
//                                hashMap.put("design_no", design_no);
//                                hashMap.put("wrap_color", wrap_color);
//                                hashMap.put("f_list", viewModel.list.getValue());
//
//                                viewModel.dao.update(viewModel.shadeMasterModel.getId(), hashMap).addOnSuccessListener(runnable -> {
//                                    DialogUtil.showUpdateSuccessDialog(ShadeMultipleActivity.this, true);
//                                });
//                            } else {
//                                viewModel.shadeMasterModel.setId(id);
//                                viewModel.shadeMasterModel.setShade_no(shade_no);
//                                viewModel.shadeMasterModel.setDesign_no(design_no);
//                                viewModel.shadeMasterModel.setWrap_color(wrap_color);
//                                viewModel.shadeMasterModel.setF_list(viewModel.list.getValue());
//
//                                viewModel.dao.insert(id, viewModel.shadeMasterModel).addOnSuccessListener(runnable -> {
//                                    DialogUtil.showSuccessDialog(ShadeMultipleActivity.this, true);
//                                });
//                            }
//                        } else {
//                            Log.d("TAG", "Error getting documents: ", task.getException());
//                        }
//                    }
//                });
//            }
//        }
    }

    public void initToolbar() {
        binding.include.textTitle.setText("Shade Master");
        binding.include.btnBack.setOnClickListener(view -> {
            onBackPressed();
        });
    }


}