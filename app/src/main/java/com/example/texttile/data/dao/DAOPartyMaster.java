package com.example.texttile.data.dao;

import android.app.Activity;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.texttile.R;
import com.example.texttile.data.model.PartyMasterModel;
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
import java.util.Map;

import javax.annotation.Nullable;

public class DAOPartyMaster {

    MutableLiveData<ArrayList<PartyMasterModel>> partyMasterLiveData = new MutableLiveData<>();
    ArrayList<PartyMasterModel> partyMasterList = new ArrayList<>();

    FirebaseFirestore db;
    CollectionReference partyMasterCollectionRef;

    Activity activity;

    public DAOPartyMaster(Activity activity) {
        this.activity = activity;
        db = FirebaseFirestore.getInstance();
        partyMasterCollectionRef = db.collection(Const.PARTY_MASTER);
    }

    public Task<Void> insert(String id, PartyMasterModel partyMasterModel) {
        return partyMasterCollectionRef.document(id).set(partyMasterModel).addOnFailureListener(runnable -> {
            DialogUtil.showErrorDialog(activity);
        });
    }

    public Task<Void> update(String id, Map<String, Object> hashMap) {
        return partyMasterCollectionRef.document(id).update(hashMap).addOnFailureListener(runnable -> {
            DialogUtil.showErrorDialog(activity);
        });
    }

    public Task<Void> delete(String id) {
        return partyMasterCollectionRef.document(id).delete().addOnFailureListener(runnable -> {
            DialogUtil.showErrorDialog(activity);
        });
    }

    public CollectionReference getReference() {
        return partyMasterCollectionRef;
    }

    public String getId() {
        return partyMasterCollectionRef.document().getId();
    }

    public MutableLiveData<ArrayList<PartyMasterModel>> getPartyMasterListLiveData() {
        if (partyMasterLiveData == null) {
            partyMasterLiveData = new MutableLiveData<>();
            getPartyMasterList("");
        }
        return partyMasterLiveData;
    }

    public void removeSelectedItem(ArrayList<Integer> selectedItems) {

        int batchSize = 500;
        int batchCount = 0;
        DialogUtil.showProgressDialog(activity);
        WriteBatch batch = db.batch();
        for (int i = selectedItems.size() - 1; i >= 0; i--) {
            DocumentReference documentReference = partyMasterCollectionRef.document(partyMasterList.get(selectedItems.get(i)).getId());
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

    public void getPartyMasterList(String filter) {
        partyMasterCollectionRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                partyMasterList.clear();
                if (e != null) {
                    return;
                }

                for (QueryDocumentSnapshot document : snapshot) {
                    PartyMasterModel partyMasterModel = document.toObject(PartyMasterModel.class);
                    Log.d("dfjsdfsdfsdfwe", "onEvent: " + partyMasterModel.getParty_Name());
                    if (partyMasterModel != null) {
                        if (partyMasterModel.getParty_Name() != null) {
                            if (partyMasterModel.getParty_Name().toLowerCase().contains(filter.toLowerCase())) {
                                partyMasterList.add(partyMasterModel);
                            }
                        }
                    }
                }
                partyMasterLiveData.setValue(partyMasterList);
            }
        });
    }

}