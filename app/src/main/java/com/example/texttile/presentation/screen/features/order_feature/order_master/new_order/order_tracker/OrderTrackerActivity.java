package com.example.texttile.presentation.screen.features.order_feature.order_master.new_order.order_tracker;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.texttile.R;
import com.example.texttile.databinding.ActivityOrderTrackerBinding;
import com.example.texttile.data.dao.DAOOrder;
import com.example.texttile.data.dao.DaoOrderHistory;
import com.example.texttile.presentation.screen.features.allot_feature.allot_program.activity.allot.AllotProgramActivity;
import com.example.texttile.presentation.screen.features.reading_feature.daily_reading.reading.DailyReadingActivity;
import com.example.texttile.data.model.OrderHistory;
import com.example.texttile.data.model.OrderMasterModel;
import com.example.texttile.presentation.ui.lib.snackUtil.CustomSnackUtil;
import com.example.texttile.presentation.ui.util.Const;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.Nullable;

public class OrderTrackerActivity extends AppCompatActivity {

    ActivityOrderTrackerBinding binding;
    OrderMasterModel current_order_model;
    Dialog dialog;
    String order_no;
    String[] category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderTrackerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        order_no = getIntent().getStringExtra("order_no");

        initToolbar();

        binding.cardPadding.setOnClickListener(view -> {
            Bundle bundle = new Bundle();
            bundle.putString("tracker", "add");
            bundle.putString("order_no", order_no);
            startActivity(new Intent(this, AllotProgramActivity.class).putExtras(bundle));

        });
        binding.cardOnMachinePadding.setOnClickListener(view -> {

            Bundle bundle = new Bundle();
            bundle.putString("tracker", "add");
            bundle.putString("order_no", order_no);
            startActivity(new Intent(this, DailyReadingActivity.class).putExtras(bundle));

        });
        binding.cardReadyToDispatch.setOnClickListener(view -> {
            if (current_order_model != null) {
                select_dot(3);
            }
        });
        binding.cardWarehouse.setOnClickListener(view -> {
            if (current_order_model != null) {
                select_dot(4);
            }
        });
        binding.cardFinalDispatch.setOnClickListener(view -> {
            if (current_order_model != null) {
                select_dot(5);
            }
        });
        binding.cardDelivered.setOnClickListener(view -> {
            if (current_order_model != null) {
                select_dot(6);
            }
        });
        binding.cardDamage.setOnClickListener(view -> {
            if (current_order_model != null) {
                select_dot(7);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        new DAOOrder(this).getReference().addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("TAG", "Listen failed.", e);
                    return;
                }

                for (QueryDocumentSnapshot doc : snapshot) {
                    OrderMasterModel orderMasterModel = doc.toObject(OrderMasterModel.class);
                    if (orderMasterModel.getSub_order_no().equals(order_no)) {
                        current_order_model = orderMasterModel;
                        binding.countPadding.setText(String.valueOf(current_order_model.getOrderStatusModel().getPending()));
                        binding.countOnMachine.setText(String.valueOf(current_order_model.getOrderStatusModel().getOnMachinePending()));
                        binding.countOnMachineCompleted.setText(String.valueOf(current_order_model.getOrderStatusModel().getOnMachineCompleted()));
                        binding.countReadyToDispatch.setText(String.valueOf(current_order_model.getOrderStatusModel().getOnReadyToDispatch()));
                        binding.countWareHouse.setText(String.valueOf(current_order_model.getOrderStatusModel().getOnWarehouse()));
                        binding.countFinalDispatch.setText(String.valueOf(current_order_model.getOrderStatusModel().getOnFinalDispatch()));
                        binding.countDelivered.setText(String.valueOf(current_order_model.getOrderStatusModel().getOnDelivered()));
                        binding.countDamage.setText(String.valueOf(current_order_model.getOrderStatusModel().getOnDamage()));
                        binding.countTotal.setText(String.valueOf(current_order_model.getOrderStatusModel().getTotal()));
                        break;
                    }
                }
            }
        });
    }

    public void select_dot(int position) {

        String[] reasonList = {"Not Selected", "Sort Length", "Jacquard Hook Problem", "Warp Cut Problem", "Garde Hook Problem", "Miss Pick", "Extra Pick", "Butta Cutting", "Oil Sport"};
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.reading_order_tracker);
        dialog.setCancelable(true);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        CardView btn_done = dialog.findViewById(R.id.btn_done);
        EditText edit_to = dialog.findViewById(R.id.edit_to);
        Spinner auto_from = dialog.findViewById(R.id.auto_from);
        EditText edit_qty = dialog.findViewById(R.id.edit_qty);
        EditText edit_slip_no = dialog.findViewById(R.id.edit_slip_no);
        LinearLayout damage_container = dialog.findViewById(R.id.damage_container);
        LinearLayout final_contanier = dialog.findViewById(R.id.final_contanier);
        Spinner spinner_reason = dialog.findViewById(R.id.spinner_reason);

        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.white_simple_spinner_item, reasonList);
        adapter.setDropDownViewResource(R.layout.white_simple_spinner_dropdown_item);
        spinner_reason.setAdapter(adapter);

        if (position == 3) {
            category = new String[]{"Warehouse", "Damage"};
            set_order_status_adapter(auto_from, category);
            edit_to.setText("Ready to Dispatch");
        } else if (position == 4) {
            category = new String[]{"Final Dispatch", "Damage"};
            set_order_status_adapter(auto_from, category);
            edit_to.setText("Warehouse");
        } else if (position == 5) {
            category = new String[]{"Delivered", "Damage"};
            set_order_status_adapter(auto_from, category);
            edit_to.setText("Final Dispatch");
        } else if (position == 6) {
            category = new String[]{"Damage"};
            set_order_status_adapter(auto_from, category);
            damage_container.setVisibility(View.VISIBLE);
            edit_to.setText("Delivered");
        } else if (position == 7) {
            category = new String[]{"Manage"};
            set_order_status_adapter(auto_from, category);
            edit_to.setText("Damage");
        }
        auto_from.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int pos, long id) {
                if (position == 5) {
                    if (pos == 0) {
                        damage_container.setVisibility(View.GONE);
                        final_contanier.setVisibility(View.VISIBLE);
                    } else {
                        damage_container.setVisibility(View.VISIBLE);
                        final_contanier.setVisibility(View.GONE);
                    }
                } else if (position == 6) {
                    damage_container.setVisibility(View.VISIBLE);
                } else {
                    if (pos == 0) {
                        damage_container.setVisibility(View.GONE);
                    } else {
                        damage_container.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });

        btn_done.setOnClickListener(v1 -> {
            String qty = edit_qty.getText().toString();

            if (TextUtils.isEmpty(qty)) {
                edit_qty.setError("Quantity is Empty");
            } else if (TextUtils.isEmpty(category[auto_from.getSelectedItemPosition()])) {
                new CustomSnackUtil().showSnack(this, "Please Select Order Destination", R.drawable.error_msg_icon);
            } else {
                dialog.dismiss();
                DAOOrder daoOrder = new DAOOrder(this);
                DaoOrderHistory orderHistory = new DaoOrderHistory(this);
                String id = orderHistory.getNewId();
                if (category[auto_from.getSelectedItemPosition()].equals("Warehouse")) {
                    OrderHistory orderHistoryModel = new OrderHistory(id, Const.ON_READY_TO_DISPATCH, Const.ON_WAREHOUSE, current_order_model.getOrder_no(), current_order_model.getSub_order_no(), current_order_model.getShade_no(), current_order_model.getDesign_no(), Integer.parseInt(qty), new SimpleDateFormat("dd/MM/yy mm:hh").format(new Date().getTime()));
                    if (Integer.parseInt(qty) <= current_order_model.getOrderStatusModel().getOnReadyToDispatch()) {
                        current_order_model.getOrderStatusModel().setOnReadyToDispatch(current_order_model.getOrderStatusModel().getOnReadyToDispatch() - Integer.parseInt(qty));
                        current_order_model.getOrderStatusModel().setOnWarehouse(current_order_model.getOrderStatusModel().getOnWarehouse() + Integer.parseInt(qty));
                        daoOrder.update_qty(current_order_model.getId(), current_order_model, orderHistoryModel);
                    } else {
                        new CustomSnackUtil().showSnack(this, "Please Enter Maximum " + current_order_model.getOrderStatusModel().getOnReadyToDispatch() + " Quantity", R.drawable.error_msg_icon);
                    }
                } else if (category[auto_from.getSelectedItemPosition()].equals("Final Dispatch")) {
                    OrderHistory orderHistoryModel = new OrderHistory(id, Const.ON_WAREHOUSE, Const.ON_FINAL_DISPATCH, current_order_model.getOrder_no(), current_order_model.getSub_order_no(), current_order_model.getShade_no(), current_order_model.getDesign_no(), Integer.parseInt(qty), new SimpleDateFormat("dd/MM/yy mm:hh").format(new Date().getTime()));
                    if (Integer.parseInt(qty) <= current_order_model.getOrderStatusModel().getOnWarehouse()) {
                        current_order_model.getOrderStatusModel().setOnWarehouse(current_order_model.getOrderStatusModel().getOnWarehouse() - Integer.parseInt(qty));
                        current_order_model.getOrderStatusModel().setOnFinalDispatch(current_order_model.getOrderStatusModel().getOnFinalDispatch() + Integer.parseInt(qty));

                        daoOrder.update_qty(current_order_model.getId(),current_order_model, orderHistoryModel);
                    } else {
                        new CustomSnackUtil().showSnack(this, "Please Enter Maximum " + current_order_model.getOrderStatusModel().getOnWarehouse() + " Quantity", R.drawable.error_msg_icon);
                    }
                } else if (category[auto_from.getSelectedItemPosition()].equals("Delivered")) {
                    OrderHistory orderHistoryModel = new OrderHistory(id, Const.ON_FINAL_DISPATCH, Const.ON_DELIVERED, current_order_model.getOrder_no(), current_order_model.getSub_order_no(), current_order_model.getShade_no(), current_order_model.getDesign_no(), Integer.parseInt(qty), new SimpleDateFormat("dd/MM/yy mm:hh").format(new Date().getTime()));
                    orderHistoryModel.setSlipNo(edit_slip_no.getText().toString());
                    if (Integer.parseInt(qty) <= current_order_model.getOrderStatusModel().getOnFinalDispatch()) {
                        current_order_model.getOrderStatusModel().setOnFinalDispatch(current_order_model.getOrderStatusModel().getOnFinalDispatch() - Integer.parseInt(qty));
                        current_order_model.getOrderStatusModel().setOnDelivered(current_order_model.getOrderStatusModel().getOnDelivered() + Integer.parseInt(qty));

                        daoOrder.update_qty(current_order_model.getId(),current_order_model, orderHistoryModel);
//                        daoOrder.update_qty(current_order_model.getId(), Const.ON_FINAL_DISPATCH, Const.ON_DELIVERED, current_order_model.getOrderStatusModel().getOnFinalDispatch(), current_order_model.getOrderStatusModel().getOnDelivered(), Integer.parseInt(qty), orderHistoryModel);
                    } else {
                        new CustomSnackUtil().showSnack(this, "Please Enter Maximum " + current_order_model.getOrderStatusModel().getOnFinalDispatch() + " Quantity", R.drawable.error_msg_icon);
                    }

                } else if (category[auto_from.getSelectedItemPosition()].equals("Manage")) {
                    OrderHistory orderHistoryModel = new OrderHistory(id, Const.ON_DAMAGE, Const.ON_PENDING, current_order_model.getOrder_no(), current_order_model.getSub_order_no(), current_order_model.getShade_no(), current_order_model.getDesign_no(), Integer.parseInt(qty), new SimpleDateFormat("dd/MM/yy mm:hh").format(new Date().getTime()));
                    if (Integer.parseInt(qty) <= current_order_model.getOrderStatusModel().getOnDamage()) {
                        current_order_model.getOrderStatusModel().setOnDamage(current_order_model.getOrderStatusModel().getOnDamage() - Integer.parseInt(qty));
                        current_order_model.getOrderStatusModel().setPending(current_order_model.getOrderStatusModel().getPending() + Integer.parseInt(qty));

                        daoOrder.update_qty(current_order_model.getId(),current_order_model, orderHistoryModel);
//                        daoOrder.update_qty(current_order_model.getId(), Const.ON_DAMAGE, Const.ON_PENDING, current_order_model.getOrderStatusModel().getOnDamage(), current_order_model.getOrderStatusModel().getPending(), Integer.parseInt(qty), orderHistoryModel);
                    } else {
                        new CustomSnackUtil().showSnack(this, "Please Enter Maximum " + current_order_model.getOrderStatusModel().getOnDamage() + " Quantity", R.drawable.error_msg_icon);
                    }
                } else if (category[auto_from.getSelectedItemPosition()].equals("Damage")) {
                    if (spinner_reason.getSelectedItemPosition() != 0) {
                        String reason = reasonList[spinner_reason.getSelectedItemPosition()];
                        if (edit_to.getText().toString().equals("Ready to Dispatch")) {
                            OrderHistory orderHistoryModel = new OrderHistory(id, Const.ON_READY_TO_DISPATCH, Const.ON_DAMAGE, current_order_model.getOrder_no(), current_order_model.getSub_order_no(), current_order_model.getShade_no(), current_order_model.getDesign_no(), Integer.parseInt(qty), new SimpleDateFormat("dd/MM/yy mm:hh").format(new Date().getTime()));
                            orderHistoryModel.setReason(reason);
                            if (Integer.parseInt(qty) <= current_order_model.getOrderStatusModel().getOnReadyToDispatch()) {
                                current_order_model.getOrderStatusModel().setOnReadyToDispatch(current_order_model.getOrderStatusModel().getOnReadyToDispatch() - Integer.parseInt(qty));
                                current_order_model.getOrderStatusModel().setOnDamage(current_order_model.getOrderStatusModel().getOnDamage() + Integer.parseInt(qty));

                                daoOrder.update_qty(current_order_model.getId(),current_order_model, orderHistoryModel);
//                                daoOrder.update_qty(current_order_model.getId(), Const.ON_READY_TO_DISPATCH, Const.ON_DAMAGE, current_order_model.getOrderStatusModel().getOnReadyToDispatch(), current_order_model.getOrderStatusModel().getOnDamage(), Integer.parseInt(qty), orderHistoryModel);
                            } else {
                                new CustomSnackUtil().showSnack(this, "Please Enter Maximum " + current_order_model.getOrderStatusModel().getOnReadyToDispatch() + " Quantity", R.drawable.error_msg_icon);
                            }
                        } else if (edit_to.getText().toString().equals("Warehouse")) {
                            OrderHistory orderHistoryModel = new OrderHistory(id, Const.ON_WAREHOUSE, Const.ON_DAMAGE, current_order_model.getOrder_no(), current_order_model.getSub_order_no(), current_order_model.getShade_no(), current_order_model.getDesign_no(), Integer.parseInt(qty), new SimpleDateFormat("dd/MM/yy mm:hh").format(new Date().getTime()));
                            orderHistoryModel.setReason(reason);
                            if (Integer.parseInt(qty) <= current_order_model.getOrderStatusModel().getOnWarehouse()) {
                                current_order_model.getOrderStatusModel().setOnWarehouse(current_order_model.getOrderStatusModel().getOnWarehouse() - Integer.parseInt(qty));
                                current_order_model.getOrderStatusModel().setOnDamage(current_order_model.getOrderStatusModel().getOnDamage() + Integer.parseInt(qty));

                                daoOrder.update_qty(current_order_model.getId(),current_order_model, orderHistoryModel);
//                                daoOrder.update_qty(current_order_model.getId(), Const.ON_WAREHOUSE, Const.ON_DAMAGE, current_order_model.getOrderStatusModel().getOnWarehouse(), current_order_model.getOrderStatusModel().getOnDamage(), Integer.parseInt(qty), orderHistoryModel);
                            } else {
                                new CustomSnackUtil().showSnack(this, "Please Enter Maximum " + current_order_model.getOrderStatusModel().getOnWarehouse() + " Quantity", R.drawable.error_msg_icon);
                            }
                        } else if (edit_to.getText().toString().equals("Final Dispatch")) {
                            OrderHistory orderHistoryModel = new OrderHistory(id, Const.ON_FINAL_DISPATCH, Const.ON_DAMAGE, current_order_model.getOrder_no(), current_order_model.getSub_order_no(), current_order_model.getShade_no(), current_order_model.getDesign_no(), Integer.parseInt(qty), new SimpleDateFormat("dd/MM/yy mm:hh").format(new Date().getTime()));
                            orderHistoryModel.setReason(reason);

                            if (Integer.parseInt(qty) <= current_order_model.getOrderStatusModel().getOnFinalDispatch()) {
                                current_order_model.getOrderStatusModel().setOnFinalDispatch(current_order_model.getOrderStatusModel().getOnFinalDispatch() - Integer.parseInt(qty));
                                current_order_model.getOrderStatusModel().setOnDamage(current_order_model.getOrderStatusModel().getOnDamage() + Integer.parseInt(qty));

                                daoOrder.update_qty(current_order_model.getId(),current_order_model, orderHistoryModel);
//                                daoOrder.update_qty(current_order_model.getId(), Const.ON_FINAL_DISPATCH, Const.ON_DAMAGE, current_order_model.getOrderStatusModel().getOnFinalDispatch(), current_order_model.getOrderStatusModel().getOnDamage(), Integer.parseInt(qty), orderHistoryModel);
                            } else {
                                new CustomSnackUtil().showSnack(this, "Please Enter Maximum " + current_order_model.getOrderStatusModel().getOnFinalDispatch() + " Quantity", R.drawable.error_msg_icon);
                            }

                        } else if (edit_to.getText().toString().equals("Delivered")) {
                            OrderHistory orderHistoryModel = new OrderHistory(id, Const.ON_DELIVERED, Const.ON_DAMAGE, current_order_model.getOrder_no(), current_order_model.getSub_order_no(), current_order_model.getShade_no(), current_order_model.getDesign_no(), Integer.parseInt(qty), new SimpleDateFormat("dd/MM/yy mm:hh").format(new Date().getTime()));
                            orderHistoryModel.setReason(reason);

                            if (Integer.parseInt(qty) <= current_order_model.getOrderStatusModel().getOnDelivered()) {
                                current_order_model.getOrderStatusModel().setOnDelivered(current_order_model.getOrderStatusModel().getOnDelivered() - Integer.parseInt(qty));
                                current_order_model.getOrderStatusModel().setOnDamage(current_order_model.getOrderStatusModel().getOnDamage() + Integer.parseInt(qty));

                                daoOrder.update_qty(current_order_model.getId(),current_order_model, orderHistoryModel);

//                                daoOrder.update_qty(current_order_model.getId(), Const.ON_DELIVERED, Const.ON_DAMAGE, current_order_model.getOrderStatusModel().getOnDelivered(), current_order_model.getOrderStatusModel().getOnDamage(), Integer.parseInt(qty), orderHistoryModel);
                            } else {
                                new CustomSnackUtil().showSnack(this, "Please Enter Maximum " + current_order_model.getOrderStatusModel().getOnDelivered() + " Quantity", R.drawable.error_msg_icon);
                            }
                        }
                    } else {
                        new CustomSnackUtil().showSnack(this, "Please Enter Reason", R.drawable.error_msg_icon);
                    }
                }

            }
        });

        dialog.show();
    }


    public void set_order_status_adapter(Spinner auto_from, String[] category) {

        ArrayAdapter aa = new ArrayAdapter(this, R.layout.white_simple_spinner_item, category);
        aa.setDropDownViewResource(R.layout.white_simple_spinner_dropdown_item);
        auto_from.setAdapter(aa);

    }


    public void initToolbar() {
        binding.include.textTitle.setText("Order Tracker");
        binding.include.btnBack.setOnClickListener(view -> {
            onBackPressed();
        });
    }
}