package com.example.texttile.presentation.ui.dialog;

import static com.example.texttile.presentation.ui.util.PermissionUtil.isInsertPermissionGranted;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;

import androidx.fragment.app.DialogFragment;

import com.example.texttile.R;
import com.example.texttile.databinding.MachineMasterDialogBinding;
import com.example.texttile.data.dao.DAOJalaMaster;
import com.example.texttile.data.dao.DAOMachineMaster;
import com.example.texttile.data.dao.DaoYarnMaster;
import com.example.texttile.presentation.ui.interfaces.AddOnSearchedItemSelectListener;
import com.example.texttile.data.model.JalaMasterModel;
import com.example.texttile.data.model.MachineMasterModel;
import com.example.texttile.data.model.YarnMasterModel;
import com.example.texttile.presentation.ui.lib.snackUtil.CustomSnackUtil;
import com.example.texttile.presentation.ui.util.DialogUtil;
import com.example.texttile.presentation.ui.util.KeyboardUtils;
import com.example.texttile.presentation.ui.util.ListShowDialog;
import com.example.texttile.presentation.ui.util.PermissionState;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import javax.annotation.Nullable;

public class MachineMasterDialog extends DialogFragment implements AddOnSearchedItemSelectListener {

    MachineMasterDialogBinding binding;

    ArrayList<String> warpColorList = new ArrayList<>();
    ArrayList<String> jala_list = new ArrayList<>();

    MachineMasterModel machineMasterModel;
    DAOMachineMaster dao;
    ListShowDialog jalaListDialog, warpListDialog;
    final int JALA_REQUEST = 1, WARP_REQUEST = 2;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dao = new DAOMachineMaster(getActivity());
    }

    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(getActivity());
        binding = MachineMasterDialogBinding.inflate(getLayoutInflater());
        dialog.getWindow().setWindowAnimations(R.style.DialogAnimation);
        dialog.setContentView(binding.getRoot());
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        binding.editMachineName.setText(getArguments().getString("machine_name"));

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

        setAutoAdapter();

        machineMasterModel = new MachineMasterModel();


        if (machineMasterModel != null) {
            binding.editMachineName.requestFocus();
        }

        binding.btnAdd.setOnClickListener(view -> addArtist());

        binding.btnClose.setOnClickListener(view -> {
            dialog.dismiss();
        });
        binding.textJala.setOnClickListener(view -> {
            if (jalaListDialog != null) {
                jalaListDialog.show_dialog();
            } else {
                jalaListDialog = new ListShowDialog(getActivity(), jala_list, MachineMasterDialog.this, JALA_REQUEST);
                jalaListDialog.show_dialog();
            }
        });

        binding.textWrapColor.setOnClickListener(view -> {
            if (warpListDialog != null) {
                warpListDialog.show_dialog();
            } else {
                warpListDialog = new ListShowDialog(getActivity(), warpColorList, MachineMasterDialog.this, WARP_REQUEST);
                warpListDialog.show_dialog();
            }
        });

        return dialog;
    }

    private void addArtist() {
        String machine_name = binding.editMachineName.getText().toString().trim();
        String jala = binding.textJala.getText().toString().trim();
        String reed = binding.editReed.getText().toString().trim();
        String wrap_color = binding.textWrapColor.getText().toString().trim();
        String wrap_thread = binding.editWrapThread.getText().toString().trim();
        boolean machineStatus = binding.switchMachine.isChecked();

        if (TextUtils.isEmpty(machine_name)) {
            new CustomSnackUtil().showSnack(getActivity(), "Please enter a Machine Name", R.drawable.error_msg_icon);
        } else if (TextUtils.isEmpty(jala)) {
            new CustomSnackUtil().showSnack(getActivity(), "Please enter a Jala", R.drawable.error_msg_icon);
        } else if (TextUtils.isEmpty(reed)) {
            new CustomSnackUtil().showSnack(getActivity(), "Please enter a Reed", R.drawable.error_msg_icon);
        } else if (TextUtils.isEmpty(wrap_color)) {
            new CustomSnackUtil().showSnack(getActivity(), "Please enter a Wrap Color", R.drawable.error_msg_icon);
        } else if (TextUtils.isEmpty(wrap_thread)) {
            new CustomSnackUtil().showSnack(getActivity(), "Please enter a Wrap Thread", R.drawable.error_msg_icon);
        } else {

            String id = dao.getId();
            DialogUtil.showProgressDialog(getActivity());

            machineMasterModel.setId(id);
            machineMasterModel.setMachine_name(machine_name);
            machineMasterModel.setJala_name(jala);
            machineMasterModel.setReed(reed);
            machineMasterModel.setWrap_color(wrap_color);
            machineMasterModel.setWrap_thread(wrap_thread);
            machineMasterModel.setStatus(machineStatus);

            dao.insert(id, machineMasterModel).addOnSuccessListener(runnable -> {
                dismiss();
                DialogUtil.showSuccessDialog(getActivity(), false);
            });
        }

    }

    private void setAutoAdapter() {
        getJalaList();
        getWrapColorList();
    }

    private void getJalaList() {
        new DAOJalaMaster(getActivity()).getReference().addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                Log.w("TAG", "Listen failed.", e);
                return;
            }

            jala_list.clear();
            for (QueryDocumentSnapshot document : snapshot) {
                JalaMasterModel jala = document.toObject(JalaMasterModel.class);
                jala_list.add(jala.getJala_name());
            }

            if (jalaListDialog != null) {
                jalaListDialog.update_list(jala_list);
            } else {
                jalaListDialog = new ListShowDialog(getActivity(), jala_list, MachineMasterDialog.this, JALA_REQUEST);
            }
        });

