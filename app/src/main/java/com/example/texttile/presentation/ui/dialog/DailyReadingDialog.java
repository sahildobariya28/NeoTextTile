package com.example.texttile.presentation.ui.dialog;

import static com.example.texttile.core.MyApplication.USER_DATA;
import static com.example.texttile.presentation.ui.util.PermissionUtil.isInsertPermissionGranted;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ArrayAdapter;

import androidx.fragment.app.DialogFragment;

import com.example.texttile.R;
import com.example.texttile.databinding.ReadingDialogBinding;
import com.example.texttile.data.dao.DAOExtraOrder;
import com.example.texttile.data.dao.DAOMachineMaster;
import com.example.texttile.data.dao.DAOOrder;
import com.example.texttile.data.dao.DAOReading;
import com.example.texttile.data.dao.DaoAuthority;
import com.example.texttile.data.dao.DaoLiveOrder;
import com.example.texttile.data.dao.DaoOrderHistory;
import com.example.texttile.presentation.ui.interfaces.AddOnSearchedItemSelectListener;
import com.example.texttile.data.model.DailyReadingModel;
import com.example.texttile.data.model.LiveOrder;
import com.example.texttile.data.model.MachineMasterModel;
import com.example.texttile.data.model.OrderHistory;
import com.example.texttile.data.model.OrderMasterModel;
import com.example.texttile.data.model.ReadyStockModel;
import com.example.texttile.data.model.UserDataModel;
import com.example.texttile.presentation.ui.lib.snackUtil.CustomSnackUtil;
import com.example.texttile.presentation.ui.util.Const;
import com.example.texttile.presentation.ui.util.DialogUtil;
import com.example.texttile.presentation.ui.util.KeyboardUtils;
import com.example.texttile.presentation.ui.util.ListShowDialog;
import com.example.texttile.presentation.ui.util.PermissionState;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DailyReadingDialog extends DialogFragment implements AddOnSearchedItemSelectListener {


    final Calendar myCalendar = Calendar.getInstance();
    ArrayList<String> order_no_list = new ArrayList<>();
    ArrayList<OrderMasterModel> orderMasterModelList = new ArrayList<>();
    ArrayList<String> machineList = new ArrayList<>();
    String[] category = {"Select Reading Type", "On Rolling", "On Cutout"};


    Dialog dialog;
    ReadingDialogBinding binding;

    DAOReading daoReading;

    ListShowDialog orderListDialog, machineListDialog;
    final int ORDER_REQUEST = 1, MACHINE_REQUEST = 2;

    int ON_ROLLING = 1, ON_CUTOUT = 2;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        daoReading = new DAOReading(getActivity());
    }

    @Nonnull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        dialog = new Dialog(getActivity());
        binding = ReadingDialogBinding.inflate(getLayoutInflater());
        dialog.getWindow().setWindowAnimations(R.style.DialogAnimation);
        dialog.setContentView(binding.getRoot());
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        KeyboardUtils.addKeyboardToggleListener(getActivity(), isVisible -> {
            if (isVisible) {
                binding.guideline3.setGuidelinePercent(0.10f);
                binding.guideline4.setGuidelinePercent(0.90f);
            } else {
                binding.guideline3.setGuidelinePercent(0.25f);
                binding.guideline4.setGuidelinePercent(0.75f);
            }
        });

        ArrayAdapter aa = new ArrayAdapter(getContext(), R.layout.white_simple_spinner_item, category);
        aa.setDropDownViewResource(R.layout.white_simple_spinner_dropdown_item);
        binding.optionType.setAdapter(aa);
        setAutoAdapter();

        binding.textOrderNo.setOnClickListener(view -> {
            if (orderListDialog != null) {
                orderListDialog.show_dialog();
            } else {
                orderListDialog = new ListShowDialog(getActivity(), order_no_list, DailyReadingDialog.this, ORDER_REQUEST);
                orderListDialog.show_dialog();
            }
        });

        binding.editCurrentDate.setOnClickListener(view -> {

            DatePickerDialog.OnDateSetListener date1 = (view1, year, month, day) -> {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, day);
                String myFormat = "dd/MM/yy";
                SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.US);
                binding.editCurrentDate.setText(dateFormat.format(myCalendar.getTime()));
            };
            DatePickerDialog mDatePicker = new DatePickerDialog(getContext(), date1, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));

            mDatePicker.getDatePicker().setMaxDate(System.currentTimeMillis());
            mDatePicker.show();
        });
        binding.textMachine.setOnClickListener(view -> {
            if (machineListDialog != null) {
                machineListDialog.show_dialog();
            } else {
                machineListDialog = new ListShowDialog(getActivity(), machineList, DailyReadingDialog.this, MACHINE_REQUEST);
                machineListDialog.show_dialog();
            }
        });

        binding.btnAdd.setOnClickListener(view -> {
            addArtist();
        });
        binding.btnClose.setOnClickListener(view -> {
            dialog.dismiss();
        });

        return dialog;
    }

    private void setAutoAdapter() {
        USER_DATA.observe(getActivity(), userDataModel -> {

            new DaoAuthority(getActivity()).getReference().whereEqualTo("u_name", userDataModel.getU_name()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NotNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            UserDataModel userModel = document.toObject(UserDataModel.class);
                            if (userModel.getType().equals(Const.ADMIN)) {
                                binding.editManagerName.setText(userModel.getF_name());
                            } else {
                                binding.editManagerName.setText(userModel.getF_name());
                            }
                        }
                    } else {
                        Log.d("fkdskfjkew2", "Error getting documents: ", task.getException());
                    }
                }
            });

