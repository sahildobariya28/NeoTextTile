package com.example.texttile.presentation.screen.master.machine_master;

import static com.example.texttile.presentation.ui.util.PermissionUtil.isInsertPermissionGranted;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.texttile.R;
import com.example.texttile.databinding.ActivityMachineMasterBinding;
import com.example.texttile.presentation.ui.dialog.JalaDialog;
import com.example.texttile.presentation.ui.dialog.YarnMasterDialog;
import com.example.texttile.data.dao.DAOJalaMaster;
import com.example.texttile.data.dao.DAOMachineMaster;
import com.example.texttile.data.dao.DaoYarnMaster;
import com.example.texttile.presentation.ui.interfaces.AddOnSearchedItemSelectListener;
import com.example.texttile.data.model.JalaMasterModel;
import com.example.texttile.data.model.MachineMasterModel;
import com.example.texttile.data.model.YarnMasterModel;
import com.example.texttile.presentation.ui.lib.snackUtil.CustomSnackUtil;
import com.example.texttile.presentation.ui.util.DialogUtil;
import com.example.texttile.presentation.ui.util.ListShowDialog;
import com.example.texttile.presentation.ui.util.PermissionState;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

import javax.annotation.Nullable;

public class MachineMasterActivity extends AppCompatActivity implements AddOnSearchedItemSelectListener {

    ArrayList<String> warpColorList = new ArrayList<>();
    ArrayList<String> jala_list = new ArrayList<>();

    String tracker;
    MachineMasterModel machineMasterModel;
    DAOMachineMaster dao;

    ActivityMachineMasterBinding binding;
    ListShowDialog jalaListDialog, warpListDialog;
    final int JALA_REQUEST = 1, WARP_REQUEST = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMachineMasterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dao = new DAOMachineMaster(this);

        initToolbar();
        setAutoAdapter();

        tracker = (getIntent().getStringExtra("tracker") != null) ? getIntent().getStringExtra("tracker") : "add";
        machineMasterModel = (tracker.equals("edit") && getIntent().getSerializableExtra("machine_data") != null) ? (MachineMasterModel) getIntent().getSerializableExtra("machine_data") : new MachineMasterModel();

        if (tracker != null && tracker.equals("edit")) {
            if (machineMasterModel != null) {

                binding.btnAdd.setText("Update");

                binding.editMachineName.setText(String.valueOf(machineMasterModel.getMachine_name()));
                binding.textJala.setText(String.valueOf(machineMasterModel.getJala_name()));
                binding.editReed.setText(String.valueOf(machineMasterModel.getReed()));
                binding.textWrapColor.setText(String.valueOf(machineMasterModel.getWrap_color()));
                binding.editWrapThread.setText(String.valueOf(machineMasterModel.getWrap_thread()));
                binding.switchMachine.setChecked(machineMasterModel.isStatus());
            }
        } else {
            if (machineMasterModel != null) {
                binding.editMachineName.requestFocus();
            }
        }

        binding.btnAdd.setOnClickListener(view -> addArtist());

        binding.textJala.setOnClickListener(view -> {
            if (jalaListDialog != null) {
                jalaListDialog.show_dialog();
            } else {
                jalaListDialog = new ListShowDialog(this, jala_list, MachineMasterActivity.this, JALA_REQUEST);
                jalaListDialog.show_dialog();
            }
        });

