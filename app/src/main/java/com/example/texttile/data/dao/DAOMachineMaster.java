package com.example.texttile.data.dao;

import android.app.Activity;

import androidx.lifecycle.MutableLiveData;

import com.example.texttile.R;
import com.example.texttile.data.model.MachineMasterModel;
import com.example.texttile.presentation.ui.lib.snackUtil.CustomSnackUtil;
import com.example.texttile.presentation.ui.util.Const;
import com.example.texttile.presentation.ui.util.DialogUtil;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class DAOMachineMaster {

    MutableLiveData<ArrayList<MachineMasterModel>> machineMasterLiveData = new MutableLiveData<>();
    ArrayList<MachineMasterModel> machineMasterList = new ArrayList<>();

    FirebaseFirestore db;
    CollectionReference collectionReference;
    Activity activity;

    public DAOMachineMaster(Activity activity) {
        this.activity = activity;
        db = FirebaseFirestore.getInstance();
        collectionReference = db.collection(Const.MACHINE_MASTER);
    }

    public Task<Void> insert(String id, MachineMasterModel machineMasterModel) {
        return collectionReference.document(id).set(machineMasterModel)
                .addOnFailureListener(e -> {
                    DialogUtil.showErrorDialog(activity);
                });
    }

    public Task<Void> update(String id, HashMap<String, Object> hashMap) {
        return collectionReference.document(id).update(hashMap)
                .addOnFailureListener(e -> {
                    DialogUtil.showErrorDialog(activity);
                });
    }

    public Task<Void> delete(String id) {
        return collectionReference.document(id).delete()
                .addOnFailureListener(e -> {
                    DialogUtil.showErrorDialog(activity);
                });
    }

    public CollectionReference getReference() {
        return collectionReference;
    }

    public String getId() {
        return collectionReference.document().getId();
    }

    public MutableLiveData<ArrayList<MachineMasterModel>> getMachineMasterListLiveData() {
        if (machineMasterLiveData == null) {
            machineMasterLiveData = new MutableLiveData<>();
            getMachineMasterList("");
        }
        return machineMasterLiveData;
    }

    public void removeSelectedItem(ArrayList<Integer> selectedItems) {
        int batchSize = 500;
        int batchCount = 0;
        DialogUtil.showProgressDialog(activity);
        WriteBatch batch = db.batch();
        for (int i = selectedItems.size() - 1; i >= 0; i--) {
            DocumentReference documentReference = collectionReference.document(machineMasterList.get(selectedItems.get(i)).getId());
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
                new CustomSnackUtil().showSnack(activity, "Something went wrong", R.drawable.error_msg_icon);
            });
        }else {
            DialogUtil.hideProgressDialog();
            DialogUtil.showDeleteDialog(activity);
        }
    }

    public void getMachineMasterList(String filter) {
        collectionReference.addSnapshotListener((queryDocumentSnapshots, e) -> {
            machineMasterList.clear();
            if (e == null && queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    MachineMasterModel machineMasterModel = documentSnapshot.toObject(MachineMasterModel.class);
                    if (machineMasterModel != null && machineMasterModel.getMachine_name().toLowerCase().contains(filter.toLowerCase())) {
                        machineMasterList.add(machineMasterModel);
                    }
                }
                Collections.reverse(machineMasterList);
            }
            machineMasterLiveData.setValue(machineMasterList);
        });
    }

}
