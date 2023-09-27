package com.example.texttile.data.dao;


import android.app.Activity;

import androidx.lifecycle.MutableLiveData;

import com.example.texttile.data.model.UserDataModel;
import com.example.texttile.presentation.ui.util.Const;
import com.example.texttile.presentation.ui.util.DialogUtil;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class DaoAuthority {

    MutableLiveData<ArrayList<UserDataModel>> userMasterLiveData = new MutableLiveData<>();
    ArrayList<UserDataModel> userMasterList = new ArrayList<>();

    FirebaseFirestore db;
    CollectionReference collectionReference;
    Activity activity;

    public DaoAuthority(Activity activity) {
        db = FirebaseFirestore.getInstance();
        collectionReference = db.collection(Const.USER_DATA);
        this.activity = activity;
    }

    public Task<Void> insert(String id, UserDataModel userDataModel) {
        return collectionReference.document(id).set(userDataModel).addOnFailureListener(runnable -> {
            DialogUtil.showErrorDialog(activity);
        });
    }

    public Task<Void> update(String id, HashMap<String, Object> hashMap) {

        return collectionReference.document(id).update(hashMap).addOnFailureListener(runnable -> {

            DialogUtil.showErrorDialog(activity);
        });
    }

    public Task<Void> delete(String id) {
        return collectionReference.document(id).delete().addOnFailureListener(runnable -> {
            DialogUtil.showErrorDialog(activity);
        });
    }

    public void removeSelectedItem(ArrayList<Integer> selected_item) {
        for (int i = selected_item.size() - 1; i >= 0; i--) {
            try {
                userMasterList.remove(i);
            } catch (Exception e) {

            }
            collectionReference.document(userMasterList.get(selected_item.get(i)).getId()).delete();
        }
    }

    public CollectionReference getReference() {
        return collectionReference;
    }

    public String getId() {
        return collectionReference.document().getId();
    }

    public MutableLiveData<ArrayList<UserDataModel>> getUserDataListLiveData() {
        if (userMasterLiveData == null) {
            userMasterLiveData = new MutableLiveData<>();
            getUserModelList("");
        }
        return userMasterLiveData;
    }

    public void getUserModelList(String filter) {
        collectionReference.orderBy("f_name").startAt(filter).endAt(filter + "\uf8ff").addSnapshotListener((value, error) -> {
            if (error != null) {
                return;
            }
            userMasterList.clear();
            for (DocumentSnapshot document : value.getDocuments()) {
                UserDataModel designMasterModel = document.toObject(UserDataModel.class);
                if (designMasterModel != null) {
                    userMasterList.add(designMasterModel);
                }
            }
            Collections.reverse(userMasterList);
            userMasterLiveData.setValue(userMasterList);
        });
    }
}
