package com.example.texttile.data.dao;

import android.app.Activity;

import androidx.lifecycle.MutableLiveData;

import com.example.texttile.data.model.LiveOrder;
import com.example.texttile.presentation.ui.util.Const;
import com.example.texttile.presentation.ui.util.DialogUtil;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class DaoLiveOrder {

    private MutableLiveData<ArrayList<LiveOrder>> liveOrderLiveData = new MutableLiveData<>();
    private ArrayList<LiveOrder> liveOrderList = new ArrayList<>();
    private FirebaseFirestore db;
    private Activity activity;
    private CollectionReference liveOrderCollection;

    public DaoLiveOrder(Activity activity) {
        db = FirebaseFirestore.getInstance();
        liveOrderCollection = db.collection(Const.LIVE_ORDER);
        this.activity = activity;
    }

    public Task<Void> insert(String id, LiveOrder liveOrder) {
        return liveOrderCollection.document(id).set(liveOrder)
                .addOnFailureListener(e -> DialogUtil.showErrorDialog(activity));
    }

    public Task<Void> update(String id, HashMap<String, Object> hashMap) {
        return liveOrderCollection.document(id).update(hashMap)
                .addOnFailureListener(e -> DialogUtil.showErrorDialog(activity));
    }

    public Task<Void> delete(String id) {
        return liveOrderCollection.document(id).delete()
                .addOnFailureListener(e -> DialogUtil.showErrorDialog(activity));
    }

    public void removeByMachineName(String id) {
        liveOrderCollection.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                if (querySnapshot != null) {
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        LiveOrder liveOrder = document.toObject(LiveOrder.class);
                        if (liveOrder.getMachine_name().equals(id)) {
                            liveOrderCollection.document(liveOrder.getId()).delete()
                                    .addOnFailureListener(e -> DialogUtil.showErrorDialog(activity));
                        }
                        liveOrderList.add(liveOrder);
                    }
                    Collections.reverse(liveOrderList);
                    liveOrderLiveData.setValue(liveOrderList);
                }
            }
        });
    }

    public void updateQtyByMachineName(String machine_name, int qty) {
        liveOrderCollection.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                if (querySnapshot != null) {
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        LiveOrder liveOrder = document.toObject(LiveOrder.class);
                        if (liveOrder.getMachine_name().equals(machine_name)) {
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("qty", liveOrder.getQty() - qty);
                            if (liveOrder.getQty() - qty == 0) {
                                removeByMachineName(machine_name);
                            } else {
                                liveOrderCollection.document(liveOrder.getId()).update(hashMap)
                                        .addOnFailureListener(e -> DialogUtil.showErrorDialog(activity));
                            }
                        }
                        liveOrderList.add(liveOrder);
                    }
                    Collections.reverse(liveOrderList);
                    liveOrderLiveData.setValue(liveOrderList);
                }
            }
        });
    }

    public MutableLiveData<ArrayList<LiveOrder>> getLiveOrderListLiveData() {
        if (liveOrderLiveData == null) {
            liveOrderLiveData = new MutableLiveData<>();
            getLiveOrderList("");
        }
        return liveOrderLiveData;
    }


    public String getNewId() {
        return liveOrderCollection.document().getId();
    }

    public CollectionReference getReference() {
        return liveOrderCollection;
    }


    public void getLiveOrderList(String filter) {
        liveOrderCollection.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                if (querySnapshot != null) {
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        LiveOrder liveOrder = document.toObject(LiveOrder.class);
                        liveOrderList.add(liveOrder);
                    }
                    Collections.reverse(liveOrderList);
                    liveOrderLiveData.setValue(liveOrderList);
                }
            }
        });
    }

}