package com.example.texttile.presentation.ui.activity;

import static com.example.texttile.presentation.ui.util.PermissionUtil.isInsertPermissionGranted;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.texttile.R;
import com.example.texttile.presentation.ui.adapter.MachineReadingAdapter;
import com.example.texttile.presentation.ui.dialog.DailyReadingDialog;
import com.example.texttile.data.dao.DAOMachineMaster;
import com.example.texttile.data.dao.DAOOrder;
import com.example.texttile.data.dao.DaoAllotProgram;
import com.example.texttile.data.model.AllotProgramModel;
import com.example.texttile.data.model.DailyReadingModel;
import com.example.texttile.data.model.MachineMasterModel;
import com.example.texttile.data.model.OrderMasterModel;
import com.example.texttile.data.model.ReadyStockModel;
import com.example.texttile.presentation.ui.util.DialogUtil;
import com.example.texttile.presentation.ui.util.PermissionState;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;

public class ScanQR implements MachineReadingAdapter.MachineReadingClickListener {

    String qrCode, barcode_formate;
    boolean isDialogVisible = false;
    String[] category = {"On Rolling", "On Cutout"};
    int spinner_position = 0;
    ReadyStockModel extraOrder = new ReadyStockModel();
    ArrayList<String> allot_order_no = new ArrayList<>();
    ArrayList<String> order_no_list = new ArrayList<>();
    Dialog dialog;
    Activity activity;
    FragmentManager fragmentManager;


