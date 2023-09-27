package com.example.texttile.data.dao;

import android.app.Activity;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.texttile.R;
import com.example.texttile.data.model.OrderMasterModel;
import com.example.texttile.data.model.ReadyStockModel;
import com.example.texttile.presentation.ui.lib.snackUtil.CustomSnackUtil;
import com.example.texttile.presentation.ui.util.Const;
import com.example.texttile.presentation.ui.util.DialogUtil;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class DAOExtraOrder {

    MutableLiveData<ArrayList<ReadyStockModel>> readyStockLiveData = new MutableLiveData<>();
    ArrayList<ReadyStockModel> readyStockList = new ArrayList<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference collectionReference = db.collection(Const.READY_STOCK);
    Activity activity;

    public DAOExtraOrder(Activity activity) {
        this.activity = activity;
    }

    public Task<Void> insert(String id, ReadyStockModel readyStockModel) {
        return collectionReference.document(id).set(readyStockModel)
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

    public String getId() {
        return collectionReference.document().getId();
    }

    public MutableLiveData<ArrayList<ReadyStockModel>> getReadyStockListLiveData() {
        if (readyStockLiveData == null) {
            readyStockLiveData = new MutableLiveData<>();
            getReadyStockList("");
        }
        return readyStockLiveData;
    }

    public void getReadyStockList(String filter_design, String filter_shade) {
        collectionReference.addSnapshotListener((value, error) -> {
            if (error != null) return;
            readyStockList.clear();
            if (value != null) {
                for (QueryDocumentSnapshot doc : value) {
                    ReadyStockModel readyStockModel = doc.toObject(ReadyStockModel.class);
                    Log.d("djfksdklfjwer", "getReadyStockList: 111: " + filter_design +" : " + readyStockModel.getDesign_no());
                    Log.d("djfksdklfjwer", "getReadyStockList: 222: " + filter_shade +" : " + readyStockModel.getShade_no());
                    if (readyStockModel != null && readyStockModel.getDesign_no().toLowerCase().contains(filter_design.toLowerCase()) && readyStockModel.getShade_no().toLowerCase().contains(filter_shade.toLowerCase())) {
                        readyStockList.add(readyStockModel);
                    }
                }
                readyStockLiveData.setValue(sortModelListByDate(readyStockList));
            } else {
                readyStockLiveData.setValue(sortModelListByDate(readyStockList));
            }
        });
    }

    public void getReadyStockList(String filter) {
        collectionReference.addSnapshotListener((value, error) -> {
            if (error != null) return;
            readyStockList.clear();
            if (value != null) {
                for (QueryDocumentSnapshot doc : value) {
                    ReadyStockModel readyStockModel = doc.toObject(ReadyStockModel.class);
                    if (readyStockModel != null && readyStockModel.getShade_no().toLowerCase().contains(filter.toLowerCase())) {
                        readyStockList.add(readyStockModel);
                    }
                }
                readyStockLiveData.setValue(sortModelListByDate(readyStockList));
            } else {
                readyStockLiveData.setValue(sortModelListByDate(readyStockList));
            }
        });
    }

    public void sortDesignQty(boolean isDesignQtyUp) {
        ArrayList<ReadyStockModel> topList = new ArrayList<>();
        ArrayList<ReadyStockModel> bottomList = new ArrayList<>();
        ArrayList<ReadyStockModel> newList = new ArrayList<>();

        CollectionReference ordersRef = FirebaseFirestore.getInstance().collection("orders");

        ordersRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot ordersSnapshot = task.getResult();
                ArrayList<OrderMasterModel> orderMasterList = new ArrayList<>();
                for (DocumentSnapshot document : ordersSnapshot.getDocuments()) {
                    OrderMasterModel orderMasterModel = document.toObject(OrderMasterModel.class);
                    orderMasterList.add(orderMasterModel);
                }

                for (int i = 0; i < readyStockList.size(); i++) {
                    for (OrderMasterModel orderMasterItem : orderMasterList) {
                        if (orderMasterItem.getDesign_no().equals(readyStockList.get(i).getDesign_no()) && orderMasterItem.getShade_no().equals(readyStockList.get(i).getShade_no())) {
                            if (!topList.contains(readyStockList.get(i))) {
                                topList.add(readyStockList.get(i));
                            }
                        }
                    }
                    for (OrderMasterModel orderMasterItem : orderMasterList) {
                        if (!orderMasterItem.getDesign_no().equals(readyStockList.get(i).getDesign_no()) && !orderMasterItem.getShade_no().equals(readyStockList.get(i).getShade_no())) {
                            if (!bottomList.contains(readyStockList.get(i)) && !topList.contains(readyStockList.get(i))) {
                                bottomList.add(readyStockList.get(i));
                            }
                        }
                    }
                }
                if (isDesignQtyUp) {
                    newList.clear();
                    newList.addAll(topList);
                    newList.addAll(bottomList);
                } else {
                    newList.clear();
                    newList.addAll(bottomList);
                    newList.addAll(topList);
                }
                readyStockLiveData.setValue(sortModelListByDate(newList));
            }
        });
    }

    public void removeSelectedItem(ArrayList<Integer> selectedItems) {
        int batchSize = 500;
        int batchCount = 0;
        DialogUtil.showProgressDialog(activity);
        WriteBatch batch = db.batch();
        for (int i = selectedItems.size() - 1; i >= 0; i--) {
            DocumentReference documentReference = collectionReference.document(readyStockList.get(selectedItems.get(i)).getId());
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


    public ArrayList sortModelListByDate(ArrayList modelList) {
        ArrayList<ReadyStockModel> readyStockModelList = modelList;
        Collections.sort(readyStockModelList, (readingModel1, readingModel2) -> {
            try {
                return ((new SimpleDateFormat("dd/MM/yy")).parse(readingModel2.getCurrent_date()).getDay() - (new SimpleDateFormat("dd/MM/yy")).parse(readingModel1.getCurrent_date()).getDay());
            } catch (ParseException e) {
                e.printStackTrace();
                return 0;
            }
        });
        return readyStockModelList;
    }

}
