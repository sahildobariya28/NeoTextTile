package com.example.texttile.presentation.screen.features.reading_feature.daily_reading.reading;

import android.app.Activity;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.texttile.data.dao.DAOExtraOrder;
import com.example.texttile.data.dao.DAOMachineMaster;
import com.example.texttile.data.dao.DAOOrder;
import com.example.texttile.data.dao.DAOReading;
import com.example.texttile.data.dao.DaoLiveOrder;
import com.example.texttile.data.dao.DaoOrderHistory;
import com.example.texttile.data.model.LiveOrder;
import com.example.texttile.data.model.MachineMasterModel;
import com.example.texttile.data.model.OrderHistory;
import com.example.texttile.data.model.OrderMasterModel;
import com.example.texttile.data.model.ReadyStockModel;
import com.example.texttile.presentation.ui.util.Const;
import com.example.texttile.presentation.ui.util.DialogUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.annotation.Nullable;

public class DailyReadingViewModel extends ViewModel {

    DAOReading dao;

    final Calendar myCalendar = Calendar.getInstance();
    String tracker;
    String orderNo;
    Activity activity;

    String[] category = {"Select Reading Type", "On Rolling", "On Cutout"};
    final int ORDER_REQUEST = 1, MACHINE_REQUEST = 2;
    int ON_ROLLING = 1, ON_CUTOUT = 2;
    MutableLiveData<ArrayList<String>> order_no_list = new MutableLiveData<>(new ArrayList<>());
    MutableLiveData<ArrayList<OrderMasterModel>> orderMasterModelList = new MutableLiveData<>(new ArrayList<>());
    MutableLiveData<ArrayList<String>> machineList = new MutableLiveData<>(new ArrayList<>());
    MutableLiveData<ArrayList<MachineMasterModel>> machineMasterList = new MutableLiveData<>(new ArrayList<>());

    public DailyReadingViewModel(String tracker, Activity activity) {
        this.tracker = tracker;
        this.activity = activity;

        dao = new DAOReading(activity);
    }

