package com.example.texttile.data.dao;

import android.app.Activity;

import androidx.lifecycle.MutableLiveData;

import com.example.texttile.data.model.OrderHistory;
import com.example.texttile.presentation.ui.util.Const;
import com.example.texttile.presentation.ui.util.DialogUtil;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

public class DaoOrderHistory {

    private MutableLiveData<ArrayList<OrderHistory>> orderHistoryLiveData = new MutableLiveData<>();
    private final ArrayList<OrderHistory> orderHistoryList = new ArrayList<>();
    CollectionReference collectionReference;

    FirebaseFirestore db;
    Activity activity;

    public DaoOrderHistory(Activity activity) {
        db = FirebaseFirestore.getInstance();
        collectionReference = db.collection(Const.ORDER_HISTORY);
        this.activity = activity;
    }

    public Task<Void> insert(String id, OrderHistory orderHistory) {
        return collectionReference.document(id)
                .set(orderHistory)
                .addOnFailureListener(e -> DialogUtil.showErrorDialog(activity));
    }

    public Task<Void> update(String id, Map<String, Object> map) {
        return collectionReference.document(id)
                .update(map)
                .addOnFailureListener(e -> DialogUtil.showErrorDialog(activity));
    }

    public Task<Void> delete(String id) {
        return collectionReference.document(id)
                .delete()
                .addOnSuccessListener(aVoid -> DialogUtil.showDeleteDialog(activity))
                .addOnFailureListener(e -> DialogUtil.showErrorDialog(activity));
    }

    public CollectionReference getReference() {
        return collectionReference;
    }

    public String getNewId() {
        return collectionReference.document().getId();
    }

    public void removeSelectedItem(ArrayList<Integer> selectedItems, OnSuccessListener onSuccessListener, OnFailureListener onFailureListener) {
        int batchSize = 500;
        int batchCount = 0;
        WriteBatch batch = db.batch();
        DialogUtil.showProgressDialog(activity);
        for (int i = selectedItems.size() - 1; i >= 0; i--) {
            DocumentReference documentReference = collectionReference.document(orderHistoryList.get(selectedItems.get(i)).getId());
            batch.delete(documentReference);
            if (++batchCount == batchSize) {
                batch.commit();
                batch = FirebaseFirestore.getInstance().batch();
                batchCount = 0;
            }
        }

        if (batchCount > 0) {

            batch.commit().addOnSuccessListener(onSuccessListener).addOnFailureListener(onFailureListener);
        }else {
            DialogUtil.hideProgressDialog();
            DialogUtil.showDeleteDialog(activity);
        }
    }

    public MutableLiveData<ArrayList<OrderHistory>> getOrderHistoryListLiveData() {
        if (orderHistoryLiveData == null) {
            orderHistoryLiveData = new MutableLiveData<>();
            getOrderHistoryList("", "all_history");
        }
        return orderHistoryLiveData;
    }

    public void getOrderHistoryList(String filter, String orderStatusModel) {
        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot value, FirebaseFirestoreException error) {
                orderHistoryList.clear();
                if (error != null) {
                    return;
                }
                for (QueryDocumentSnapshot documentSnapshot : value){
                    OrderHistory orderHistory = documentSnapshot.toObject(OrderHistory.class);
                    if (orderHistory != null && orderHistory.getDestination().equals(orderStatusModel)){
                        if (orderHistory != null && orderHistory.getSubOrderNo().toLowerCase().contains(filter.toLowerCase())){
                            orderHistoryList.add(orderHistory);
                        }
                    }else {
                        if (orderStatusModel.equals("all_history")) {
                            if (orderHistory != null && orderHistory.getSubOrderNo().toLowerCase().contains(filter.toLowerCase())) {
                                orderHistoryList.add(orderHistory);
                            }
                        }
                    }
                }
//                Log.d("djfldsjfwer", "onEvent: " + orderHistoryList);
                orderHistoryLiveData.setValue(orderHistoryList);
            }
        });


//        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
//            @Override
//            public void onEvent(@Nullable QuerySnapshot snapshot,
//                                @Nullable FirebaseFirestoreException e) {
//                orderHistoryList.clear();
//                if (e != null) {
//                    return;
//                }
//
//                for (QueryDocumentSnapshot document : snapshot) {
//                    OrderHistory orderHistory = document.toObject(OrderHistory.class);
//                    if (orderHistory != null && orderHistory.getDestination().equals(orderStatusModel)) {
//                        if (orderHistory != null && orderHistory.getSubOrderNo().toLowerCase().contains(filter.toLowerCase())) {
//                            orderHistoryList.add(orderHistory);
//                        }
//                    } else {
//                        if (orderStatusModel.equals("all_history")) {
//                            if (orderHistory != null && orderHistory.getSubOrderNo().toLowerCase().contains(filter.toLowerCase())) {
//                                orderHistoryList.add(orderHistory);
//                            }
//                        }
//                    }
//                }
//                Log.d("djfldsjfwer", "onEvent: " + orderHistoryList);
//                orderHistoryLiveData.setValue(orderHistoryList);
//            }
//        });
    }

}