//            new DaoAuthority(getActivity()).getReference().orderByChild("u_name").equalTo(userDataModel.getU_name()).addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@Nonnull DataSnapshot snapshot) {
//                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
//                        UserDataModel userModel = postSnapshot.getValue(UserDataModel.class);
//                        if (userModel.getType().equals(Const.ADMIN)) {
//                            binding.editManagerName.setText(userModel.getF_name());
//                        } else {
//                            binding.editManagerName.setText(userModel.getF_name());
//                        }
//                    }
//                }
//                @Override
//                public void onCancelled(@Nonnull DatabaseError error) {
//
//                }
//            });

        });


        getMachineList("");
        getOrderList();
    }

    public void getMachineList(String filter) {

        ArrayList<String> liveOrderMachineName = new ArrayList<>();


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        new DaoLiveOrder(getActivity()).getReference().addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    return;
                }

                machineList.clear();
                for (QueryDocumentSnapshot document : snapshot) {
                    LiveOrder liveOrder = document.toObject(LiveOrder.class);
                    liveOrderMachineName.add(liveOrder.getMachine_name());
                }

                new DAOMachineMaster(getActivity()).getReference().get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        machineList.clear();
                        for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                            MachineMasterModel machineMasterModel = documentSnapshot.toObject(MachineMasterModel.class);
                            for (int i = 0; i < liveOrderMachineName.size(); i++) {
                                if (machineMasterModel.getMachine_name().equals(liveOrderMachineName.get(i))) {
                                    if (machineMasterModel.getMachine_name().contains(filter)) {
                                        machineList.add(machineMasterModel.getMachine_name());
                                    }
                                }
                            }
                        }
                    }
                }).addOnFailureListener(e -> {
// handle error
                });

//                new DAOMachineMaster(getActivity()).getReference().addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@Nonnull DataSnapshot snapshot) {
//
//                        machineList.clear();
//                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {
//                            MachineMasterModel machineMasterModel = postSnapshot.getValue(MachineMasterModel.class);
//                            for (int i = 0; i < liveOrderMachineName.size(); i++) {
//                                if (machineMasterModel.getMachine_name().equals(liveOrderMachineName.get(i))) {
//                                    if (machineMasterModel.getMachine_name().contains(filter)) {
//                                        machineList.add(machineMasterModel.getMachine_name());
//                                    }
//                                }
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@Nonnull DatabaseError error) {
//
//                    }
//                });
            }
        });