    protected void onCreate(FragmentManager manager, Activity activity, String qrCode, String barcode_formate) {
        fragmentManager = manager;
        this.activity = activity;
        this.qrCode = qrCode;
        this.barcode_formate = barcode_formate;

        dialog = new Dialog(activity);


        DialogUtil.showProgressDialog(activity);

        if (qrCode.startsWith("@slip:")) {
            new DAOOrder(activity).getReference().addSnapshotListener((querySnapshot, error) -> {
                boolean isMatch = false;
                if (error != null) {
                    return;
                }
                for (QueryDocumentSnapshot document : querySnapshot) {
                    OrderMasterModel allotProgramModel = document.toObject(OrderMasterModel.class);
                    if (qrCode.equals("@slip:" + allotProgramModel.getSub_order_no())) {
                        isMatch = true;
                        showReadingBySubOrderNoDialog();
                    }
                }
                if (!isMatch) {
                    showNoQrMatchDialog();
                }
            });

//            new DAOOrder(activity).getReference().addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@Nonnull DataSnapshot dataSnapshot) {
//                    boolean isMatch = false;
//                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
//                        OrderMasterModel allotProgramModel = postSnapshot.getValue(OrderMasterModel.class);
//                        if (qrCode.equals("@slip:" + allotProgramModel.getSub_order_no())) {
//                            isMatch = true;
//                            showReadingBySubOrderNoDialog();
//                        }
//                    }
//                    if (!isMatch) {
//                        showNoQrMatchDialog();
//                    }
//                }
//
//                @Override
//                public void onCancelled(@Nonnull DatabaseError error) {
//
//                }
//            });

        } else if (qrCode.startsWith("@MachineName:")) {

            new DAOMachineMaster(activity).getReference().get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        MachineMasterModel machineMasterModel = documentSnapshot.toObject(MachineMasterModel.class);
                        if (qrCode.equals("@MachineName:" + machineMasterModel.getMachine_name())) {
                            showReadingByMachineNoDialog();
                        }
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });

        } else {
            showNoQrMatchDialog();
        }
    }

    public void showReadingByMachineNoDialog() {
        String machineName = qrCode.replace("@MachineName:", "");
        DialogUtil.hideProgressDialog();
        isDialogVisible = true;
        dialog.setContentView(R.layout.dialog_machine_reading);
        dialog.setCancelable(true);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        RecyclerView recyclerView = dialog.findViewById(R.id.recyclerView);

        getAllotList(machineName, recyclerView, dialog);


        dialog.setOnDismissListener(dialogInterface -> {
            isDialogVisible = false;
        });
    }


    public void showNoQrMatchDialog() {
        DialogUtil.hideProgressDialog();
        isDialogVisible = true;

        dialog.setContentView(R.layout.dialog_not_qr_match);
        dialog.setCancelable(true);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        CardView btn_cancel = dialog.findViewById(R.id.btn_cancel);

        btn_cancel.setOnClickListener(view -> {
            dialog.dismiss();
        });

        dialog.setOnDismissListener(dialogInterface -> {
            isDialogVisible = false;
        });
        dialog.show();
    }

    public void showReadingBySubOrderNoDialog() {
        DialogUtil.hideProgressDialog();
        String orderNo = qrCode.replace("@slip:", "").trim();

        if (isInsertPermissionGranted(PermissionState.DAILY_READING.getValue())) {
            DailyReadingDialog dailyReadingDialog = new DailyReadingDialog();
            Bundle bundle = new Bundle();
            bundle.putString("party_name", orderNo);
            dailyReadingDialog.setArguments(bundle);
            dailyReadingDialog.show(fragmentManager, "Party Dialog");
        } else {
            DialogUtil.showAccessDeniedDialog(activity);
        }
    }


    public void isOrderValid(String orderNo, DailyReadingModel dailyReadingModel, DatabaseReference databaseReference) {
        new DAOOrder(activity).getReference().whereEqualTo("sub_order_no", orderNo).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ArrayList<OrderMasterModel> orderMasterModels = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    OrderMasterModel orderMasterModel = document.toObject(OrderMasterModel.class);
                    orderMasterModels.add(orderMasterModel);
                }
                if (orderMasterModels.size() != 0) {
                    new DAOOrder(activity).getReference().document(dailyReadingModel.getId()).set(dailyReadingModel)
                            .addOnSuccessListener(aVoid -> DialogUtil.showSuccessDialog(activity, true))
                            .addOnFailureListener(e -> DialogUtil.showErrorDialog(activity));
                } else {
                    DialogUtil.emptyRecordDialog(activity);
                }
            } else {
                Log.d("sdfsdf", "Error getting documents: ", task.getException());
            }
        });
    }

    /*private void updateOrderStatus(String order_no, int quantity) {

        new DAOOrder(activity).getList().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@Nonnull DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    OrderMasterModel orderMasterModel = postSnapshot.getValue(OrderMasterModel.class);
                    if (orderMasterModel.getSub_order_no().equals(order_no)) {
                        if (quantity > orderMasterModel.getOrderStatusModel().getOnMachinePending()) {


                            if (spinner_position == 1) {
                                int onMachine = 0;
                                int extra_order = quantity - orderMasterModel.getOrderStatusModel().getOnMachinePending();
                                int onMachineCompleted = orderMasterModel.getOrderStatusModel().getOnMachineCompleted() + orderMasterModel.getOrderStatusModel().getOnMachinePending();
                                new DAOOrder(activity).getList().child(orderMasterModel.getId()).child("orderStatusModel").child(Const.ON_MACHINE_PENDING).setValue(onMachine);
                                new DAOOrder(activity).getList().child(orderMasterModel.getId()).child("orderStatusModel").child(Const.ON_MACHINE_COMPLETED).setValue(onMachineCompleted);
                                extraOrder.setCurrent_date(new SimpleDateFormat("dd/MM/yyyy ss:mm:hh").format(new Date()));
                                extraOrder.setQuantity(extra_order);
                                getOrderList(order_no);
                                deleteByAllot(order_no);
                            } else if (spinner_position == 2) {
                                int onMachine = 0;
                                int extra_order = quantity - orderMasterModel.getOrderStatusModel().getOnMachinePending();
                                int ready_to_dispatch = orderMasterModel.getOrderStatusModel().getOnReadyToDispatch() + orderMasterModel.getOrderStatusModel().getOnMachinePending();
                                new DAOOrder(activity).getList().child(orderMasterModel.getId()).child("orderStatusModel").child(Const.ON_MACHINE_PENDING).setValue(onMachine);
                                new DAOOrder(activity).getList().child(orderMasterModel.getId()).child("orderStatusModel").child(Const.ON_READY_TO_DISPATCH).setValue(ready_to_dispatch);
                                extraOrder.setCurrent_date(new SimpleDateFormat("dd/MM/yyyy ss:mm:hh").format(new Date()));
                                extraOrder.setQuantity(extra_order);
                                getOrderList(order_no);
                                deleteByAllot(order_no);
                            } else {
                                new CustomSnackUtil().showSnack(activity, "Please Select Reading Type", R.drawable.error_msg_icon);
                            }


                        } else {
                            if (spinner_position == 1) {
                                int onMachine = orderMasterModel.getOrderStatusModel().getOnMachinePending() - quantity;
                                int onMachineCompleted = orderMasterModel.getOrderStatusModel().getOnMachineCompleted() + quantity;
                                new DAOOrder(activity).getList().child(orderMasterModel.getId()).child("orderStatusModel").child(Const.ON_MACHINE_PENDING).setValue(onMachine);
                                new DAOOrder(activity).getList().child(orderMasterModel.getId()).child("orderStatusModel").child(Const.ON_MACHINE_COMPLETED).setValue(onMachineCompleted);
                            } else if (spinner_position == 2) {
                                int onMachine = orderMasterModel.getOrderStatusModel().getOnMachinePending() - quantity;
                                int ready_to_dispatch = orderMasterModel.getOrderStatusModel().getOnReadyToDispatch() + quantity;
                                new DAOOrder(activity).getList().child(orderMasterModel.getId()).child("orderStatusModel").child(Const.ON_MACHINE_PENDING).setValue(onMachine);
                                new DAOOrder(activity).getList().child(orderMasterModel.getId()).child("orderStatusModel").child(Const.ON_READY_TO_DISPATCH).setValue(ready_to_dispatch);
                            } else {
                                new CustomSnackUtil().showSnack(activity, "Please Select Reading Type", R.drawable.error_msg_icon);
                            }

                        }
                    }
                }
            }

            @Override
            public void onCancelled(@Nonnull DatabaseError databaseError) {

            }
        });
    }*/

    private void getAllotList(String machineName, RecyclerView recyclerView, Dialog dialog) {
        new DaoAllotProgram(activity).getCollectionReference().get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                allot_order_no.clear();

                for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                    AllotProgramModel allotProgramModel = queryDocumentSnapshot.toObject(AllotProgramModel.class);

                    if (allotProgramModel.getMachine_name().equals(machineName)) {
                        allot_order_no.add(allotProgramModel.getOrder_no());
                    }
                }
                if (allot_order_no.size() == 0) {
                    dialog.show();
                    isDialogVisible = true;
                    DialogUtil.emptyRecordDialog(activity);
                } else {
                    dialog.show();
                    isDialogVisible = true;

                    MachineReadingAdapter machineReadingAdapter = new MachineReadingAdapter(activity, allot_order_no, dialog, ScanQR.this);
                    recyclerView.setLayoutManager(new LinearLayoutManager(activity));
                    recyclerView.setAdapter(machineReadingAdapter);
                }
            }
        });
    }

    public void deleteByAllot(String id) {
        ArrayList<AllotProgramModel> allotProgramModelArrayList = new ArrayList<>();

        new DaoAllotProgram(activity).getCollectionReference().get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                    AllotProgramModel allotProgramModel = queryDocumentSnapshot.toObject(AllotProgramModel.class);

                    if (!allotProgramModel.getOrder_no().equals(id)) {
                        allotProgramModelArrayList.add(allotProgramModel);
                    }
                }
                new DaoAllotProgram(activity).getCollectionReference().add(allotProgramModelArrayList);
            }
        });
    }

    @Override
    public void onclick(int position, String orderNo) {

        if (isInsertPermissionGranted(PermissionState.DAILY_READING.getValue())) {
            DailyReadingDialog dailyReadingDialog = new DailyReadingDialog();
            Bundle bundle = new Bundle();
            bundle.putString("party_name", orderNo);
            dailyReadingDialog.setArguments(bundle);
            dailyReadingDialog.show(fragmentManager, "Party Dialog");
        } else {
            DialogUtil.showAccessDeniedDialog(activity);
        }
    }
}