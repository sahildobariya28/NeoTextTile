package com.example.texttile.data.dao;

import android.app.Activity;
import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.texttile.R;
import com.example.texttile.data.model.EmployeeMasterModel;
import com.example.texttile.presentation.ui.lib.snackUtil.CustomSnackUtil;
import com.example.texttile.presentation.ui.util.Const;
import com.example.texttile.presentation.ui.util.DialogUtil;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class DAOEmployeeMaster {

    MutableLiveData<ArrayList<EmployeeMasterModel>> employeeMasterLiveData = new MutableLiveData<>();
    ArrayList<EmployeeMasterModel> employeeMasterList = new ArrayList<>();

    FirebaseFirestore db;
    StorageReference storageReference;
    CollectionReference collectionReference;
    Activity activity;

    public DAOEmployeeMaster(Activity activity) {
        db = FirebaseFirestore.getInstance();
        collectionReference = db.collection(Const.EMPLOYEE_MASTER);
        storageReference = FirebaseStorage.getInstance().getReference().child("employee/");
        this.activity = activity;
    }

    public Task<Void> insert(String id, EmployeeMasterModel employeeMasterModel) {
        return collectionReference.document(id).set(employeeMasterModel)
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

    public Task<Void> setJalaFileList(String id, EmployeeMasterModel employeeMasterModel) {
        return collectionReference.document(id).set(employeeMasterModel)
                .addOnSuccessListener(aVoid -> DialogUtil.showSuccessDialog(activity, true))
                .addOnFailureListener(e -> DialogUtil.showErrorDialog(activity));
    }

    public CollectionReference getReference() {
        return collectionReference;
    }

    public String getNewId() {
        return collectionReference.document().getId();
    }

    public UploadTask uploadPhoto(String id, Uri mImageUri) {
        return storageReference.child("personal/").child(id).putFile(mImageUri);
    }

    public UploadTask uploadIdPhoto(String id, Uri mImageUri) {
        return storageReference.child("idProof/").child(id).putFile(mImageUri);
    }

    public Task<Uri> getDownloadUri(String id) {
        return storageReference.child("personal/").child(id).getDownloadUrl();
    }

    public Task<Uri> getIdDownloadUri(String id) {
        return storageReference.child("idProof/").child(id).getDownloadUrl();
    }

    public Task<Void> deleteFile(String download_link) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(download_link);
        return storageReference.delete().addOnFailureListener(e -> DialogUtil.showErrorDialog(activity));
    }

    public MutableLiveData<ArrayList<EmployeeMasterModel>> getEmployeeMasterListLiveData() {
        if (employeeMasterLiveData == null) {
            employeeMasterLiveData = new MutableLiveData<>();
            getEmployeeMasterList("");
        }
        return employeeMasterLiveData;
    }

    public void removeSelectedItem(ArrayList<Integer> selectedItems) {
        int batchSize = 500;
        int batchCount = 0;
        DialogUtil.showProgressDialog(activity);
        WriteBatch batch = db.batch();
        for (int i = selectedItems.size() - 1; i >= 0; i--) {
            Log.d("dfdjksfjklwe423", "removeSelectedItem: " + employeeMasterList.get(selectedItems.get(i)).getEmployee_photo());
            if (!employeeMasterList.get(selectedItems.get(i)).getEmployee_photo().equals("null")){
                deleteFile(employeeMasterList.get(selectedItems.get(i)).getEmployee_photo());
            }
            if (!employeeMasterList.get(selectedItems.get(i)).getId_proof().equals("null")){
                deleteFile(employeeMasterList.get(selectedItems.get(i)).getId_proof());
            }

            DocumentReference documentReference = collectionReference.document(employeeMasterList.get(selectedItems.get(i)).getId());
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


    public void getEmployeeMasterList(String filter) {
        collectionReference.addSnapshotListener((queryDocumentSnapshots, error) -> {
            employeeMasterList.clear();
            if (error == null && queryDocumentSnapshots != null) {
                for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                    EmployeeMasterModel employeeMasterModel = snapshot.toObject(EmployeeMasterModel.class);
                    if (employeeMasterModel != null && employeeMasterModel.getEmployee_name().toLowerCase().contains(filter.toLowerCase())) {
                        employeeMasterList.add(employeeMasterModel);
                    }
                }
                Collections.reverse(employeeMasterList);
                employeeMasterLiveData.setValue(employeeMasterList);
            } else {
                // handle error
            }
        });
    }

}
