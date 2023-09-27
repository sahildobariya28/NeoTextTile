package com.example.texttile.data.dao;

import android.app.Activity;
import android.net.Uri;

import androidx.lifecycle.MutableLiveData;

import com.example.texttile.R;
import com.example.texttile.data.model.JalaMasterModel;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Map;

import javax.annotation.Nullable;

public class DAOJalaMaster {

    private MutableLiveData<ArrayList<JalaMasterModel>> jalaMasterLiveData = new MutableLiveData<>();
    private ArrayList<JalaMasterModel> jalaMasterList = new ArrayList<>();

    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = firestore.collection(Const.JALA_MASTER);
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageReference = storage.getReference().child("jala/pattern/");
    private Activity activity;

    public DAOJalaMaster(Activity activity) {
        this.activity = activity;
    }

    public Task<Void> insert(String id, JalaMasterModel jalaMasterModel) {
        return collectionReference.document(id).set(jalaMasterModel).addOnFailureListener(e -> DialogUtil.showErrorDialog(activity));
    }

    public Task<Void> update(String id, Map<String, Object> hashMap) {
        return collectionReference.document(id).update(hashMap).addOnFailureListener(e -> DialogUtil.showErrorDialog(activity));
    }

    public Task<Void> delete(String id) {
        return collectionReference.document(id).delete().addOnFailureListener(e -> DialogUtil.showErrorDialog(activity));
    }

    public CollectionReference getReference() {
        return collectionReference;
    }

    public String getNewId() {
        return collectionReference.document().getId();
    }

    public UploadTask uploadImage(String id, Uri mImageUri) {
        return storageReference.child(id).putFile(mImageUri);
    }

    public Task<Uri> getDownloadUri(String id) {
        return storageReference.child(id).getDownloadUrl();
    }

    public MutableLiveData<ArrayList<JalaMasterModel>> getJalaMasterListLiveData() {
        if (jalaMasterLiveData == null) {
            jalaMasterLiveData = new MutableLiveData<>();
            getJalaMasterList("");
        }
        return jalaMasterLiveData;
    }


    public void getJalaMasterList(String filter) {
        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                jalaMasterList.clear();
                if (e != null) {
                    return;
                }

                for (QueryDocumentSnapshot document : snapshot) {
                    JalaMasterModel jalaMasterModel = document.toObject(JalaMasterModel.class);
                    if (jalaMasterModel != null && jalaMasterModel.getJala_name().toLowerCase().contains(filter.toLowerCase())) {
                        jalaMasterList.add(jalaMasterModel);
                    }
                }
                jalaMasterLiveData.setValue(jalaMasterList);
            }
        });
    }

    public void removeSelectedItem(ArrayList<Integer> selectedItems) {
        int batchSize = 500;
        int batchCount = 0;
        DialogUtil.showProgressDialog(activity);
        WriteBatch batch = firestore.batch();
        for (int i = selectedItems.size() - 1; i >= 0; i--) {
            if (!jalaMasterList.get(selectedItems.get(i)).getSelect_photo().equals("null")) {
                storage.getReferenceFromUrl(jalaMasterList.get(selectedItems.get(i)).getSelect_photo()).delete();
            }
            DocumentReference documentReference = collectionReference.document(jalaMasterList.get(selectedItems.get(i)).getId());
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
        } else {
            DialogUtil.hideProgressDialog();
            DialogUtil.showDeleteDialog(activity);
        }
    }

    public void removeSingleItem(int item) {
        JalaMasterModel jalaMasterModel = jalaMasterList.get(item);

        if (!jalaMasterModel.getSelect_photo().equals("null")) {
            storage.getReferenceFromUrl(jalaMasterModel.getSelect_photo()).delete();
        }

        collectionReference.document(jalaMasterModel.getId()).delete().addOnSuccessListener(aVoid -> {
            DialogUtil.showDeleteDialog(activity);
        }).addOnFailureListener(e -> {
            new CustomSnackUtil().showSnack(activity, "Something Want Wrong", R.drawable.error_msg_icon);
        });
    }

}
