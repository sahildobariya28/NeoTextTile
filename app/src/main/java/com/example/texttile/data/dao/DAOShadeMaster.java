package com.example.texttile.data.dao;

import android.app.Activity;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.texttile.R;
import com.example.texttile.data.model.ShadeMasterModel;
import com.example.texttile.presentation.ui.lib.snackUtil.CustomSnackUtil;
import com.example.texttile.presentation.ui.util.Const;
import com.example.texttile.presentation.ui.util.DialogUtil;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class DAOShadeMaster {
    FirebaseFirestore db;
    CollectionReference shadeMasterRef;
    Activity activity;

    MutableLiveData<ArrayList<ShadeMasterModel>> shadeMasterLiveData = new MutableLiveData<>();
    MutableLiveData<ArrayList<ShadeMasterModel>> groupShadeMasterLiveData = new MutableLiveData<>();
    MutableLiveData<ArrayList<ShadeMasterModel>> subGroupShadeMasterLiveData = new MutableLiveData<>();
    MutableLiveData<ArrayList<String>> shadeNoLiveData = new MutableLiveData<>();

    ArrayList<ShadeMasterModel> shadeMasterList = new ArrayList<>();
    ArrayList<ShadeMasterModel> groupShadeMasterList = new ArrayList<>();
    ArrayList<ShadeMasterModel> subGroupShadeMasterList = new ArrayList<>();
    ArrayList<String> shadeNoList = new ArrayList<>();


    public DAOShadeMaster(Activity activity) {
        db = FirebaseFirestore.getInstance();
        shadeMasterRef = db.collection(Const.SHADE_MASTER);
        this.activity = activity;
    }

    public Task<Void> insert(String id, ShadeMasterModel shadeMasterModel) {
        return shadeMasterRef.document(id).set(shadeMasterModel)
                .addOnFailureListener(e -> DialogUtil.showErrorDialog(activity));
    }

    public Task<Void> update(String id, Map<String, Object> updates) {
        return shadeMasterRef.document(id).update(updates)
                .addOnFailureListener(e -> DialogUtil.showErrorDialog(activity));
    }

    public Task<Void> delete(String id) {
        return shadeMasterRef.document(id).delete()
                .addOnFailureListener(e -> DialogUtil.showErrorDialog(activity));
    }

    public CollectionReference getReference() {
        return shadeMasterRef;
    }

    public String getId() {
        return shadeMasterRef.document().getId();
    }

    public MutableLiveData<ArrayList<ShadeMasterModel>> getShadeMasterListLiveData() {
        if (shadeMasterLiveData == null) {
            shadeMasterLiveData = new MutableLiveData<>();
            getShadeMasterList("");
        }
        return shadeMasterLiveData;
    }

    public MutableLiveData<ArrayList<ShadeMasterModel>> groupShadeMasterListLiveData() {
        if (groupShadeMasterLiveData == null) {
            groupShadeMasterLiveData = new MutableLiveData<>();
            getGroupShadeMaster("");
        }
        return groupShadeMasterLiveData;
    }

    public MutableLiveData<ArrayList<ShadeMasterModel>> subGroupShadeMasterListLiveData(String tracker) {
        if (subGroupShadeMasterLiveData == null) {
            subGroupShadeMasterLiveData = new MutableLiveData<>();
            getSubGroupShadeMaster("", tracker);
        }
        return subGroupShadeMasterLiveData;
    }

    public MutableLiveData<ArrayList<String>> getShadeNoListLiveData() {
        if (shadeNoLiveData == null) {
            shadeNoLiveData = new MutableLiveData<>();
            getShadeMasterList("");
        }
        return shadeNoLiveData;
    }

    public void removeSelectedItem(ArrayList<Integer> selectedItems) {

        int batchSize = 500;
        int batchCount = 0;
        WriteBatch batch = db.batch();
        DialogUtil.showProgressDialog(activity);
        for (int i = selectedItems.size() - 1; i >= 0; i--) {
            DocumentReference documentReference = shadeMasterRef.document(shadeMasterList.get(selectedItems.get(i)).getId());
            batch.delete(documentReference);
            if (++batchCount == batchSize) {
                batch.commit();
                batch = FirebaseFirestore.getInstance().batch();
                batchCount = 0;
            }
        }
        if (batchCount > 0) {
            batch.commit().addOnSuccessListener(aVoid -> {
                DialogUtil.hideProgressDialog();
                DialogUtil.showDeleteDialog(activity);
            }).addOnFailureListener(e -> {
                DialogUtil.hideProgressDialog();
                Log.e("dfsdffsd", "removeSelectedItem: " + e.getMessage());
                new CustomSnackUtil().showSnack(activity, "Something went wrong", R.drawable.error_msg_icon);
            });
        } else {
            DialogUtil.hideProgressDialog();
            DialogUtil.showDeleteDialog(activity);
        }
    }

    public void getGroupShadeMaster(String filter) {
        shadeMasterRef.addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                Log.w("TAG", "Listen failed.", e);
                return;
            }
            groupShadeMasterList.clear();
            if (!snapshot.isEmpty()) {
                for (QueryDocumentSnapshot doc : snapshot) {
                    ShadeMasterModel orderMasterModel = doc.toObject(ShadeMasterModel.class);

                    if (groupShadeMasterList.size() == 0) {
                        if (orderMasterModel != null && orderMasterModel.getDesign_no().toLowerCase().contains(filter.toLowerCase())) {
                            groupShadeMasterList.add(orderMasterModel);
                        }
                    } else {
                        boolean bool = true;
                        for (int j = 0; j < groupShadeMasterList.size(); j++) {
                            if (orderMasterModel.getDesign_no() != null && groupShadeMasterList != null) {
                                if (orderMasterModel.getDesign_no().equals(groupShadeMasterList.get(j).getDesign_no())) {
                                    bool = false;
                                    break;
                                }
                            }
                        }
                        if (bool) {
                            if (orderMasterModel != null && orderMasterModel.getDesign_no().toLowerCase().contains(filter.toLowerCase())) {
                                groupShadeMasterList.add(orderMasterModel);
                            }
                        }
                    }
                }
            }
            groupShadeMasterLiveData.setValue(groupShadeMasterList);
            // Handle snapshot data
        });
    }

    public void getSubGroupShadeMaster(String filter, String designNo) {
        shadeMasterRef.addSnapshotListener((snapshot, e) -> {
            subGroupShadeMasterList.clear();
            shadeNoList.clear();
            if (snapshot != null && !snapshot.isEmpty()) {
                for (DocumentSnapshot docSnapshot : snapshot.getDocuments()) {
                    ShadeMasterModel shadeMasterModel = docSnapshot.toObject(ShadeMasterModel.class);
                    if (shadeMasterModel != null && shadeMasterModel.getShade_no().toLowerCase().contains(filter.toLowerCase())) {
                        if (shadeMasterModel.getDesign_no().equals(designNo)){
                            subGroupShadeMasterList.add(shadeMasterModel);
                            shadeNoList.add(shadeMasterModel.getShade_no());
                        }
                    }
                }
                Collections.reverse(subGroupShadeMasterList);
            }
            subGroupShadeMasterLiveData.setValue(subGroupShadeMasterList);
        });
    }

    public void getShadeMasterList(String filter) {
        shadeMasterRef.addSnapshotListener((value, error) -> {
            shadeMasterList.clear();
            shadeNoList.clear();
            if (value != null && !value.isEmpty()) {
                for (DocumentSnapshot docSnapshot : value.getDocuments()) {
                    ShadeMasterModel shadeMasterModel = docSnapshot.toObject(ShadeMasterModel.class);
                    if (shadeMasterModel != null && shadeMasterModel.getShade_no().toLowerCase().contains(filter.toLowerCase())) {
                        shadeMasterList.add(shadeMasterModel);
                        shadeNoList.add(shadeMasterModel.getShade_no());
                    }
                }
                Collections.reverse(shadeMasterList);
            }
            shadeMasterLiveData.setValue(shadeMasterList);
        });
    }
}