//        new DaoLiveOrder(getActivity()).getReference().addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@Nonnull DataSnapshot snapshot) {
//                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
//                    LiveOrder liveOrder = postSnapshot.getValue(LiveOrder.class);
//                    liveOrderMachineName.add(liveOrder.getMachine_name());
//                }
//                Log.d("dfjlsdjriw3740", "onDataChange: " + liveOrderMachineName);
//
//                new DAOMachineMaster(getActivity()).getReference().addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@Nonnull DataSnapshot snapshot) {
//
//                        machineList.clear();
//                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {
//                            MachineMasterModel machineMasterModel = postSnapshot.getValue(MachineMasterModel.class);
//                            for (int i = 0; i < liveOrderMachineName.size(); i++) {
//                                if (machineMasterModel.getMachine_name().equals(liveOrderMachineName.get(i))) {
//                                    if (machineMasterModel.getMachine_name().contains(filter)) {
//                                        machineList.add(machineMasterModel.getMachine_name());
//                                    }
//                                }
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@Nonnull DatabaseError error) {
//
//                    }
//                });
//
//            }
//
//            @Override
//            public void onCancelled(@Nonnull DatabaseError error) {
//
//            }
//        });
    }

    private void getOrderList() {

        new DAOOrder(getActivity()).getReference().addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w("dfsdf", "Listen failed.", e);
                            return;
                        }
                        order_no_list.clear();
                        orderMasterModelList.clear();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            OrderMasterModel orderMasterModel = document.toObject(OrderMasterModel.class);
                            if (orderMasterModel.getOrderStatusModel().getOnMachinePending() > 0) {
                                order_no_list.add(orderMasterModel.getSub_order_no());
                                orderMasterModelList.add(orderMasterModel);
                            } else if (orderMasterModel.getOrderStatusModel().getOnMachineCompleted() > 0) {
                                order_no_list.add(orderMasterModel.getSub_order_no());
                                orderMasterModelList.add(orderMasterModel);
                            }
                        }
                        if (orderListDialog != null) {
                            orderListDialog.update_list(order_no_list);
                        } else {
                            orderListDialog = new ListShowDialog(getActivity(), order_no_list, DailyReadingDialog.this, ORDER_REQUEST);
                        }
                    }
                });

