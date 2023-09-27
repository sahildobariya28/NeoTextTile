package com.example.texttile.presentation.screen.features.allot_feature.allot_program.activity.allot;

import static com.example.texttile.presentation.ui.util.PermissionUtil.isInsertPermissionGranted;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.texttile.R;
import com.example.texttile.databinding.ActivityAllotProgramBinding;
import com.example.texttile.presentation.ui.dialog.MachineMasterDialog;
import com.example.texttile.data.dao.DAOOrder;
import com.example.texttile.data.dao.DaoLiveOrder;
import com.example.texttile.data.dao.DaoOrderHistory;
import com.example.texttile.presentation.ui.interfaces.AddOnSearchedItemSelectListener;
import com.example.texttile.data.model.AllotProgramModel;
import com.example.texttile.data.model.LiveOrder;
import com.example.texttile.data.model.OrderHistory;
import com.example.texttile.data.model.OrderMasterModel;
import com.example.texttile.presentation.ui.lib.snackUtil.CustomSnackUtil;
import com.example.texttile.presentation.ui.util.Const;
import com.example.texttile.presentation.ui.util.DialogUtil;
import com.example.texttile.presentation.ui.util.ListShowDialog;
import com.example.texttile.presentation.ui.util.PermissionState;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javax.annotation.Nullable;

public class AllotProgramActivity extends AppCompatActivity implements AddOnSearchedItemSelectListener {

    OrderMasterModel orderModel;

    ActivityAllotProgramBinding binding;

    AllotProgramViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAllotProgramBinding.inflate(getLayoutInflater());
        viewModel = new ViewModelProvider(this, new AllotProgramViewModelFactory((getIntent().getStringExtra("tracker") != null) ? getIntent().getStringExtra("tracker") : "add", this)).get(AllotProgramViewModel.class);
        setContentView(binding.getRoot());

        initToolbar();

        viewModel.machine_name_list.observe(this, machineNameList -> {

            if (viewModel.orderMasterModel.getValue() != null) {
                binding.editPartyName.setText(viewModel.orderMasterModel.getValue().getParty_name());
                binding.editDesignNo.setText(viewModel.orderMasterModel.getValue().getDesign_no());
                binding.editShadeNo.setText(viewModel.orderMasterModel.getValue().getShade_no());
            }

            if (viewModel.machineListDialog != null) {
                viewModel.machineListDialog.update_list(machineNameList);
            } else {
                viewModel.machineListDialog = new ListShowDialog(AllotProgramActivity.this, machineNameList, AllotProgramActivity.this, viewModel.MACHINE_REQUEST);
            }

            binding.progress.setVisibility(View.GONE);
        });


            binding.textOrderNo.setText(viewModel.order_no.getValue());


        if (viewModel.tracker.getValue() != null && viewModel.tracker.getValue().equals("edit")) {
            if (viewModel.allotProgramModel != null) {
                binding.btnAdd.setText("Update Allot");

                binding.textMachine.setText(String.valueOf(viewModel.allotProgramModel.getMachine_name()));
                binding.textOrderNo.setText(String.valueOf(viewModel.allotProgramModel.getOrder_no()));
                binding.editPartyName.setText(String.valueOf(viewModel.allotProgramModel.getParty_name()));
                binding.editDesignNo.setText(String.valueOf(viewModel.allotProgramModel.getDesign_no()));
                binding.editShadeNo.setText(String.valueOf(viewModel.allotProgramModel.getShade_no()));
                binding.editQty.setText(String.valueOf(viewModel.allotProgramModel.getQty()));
            }
        } else {
            if (viewModel.allotProgramModel != null) {
                viewModel.daoOrder.getReference().addSnapshotListener((snapshot, e) -> {
                    if (!snapshot.isEmpty()) {
                        for (QueryDocumentSnapshot doc : snapshot) {
                            OrderMasterModel orderMasterModel = doc.toObject(OrderMasterModel.class);
                            if (viewModel.order_no.getValue().equals(orderMasterModel.getSub_order_no())){
                                orderModel = orderMasterModel;
                                binding.editPartyName.setText(orderModel.getParty_name());
                                binding.editDesignNo.setText(orderModel.getDesign_no());
                                binding.editShadeNo.setText(orderModel.getShade_no());
                            }
                        }
                    }

                });
            }
        }


