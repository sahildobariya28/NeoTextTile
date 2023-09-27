package com.example.texttile.presentation.screen.master.shade_master.shade_master;

import static com.example.texttile.presentation.ui.util.PermissionUtil.isInsertPermissionGranted;

import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.texttile.presentation.ui.dialog.YarnMasterDialog;
import com.example.texttile.data.dao.DAODesignMaster;
import com.example.texttile.data.dao.DAOShadeMaster;
import com.example.texttile.data.dao.DaoYarnMaster;
import com.example.texttile.presentation.ui.interfaces.AddOnSearchedItemSelectListener;
import com.example.texttile.presentation.screen.master.design_master.dialog.DesignMasterDialogFragment;
import com.example.texttile.data.model.DesignMasterModel;
import com.example.texttile.data.model.ShadeMasterModel;
import com.example.texttile.data.model.YarnMasterModel;
import com.example.texttile.presentation.ui.util.DialogUtil;
import com.example.texttile.presentation.ui.util.ListShowDialog;
import com.example.texttile.presentation.ui.util.PermissionState;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class ShadeMultipleViewModel extends ViewModel implements AddOnSearchedItemSelectListener {

    DAOShadeMaster dao;

    MutableLiveData<ArrayList<String>> shade_no_list = new MutableLiveData<>(new ArrayList<>());
    MutableLiveData<ArrayList<ShadeMasterModel>> shade_master_list = new MutableLiveData<>(new ArrayList<>());
    MutableLiveData<ArrayList<String>> design_no_list = new MutableLiveData<>(new ArrayList<>());
    MutableLiveData<ArrayList<DesignMasterModel>> designMasterModelsList = new MutableLiveData<>(new ArrayList<>());
    MutableLiveData<ArrayList<String>> warpColorList = new MutableLiveData<>(new ArrayList<>());
    MutableLiveData<ArrayList<String>> weftColorList = new MutableLiveData<>(new ArrayList<>());
    MutableLiveData<ArrayList<String>> list = new MutableLiveData<>(new ArrayList<>());

    MutableLiveData<String> autoDesignNo = new MutableLiveData<>("");
    MutableLiveData<String> editWrapColor = new MutableLiveData<>("");
    MutableLiveData<String> textCopyToDesign = new MutableLiveData<>("");
    MutableLiveData<String> textCopyFromDesign = new MutableLiveData<>("");

    public int SINGLE_ITEM = 1, MULTIPLE_ITEM = 2;
    MutableLiveData<Integer> addItemState = new MutableLiveData<>(SINGLE_ITEM);

    MutableLiveData<Integer> requestInsertDataCount = new MutableLiveData<>(0);
    MutableLiveData<Integer> responseInsertDataCount = new MutableLiveData<>(0);

    FAdapter fAdapter;
    int adapterSize = 0;

    Activity activity;
    String tracker;
    ShadeMasterModel shadeMasterModel;

    ListShowDialog designListDialog, warpListDialog, f1_dialog, f2_dialog, f3_dialog, f4_dialog, f5_dialog, f6_dialog, f7_dialog, f8_dialog, designCopyToDialog, designCopyFromDialog;
    final int DESIGN_REQUEST = 1, WARP_REQUEST = 2, DESIGN_REQUEST_COPY_TO = 3, DESIGN_REQUEST_COPY_FROM = 4;
    FragmentManager fragmentManager;

    public ShadeMultipleViewModel(Activity activity, FragmentManager fragmentManager) {
        this.activity = activity;
        this.fragmentManager = fragmentManager;
        this.tracker = (activity.getIntent().getStringExtra("tracker") != null) ? activity.getIntent().getStringExtra("tracker") : "add";
        this.shadeMasterModel = (tracker.equals("edit") && activity.getIntent().getSerializableExtra("shade_data") != null) ? (ShadeMasterModel) activity.getIntent().getSerializableExtra("shade_data") : new ShadeMasterModel();

        dao = new DAOShadeMaster(activity);

        fetchData();
        getSelectDialogRef();
    }

    private void fetchData() {
        getShadeList();
        getDesignNoList();
        getYarnMaster();
    }

    public void getShadeList() {
        new DAODesignMaster(activity).getReference().addSnapshotListener((value, error) -> {
            design_no_list.getValue().clear();
            designMasterModelsList.getValue().clear();
            if (value != null && !value.isEmpty()) {
                for (QueryDocumentSnapshot doc : value) {
                    DesignMasterModel designMasterModel = doc.toObject(DesignMasterModel.class);

                    if (designMasterModel != null && designMasterModel.getDesign_no() != null) {
                        design_no_list.getValue().add(designMasterModel.getDesign_no());
                        designMasterModelsList.getValue().add(designMasterModel);
                        fAdapter.notifyDataSetChanged();
                    }
                }

                if (designListDialog != null) {
                    designListDialog.update_list(design_no_list.getValue());
                } else {
                    designListDialog = new ListShowDialog(activity, design_no_list.getValue(), this, DESIGN_REQUEST);
                }
            }
        });
    }

    private void getDesignNoList() {
        new DAODesignMaster(activity).getReference().addSnapshotListener((value, error) -> {
            design_no_list.getValue().clear();
            designMasterModelsList.getValue().clear();
            for (QueryDocumentSnapshot doc : value) {
                DesignMasterModel designMasterModel = doc.toObject(DesignMasterModel.class);

                if (designMasterModel != null && designMasterModel.getDesign_no() != null) {
                    design_no_list.getValue().add(designMasterModel.getDesign_no());
                    designMasterModelsList.getValue().add(designMasterModel);
                    fAdapter.notifyDataSetChanged();
                }
            }

            if (designListDialog != null) {
                designListDialog.update_list(design_no_list.getValue());
            } else {
                designListDialog = new ListShowDialog(activity, design_no_list.getValue(), this, DESIGN_REQUEST);
            }
        });
    }

    private void getYarnMaster() {
        new DaoYarnMaster(activity).getReference().addSnapshotListener((querySnapshot, e) -> {
            warpColorList.getValue().clear();
            weftColorList.getValue().clear();
            for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                YarnMasterModel yarnMasterModel = documentSnapshot.toObject(YarnMasterModel.class);
                if (yarnMasterModel != null) {
                    if (yarnMasterModel.getYarn_type().equals("WARP")) {
                        warpColorList.getValue().add(yarnMasterModel.getYarn_name());
                    } else {
                        weftColorList.getValue().add(yarnMasterModel.getYarn_name());
                    }
                }
            }
            if (warpListDialog != null) {
                warpListDialog.update_list(warpColorList.getValue());
            } else {
                warpListDialog = new ListShowDialog(activity, warpColorList.getValue(), this, WARP_REQUEST);
            }
        });
    }


    public AddOnSearchedItemSelectListener getSelectDialogRef(){
        return this;
    }


    //dialog listener
    @Override
    public void onSelected(int position, String name, int request_code) {
        if (request_code == DESIGN_REQUEST) {

            autoDesignNo.setValue(name);
            adapterSize = designMasterModelsList.getValue().get(position).getF_list().size();
            list.getValue().clear();

            if (tracker.equals("edit")) {
                for (int i = 0; i < adapterSize; i++) {
                    if (i < shadeMasterModel.getF_list().size()) {
                        list.getValue().add(shadeMasterModel.getF_list().get(i));
                    } else {
                        list.getValue().add(null);
                    }
                }
            } else {
                for (int i = 0; i < adapterSize; i++) {
                    list.getValue().add(null);
                }
            }

            fAdapter.notifyDataSetChanged();
        } else if (request_code == WARP_REQUEST) {
            editWrapColor.setValue(name);
        } else if (request_code == DESIGN_REQUEST_COPY_TO) {
            textCopyToDesign.setValue(name);
            for (int i = 0; i < shade_master_list.getValue().size(); i++) {
                if (shade_master_list.getValue().get(i).getShade_no().equals(name)){
                    for (int j = 0; j < shade_master_list.getValue().get(i).getF_list().size(); j++) {
                        if (j < list.getValue().size()) {
                            list.getValue().set(j, shade_master_list.getValue().get(i).getF_list().get(j));
                            fAdapter.notifyItemChanged(j);
                        }
                    }
                }
            }
        }else if (request_code == DESIGN_REQUEST_COPY_FROM) {
            textCopyFromDesign.setValue(name);

        } else if (request_code == 100) {
            list.getValue().set(0, name);
            fAdapter.notifyDataSetChanged();
        } else if (request_code == 101) {
            list.getValue().set(1, name);
            fAdapter.notifyItemChanged(1);
        } else if (request_code == 102) {
            list.getValue().set(2, name);
            fAdapter.notifyItemChanged(2);
        } else if (request_code == 103) {
            list.getValue().set(3, name);
            fAdapter.notifyItemChanged(3);
        } else if (request_code == 104) {
            list.getValue().set(4, name);
            fAdapter.notifyItemChanged(4);
        } else if (request_code == 105) {
            list.getValue().set(5, name);
            fAdapter.notifyItemChanged(5);
        } else if (request_code == 106) {
            list.getValue().set(6, name);
            fAdapter.notifyItemChanged(6);
        } else if (request_code == 107) {
            list.getValue().set(7, name);
            fAdapter.notifyItemChanged(7);
        }

    }

    @Override
    public void onAddButtonClicked(int position, String name, int request_code) {
        if (request_code == DESIGN_REQUEST) {

            if (isInsertPermissionGranted(PermissionState.DESIGN_MASTER.getValue())) {
                DesignMasterDialogFragment dialogFragment = new DesignMasterDialogFragment();
                Bundle bundle = new Bundle();
                bundle.putString("design_no", name);
                dialogFragment.setArguments(bundle);
                dialogFragment.show(fragmentManager, "My Fragment");
            } else {
                DialogUtil.showAccessDeniedDialog(activity);
            }
        } else if (request_code == WARP_REQUEST) {
            YarnMasterDialog yarnMasterDialog = new YarnMasterDialog();
            Bundle bundle = new Bundle();
            bundle.putString("yarn_name", name);
            bundle.putInt("yarn_type", 1);
            yarnMasterDialog.setArguments(bundle);
            yarnMasterDialog.show(fragmentManager, "Yarn Dialog");
        } else if (request_code == 100 || request_code == 101 || request_code == 102 || request_code == 103 || request_code == 104 || request_code == 105 || request_code == 106 || request_code == 107) {
            YarnMasterDialog yarnMasterDialog = new YarnMasterDialog();
            Bundle bundle = new Bundle();
            bundle.putString("yarn_name", name);
            bundle.putInt("yarn_type", 2);
            yarnMasterDialog.setArguments(bundle);
            yarnMasterDialog.show(fragmentManager, "Yarn Dialog");
        }
    }

    @Override
    public void onMultiSelected(ArrayList<String> shadeList, int request_code) {

    }
}