//        new DAOJalaMaster(getActivity()).getReference().addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                jala_list.clear();
//                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
//                    JalaMasterModel jala = postSnapshot.getValue(JalaMasterModel.class);
//                    jala_list.add(jala.getJala_name());
//                }
//                if (jalaListDialog != null) {
//                    jalaListDialog.update_list(jala_list);
//                } else {
//                    jalaListDialog = new ListShowDialog(getActivity(), jala_list, MachineMasterDialog.this, JALA_REQUEST);
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
    }

    private void getWrapColorList() {
        new DaoYarnMaster(getActivity()).getReference().whereEqualTo("yarn_type", "WARP").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    // Handle error
                    return;
                }

                warpColorList.clear();
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    YarnMasterModel yarnMasterModel = documentSnapshot.toObject(YarnMasterModel.class);
                    warpColorList.add(yarnMasterModel.getYarn_name());
                }

                if (warpListDialog != null) {
                    warpListDialog.update_list(warpColorList);
                } else {
                    warpListDialog = new ListShowDialog(getActivity(), warpColorList, MachineMasterDialog.this, WARP_REQUEST);
                }
            }
        });

//        new DaoYarnMaster(getActivity()).getReference().orderByChild("yarn_type").equalTo("WARP").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//                warpColorList.clear();
//                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
//                    YarnMasterModel yarnMasterModel = postSnapshot.getValue(YarnMasterModel.class);
//                    warpColorList.add(yarnMasterModel.getYarn_name());
//                }
//                if (warpListDialog != null) {
//                    warpListDialog.update_list(warpColorList);
//                } else {
//                    warpListDialog = new ListShowDialog(getActivity(), warpColorList, MachineMasterDialog.this, WARP_REQUEST);
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
    }


    @Override
    public void onSelected(int position, String name, int request_code) {
        if (request_code == JALA_REQUEST) {
            binding.textJala.setText(name);
        } else if (request_code == WARP_REQUEST) {
            binding.textWrapColor.setText(name);
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

        } else if (request_code == WARP_REQUEST) {


                if (isInsertPermissionGranted(PermissionState.DESIGN_MASTER.getValue())) {
                    YarnMasterDialog yarnMasterDialog = new YarnMasterDialog();
                    Bundle bundle = new Bundle();
                    bundle.putString("yarn_name", name);
                    bundle.putInt("yarn_type", 1);
                    yarnMasterDialog.setArguments(bundle);
                    yarnMasterDialog.show(getChildFragmentManager(), "Yarn Dialog");
                } else {
                    DialogUtil.showAccessDeniedDialog(getActivity());
                }


//            YarnMasterDialog yarnMasterDialog = new YarnMasterDialog();
//            yarnMasterDialog.showDialog(getActivity(), binding.textWrapColor.getText().toString().trim(), 1);
        }
    }

    @Override
    public void onMultiSelected(ArrayList<String> shadeList, int request_code) {

    }
}