        binding.btnAdd.setOnClickListener(view -> {
            addArtist();
        });

        binding.textMachine.setOnClickListener(view -> {
            if (viewModel.machineListDialog != null) {
                viewModel.machineListDialog.show_dialog();
            } else {
                viewModel.machineListDialog = new ListShowDialog(this, viewModel.machine_name_list.getValue(), AllotProgramActivity.this, viewModel.MACHINE_REQUEST);
                viewModel.machineListDialog.show_dialog();
            }
        });

        binding.textOrderNo.setOnClickListener(view -> {
            if (viewModel.order_no.getValue().isEmpty()) {
                if (viewModel.orderListDialog != null) {
                    viewModel.orderListDialog.show_dialog();
                } else {
                    viewModel.orderListDialog = new ListShowDialog(this, viewModel.orderNoList.getValue(), AllotProgramActivity.this, viewModel.ORDER_REQUEST);
                    viewModel.orderListDialog.show_dialog();
                }
            }
        });
    }

    private void addArtist() {
        String machine_name = binding.textMachine.getText().toString().trim();
        String order_no = binding.textOrderNo.getText().toString().trim();
        String party_name = binding.editPartyName.getText().toString().trim();
        String design_no = binding.editDesignNo.getText().toString().trim();
        String shade_no = binding.editShadeNo.getText().toString().trim();
        String qty = binding.editQty.getText().toString().trim();

        if (TextUtils.isEmpty(machine_name)) {
            new CustomSnackUtil().showSnack(this, "Please enter a Machine Name", R.drawable.error_msg_icon);
        } else if (TextUtils.isEmpty(order_no)) {
            new CustomSnackUtil().showSnack(this, "Please enter a Order no", R.drawable.error_msg_icon);
        } else if (TextUtils.isEmpty(party_name)) {
            new CustomSnackUtil().showSnack(this, "Please enter a Party name", R.drawable.error_msg_icon);
        } else if (TextUtils.isEmpty(design_no)) {
            new CustomSnackUtil().showSnack(this, "Please enter a Design no", R.drawable.error_msg_icon);
        } else if (TextUtils.isEmpty(shade_no)) {
            new CustomSnackUtil().showSnack(this, "Please enter a Shade no", R.drawable.error_msg_icon);
        } else if (TextUtils.isEmpty(qty)) {
            new CustomSnackUtil().showSnack(this, "Please enter a Quantity", R.drawable.error_msg_icon);
        } else if (viewModel.getOrderModelById(order_no) != null && Integer.parseInt(qty) > viewModel.getOrderModelById(order_no).getOrderStatusModel().getPending()) {
            new CustomSnackUtil().showSnack(this, "Select Maximum " + viewModel.getOrderModelById(order_no).getOrderStatusModel().getPending() + " order", R.drawable.error_msg_icon);
        } else {

            String id = viewModel.dao.getId();
            if (viewModel.tracker.getValue().equals("edit")) {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("machine_name", machine_name);
                hashMap.put("order_no", order_no);
                hashMap.put("party_name", party_name);
                hashMap.put("design_no", design_no);
                hashMap.put("shade_no", shade_no);
                hashMap.put("qty", Integer.valueOf(qty));

                updateOrderStatus(order_no, Integer.parseInt(qty), machine_name);
                viewModel.dao.update(viewModel.allotProgramModel.getId(), hashMap).addOnSuccessListener(runnable -> {
                    DialogUtil.showUpdateSuccessDialog(this, true);
                });
            } else {
                viewModel.allotProgramModel = new AllotProgramModel(id, machine_name, order_no, party_name, design_no, shade_no, Integer.parseInt(qty));
                updateOrderStatus(order_no, Integer.parseInt(qty), machine_name);
                viewModel.dao.insert(id, viewModel.allotProgramModel).addOnSuccessListener(runnable -> {
//                    DialogUtil.showSuccessDialog(this, true);
                });
            }
        }
    }

    private void updateOrderStatus(String order_no, int quantity, String machine_name) {

        new DAOOrder(this).getReference().get().addOnCompleteListener(task -> {

            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    OrderMasterModel orderMasterModel = document.toObject(OrderMasterModel.class);
                    if (orderMasterModel.getSub_order_no().equals(order_no)) {

                        int paddingOrder;
                        int onMachine;

                        if (viewModel.tracker.getValue().equals("add")) {
                            if (quantity <= orderMasterModel.getOrderStatusModel().getPending()) {
                                paddingOrder = orderMasterModel.getOrderStatusModel().getPending() - quantity;
                                onMachine = orderMasterModel.getOrderStatusModel().getOnMachinePending() + quantity;
                                set_qty(paddingOrder, onMachine, orderMasterModel, machine_name, quantity);
                            } else {
                                new CustomSnackUtil().showSnack(AllotProgramActivity.this, quantity + "quantity not found in pending order", R.drawable.error_msg_icon);
                            }
                        } else {

                            if (viewModel.allotProgramModel.getQty() < quantity) {
                                int realQty = (quantity - viewModel.allotProgramModel.getQty());
                                Log.d("dfdsfjkdsjfkd", "onDataChange: " + realQty + "   " + orderMasterModel.getOrderStatusModel().getPending());

                                paddingOrder = orderMasterModel.getOrderStatusModel().getPending() - realQty;
                                onMachine = orderMasterModel.getOrderStatusModel().getOnMachinePending() + realQty;
                                set_qty(paddingOrder, onMachine, orderMasterModel, machine_name, quantity);

                            } else {
                                int realqty = (viewModel.allotProgramModel.getQty() - quantity);

                                paddingOrder = orderMasterModel.getOrderStatusModel().getPending() + realqty;
                                onMachine = orderMasterModel.getOrderStatusModel().getOnMachinePending() - realqty;
                                set_qty(paddingOrder, onMachine, orderMasterModel, machine_name, quantity);


                            }

                        }

//                        DAOOrder daoOrder = new DAOOrder(AllotProgramActivity.this);
//                        HashMap<String, Object> hashMap = new HashMap<>();
//                        hashMap.put(Const.ON_PENDING, paddingOrder);
//                        hashMap.put(Const.ON_MACHINE_PENDING, onMachine);
//                        daoOrder.updateOrderStatusModel(orderMasterModel.getId(), hashMap);
//
//                        //insert liveOrder
//                        DaoLiveOrder daoLiveOrder = new DaoLiveOrder(AllotProgramActivity.this);
//                        String liveOrderId = daoLiveOrder.getId();
//                        daoLiveOrder.add(liveOrderId, new LiveOrder(liveOrderId, machine_name, quantity));
//
//                        DaoOrderHistory daoOrderHistory = new DaoOrderHistory(AllotProgramActivity.this);
//                        String id = daoOrderHistory.getId();
//                        OrderHistory orderHistory = new OrderHistory(id, Const.ON_PENDING, Const.ON_MACHINE_PENDING, orderMasterModel.getOrder_no(), orderMasterModel.getSub_order_no(), orderMasterModel.getShade_no(), orderMasterModel.getDesign_no(), quantity, new SimpleDateFormat("dd/MM/yy mm:hh").format(new Date().getTime()));
//                        daoOrderHistory.add(id, orderHistory);

                    }
                }
            } else {
                Log.d("TAG", "Error getting documents: ", task.getException());
            }
        });
    }

    public void set_qty(int paddingOrder, int onMachine, OrderMasterModel orderMasterModel, String machine_name, int quantity) {
        DAOOrder daoOrder = new DAOOrder(AllotProgramActivity.this);

        DaoOrderHistory daoOrderHistory = new DaoOrderHistory(AllotProgramActivity.this);
        String id = daoOrderHistory.getNewId();
        OrderHistory orderHistory = new OrderHistory(id, Const.ON_PENDING, Const.ON_MACHINE_PENDING, orderMasterModel.getOrder_no(), orderMasterModel.getSub_order_no(), orderMasterModel.getShade_no(), orderMasterModel.getDesign_no(), quantity, new SimpleDateFormat("dd/MM/yy mm:hh").format(new Date().getTime()));


        orderMasterModel.getOrderStatusModel().setPending(paddingOrder);
        orderMasterModel.getOrderStatusModel().setOnMachinePending(onMachine);

        daoOrder.update_qty(orderMasterModel.getId(), orderMasterModel, orderHistory);


//        daoOrder.update_qty(orderMasterModel.getId(), hashMap);

        //insert liveOrder
        DaoLiveOrder daoLiveOrder = new DaoLiveOrder(AllotProgramActivity.this);
        String liveOrderId = daoLiveOrder.getNewId();
        daoLiveOrder.insert(liveOrderId, new LiveOrder(liveOrderId, machine_name, orderMasterModel.getSub_order_no(), quantity));


    }


    public void initToolbar() {
        binding.include.textTitle.setText("Allot Program");
        binding.include.btnBack.setOnClickListener(view -> {
            this.onBackPressed();
        });
    }

    public void findOrderModel(String order_no) {

        new DAOOrder(this).getReference().whereEqualTo("sub_order_no", order_no.replace(order_no.substring(order_no.indexOf("("), order_no.indexOf(")") + 1).trim(), "").trim()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("TAG", "Listen failed.", e);
                    return;
                }

                for (QueryDocumentSnapshot doc : snapshot) {
                    OrderMasterModel orderMasterModel = doc.toObject(OrderMasterModel.class);

                    if (orderMasterModel != null && orderMasterModel.getSub_order_no() != null && orderMasterModel.getParty_name() != null && orderMasterModel.getDesign_no() != null && orderMasterModel.getShade_no() != null) {
                        binding.textOrderNo.setText(orderMasterModel.getSub_order_no());
                        binding.editPartyName.setText(orderMasterModel.getParty_name());
                        binding.editDesignNo.setText(orderMasterModel.getDesign_no());
                        binding.editShadeNo.setText(orderMasterModel.getShade_no());
                        viewModel.getMachineListWithOrderFilter(orderMasterModel.getSub_order_no());
                    }
                }
            }
        });
    }

    @Override
    public void onSelected(int position, String name, int request_code) {
        if (request_code == viewModel.MACHINE_REQUEST) {
            binding.progress.setVisibility(View.VISIBLE);

            binding.textMachine.setText(name);
            viewModel.getOrderList(name);
            viewModel.orderNoList.observe(this, orderNoList -> {
                if (viewModel.orderListDialog != null) {
                    viewModel.orderListDialog.update_list(orderNoList);
                } else {
                    viewModel.orderListDialog = new ListShowDialog(AllotProgramActivity.this, orderNoList, AllotProgramActivity.this, viewModel.ORDER_REQUEST);
                }
                binding.progress.setVisibility(View.GONE);
            });
        } else if (request_code == viewModel.ORDER_REQUEST) {
            findOrderModel(name);
            binding.textOrderNo.setText(name.replace(name.substring(name.indexOf("("), name.indexOf(")") + 1).trim(), "").trim());
        }
    }

    @Override
    public void onAddButtonClicked(int position, String name, int request_code) {
        if (request_code == viewModel.MACHINE_REQUEST) {
            if (isInsertPermissionGranted(PermissionState.MACHINE_MASTER.getValue())) {
                MachineMasterDialog machineMasterDialog = new MachineMasterDialog();
                Bundle bundle = new Bundle();
                bundle.putString("tracker", "add");
                bundle.putString("machine_name", name);
                machineMasterDialog.setArguments(bundle);
                machineMasterDialog.show(getSupportFragmentManager(), "My Fragment");
            } else {
                DialogUtil.showAccessDeniedDialog(this);
            }

        } else if (request_code == viewModel.ORDER_REQUEST) {
            new CustomSnackUtil().showSnack(this, "Please Create Order Manually", R.drawable.icon_warning);
        }
    }

    @Override
    public void onMultiSelected(ArrayList<String> shadeList, int request_code) {

    }
}