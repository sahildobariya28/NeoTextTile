package com.example.texttile.data.dao;

import android.app.Activity;

import androidx.lifecycle.MutableLiveData;

import com.example.texttile.R;
import com.example.texttile.data.model.AllotProgramModel;
import com.example.texttile.presentation.ui.lib.snackUtil.CustomSnackUtil;
import com.example.texttile.presentation.ui.util.Const;
import com.example.texttile.presentation.ui.util.DialogUtil;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Map;

import javax.annotation.Nullable;

public class DaoAllotProgram {

    MutableLiveData<ArrayList<AllotProgramModel>> allotProgramLiveData = new MutableLiveData<>();
    ArrayList<AllotProgramModel> allotProgramList = new ArrayList<>();

    FirebaseFirestore firestore;
    CollectionReference collectionReference;
    Activity activity;

    public DaoAllotProgram(Activity activity) {
        firestore = FirebaseFirestore.getInstance();
        collectionReference = firestore.collection(Const.ALLOT_PROGRAM);
        this.activity = activity;
    }

    public Task<Void> insert(String id, AllotProgramModel allotProgramModel) {
        return collectionReference.document(id).set(allotProgramModel)
                .addOnFailureListener(e -> {
                    DialogUtil.showErrorDialog(activity);
                });
    }

    public Task<Void> update(String id, Map<String, Object> map) {
        return collectionReference.document(id).update(map)
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

    public void read(String filter) {
        Query query = collectionReference.orderBy("order_no", Query.Direction.DESCENDING);
        if (filter != null && !filter.isEmpty()) {
            query = query.whereEqualTo("order_no", filter);
        }
        query.addSnapshotListener((value, error) -> {
            allotProgramList.clear();
            if (error == null && value != null && !value.isEmpty()) {
                for (QueryDocumentSnapshot doc : value) {
                    AllotProgramModel allotProgramModel = doc.toObject(AllotProgramModel.class);
                    allotProgramModel.setId(doc.getId());
                    allotProgramList.add(allotProgramModel);
                }
            } else {
                new CustomSnackUtil().showSnack(activity, "Allot Program List is Empty", R.drawable.error_msg_icon);
            }
            allotProgramLiveData.setValue(allotProgramList);
        });
    }

    public CollectionReference getCollectionReference() {
        return collectionReference;
    }

    public String getId() {
        return collectionReference.document().getId();
    }

    public MutableLiveData<ArrayList<AllotProgramModel>> getAllotProgramListLiveData() {
        if (allotProgramLiveData == null) {
            allotProgramLiveData = new MutableLiveData<>();
            read("");
        }
        return allotProgramLiveData;
    }

    public void removeSelectedItems(ArrayList<Integer> selectedItems) {
        DialogUtil.showProgressDialog(activity);
        int batchSize = 500;
        int batchCount = 0;
        WriteBatch batch = firestore.batch();
        for (int i = selectedItems.size() - 1; i >= 0; i--) {
            DocumentReference documentReference = collectionReference.document(allotProgramList.get(selectedItems.get(i)).getId());
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

    public void getAllotProgramList(String filter) {
        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                allotProgramList.clear();
                if (e != null) {
                    return;
                }

                for (QueryDocumentSnapshot document : snapshot) {
                    AllotProgramModel allotProgramModel = document.toObject(AllotProgramModel.class);
                    if (allotProgramModel != null && allotProgramModel.getDesign_no().toLowerCase().contains(filter.toLowerCase())) {
                        allotProgramList.add(allotProgramModel);
                    }
                }
                allotProgramLiveData.setValue(allotProgramList);
            }
        });
    }

}
