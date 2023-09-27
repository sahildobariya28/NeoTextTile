package com.example.texttile.data.dao;

import android.app.Activity;
import android.net.Uri;
import android.text.TextUtils;

import androidx.lifecycle.MutableLiveData;

import com.example.texttile.R;
import com.example.texttile.data.model.DesignMasterModel;
import com.example.texttile.data.model.JalaWithFileModel;
import com.example.texttile.presentation.ui.lib.snackUtil.CustomSnackUtil;
import com.example.texttile.presentation.ui.util.Const;
import com.example.texttile.presentation.ui.util.DialogUtil;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
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

public class DAODesignMaster {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    CollectionReference collectionReference;
    StorageReference storageReference;
    private final Activity activity;
    private MutableLiveData<ArrayList<DesignMasterModel>> designMasterLiveData = new MutableLiveData<>();
    private final ArrayList<DesignMasterModel> designMasterList = new ArrayList<>();

    public DAODesignMaster(Activity activity) {
        this.activity = activity;
        collectionReference = db.collection(Const.DESIGN_MASTER);
        storageReference = storage.getReference().child("design/");
    }

    public Task<Void> insert(String id, DesignMasterModel designMasterModel) {
        return collectionReference.document(id).set(designMasterModel).addOnFailureListener(runnable -> {
            DialogUtil.showErrorDialog(activity);
        });
    }

    public Task<Void> update(String id, Map<String, Object> updates) {
        return collectionReference.document(id).update(updates).addOnFailureListener(runnable -> {
            DialogUtil.showErrorDialog(activity);
        });
    }

    public Task<Void> delete(String id) {
        return collectionReference.document(id).delete().addOnFailureListener(runnable -> {
            DialogUtil.showErrorDialog(activity);
        });
    }

    public CollectionReference getReference() {
        return collectionReference;
    }

    public String getNewId() {
        return collectionReference.document().getId();
    }

    public UploadTask uploadPhoto(String id, String file_name, Uri mImageUri) {
        StorageReference storageReference = storage.getReference().child("design/").child(id).child(file_name);
        return storageReference.putFile(mImageUri);
    }

    public Task<Void> deleteFile(String download_link) {
        if (!TextUtils.isEmpty(download_link)) {
            StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(download_link);
            return storageReference.delete();
        } else {
            return Tasks.forException(new IllegalArgumentException("download_link cannot be null or empty"));
        }
    }

    public void removeSingleItem(int item) {
        DesignMasterModel designMasterModel = designMasterList.get(item);
        for (JalaWithFileModel jalaWithFileModel : designMasterModel.getJalaWithFileModelList()) {
            try {
                storage.getReferenceFromUrl(jalaWithFileModel.getFile_uri()).delete();
            } catch (Exception e) {
                // ignore error
            }
        }
        collectionReference.document(designMasterModel.getId()).delete().addOnSuccessListener(aVoid -> {
            DialogUtil.showDeleteDialog(activity);
        })
                .addOnFailureListener(e -> {
                    new CustomSnackUtil().showSnack(activity, "Something Want Wrong", R.drawable.error_msg_icon);
                });
    }

    public void removeSelectedItem(ArrayList<Integer> selectedItems) {
        int batchSize = 500;
        int batchCount = 0;
        DialogUtil.showProgressDialog(activity);
        WriteBatch batch = db.batch();
        for (int i = selectedItems.size() - 1; i >= 0; i--) {
            DocumentReference documentReference = collectionReference.document(designMasterList.get(selectedItems.get(i)).getId());
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


    public Task<Uri> getDownloadUri(String id, String fileName) {
        return storageReference.child(id).child(fileName).getDownloadUrl();
    }

    public MutableLiveData<ArrayList<DesignMasterModel>> getDesignMasterListLiveData() {
        if (designMasterLiveData == null) {
            designMasterLiveData = new MutableLiveData<>();
            getDesignMasterList("");
        }
        return designMasterLiveData;
    }

    public void getDesignMasterList(String filter) {
        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                designMasterList.clear();
                if (e != null) {
                    return;
                }

                for (QueryDocumentSnapshot document : snapshot) {
                    DesignMasterModel designMasterModel = document.toObject(DesignMasterModel.class);
                    if (designMasterModel != null && designMasterModel.getDesign_no().toLowerCase().contains(filter.toLowerCase())) {
                        designMasterList.add(designMasterModel);
                    }
                }
                designMasterLiveData.setValue(designMasterList);
            }
        });
    }

}