//        new DAOOrder(getActivity()).getReference().addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@Nonnull DataSnapshot dataSnapshot) {
//
//                order_no_list.clear();
//                orderMasterModelList.clear();
//                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
//                    OrderMasterModel orderMasterModel = postSnapshot.getValue(OrderMasterModel.class);
//                    if (orderMasterModel.getOrderStatusModel().getOnMachinePending() > 0) {
//                        order_no_list.add(orderMasterModel.getSub_order_no());
//                        orderMasterModelList.add(orderMasterModel);
//                    } else if (orderMasterModel.getOrderStatusModel().getOnMachineCompleted() > 0) {
//                        order_no_list.add(orderMasterModel.getSub_order_no());
//                        orderMasterModelList.add(orderMasterModel);
//                    }
//                }
//                if (orderListDialog != null) {
//                    orderListDialog.update_list(order_no_list);
//                } else {
//                    orderListDialog = new ListShowDialog(getActivity(), order_no_list, DailyReadingDialog.this, ORDER_REQUEST);
//                }
//            }
//
//            @Override
//            public void onCancelled(@Nonnull DatabaseError databaseError) {
//
//            }
//        });
    }

    private void addArtist() {
        String machine_name = binding.editCurrentDate.getText().toString().trim();
        String currentDate = binding.editCurrentDate.getText().toString().trim();
        String managerName = binding.editManagerName.getText().toString().trim();
        String orderNo = binding.textOrderNo.getText().toString().trim();
        String readingQty = binding.editDailyReadingQty.getText().toString().trim();

        if (TextUtils.isEmpty(machine_name)) {
            new CustomSnackUtil().showSnack(getActivity(), "Please enter a Machine Name", R.drawable.error_msg_icon);
        } else if (TextUtils.isEmpty(currentDate)) {
            new CustomSnackUtil().showSnack(getActivity(), "Please enter a Current Date", R.drawable.error_msg_icon);
        } else if (TextUtils.isEmpty(managerName)) {
            new CustomSnackUtil().showSnack(getActivity(), "Please enter a Manager Name", R.drawable.error_msg_icon);
        } else if (TextUtils.isEmpty(orderNo)) {
            new CustomSnackUtil().showSnack(getActivity(), "Please enter a Order No", R.drawable.error_msg_icon);
        } else if (binding.optionType.getSelectedItemPosition() == 0) {
            new CustomSnackUtil().showSnack(getActivity(), "Please enter a Reading Type", R.drawable.error_msg_icon);
        } else if (TextUtils.isEmpty(readingQty)) {
            new CustomSnackUtil().showSnack(getActivity(), "Please enter a Reading Qty", R.drawable.error_msg_icon);
        } else {

            String id = daoReading.getId();
            DialogUtil.showProgressDialog(getActivity());

            DailyReadingModel dailyReadingModel = new DailyReadingModel(id, currentDate, managerName, machine_name, orderNo, Integer.parseInt(readingQty));
            updateOrderStatus(orderNo, Integer.parseInt(readingQty), machine_name);
            daoReading.insert(id, dailyReadingModel).addOnSuccessListener(runnable -> {
                DialogUtil.showSuccessDialog(getActivity(), false);
                dismiss();
            });
        }

    }

    private void updateOrderStatus(String order_no, int quantity, String machine_name) {
        DAOOrder daoOrder = new DAOOrder(getActivity());

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("orders").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        OrderMasterModel orderMasterModel = document.toObject(OrderMasterModel.class);

                        if (orderMasterModel.getSub_order_no().equals(order_no)) {
                            if (binding.optionType.getSelectedItemPosition() == ON_ROLLING) {
                                if (quantity > orderMasterModel.getOrderStatusModel().getOnMachinePending()) {
                                    int onMachine = 0;
                                    int extra_order = quantity - orderMasterModel.getOrderStatusModel().getOnMachinePending();
                                    int onMachineCompleted = orderMasterModel.getOrderStatusModel().getOnMachineCompleted() + orderMasterModel.getOrderStatusModel().getOnMachinePending();

//                                    HashMap<String, Object> hashMap = new HashMap<>();
//                                    hashMap.put(Const.ON_MACHINE_PENDING, onMachine);
//                                    hashMap.put("onMachineCompleted", onMachineCompleted);
//                                    daoOrder.updateOrderStatusModel(orderMasterModel.getId(), hashMap);
                                    DaoOrderHistory orderHistory = new DaoOrderHistory(getActivity());
                                    String id = orderHistory.getNewId();
                                    OrderHistory orderHistoryModel = new OrderHistory(id, Const.ON_MACHINE_PENDING, Const.ON_MACHINE_COMPLETED, orderMasterModel.getOrder_no(), orderMasterModel.getSub_order_no(), orderMasterModel.getShade_no(), orderMasterModel.getDesign_no(), quantity, new SimpleDateFormat("dd/MM/yy mm:hh").format(new Date().getTime()));
                                    orderMasterModel.getOrderStatusModel().setOnMachinePending(onMachine);

                                    orderMasterModel.getOrderStatusModel().setOnMachineCompleted(onMachineCompleted);
                                    daoOrder.update_qty(orderMasterModel.getId(),orderMasterModel, orderHistoryModel);

                                    addOnMachineCompleted(orderMasterModel, orderMasterModel.getOrderStatusModel().getOnMachinePending());
                                    getOrderList(extra_order, order_no, true);
                                    addExtraOrder(orderMasterModel, extra_order);
                                } else {
                                    int onMachine = orderMasterModel.getOrderStatusModel().getOnMachinePending() - quantity;
                                    int onMachineCompleted = orderMasterModel.getOrderStatusModel().getOnMachineCompleted() + quantity;

                                    DaoOrderHistory orderHistory = new DaoOrderHistory(getActivity());
                                    String id = orderHistory.getNewId();
                                    OrderHistory orderHistoryModel = new OrderHistory(id, Const.ON_MACHINE_PENDING, Const.ON_MACHINE_COMPLETED, orderMasterModel.getOrder_no(), orderMasterModel.getSub_order_no(), orderMasterModel.getShade_no(), orderMasterModel.getDesign_no(), quantity, new SimpleDateFormat("dd/MM/yy mm:hh").format(new Date().getTime()));
                                    orderMasterModel.getOrderStatusModel().setOnMachinePending(onMachine);
                                    orderMasterModel.getOrderStatusModel().setOnMachineCompleted(onMachineCompleted);
                                    daoOrder.update_qty(orderMasterModel.getId(),orderMasterModel, orderHistoryModel);


//                                    HashMap<String, Object> hashMap = new HashMap<>();
//                                    hashMap.put(Const.ON_MACHINE_PENDING, onMachine);
//                                    hashMap.put("onMachineCompleted", onMachineCompleted);
//                                    daoOrder.updateOrderStatusModel(orderMasterModel.getId(), hashMap);

                                    addOnMachineCompleted(orderMasterModel, orderMasterModel.getOrderStatusModel().getOnMachinePending());

                                }
                            } else if (binding.optionType.getSelectedItemPosition() == ON_CUTOUT) {
                                if (orderMasterModel.getOrderStatusModel().getOnMachineCompleted() == 0) {
                                    if (quantity > orderMasterModel.getOrderStatusModel().getOnMachinePending()) {
                                        int onMachine = 0;
                                        int extra_order = quantity - orderMasterModel.getOrderStatusModel().getOnMachinePending();
                                        int ready_to_dispatch = orderMasterModel.getOrderStatusModel().getOnReadyToDispatch() + orderMasterModel.getOrderStatusModel().getOnMachinePending();

//                                        HashMap<String, Object> hashMap = new HashMap<>();
//                                        hashMap.put(Const.ON_MACHINE_PENDING, onMachine);
//                                        hashMap.put(Const.ON_READY_TO_DISPATCH, ready_to_dispatch);
//                                        daoOrder.updateOrderStatusModel(orderMasterModel.getId(), hashMap);

                                        DaoOrderHistory orderHistory = new DaoOrderHistory(getActivity());
                                        String id = orderHistory.getNewId();
                                        OrderHistory orderHistoryModel = new OrderHistory(id, Const.ON_MACHINE_PENDING, Const.ON_READY_TO_DISPATCH, orderMasterModel.getOrder_no(), orderMasterModel.getSub_order_no(), orderMasterModel.getShade_no(), orderMasterModel.getDesign_no(), quantity, new SimpleDateFormat("dd/MM/yy mm:hh").format(new Date().getTime()));
                                        orderMasterModel.getOrderStatusModel().setOnMachinePending(onMachine);
                                        orderMasterModel.getOrderStatusModel().setOnReadyToDispatch(ready_to_dispatch);
                                        daoOrder.update_qty(orderMasterModel.getId(),orderMasterModel, orderHistoryModel);

                                        //insert liveOrder
                                        new DaoLiveOrder(getActivity()).removeByMachineName(machine_name);

                                        addReadyToDispatch(orderMasterModel, orderMasterModel.getOrderStatusModel().getOnMachinePending());
                                        getOrderList(extra_order, order_no, true);
                                        addExtraOrder(orderMasterModel, extra_order);
                                    } else {
                                        int onMachine = orderMasterModel.getOrderStatusModel().getOnMachinePending() - quantity;
                                        int ready_to_dispatch = orderMasterModel.getOrderStatusModel().getOnReadyToDispatch() + quantity;

//                                        HashMap<String, Object> hashMap = new HashMap<>();
//                                        hashMap.put(Const.ON_MACHINE_PENDING, onMachine);
//                                        hashMap.put(Const.ON_READY_TO_DISPATCH, ready_to_dispatch);
//                                        daoOrder.updateOrderStatusModel(orderMasterModel.getId(), hashMap);

                                        DaoOrderHistory orderHistory = new DaoOrderHistory(getActivity());
                                        String id = orderHistory.getNewId();
                                        OrderHistory orderHistoryModel = new OrderHistory(id, Const.ON_MACHINE_PENDING, Const.ON_READY_TO_DISPATCH, orderMasterModel.getOrder_no(), orderMasterModel.getSub_order_no(), orderMasterModel.getShade_no(), orderMasterModel.getDesign_no(), quantity, new SimpleDateFormat("dd/MM/yy mm:hh").format(new Date().getTime()));
                                        orderMasterModel.getOrderStatusModel().setOnMachinePending(onMachine);
                                        orderMasterModel.getOrderStatusModel().setOnReadyToDispatch(ready_to_dispatch);
                                        daoOrder.update_qty(orderMasterModel.getId(),orderMasterModel, orderHistoryModel);

                                        //insert liveOrder

                                        new DaoLiveOrder(getActivity()).updateQtyByMachineName(machine_name, quantity);

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

                                        DaoOrderHistory orderHistory = new DaoOrderHistory(getActivity());
                                        String id = orderHistory.getNewId();
                                        OrderHistory orderHistoryModel = new OrderHistory(id, Const.ON_MACHINE_PENDING, Const.ON_READY_TO_DISPATCH, orderMasterModel.getOrder_no(), orderMasterModel.getSub_order_no(), orderMasterModel.getShade_no(), orderMasterModel.getDesign_no(), quantity, new SimpleDateFormat("dd/MM/yy mm:hh").format(new Date().getTime()));
                                        orderMasterModel.getOrderStatusModel().setOnMachinePending(onMachine);
                                        orderMasterModel.getOrderStatusModel().setOnMachineCompleted(onMachineCompleted);
                                        orderMasterModel.getOrderStatusModel().setOnReadyToDispatch(ready_to_dispatch);
                                        daoOrder.update_qty(orderMasterModel.getId(), orderMasterModel, orderHistoryModel);

                                        //insert liveOrder
                                        new DaoLiveOrder(getActivity()).removeByMachineName(machine_name);

                                        addReadyToDispatch(orderMasterModel, orderMasterModel.getOrderStatusModel().getOnMachinePending());
                                        getOrderList(extra_order, order_no, true);
                                        addExtraOrder(orderMasterModel, extra_order);

                                    } else {
                                        if (quantity <= orderMasterModel.getOrderStatusModel().getOnMachineCompleted()) {
                                            int onMachineCompleted = orderMasterModel.getOrderStatusModel().getOnMachineCompleted() - quantity;
                                            int ready_to_dispatch = orderMasterModel.getOrderStatusModel().getOnReadyToDispatch() + quantity;

//                                            HashMap<String, Object> hashMap = new HashMap<>();
//                                            hashMap.put(Const.ON_MACHINE_COMPLETED, onMachineCompleted);
//                                            hashMap.put(Const.ON_READY_TO_DISPATCH, ready_to_dispatch);
//                                            daoOrder.updateOrderStatusModel(orderMasterModel.getId(), hashMap);

                                            DaoOrderHistory orderHistory = new DaoOrderHistory(getActivity());
                                            String id = orderHistory.getNewId();
                                            OrderHistory orderHistoryModel = new OrderHistory(id, Const.ON_MACHINE_COMPLETED, Const.ON_READY_TO_DISPATCH, orderMasterModel.getOrder_no(), orderMasterModel.getSub_order_no(), orderMasterModel.getShade_no(), orderMasterModel.getDesign_no(), quantity, new SimpleDateFormat("dd/MM/yy mm:hh").format(new Date().getTime()));
                                            orderMasterModel.getOrderStatusModel().setOnMachineCompleted(onMachineCompleted);
                                            orderMasterModel.getOrderStatusModel().setOnReadyToDispatch(ready_to_dispatch);
                                            daoOrder.update_qty(orderMasterModel.getId(), orderMasterModel, orderHistoryModel);

                                            //insert liveOrder
                                            new DaoLiveOrder(getActivity()).updateQtyByMachineName(machine_name, quantity);

                                            addReadyToDispatch(orderMasterModel, orderMasterModel.getOrderStatusModel().getOnMachinePending());
                                        } else {
                                            int onMachine = orderMasterModel.getOrderStatusModel().getOnMachinePending() - (quantity - orderMasterModel.getOrderStatusModel().getOnMachineCompleted());
                                            int onMachineCompleted = 0;
                                            int ready_to_dispatch = orderMasterModel.getOrderStatusModel().getOnReadyToDispatch() + quantity;

                                            DaoOrderHistory orderHistory = new DaoOrderHistory(getActivity());
                                            String id = orderHistory.getNewId();
                                            OrderHistory orderHistoryModel = new OrderHistory(id, Const.ON_MACHINE_PENDING, Const.ON_READY_TO_DISPATCH, orderMasterModel.getOrder_no(), orderMasterModel.getSub_order_no(), orderMasterModel.getShade_no(), orderMasterModel.getDesign_no(), quantity, new SimpleDateFormat("dd/MM/yy mm:hh").format(new Date().getTime()));
                                            orderMasterModel.getOrderStatusModel().setOnMachinePending(onMachine);
                                            orderMasterModel.getOrderStatusModel().setOnMachineCompleted(onMachineCompleted);
                                            orderMasterModel.getOrderStatusModel().setOnReadyToDispatch(ready_to_dispatch);
                                            daoOrder.update_qty(orderMasterModel.getId(), orderMasterModel, orderHistoryModel);
                                            
//                                            HashMap<String, Object> hashMap = new HashMap<>();
//                                            hashMap.put(Const.ON_MACHINE_PENDING, onMachine);
//                                            hashMap.put(Const.ON_MACHINE_COMPLETED, onMachineCompleted);
//                                            hashMap.put(Const.ON_READY_TO_DISPATCH, ready_to_dispatch);
//                                            daoOrder.updateOrderStatusModel(orderMasterModel.getId(), hashMap);

                                            //insert liveOrder
                                            new DaoLiveOrder(getActivity()).updateQtyByMachineName(machine_name, quantity);

                                            addReadyToDispatch(orderMasterModel, orderMasterModel.getOrderStatusModel().getOnMachinePending());
                                        }

                                    }
                                }
                            }
                        }
                        // Do something with the OrderMasterModel object
                    }
                } else {
                    Log.d("TAG", "Error getting documents: ", task.getException());
                }
            }
        });
    }

    public void addOnMachineCompleted(OrderMasterModel orderMasterModel, int value) {
        String id = new DaoOrderHistory(getActivity()).getNewId();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yy mm:hh");
        String date = simpleDateFormat.format(new Date().getTime());
        OrderHistory orderHistory = new OrderHistory(id, Const.ON_MACHINE_PENDING, Const.ON_MACHINE_COMPLETED, orderMasterModel.getOrder_no(), orderMasterModel.getSub_order_no(), orderMasterModel.getShade_no(), orderMasterModel.getDesign_no(), value, date);
        new DaoOrderHistory(getActivity()).getReference().document(id).set(orderHistory);
    }

    public void addReadyToDispatch(OrderMasterModel orderMasterModel, int value) {
        String id = new DaoOrderHistory(getActivity()).getNewId();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yy mm:hh");
        String date = simpleDateFormat.format(new Date().getTime());
        OrderHistory orderHistory = new OrderHistory(id, Const.ON_MACHINE_PENDING, Const.ON_READY_TO_DISPATCH, orderMasterModel.getOrder_no(), orderMasterModel.getSub_order_no(), orderMasterModel.getShade_no(), orderMasterModel.getDesign_no(), value, date);
        new DaoOrderHistory(getActivity()).getReference().document(id).set(orderHistory);
    }

    public void addExtraOrder(OrderMasterModel orderMasterModel, int value) {
        String id = new DaoOrderHistory(getActivity()).getNewId();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yy mm:hh");
        String date = simpleDateFormat.format(new Date().getTime());
        OrderHistory orderHistory = new OrderHistory(id, Const.ON_MACHINE_PENDING, Const.READY_STOCK, orderMasterModel.getOrder_no(), orderMasterModel.getSub_order_no(), orderMasterModel.getShade_no(), orderMasterModel.getDesign_no(), value, date);
        new DaoOrderHistory(getActivity()).getReference().document(id).set(orderHistory);
    }


    private void getOrderList(int extra_quantity, String order_no, boolean isAddInDatabase) {

        new DAOOrder(getActivity()).getReference().get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {
                    order_no_list.clear();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        OrderMasterModel orderMasterModel = document.toObject(OrderMasterModel.class);
                        // Do something with the OrderMasterModel object
                        if (orderMasterModel.getSub_order_no().equals(order_no) && isAddInDatabase) {

                            DAOExtraOrder daoExtraOrder = new DAOExtraOrder(getActivity());
                            String id = daoReading.getId();
                            ReadyStockModel extraOrder = new ReadyStockModel(id, orderMasterModel.getDesign_no(), orderMasterModel.getShade_no(), new SimpleDateFormat("dd/MM/yyyy").format(new Date()), extra_quantity);
                            daoExtraOrder.insert(id, extraOrder).addOnSuccessListener(runnable -> {
                                DialogUtil.showSuccessDialog(getActivity(), false);

                            });

                        }
                        order_no_list.add(orderMasterModel.getSub_order_no());
                    }
                } else {
                    Log.d("TAG", "Error getting documents: ", task.getException());
                }
            }
        });

