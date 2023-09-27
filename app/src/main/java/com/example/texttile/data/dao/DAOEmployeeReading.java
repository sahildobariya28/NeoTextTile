package com.example.texttile.data.dao;

import android.app.Activity;

import androidx.lifecycle.MutableLiveData;

import com.example.texttile.R;
import com.example.texttile.data.model.EmployeeReadingModel;
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

public class DAOEmployeeReading {

    MutableLiveData<ArrayList<EmployeeReadingModel>> employeeReadingLiveData = new MutableLiveData<>();
    ArrayList<EmployeeReadingModel> employeeReadingList = new ArrayList<>();

    FirebaseFirestore db;
    CollectionReference collectionReference;
    Activity activity;

    public DAOEmployeeReading(Activity activity) {
        db = FirebaseFirestore.getInstance();
        collectionReference = db.collection(Const.EMPLOYEE_READING);
        this.activity = activity;
    }

    public Task<Void> insert(String id, EmployeeReadingModel employeeReadingModel) {
        return collectionReference.document(id).set(employeeReadingModel)
                .addOnFailureListener(e -> DialogUtil.showErrorDialog(activity));
    }

    public Task<Void> update(String id, HashMap<String, Object> hashMap) {
        return collectionReference.document(id).update(hashMap)
                .addOnFailureListener(e -> DialogUtil.showErrorDialog(activity));
    }

    public Task<Void> delete(String id) {
        return collectionReference.document(id).delete()
                .addOnFailureListener(e -> DialogUtil.showErrorDialog(activity));
    }

    public CollectionReference getReference() {
        return collectionReference;
    }

    public String getNewId() {
        return collectionReference.document().getId();
    }


    public MutableLiveData<ArrayList<EmployeeReadingModel>> getEmployeeReadingListLiveData() {
        if (employeeReadingLiveData == null) {
            employeeReadingLiveData = new MutableLiveData<>();
            getEmployeeReadingList("");
        }
        return employeeReadingLiveData;
    }

    public void removeSelectedItem(ArrayList<Integer> selectedItems) {
        int batchSize = 500;
        int batchCount = 0;
        DialogUtil.showProgressDialog(activity);
        WriteBatch batch = db.batch();
        for (int i = selectedItems.size() - 1; i >= 0; i--) {

            DocumentReference documentReference = collectionReference.document(employeeReadingList.get(selectedItems.get(i)).getId());
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


    public void getEmployeeReadingList(String filter) {
        collectionReference.addSnapshotListener((queryDocumentSnapshots, error) -> {
            employeeReadingList.clear();
            if (error == null && queryDocumentSnapshots != null) {
                for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                    EmployeeReadingModel employeeReadingModel = snapshot.toObject(EmployeeReadingModel.class);
                    if (employeeReadingModel != null && employeeReadingModel.getEmp_name().toLowerCase().contains(filter.toLowerCase())) {
                        employeeReadingList.add(employeeReadingModel);
                    }
                }
                Collections.reverse(employeeReadingList);
                employeeReadingLiveData.setValue(employeeReadingList);
            } else {
                // handle error
            }
        });
    }
}
