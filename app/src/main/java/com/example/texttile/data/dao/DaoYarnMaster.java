package com.example.texttile.data.dao;

import android.app.Activity;

import androidx.lifecycle.MutableLiveData;

import com.example.texttile.R;
import com.example.texttile.data.model.YarnMasterModel;
import com.example.texttile.presentation.ui.lib.snackUtil.CustomSnackUtil;
import com.example.texttile.presentation.ui.util.Const;
import com.example.texttile.presentation.ui.util.DialogUtil;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.HashMap;

import javax.annotation.Nullable;

public class DaoYarnMaster {

    private MutableLiveData<ArrayList<YarnMasterModel>> yarnMasterLiveData = new MutableLiveData<>();
    private ArrayList<YarnMasterModel> yarnMasterList = new ArrayList<>();

    private final FirebaseFirestore firestore =  FirebaseFirestore.getInstance();;
    private CollectionReference collectionReference;
    private Activity activity;

    public DaoYarnMaster(Activity activity) {
        collectionReference = firestore.collection(Const.YARN_MASTER);
        this.activity = activity;
    }

    public Task<Void> insert(String id, YarnMasterModel yarnMasterModel) {
        return collectionReference.document(id).set(yarnMasterModel).addOnFailureListener(e -> {
            DialogUtil.showErrorDialog(activity);
        });
    }

    public Task<Void> update(String id, HashMap<String, Object> hashMap) {
        return collectionReference.document(id).update(hashMap).addOnFailureListener(e -> {
            DialogUtil.showErrorDialog(activity);
        });
    }

    public Task<Void> delete(String id) {
        return collectionReference.document(id).delete().addOnFailureListener(e -> {
            DialogUtil.showErrorDialog(activity);
        });
    }

    public CollectionReference getReference(){
        return collectionReference;
    }

    public String getId() {
        return collectionReference.document().getId();
    }

    public MutableLiveData<ArrayList<YarnMasterModel>> getYarnMasterListLiveData() {
        if (yarnMasterLiveData == null) {
            yarnMasterLiveData = new MutableLiveData<>();
            getYarnMasterList("");
        }
        return yarnMasterLiveData;
    }

    public void removeSelectedItem(ArrayList<Integer> selected_item) {
        int batchSize = 500;
        int batchCount = 0;
        WriteBatch batch = firestore.batch();
        DialogUtil.showProgressDialog(activity);
        for (int i = 0; i < selected_item.size(); i++) {
            DocumentReference documentReference = collectionReference.document(yarnMasterLiveData.getValue().get(selected_item.get(i)).getId());
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
                new CustomSnackUtil().showSnack(activity, "Something Want Wrong", R.drawable.error_msg_icon);
            });
        }else {
            DialogUtil.hideProgressDialog();
            DialogUtil.showDeleteDialog(activity);
        }
    }

    public void getYarnMasterList(String filter) {
        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                yarnMasterList.clear();
                if (e != null) {
                    return;
                }

                for (QueryDocumentSnapshot document : snapshot) {
                    YarnMasterModel yarnMasterModel = document.toObject(YarnMasterModel.class);
                    if (yarnMasterModel != null && yarnMasterModel.getYarn_name().toLowerCase().contains(filter.toLowerCase())) {
                        yarnMasterList.add(yarnMasterModel);
                    }
                }
                yarnMasterLiveData.setValue(yarnMasterList);
            }
        });
    }

}