//        new DAOOrder(getActivity()).getReference().addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@Nonnull DataSnapshot dataSnapshot) {
//
//                order_no_list.clear();
//
//                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
//                    OrderMasterModel orderMasterModel = postSnapshot.getValue(OrderMasterModel.class);
//                    if (orderMasterModel.getSub_order_no().equals(order_no) && isAddInDatabase) {
//
//                        DAOExtraOrder daoExtraOrder = new DAOExtraOrder(getActivity());
//                        String id = daoReading.getId();
//                        ReadyStockModel extraOrder = new ReadyStockModel(id, orderMasterModel.getDesign_no(), orderMasterModel.getShade_no(), new SimpleDateFormat("dd/MM/yyyy").format(new Date()), extra_quantity);
//                        daoExtraOrder.add(id, extraOrder).addOnSuccessListener(runnable -> {
//                            DialogUtil.showSuccessDialog(getActivity(), false);
//
//                        });
//
//                    }
//                    order_no_list.add(orderMasterModel.getSub_order_no());
//                }
//            }
//
//            @Override
//            public void onCancelled(@Nonnull DatabaseError databaseError) {
//
//            }
//        });
    }

    @Override
    public void onSelected(int position, String name, int request_code) {
        if (request_code == MACHINE_REQUEST) {
            binding.textMachine.setText(name);
            getMachineList(name);
        } else if (request_code == ORDER_REQUEST) {
            binding.textOrderNo.setText(name);
        }
    }

    @Override
    public void onAddButtonClicked(int position, String name, int request_code) {
        if (request_code == MACHINE_REQUEST) {
            if (isInsertPermissionGranted(PermissionState.MACHINE_MASTER.getValue())) {
                MachineMasterDialog machineMasterDialog = new MachineMasterDialog();
                Bundle bundle = new Bundle();
                bundle.putString("tracker", "add");
                bundle.putString("machine_name", name);
                machineMasterDialog.setArguments(bundle);
                machineMasterDialog.show(getChildFragmentManager(), "My Fragment");
            } else {
                DialogUtil.showAccessDeniedDialog(getActivity());
            }

        } else if (request_code == ORDER_REQUEST) {
            new CustomSnackUtil().showSnack(getActivity(), "Please Create Order Manually", R.drawable.icon_warning);
        }
    }

    @Override
    public void onMultiSelected(ArrayList<String> shadeList, int request_code) {

    }
}