        binding.textWrapColor.setOnClickListener(view -> {
            if (warpListDialog != null) {
                warpListDialog.show_dialog();
            } else {
                warpListDialog = new ListShowDialog(this, warpColorList, MachineMasterActivity.this, WARP_REQUEST);
                warpListDialog.show_dialog();
            }
        });

    }

    private void addArtist() {
        String machine_name = binding.editMachineName.getText().toString().trim();
        String jala = binding.textJala.getText().toString().trim();
        String reed = binding.editReed.getText().toString().trim();
        String wrap_color = binding.textWrapColor.getText().toString().trim();
        String wrap_thread = binding.editWrapThread.getText().toString().trim();
        boolean machineStatus = binding.switchMachine.isChecked();
        float labour_charge = binding.editLabourCharge.getText().toString().isEmpty() ? Float.parseFloat(binding.editLabourCharge.getText().toString()):0.0f;

        if (TextUtils.isEmpty(machine_name)) {
            new CustomSnackUtil().showSnack(this, "Please enter a Machine Name", R.drawable.icon_warning);
        } else if (TextUtils.isEmpty(jala)) {
            new CustomSnackUtil().showSnack(this, "Please enter a Jala", R.drawable.icon_warning);
        } else if (TextUtils.isEmpty(reed)) {
            new CustomSnackUtil().showSnack(this, "Please enter a Reed", R.drawable.icon_warning);
        } else if (TextUtils.isEmpty(wrap_color)) {
            new CustomSnackUtil().showSnack(this, "Please enter a Wrap Color", R.drawable.icon_warning);
        } else if (TextUtils.isEmpty(wrap_thread)) {
            new CustomSnackUtil().showSnack(this, "Please enter a Wrap Thread", R.drawable.icon_warning);
        } else if (labour_charge != 0.0f) {
            new CustomSnackUtil().showSnack(this, "Please enter a Labour Charge", R.drawable.icon_warning);
        } else {

            String id = dao.getId();
            DialogUtil.showProgressDialog(this);
            if (tracker.equals("edit")) {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("machine_name", machine_name);
                hashMap.put("jala_name", jala);
                hashMap.put("reed", reed);
                hashMap.put("wrap_color", wrap_color);
                hashMap.put("wrap_thread", wrap_thread);
                hashMap.put("status", machineStatus);
                hashMap.put("labour_charge", labour_charge);

                dao.update(machineMasterModel.getId(), hashMap).addOnSuccessListener(runnable -> {
                    DialogUtil.showUpdateSuccessDialog(this, true);
                });

            } else {

                machineMasterModel.setId(id);
                machineMasterModel.setMachine_name(machine_name);
                machineMasterModel.setJala_name(jala);
                machineMasterModel.setReed(reed);
                machineMasterModel.setWrap_color(wrap_color);
                machineMasterModel.setWrap_thread(wrap_thread);
                machineMasterModel.setStatus(machineStatus);

                dao.insert(id, machineMasterModel).addOnSuccessListener(runnable -> {
                    DialogUtil.showSuccessDialog(this, true);
                });
            }
        }

    }

    private void setAutoAdapter() {
        getJalaList();
        getWrapColorList();
    }

    private void getJalaList() {
        new DAOJalaMaster(this).getReference().addSnapshotListener((snapshot, e) -> {
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
                jalaListDialog = new ListShowDialog(this, jala_list, MachineMasterActivity.this, JALA_REQUEST);
            }
        });
    }

    private void getWrapColorList() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        new DaoYarnMaster(this).getReference().whereEqualTo("yarn_type", "WARP").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    // handle error
                    return;
                }

                warpColorList.clear();
                for (QueryDocumentSnapshot document : querySnapshot) {
                    YarnMasterModel yarnMasterModel = document.toObject(YarnMasterModel.class);
                    warpColorList.add(yarnMasterModel.getYarn_name());
                }
                if (warpListDialog != null) {
                    warpListDialog.update_list(warpColorList);
                } else {
                    warpListDialog = new ListShowDialog(MachineMasterActivity.this, warpColorList, MachineMasterActivity.this, WARP_REQUEST);
                }
            }
        });
    }


    public void initToolbar() {
        binding.include.textTitle.setText("Machine Master");
        binding.include.btnBack.setOnClickListener(view -> {
            onBackPressed();
        });
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
                jalaDialog.show(getSupportFragmentManager(), "My Fragment");
            } else {
                DialogUtil.showAccessDeniedDialog(this);
            }
        } else if (request_code == WARP_REQUEST) {

            if (isInsertPermissionGranted(PermissionState.DESIGN_MASTER.getValue())) {
                YarnMasterDialog yarnMasterDialog = new YarnMasterDialog();
                Bundle bundle = new Bundle();
                bundle.putString("yarn_name", binding.textWrapColor.getText().toString().trim());
                bundle.putInt("yarn_type", 1);
                yarnMasterDialog.setArguments(bundle);
                yarnMasterDialog.show(getSupportFragmentManager(), "Yarn Dialog");
            } else {
                DialogUtil.showAccessDeniedDialog(this);
            }
        }
    }

    @Override
    public void onMultiSelected(ArrayList<String> shadeList, int request_code) {

    }
}