    public void getMachineList(String filter) {

        new DAOMachineMaster(activity).getReference().get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {


                new DaoLiveOrder(activity).getReference().addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                        machineList.getValue().clear();
                        machineMasterList.getValue().clear();
                        for (QueryDocumentSnapshot document : snapshot) {
                            LiveOrder liveOrder = document.toObject(LiveOrder.class);
                            if (liveOrder.getOrder_no().equals(filter) || liveOrder.getOrder_no().equals("")) {
                                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                    MachineMasterModel machineMasterModel = documentSnapshot.toObject(MachineMasterModel.class);
                                    if (liveOrder.getMachine_name().equals(machineMasterModel.getMachine_name())) {
                                        machineMasterList.getValue().add(machineMasterModel);
                                        machineList.getValue().add(machineMasterModel.getMachine_name() + "(" + liveOrder.getQty() + ")");
                                    }
                                }
                            }
                        }

                    }
                });
            }
        }).addOnFailureListener(e -> {

        });

    }

    public void getOrderList() {

        new DAOOrder(activity).getReference().addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("TAG", "Listen failed.", e);
                    return;
                }

                for (QueryDocumentSnapshot doc : snapshot) {
                    OrderMasterModel orderMasterModel = doc.toObject(OrderMasterModel.class);
                    // TODO: do something with the OrderMasterModel object
                    if (orderMasterModel.getOrderStatusModel().getOnMachinePending() > 0) {
                        order_no_list.getValue().add(orderMasterModel.getSub_order_no());
                        orderMasterModelList.getValue().add(orderMasterModel);
                    } else if (orderMasterModel.getOrderStatusModel().getOnMachineCompleted() > 0) {
                        order_no_list.getValue().add(orderMasterModel.getSub_order_no());
                        orderMasterModelList.getValue().add(orderMasterModel);
                    }
                }


            }
        });
    }
    public void getOrderList(int extra_quantity, String order_no, boolean isAddInDatabase, DAOReading dao) {
        new DAOOrder(activity).getReference().get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        OrderMasterModel orderMasterModel = document.toObject(OrderMasterModel.class);
                        // Do something with the OrderMasterModel object
                        if (orderMasterModel.getSub_order_no().equals(order_no) && isAddInDatabase) {

                            DAOExtraOrder daoExtraOrder = new DAOExtraOrder(activity);
                            String id = dao.getId();
                            ReadyStockModel extraOrder = new ReadyStockModel(id, orderMasterModel.getDesign_no(), orderMasterModel.getShade_no(), new SimpleDateFormat("dd/MM/yy").format(new Date()), extra_quantity);
                            daoExtraOrder.insert(id, extraOrder).addOnSuccessListener(runnable -> {
                                DialogUtil.showSuccessDialog(activity, false);

                            });

                        }
                        order_no_list.getValue().add(orderMasterModel.getSub_order_no());
                    }
                } else {
                    Log.d("TAG", "Error getting documents: ", task.getException());
                }
            }
        });
    }

    public OrderMasterModel getOrderModel(String orderId) {
        OrderMasterModel orderMasterModel = null;
        for (int i = 0; i < orderMasterModelList.getValue().size(); i++) {
            if (orderMasterModelList.getValue().get(i).getSub_order_no().equals(orderId)) {
                orderMasterModel = orderMasterModelList.getValue().get(i);
                break;
            }
        }
        return orderMasterModel;
    }

    public void updateOrderStatus(String order_no, int quantity, String machine_name, int selectedItemPosition) {
        DAOOrder daoOrder = new DAOOrder(activity);


        new DAOOrder(activity).getReference().get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        OrderMasterModel orderMasterModel = document.toObject(OrderMasterModel.class);
                        // Do something with the OrderMasterModel object
                        if (orderMasterModel.getSub_order_no().equals(order_no)) {
                            if (selectedItemPosition == ON_ROLLING) {
                                if (quantity > orderMasterModel.getOrderStatusModel().getOnMachinePending()) {
                                    int onMachine = 0;
                                    int extra_order = quantity - orderMasterModel.getOrderStatusModel().getOnMachinePending();
                                    int onMachineCompleted = orderMasterModel.getOrderStatusModel().getOnMachineCompleted() + orderMasterModel.getOrderStatusModel().getOnMachinePending();

//                                    HashMap<String, Object> hashMap = new HashMap<>();
//                                    hashMap.put(Const.ON_MACHINE_PENDING, onMachine);
//                                    hashMap.put(Const.ON_MACHINE_COMPLETED, onMachineCompleted);
//                                    daoOrder.updateOrderStatusModel(orderMasterModel.getId(), hashMap);

                                    DaoOrderHistory orderHistory = new DaoOrderHistory(activity);
                                    String id = orderHistory.getNewId();
                                    OrderHistory orderHistoryModel = new OrderHistory(id, Const.ON_MACHINE_PENDING, Const.ON_MACHINE_COMPLETED, orderMasterModel.getOrder_no(), orderMasterModel.getSub_order_no(), orderMasterModel.getShade_no(), orderMasterModel.getDesign_no(), quantity, new SimpleDateFormat("dd/MM/yy mm:hh").format(new Date().getTime()));
                                    orderMasterModel.getOrderStatusModel().setOnMachinePending(onMachine);
                                    orderMasterModel.getOrderStatusModel().setOnMachineCompleted(onMachineCompleted);
                                    daoOrder.update_qty(orderMasterModel.getId(), orderMasterModel, orderHistoryModel);

                                    addOnMachineCompleted(orderMasterModel, orderMasterModel.getOrderStatusModel().getOnMachinePending());
                                    getOrderList(extra_order, order_no, true, dao);
                                    addExtraOrder(orderMasterModel, extra_order);

                                } else {
                                    int onMachine = orderMasterModel.getOrderStatusModel().getOnMachinePending() - quantity;
                                    int onMachineCompleted = orderMasterModel.getOrderStatusModel().getOnMachineCompleted() + quantity;

//                                    HashMap<String, Object> hashMap = new HashMap<>();
//                                    hashMap.put(Const.ON_MACHINE_PENDING, onMachine);
//                                    hashMap.put(Const.ON_MACHINE_COMPLETED, onMachineCompleted);
//                                    daoOrder.updateOrderStatusModel(orderMasterModel.getId(), hashMap);

                                    DaoOrderHistory orderHistory = new DaoOrderHistory(activity);
                                    String id = orderHistory.getNewId();
                                    OrderHistory orderHistoryModel = new OrderHistory(id, Const.ON_MACHINE_PENDING, Const.ON_MACHINE_COMPLETED, orderMasterModel.getOrder_no(), orderMasterModel.getSub_order_no(), orderMasterModel.getShade_no(), orderMasterModel.getDesign_no(), quantity, new SimpleDateFormat("dd/MM/yy mm:hh").format(new Date().getTime()));
                                    orderMasterModel.getOrderStatusModel().setOnMachinePending(onMachine);
                                    orderMasterModel.getOrderStatusModel().setOnMachineCompleted(onMachineCompleted);
                                    daoOrder.update_qty(orderMasterModel.getId(), orderMasterModel, orderHistoryModel);

                                    addOnMachineCompleted(orderMasterModel, orderMasterModel.getOrderStatusModel().getOnMachinePending());

                                }
                            } else if (selectedItemPosition == ON_CUTOUT) {
                                if (orderMasterModel.getOrderStatusModel().getOnMachineCompleted() == 0) {
                                    if (quantity > orderMasterModel.getOrderStatusModel().getOnMachinePending()) {
                                        int onMachine = 0;
                                        int extra_order = quantity - orderMasterModel.getOrderStatusModel().getOnMachinePending();
                                        int ready_to_dispatch = orderMasterModel.getOrderStatusModel().getOnReadyToDispatch() + orderMasterModel.getOrderStatusModel().getOnMachinePending();

//                                        HashMap<String, Object> hashMap = new HashMap<>();
//                                        hashMap.put(Const.ON_MACHINE_PENDING, onMachine);
//                                        hashMap.put(Const.ON_READY_TO_DISPATCH, ready_to_dispatch);
//                                        daoOrder.updateOrderStatusModel(orderMasterModel.getId(), hashMap);

                                        DaoOrderHistory orderHistory = new DaoOrderHistory(activity);
                                        String id = orderHistory.getNewId();
                                        OrderHistory orderHistoryModel = new OrderHistory(id, Const.ON_MACHINE_PENDING, Const.ON_READY_TO_DISPATCH, orderMasterModel.getOrder_no(), orderMasterModel.getSub_order_no(), orderMasterModel.getShade_no(), orderMasterModel.getDesign_no(), quantity, new SimpleDateFormat("dd/MM/yy mm:hh").format(new Date().getTime()));
                                        orderMasterModel.getOrderStatusModel().setOnMachinePending(onMachine);
                                        orderMasterModel.getOrderStatusModel().setOnReadyToDispatch(ready_to_dispatch);
                                        daoOrder.update_qty(orderMasterModel.getId(), orderMasterModel, orderHistoryModel);

                                        //insert liveOrder
                                        DaoLiveOrder daoLiveOrder = new DaoLiveOrder(activity);
                                        daoLiveOrder.removeByMachineName(machine_name);

                                        addReadyToDispatch(orderMasterModel, orderMasterModel.getOrderStatusModel().getOnMachinePending());
                                        getOrderList(extra_order, order_no, true, dao);
                                        addExtraOrder(orderMasterModel, extra_order);
                                    } else {
                                        int onMachine = orderMasterModel.getOrderStatusModel().getOnMachinePending() - quantity;
                                        int ready_to_dispatch = orderMasterModel.getOrderStatusModel().getOnReadyToDispatch() + quantity;

//                                        HashMap<String, Object> hashMap = new HashMap<>();
//                                        hashMap.put(Const.ON_MACHINE_PENDING, onMachine);
//                                        hashMap.put(Const.ON_READY_TO_DISPATCH, ready_to_dispatch);
//                                        daoOrder.updateOrderStatusModel(orderMasterModel.getId(), hashMap);

                                        DaoOrderHistory orderHistory = new DaoOrderHistory(activity);
                                        String id = orderHistory.getNewId();
                                        OrderHistory orderHistoryModel = new OrderHistory(id, Const.ON_MACHINE_PENDING, Const.ON_READY_TO_DISPATCH, orderMasterModel.getOrder_no(), orderMasterModel.getSub_order_no(), orderMasterModel.getShade_no(), orderMasterModel.getDesign_no(), quantity, new SimpleDateFormat("dd/MM/yy mm:hh").format(new Date().getTime()));
                                        orderMasterModel.getOrderStatusModel().setOnMachinePending(onMachine);
                                        orderMasterModel.getOrderStatusModel().setOnReadyToDispatch(ready_to_dispatch);
                                        daoOrder.update_qty(orderMasterModel.getId(), orderMasterModel, orderHistoryModel);

                                        //insert liveOrder
                                        DaoLiveOrder daoLiveOrder = new DaoLiveOrder(activity);
                                        daoLiveOrder.updateQtyByMachineName(machine_name, quantity);

                                        addReadyToDispatch(orderMasterModel, orderMasterModel.getOrderStatusModel().getOnMachinePending());
                                    }

                                } else {
                                    if (quantity > (orderMasterModel.getOrderStatusModel().getOnMachinePending() + orderMasterModel.getOrderStatusModel().getOnMachineCompleted())) {
                                        int onMachine = 0;
                                        int onMachineCompleted = 0;
                                        int extra_order = quantity - (orderMasterModel.getOrderStatusModel().getOnMachinePending() + orderMasterModel.getOrderStatusModel().getOnMachineCompleted());
                                        int ready_to_dispatch = orderMasterModel.getOrderStatusModel().getOnReadyToDispatch() + (orderMasterModel.getOrderStatusModel().getOnMachinePending() + orderMasterModel.getOrderStatusModel().getOnMachineCompleted());

//                                        HashMap<String, Object> hashMap = new HashMap<>();
//                                        hashMap.put(Const.ON_MACHINE_PENDING, onMachine);
//                                        hashMap.put(Const.ON_MACHINE_COMPLETED, onMachineCompleted);
//                                        hashMap.put(Const.ON_READY_TO_DISPATCH, ready_to_dispatch);
//                                        daoOrder.updateOrderStatusModel(orderMasterModel.getId(), hashMap);

                                        DaoOrderHistory orderHistory = new DaoOrderHistory(activity);
                                        String id = orderHistory.getNewId();
                                        OrderHistory orderHistoryModel = new OrderHistory(id, Const.ON_MACHINE_PENDING, Const.ON_READY_TO_DISPATCH, orderMasterModel.getOrder_no(), orderMasterModel.getSub_order_no(), orderMasterModel.getShade_no(), orderMasterModel.getDesign_no(), quantity, new SimpleDateFormat("dd/MM/yy mm:hh").format(new Date().getTime()));
                                        orderMasterModel.getOrderStatusModel().setOnMachinePending(onMachine);
                                        orderMasterModel.getOrderStatusModel().setOnMachineCompleted(onMachineCompleted);
                                        orderMasterModel.getOrderStatusModel().setOnReadyToDispatch(ready_to_dispatch);
                                        daoOrder.update_qty(orderMasterModel.getId(), orderMasterModel, orderHistoryModel);

                                        //insert liveOrder
                                        DaoLiveOrder daoLiveOrder = new DaoLiveOrder(activity);
                                        daoLiveOrder.removeByMachineName(machine_name);

                                        addReadyToDispatch(orderMasterModel, orderMasterModel.getOrderStatusModel().getOnMachinePending());
                                        getOrderList(extra_order, order_no, true, dao);
                                        addExtraOrder(orderMasterModel, extra_order);

                                    } else {
                                        if (quantity <= orderMasterModel.getOrderStatusModel().getOnMachineCompleted()) {
                                            int onMachineCompleted = orderMasterModel.getOrderStatusModel().getOnMachineCompleted() - quantity;
                                            int ready_to_dispatch = orderMasterModel.getOrderStatusModel().getOnReadyToDispatch() + quantity;

//                                            HashMap<String, Object> hashMap = new HashMap<>();
//                                            hashMap.put(Const.ON_MACHINE_COMPLETED, onMachineCompleted);
//                                            hashMap.put(Const.ON_READY_TO_DISPATCH, ready_to_dispatch);
//                                            daoOrder.updateOrderStatusModel(orderMasterModel.getId(), hashMap);

                                            DaoOrderHistory orderHistory = new DaoOrderHistory(activity);
                                            String id = orderHistory.getNewId();
                                            OrderHistory orderHistoryModel = new OrderHistory(id, Const.ON_MACHINE_COMPLETED, Const.ON_READY_TO_DISPATCH, orderMasterModel.getOrder_no(), orderMasterModel.getSub_order_no(), orderMasterModel.getShade_no(), orderMasterModel.getDesign_no(), quantity, new SimpleDateFormat("dd/MM/yy mm:hh").format(new Date().getTime()));
                                            orderMasterModel.getOrderStatusModel().setOnMachineCompleted(onMachineCompleted);
                                            orderMasterModel.getOrderStatusModel().setOnReadyToDispatch(ready_to_dispatch);
                                            daoOrder.update_qty(orderMasterModel.getId(), orderMasterModel, orderHistoryModel);

                                            //insert liveOrder
                                            DaoLiveOrder daoLiveOrder = new DaoLiveOrder(activity);
                                            daoLiveOrder.updateQtyByMachineName(machine_name, quantity);

                                            addReadyToDispatch(orderMasterModel, orderMasterModel.getOrderStatusModel().getOnMachinePending());
                                        } else {
                                            int onMachine = orderMasterModel.getOrderStatusModel().getOnMachinePending() - (quantity - orderMasterModel.getOrderStatusModel().getOnMachineCompleted());
                                            int onMachineCompleted = 0;
                                            int ready_to_dispatch = orderMasterModel.getOrderStatusModel().getOnReadyToDispatch() + quantity;

//                                            HashMap<String, Object> hashMap = new HashMap<>();
//                                            hashMap.put(Const.ON_MACHINE_PENDING, onMachine);
//                                            hashMap.put(Const.ON_MACHINE_COMPLETED, onMachineCompleted);
//                                            hashMap.put(Const.ON_READY_TO_DISPATCH, ready_to_dispatch);
//                                            daoOrder.updateOrderStatusModel(orderMasterModel.getId(), hashMap);

                                            DaoOrderHistory orderHistory = new DaoOrderHistory(activity);
                                            String id = orderHistory.getNewId();
                                            OrderHistory orderHistoryModel = new OrderHistory(id, Const.ON_MACHINE_PENDING, Const.ON_READY_TO_DISPATCH, orderMasterModel.getOrder_no(), orderMasterModel.getSub_order_no(), orderMasterModel.getShade_no(), orderMasterModel.getDesign_no(), quantity, new SimpleDateFormat("dd/MM/yy mm:hh").format(new Date().getTime()));
                                            orderMasterModel.getOrderStatusModel().setOnMachinePending(onMachine);
                                            orderMasterModel.getOrderStatusModel().setOnMachineCompleted(onMachineCompleted);
                                            orderMasterModel.getOrderStatusModel().setOnReadyToDispatch(ready_to_dispatch);
                                            daoOrder.update_qty(orderMasterModel.getId(), orderMasterModel, orderHistoryModel);

                                            //insert liveOrder
                                            DaoLiveOrder daoLiveOrder = new DaoLiveOrder(activity);
                                            daoLiveOrder.updateQtyByMachineName(machine_name, quantity);

                                            addReadyToDispatch(orderMasterModel, orderMasterModel.getOrderStatusModel().getOnMachinePending());
                                        }

                                    }
                                }
                            }
                        }

                    }
                } else {
                    Log.d("TAG", "Error getting documents: ", task.getException());
                }
            }
        });
    }

    public void addOnMachineCompleted(OrderMasterModel orderMasterModel, int value) {
        String id = new DaoOrderHistory(activity).getNewId();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yy mm:hh");
        String date = simpleDateFormat.format(new Date().getTime());
        OrderHistory orderHistory = new OrderHistory(id, Const.ON_MACHINE_PENDING, Const.ON_MACHINE_COMPLETED, orderMasterModel.getOrder_no(), orderMasterModel.getSub_order_no(), orderMasterModel.getShade_no(), orderMasterModel.getDesign_no(), value, date);
        new DaoOrderHistory(activity).getReference().document(id).set(orderHistory);
    }

    public void addReadyToDispatch(OrderMasterModel orderMasterModel, int value) {
        String id = new DaoOrderHistory(activity).getNewId();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yy mm:hh");
        String date = simpleDateFormat.format(new Date().getTime());
        OrderHistory orderHistory = new OrderHistory(id, Const.ON_MACHINE_PENDING, Const.ON_READY_TO_DISPATCH, orderMasterModel.getOrder_no(), orderMasterModel.getSub_order_no(), orderMasterModel.getShade_no(), orderMasterModel.getDesign_no(), value, date);
        new DaoOrderHistory(activity).getReference().document(id).set(orderHistory);
    }

    public void addExtraOrder(OrderMasterModel orderMasterModel, int value) {
        String id = new DaoOrderHistory(activity).getNewId();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yy mm:hh");
        String date = simpleDateFormat.format(new Date().getTime());
        OrderHistory orderHistory = new OrderHistory(id, Const.ON_MACHINE_PENDING, Const.READY_STOCK, orderMasterModel.getOrder_no(), orderMasterModel.getSub_order_no(), orderMasterModel.getShade_no(), orderMasterModel.getDesign_no(), value, date);
        new DaoOrderHistory(activity).getReference().document(id).set(orderHistory);
    }

}
