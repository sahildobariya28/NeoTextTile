package com.example.texttile.data.dao;

import android.app.Activity;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.texttile.R;
import com.example.texttile.data.model.OrderHistory;
import com.example.texttile.data.model.OrderMasterModel;
import com.example.texttile.presentation.ui.lib.snackUtil.CustomSnackUtil;
import com.example.texttile.presentation.ui.util.Const;
import com.example.texttile.presentation.ui.util.DialogUtil;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class DAOOrder {

    MutableLiveData<ArrayList<OrderMasterModel>> orderMasterLiveData = new MutableLiveData<>();
    MutableLiveData<ArrayList<OrderMasterModel>> filterOrderMasterLiveData = new MutableLiveData<>();
    ArrayList<OrderMasterModel> orderMasterList = new ArrayList<>();
    ArrayList<OrderMasterModel> filterOrderMasterList = new ArrayList<>();

    FirebaseFirestore db;
    CollectionReference collectionReference;
    Activity activity;

    public DAOOrder(Activity activity) {

        db = FirebaseFirestore.getInstance();
        collectionReference = db.collection(Const.ORDER_MASTER);
        this.activity = activity;
    }

    public Task<Void> insert(String id, OrderMasterModel orderMasterModel) {
        return collectionReference.document(id).set(orderMasterModel).addOnFailureListener(runnable -> DialogUtil.showErrorDialog(activity));
    }

    public Task<Void> update(String id, HashMap<String, Object> hashMap) {

        return collectionReference.document(id).update(hashMap).addOnFailureListener(runnable -> DialogUtil.showErrorDialog(activity));
    }

    public Task<Void> delete(String id) {
        return collectionReference.document(id).delete().addOnFailureListener(runnable -> DialogUtil.showErrorDialog(activity));
    }

    public CollectionReference getReference() {
        return collectionReference;
    }

    public String getId() {
        return collectionReference.document().getId();
    }


    public Task<Void> update_qty(String id, OrderMasterModel orderMasterModel, OrderHistory orderHistoryModel) {
        DialogUtil.showProgressDialog(activity);

        return collectionReference.document(id).update(objectToHashMap(orderMasterModel)).addOnSuccessListener(runnable -> {
            DaoOrderHistory orderHistory = new DaoOrderHistory(activity);
            DialogUtil.hideProgressDialog();
            orderHistory.insert(orderHistoryModel.getId(), orderHistoryModel).addOnSuccessListener(runnable1 -> {
                DialogUtil.showUpdateSuccessDialog(activity, false);
            });
        }).addOnFailureListener(runnable -> {
            DialogUtil.hideProgressDialog();
            DialogUtil.showErrorDialog(activity);
        });
    }

    public Task<Void> update_qty_without_dialog(String id, OrderMasterModel orderMasterModel, OrderHistory orderHistoryModel) {

        return collectionReference.document(id).update(objectToHashMap(orderMasterModel)).addOnSuccessListener(runnable -> {
            DaoOrderHistory orderHistory = new DaoOrderHistory(activity);
            orderHistory.insert(orderHistoryModel.getId(), orderHistoryModel).addOnSuccessListener(runnable1 -> {
                DialogUtil.showUpdateSuccessDialog(activity, false);
            });
        }).addOnFailureListener(runnable -> {
            DialogUtil.showErrorDialog(activity);
        });
    }

    public HashMap<String, Object> objectToHashMap(Object obj) {
        HashMap<String, Object> map = new HashMap<>();
        Class<?> clazz = obj.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                map.put(field.getName(), field.get(obj));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return map;
    }

    public MutableLiveData<ArrayList<OrderMasterModel>> getOrderListLiveData() {
        if (orderMasterLiveData == null) {
            orderMasterLiveData = new MutableLiveData<>();
            getOrderList("");
        }
        return orderMasterLiveData;
    }

    public MutableLiveData<ArrayList<OrderMasterModel>> getFilterOrderListLiveData() {
        if (filterOrderMasterLiveData == null) {
            filterOrderMasterLiveData = new MutableLiveData<>();
            getOrderList("");
        }
        return filterOrderMasterLiveData;
    }

    public void removeSelectedItem(ArrayList<Integer> selectedItems) {
        int batchSize = 500;
        int batchCount = 0;
        DialogUtil.showProgressDialog(activity);
        WriteBatch batch = FirebaseFirestore.getInstance().batch();
        for (int i = selectedItems.size() - 1; i >= 0; i--) {
            DocumentReference documentReference = collectionReference.document(orderMasterList.get(selectedItems.get(i)).getId());
            batch.delete(documentReference);
            if (++batchCount == batchSize) {
                batch.commit().addOnSuccessListener(aVoid -> {
                    DialogUtil.hideProgressDialog();
                }).addOnFailureListener(e -> {
                    DialogUtil.hideProgressDialog();
                    new CustomSnackUtil().showSnack(activity, "Something went wrong", R.drawable.error_msg_icon);
                });
                batch = FirebaseFirestore.getInstance().batch();
                batchCount = 0;
            }
        }
        if (batchCount > 0) {
            batch.commit().addOnSuccessListener(aVoid -> {
                DialogUtil.hideProgressDialog();
            }).addOnFailureListener(e -> {
                DialogUtil.hideProgressDialog();
                new CustomSnackUtil().showSnack(activity, "Something went wrong", R.drawable.error_msg_icon);
            });
        } else {
            DialogUtil.hideProgressDialog();
            DialogUtil.showDeleteDialog(activity);
        }
    }

    public void getOrderListByStatus(String status, String filter) {
        collectionReference.addSnapshotListener((value, error) -> {
            orderMasterList.clear();
            if (value != null) {
                for (QueryDocumentSnapshot doc : value) {
                    OrderMasterModel orderMasterModel = doc.toObject(OrderMasterModel.class);
                    if (orderMasterModel != null && orderMasterModel.getSub_order_no().toLowerCase().contains(filter.toLowerCase())) {
                        switch (status) {
                            case "pending":
                                if (orderMasterModel.getOrderStatusModel().getPending() > 0) {
                                    orderMasterList.add(orderMasterModel);
                                }
                                break;
                            case "onMachinePending":
                                if (orderMasterModel.getOrderStatusModel().getOnMachinePending() > 0) {
                                    orderMasterList.add(orderMasterModel);
                                }
                                break;
                            case "onMachineCompleted":
                                if (orderMasterModel.getOrderStatusModel().getOnMachineCompleted() > 0) {
                                    orderMasterList.add(orderMasterModel);
                                }
                                break;
                            case "onReadyToDispatch":
                                if (orderMasterModel.getOrderStatusModel().getOnReadyToDispatch() > 0) {
                                    orderMasterList.add(orderMasterModel);
                                }
                                break;
                            case "onWarehouse":
                                if (orderMasterModel.getOrderStatusModel().getOnWarehouse() > 0) {
                                    orderMasterList.add(orderMasterModel);
                                }
                                break;
                            case "onFinalDispatch":
                                if (orderMasterModel.getOrderStatusModel().getOnFinalDispatch() > 0) {
                                    orderMasterList.add(orderMasterModel);
                                }
                                break;
                            case "onDelivered":
                                if (orderMasterModel.getOrderStatusModel().getOnDelivered() > 0) {
                                    orderMasterList.add(orderMasterModel);
                                }
                                break;
                            case "onDamage":
                                if (orderMasterModel.getOrderStatusModel().getOnDamage() > 0) {
                                    orderMasterList.add(orderMasterModel);
                                }
                                break;
                            default:
                                orderMasterList.add(orderMasterModel);
                                break;

                        }

                    }
                }
            }
            Log.d("dfjdjwe23", "getOrderListByStatus: " + orderMasterList);
            orderMasterLiveData.setValue(orderMasterList);
        });
    }


    public void getOrderList(String filter) {
        collectionReference.addSnapshotListener((value, error) -> {
            orderMasterList.clear();
            if (value != null) {
                for (QueryDocumentSnapshot doc : value) {
                    OrderMasterModel orderMasterModel = doc.toObject(OrderMasterModel.class);
                    if (orderMasterModel != null && orderMasterModel.getOrder_no().toLowerCase().contains(filter.toLowerCase())) {
                        orderMasterList.add(orderMasterModel);
                    }
                }
                Collections.reverse(orderMasterList);
            }
            orderMasterLiveData.setValue(orderMasterList);
        });
    }

    public void getFilterOrderList(String filter) {
        collectionReference.addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                Log.w("TAG", "Listen failed.", e);
                return;
            }
            filterOrderMasterList.clear();
            if (!snapshot.isEmpty()) {
                for (QueryDocumentSnapshot doc : snapshot) {
                    OrderMasterModel orderMasterModel = doc.toObject(OrderMasterModel.class);

                    if (filterOrderMasterList.size() == 0) {
                        filterOrderMasterList.add(orderMasterModel);
                    } else {
                        boolean bool = true;
                        for (int j = 0; j < filterOrderMasterList.size(); j++) {
                            if (orderMasterModel.getOrder_no() != null && filterOrderMasterList != null && orderMasterModel.getOrder_type() != null) {
                                if (orderMasterModel.getOrder_no().equals(filterOrderMasterList.get(j).getOrder_no())) {
                                    bool = false;
                                    break;
                                }
                            }
                        }
                        if (bool) {
                            if (orderMasterModel != null && orderMasterModel.getOrder_no().toLowerCase().contains(filter.toLowerCase())) {
                                filterOrderMasterList.add(orderMasterModel);
                            }
                        }
                    }
                }
            }
            filterOrderMasterLiveData.setValue(filterOrderMasterList);
            // Handle snapshot data
        });
    }

    public void getSubOrderList(String filter, String order_no) {
        collectionReference.whereEqualTo("order_no", order_no).addSnapshotListener((value, error) -> {
            orderMasterList.clear();
            if (value != null) {
                for (QueryDocumentSnapshot doc : value) {
                    OrderMasterModel orderMasterModel = doc.toObject(OrderMasterModel.class);
                    if (orderMasterModel != null && orderMasterModel.getSub_order_no().toLowerCase().contains(filter.toLowerCase())) {
                        orderMasterList.add(orderMasterModel);
                    }
                }
                Collections.reverse(orderMasterList);
            }
            orderMasterLiveData.setValue(orderMasterList);
        });
    }

}
