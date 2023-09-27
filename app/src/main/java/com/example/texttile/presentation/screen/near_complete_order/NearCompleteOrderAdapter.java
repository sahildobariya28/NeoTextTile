package com.example.texttile.presentation.screen.near_complete_order;

import static com.example.texttile.presentation.ui.util.PermissionUtil.isReadPermissionGranted;
import static com.example.texttile.presentation.ui.util.PermissionUtil.isUpdatePermissionGranted;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.texttile.R;
import com.example.texttile.databinding.NearCompeleteOrderItemBinding;
import com.example.texttile.data.dao.DAOExtraOrder;
import com.example.texttile.data.dao.DAOOrder;
import com.example.texttile.data.dao.DaoOrderHistory;
import com.example.texttile.presentation.ui.interfaces.EmptyDataListener;
import com.example.texttile.presentation.ui.interfaces.OnSelectionCountListeners;
import com.example.texttile.presentation.screen.features.order_feature.order_master.new_order.order_tracker.OrderTrackerActivity;
import com.example.texttile.data.model.ItemState;
import com.example.texttile.data.model.OrderHistory;
import com.example.texttile.data.model.OrderMasterModel;
import com.example.texttile.data.model.ReadyStockModel;
import com.example.texttile.presentation.ui.lib.new_swipe_feature.SwipeRevealLayout;
import com.example.texttile.presentation.ui.lib.new_swipe_feature.ViewBinderHelper;
import com.example.texttile.presentation.ui.lib.snackUtil.CustomSnackUtil;
import com.example.texttile.presentation.ui.util.Const;
import com.example.texttile.presentation.ui.util.DialogUtil;
import com.example.texttile.presentation.ui.util.PermissionState;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javax.annotation.Nonnull;

public class NearCompleteOrderAdapter extends RecyclerView.Adapter<NearCompleteOrderAdapter.ViewHolder> {

    Activity activity;
    int old_item = -1, new_item = -1;
    String tracker;
    EmptyDataListener emptyDataListener;
    ArrayList<OrderMasterModel> orderMasterList;

    ArrayList<ItemState> itemModels = new ArrayList<>();
    private final ViewBinderHelper binderHelper = new ViewBinderHelper();
    public boolean isSelectionMode = false;
    OnSelectionCountListeners onSelectionCountListeners;
    DAOExtraOrder daoExtraOrder;
    DAOOrder daoOrder;

    String[] category;

    public NearCompleteOrderAdapter(ArrayList<OrderMasterModel> orderMasterList, Activity activity, String tracker, EmptyDataListener emptyDataListener, OnSelectionCountListeners onSelectionCountListeners, DAOOrder daoOrder) {
        this.orderMasterList = orderMasterList;
        this.emptyDataListener = emptyDataListener;
        this.activity = activity;
        this.tracker = tracker;
        this.onSelectionCountListeners = onSelectionCountListeners;
        daoExtraOrder = new DAOExtraOrder(activity);
        this.daoOrder = daoOrder;

        emptyDataListener.onDataChanged(true, getItemCount());
        itemModels.clear();
        for (int i = 0; i < orderMasterList.size(); i++) {
            itemModels.add(new ItemState(false, false, false, i));
        }
        binderHelper.setOpenOnlyOne(true);

    }

    @Nonnull
    @Override
    public ViewHolder onCreateViewHolder(@Nonnull ViewGroup parent, int viewType) {
        NearCompeleteOrderItemBinding binding = NearCompeleteOrderItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@Nonnull ViewHolder holder, int position) {

        holder.binding.swipeRevealLayout.setSwipeListener(new SwipeRevealLayout.SwipeListener() {
            @Override
            public void onClosed(SwipeRevealLayout view) {
                itemModels.get(holder.getAdapterPosition()).setSwiped(false);
            }

            @Override
            public void onOpened(SwipeRevealLayout view) {
                itemModels.get(holder.getAdapterPosition()).setSwiped(true);

            }

            @Override
            public void onSlide(SwipeRevealLayout view, float slideOffset) {
                itemModels.get(holder.getAdapterPosition()).setSwiped(false);
            }
        });

        holder.binding.viewMain.setOnClickListener(view -> {
            if (holder.binding.swipeRevealLayout.isOpened()) {
                binderHelper.closeLayout(String.valueOf(holder.getAdapterPosition()));
                itemModels.get(holder.getAdapterPosition()).setSwiped(false);
            } else {
                if (isSelectionMode) {
                    if (itemModels.get(holder.getAdapterPosition()).isSelected()) {
                        holder.binding.viewMain.setBackground(activity.getDrawable(R.drawable.border_bg_unselected));
                        itemModels.get(holder.getAdapterPosition()).setSelected(false);
                        selectionModeUpdate();
                    } else {
                        holder.binding.viewMain.setBackground(activity.getDrawable(R.drawable.border_bg_selected));
                        itemModels.get(holder.getAdapterPosition()).setSelected(true);
                        selectionModeUpdate();
                    }
                } else {
                    expand_item(holder.getAdapterPosition(), holder.binding.btnDown);
                }
            }
        });
        holder.binding.viewMain.setOnLongClickListener(view -> {
            if (holder.binding.swipeRevealLayout.isOpened()) {
                binderHelper.closeLayout(String.valueOf(holder.getAdapterPosition()));
                holder.binding.viewMain.setBackground(activity.getDrawable(R.drawable.border_bg_selected));
                itemModels.get(holder.getAdapterPosition()).setSelected(true);
                selectionModeUpdate();
            } else {
                if (itemModels.get(holder.getAdapterPosition()).isSelected()) {
                    holder.binding.viewMain.setBackground(activity.getDrawable(R.drawable.border_bg_unselected));
                    itemModels.get(holder.getAdapterPosition()).setSelected(false);
                    selectionModeUpdate();
                } else {
                    binderHelper.closeLayout(String.valueOf(holder.getAdapterPosition()));
                    holder.binding.viewMain.setBackground(activity.getDrawable(R.drawable.border_bg_selected));
                    itemModels.get(holder.getAdapterPosition()).setSelected(true);
                    selectionModeUpdate();
                }
            }
            return true;
        });

        binderHelper.bind(holder.binding.swipeRevealLayout, String.valueOf(position));

        OrderMasterModel orderMasterModel = orderMasterList.get(position);
        Animation animation = AnimationUtils.loadAnimation(activity, R.anim.scale_up);
        holder.binding.layoutMain.startAnimation(animation);

        if (itemModels.get(position).isExpanded()) {
            holder.binding.detailContainer.setVisibility(View.VISIBLE);
            holder.binding.btnDown.setImageResource(R.drawable.up);
        } else {
            holder.binding.detailContainer.setVisibility(View.GONE);
            holder.binding.btnDown.setImageResource(R.drawable.down);
        }
        if (itemModels.get(position).isSelected()) {
            holder.binding.viewMain.setBackground(activity.getDrawable(R.drawable.border_bg_selected));
            itemModels.get(holder.getAdapterPosition()).setSelected(true);
        } else {
            holder.binding.viewMain.setBackground(activity.getDrawable(R.drawable.border_bg_unselected));
            itemModels.get(holder.getAdapterPosition()).setSelected(false);
        }
        if (itemModels.get(position).isSwiped()) {
            binderHelper.openLayout(String.valueOf(holder.getAdapterPosition()));
        } else {
            binderHelper.closeLayout(String.valueOf(holder.getAdapterPosition()));
        }

        holder.binding.textDate.setText(orderMasterModel.getDate());
        holder.binding.textOrderNo.setText(orderMasterModel.getSub_order_no());
        holder.binding.textOrderType.setText(orderMasterModel.getOrder_type());
        holder.binding.textPartyName.setText(orderMasterModel.getParty_name());
        holder.binding.textDesignNo.setText(orderMasterModel.getDesign_no());
        holder.binding.textShadeNo.setText(orderMasterModel.getShade_no());
        holder.binding.textQty.setText("\\" + orderMasterModel.getQuantity());
        holder.binding.textApproxDeliveryDate.setText(orderMasterModel.getApprox_Delivery_date());

        holder.itemView.setOnClickListener(view -> {
            expand_item(position, holder.binding.btnDown);
        });
        if (orderMasterModel.getOrderStatusModel().getPending() != 0) {
            //readstock add karvanu che
            new DAOExtraOrder(activity).getReference().addSnapshotListener((snapshot, error) -> {
                if (error != null) {
                    Log.w("TAG", "Listen failed.", error);
                    return;
                }

                boolean isExtra = false;
                int extra_count = 0;

                for (QueryDocumentSnapshot document : snapshot) {
                    ReadyStockModel readyStockModel = document.toObject(ReadyStockModel.class);
                    if (readyStockModel.getDesign_no().equals(orderMasterModel.getDesign_no()) && readyStockModel.getShade_no().equals(orderMasterModel.getShade_no())) {
                        isExtra = true;
                        extra_count += readyStockModel.getQuantity();
                    }
                }

                if (isExtra) {
                    holder.binding.btnExtraAdd.setVisibility(View.VISIBLE);

                    if (extra_count < orderMasterModel.getOrderStatusModel().getPending()) {
                        holder.binding.btnExtraAdd.setText("Extra (" + extra_count + ")");
                    } else {
                        holder.binding.btnExtraAdd.setText("Extra (" + orderMasterModel.getOrderStatusModel().getPending() + ")");
                    }

                } else {
                    holder.binding.btnExtraAdd.setVisibility(View.GONE);
                }
            });

//            daoExtraOrder.getReference().addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@Nonnull DataSnapshot snapshot) {
//                    boolean isExtra = false;
//                    int extra_count = 0;
//                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
//                        ReadyStockModel readyStockModel = postSnapshot.getValue(ReadyStockModel.class);
//                        if (readyStockModel.getDesign_no().equals(orderMasterModel.getDesign_no()) && readyStockModel.getShade_no().equals(orderMasterModel.getShade_no())) {
//                            isExtra = true;
//                            extra_count += readyStockModel.getQuantity();
//                        }
//                    }
//                    if (isExtra) {
//                        holder.binding.btnExtraAdd.setVisibility(View.VISIBLE);
//
//                        if (extra_count < orderMasterModel.getOrderStatusModel().getPending()) {
//                            holder.binding.btnExtraAdd.setText("Extra (" + extra_count + ")");
//                        } else {
//                            holder.binding.btnExtraAdd.setText("Extra (" + orderMasterModel.getOrderStatusModel().getPending() + ")");
//                        }
//
//                    } else {
//                        holder.binding.btnExtraAdd.setVisibility(View.GONE);
//                    }
//                }
//
//                @Override
//                public void onCancelled(@Nonnull DatabaseError error) {
//
//                }
//            });
        } else {
            holder.binding.btnExtraAdd.setVisibility(View.GONE);
        }

        holder.binding.btnExtraAdd.setOnClickListener(view -> {

            int total_count = Integer.parseInt(holder.binding.btnExtraAdd.getText().toString().replace("Extra (", "").replace(")", "").trim());
            Dialog dialog = new Dialog(activity);
            dialog.setContentView(R.layout.extra_order_conform_dialog);
            dialog.setCancelable(true);
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            CardView btn_add = dialog.findViewById(R.id.btn_add);
            CardView btn_cancel = dialog.findViewById(R.id.btn_cancel);
            EditText edit_qty = dialog.findViewById(R.id.edit_qty);
            TextView count_extra = dialog.findViewById(R.id.count_extra);
            count_extra.setText("\\" + total_count);
            btn_cancel.setOnClickListener(view1 -> {
                dialog.dismiss();
            });
            btn_add.setOnClickListener(view1 -> {

                if (Integer.parseInt(edit_qty.getText().toString()) <= total_count) {
                    daoExtraOrder.getReference().get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                ArrayList<ReadyStockModel> extraOrderList = new ArrayList<>();
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    ReadyStockModel readyStockModel = document.toObject(ReadyStockModel.class);
                                    if (readyStockModel.getDesign_no().equals(orderMasterModel.getDesign_no()) && readyStockModel.getShade_no().equals(orderMasterModel.getShade_no())) {
                                        extraOrderList.add(readyStockModel);
                                    }
                                }
                                int qty = Integer.parseInt(edit_qty.getText().toString());

                                OrderHistory orderHistoryModel = new OrderHistory(new DaoOrderHistory(activity).getNewId(), Const.ON_PENDING, Const.ON_FINAL_DISPATCH, orderMasterModel.getOrder_no(), orderMasterModel.getSub_order_no(), orderMasterModel.getShade_no(), orderMasterModel.getDesign_no(), orderMasterModel.getOrderStatusModel().getPending(), new SimpleDateFormat("dd/MM/yy mm:hh").format(new Date().getTime()));
                                orderMasterModel.getOrderStatusModel().setOnDelivered(orderMasterModel.getOrderStatusModel().getOnDelivered() - qty);
                                orderMasterModel.getOrderStatusModel().setOnDamage(orderMasterModel.getOrderStatusModel().getOnDamage() + qty);

                                daoOrder.update_qty(orderMasterModel.getId(), orderMasterModel, orderHistoryModel);

//                                new DAOOrder(activity).update_qty(orderMasterModel.getId(), Const.ON_PENDING, Const.ON_FINAL_DISPATCH, orderMasterModel.getOrderStatusModel().getPending(), orderMasterModel.getOrderStatusModel().getOnFinalDispatch(), qty, orderHistoryModel);
                                for (int i = 0; i < extraOrderList.size(); i++) {
                                    if (extraOrderList.get(i).getQuantity() <= qty) {
                                        qty = qty - extraOrderList.get(i).getQuantity();
                                        daoExtraOrder.delete(extraOrderList.get(i).getId()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                // do nothing
                                            }
                                        });
                                    } else {
                                        HashMap<String, Object> hashMap = new HashMap<>();
                                        hashMap.put("quantity", extraOrderList.get(i).getQuantity() - qty);
                                        qty = 0;
                                        daoExtraOrder.update(extraOrderList.get(i).getId(), hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                // do nothing
                                            }
                                        });
                                    }
                                }
                                dialog.dismiss();
                            } else {
                                Log.d("TAG", "Error getting documents: ", task.getException());
                            }
                        }
                    });
                } else {
                    new CustomSnackUtil().showSnack(activity, "Select Maximum " + total_count + " Quantity", R.drawable.error_msg_icon);
                }

//                if (Integer.parseInt(edit_qty.getText().toString()) <= total_count) {
//                    daoExtraOrder.getReference().addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@Nonnull DataSnapshot snapshot) {
//                            ArrayList<ReadyStockModel> extraOrderList = new ArrayList<>();
//                            for (DataSnapshot postSnapshot : snapshot.getChildren()) {
//                                ReadyStockModel readyStockModel = postSnapshot.getValue(ReadyStockModel.class);
//                                if (readyStockModel.getDesign_no().equals(orderMasterModel.getDesign_no()) && readyStockModel.getShade_no().equals(orderMasterModel.getShade_no())) {
//                                    extraOrderList.add(readyStockModel);
//                                }
//                            }
//                            int qty = Integer.parseInt(edit_qty.getText().toString());
//                            OrderHistory orderHistoryModel = new OrderHistory(new DaoOrderHistory(activity).getId(), Const.ON_PENDING, Const.ON_FINAL_DISPATCH, orderMasterModel.getOrder_no(), orderMasterModel.getSub_order_no(), orderMasterModel.getShade_no(), orderMasterModel.getDesign_no(), orderMasterModel.getOrderStatusModel().getPending(), new SimpleDateFormat("dd/MM/yy mm:hh").format(new Date().getTime()));
//                            new DAOOrder(activity).update_qty(orderMasterModel.getId(), Const.ON_PENDING, Const.ON_FINAL_DISPATCH, orderMasterModel.getOrderStatusModel().getPending(), orderMasterModel.getOrderStatusModel().getOnFinalDispatch(), qty, orderHistoryModel);
//                            for (int i = 0; i < extraOrderList.size(); i++) {
//                                if (extraOrderList.get(i).getQuantity() <= qty) {
//                                    qty = qty - extraOrderList.get(i).getQuantity();
//                                    daoExtraOrder.remove(extraOrderList.get(i).getId());
//                                } else {
//                                    HashMap<String, Object> hashMap = new HashMap<>();
//                                    hashMap.put("quantity", extraOrderList.get(i).getQuantity() - qty);
//                                    qty = 0;
//                                    daoExtraOrder.update(extraOrderList.get(i).getId(), hashMap);
//                                }
//                            }
//                            dialog.dismiss();
//                        }
//
//                        @Override
//                        public void onCancelled(@Nonnull DatabaseError error) {
//
//                        }
//                    });
//                } else {
//                    new CustomSnackUtil().showSnack(activity, "Select Maximum " + total_count + " Quantity", R.drawable.error_msg_icon);
//                }

            });
            dialog.show();

        });


        holder.binding.btnTracker.setOnClickListener(view -> {
            if (isReadPermissionGranted(PermissionState.ORDER_TRACKER.getValue())) {
                Bundle bundle = new Bundle();
                bundle.putString("order_no", orderMasterModel.getSub_order_no());
                activity.startActivity(new Intent(activity, OrderTrackerActivity.class).putExtras(bundle));
            } else {
                DialogUtil.showAccessDeniedDialog(activity);
            }


        });

        holder.binding.btnManage.setOnClickListener(view -> {
            WriteBatch batch = FirebaseFirestore.getInstance().batch();
            DialogUtil.showProgressDialog(activity);
            if (orderMasterModel.getOrderStatusModel().getOnDelivered() == (orderMasterModel.getOrderStatusModel().getTotal() - 1)) {
                if (orderMasterModel.getOrderStatusModel().getPending() == 1) {
                    select_dot(orderMasterModel, 0, batch);
                } else if (orderMasterModel.getOrderStatusModel().getOnMachinePending() == 1) {
                    select_dot(orderMasterModel, 1, batch);
                } else if (orderMasterModel.getOrderStatusModel().getOnMachineCompleted() == 1) {
                    select_dot(orderMasterModel, 2, batch);
                } else if (orderMasterModel.getOrderStatusModel().getOnReadyToDispatch() == 1) {
                    select_dot(orderMasterModel, 3, batch);
                } else if (orderMasterModel.getOrderStatusModel().getOnWarehouse() == 1) {
                    select_dot(orderMasterModel, 4, batch);
                } else if (orderMasterModel.getOrderStatusModel().getOnFinalDispatch() == 1) {
                    select_dot(orderMasterModel, 5, batch);
                } else if (orderMasterModel.getOrderStatusModel().getOnDamage() == 1) {
                    select_dot(orderMasterModel, 7, batch);
                }
            }
            batch.commit().addOnSuccessListener(aVoid -> {
                DialogUtil.hideProgressDialog();
                DialogUtil.showUpdateSuccessDialog(activity, false);
            }).addOnFailureListener(e -> {
                DialogUtil.hideProgressDialog();
                new CustomSnackUtil().showSnack(activity, "Something went wrong", R.drawable.error_msg_icon);
            });

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

    public void select_dot(OrderMasterModel current_order_model, int position, WriteBatch batch) {
        DAOOrder daoOrder = new DAOOrder(activity);
        DaoOrderHistory orderHistory = new DaoOrderHistory(activity);
        String id = orderHistory.getNewId();
        Log.d("fsdfwer", "select_dot: " + position);
        if (position == 0) {
            Log.d("fsdfwer", "select_dot: " + current_order_model.getOrderStatusModel().getPending());
            if (current_order_model.getOrderStatusModel().getPending() == 1) {
                OrderHistory orderHistoryModel = new OrderHistory(id, Const.ON_PENDING, Const.ON_DELIVERED, current_order_model.getOrder_no(), current_order_model.getSub_order_no(), current_order_model.getShade_no(), current_order_model.getDesign_no(), 1, new SimpleDateFormat("dd/MM/yy mm:hh").format(new Date().getTime()));

                current_order_model.getOrderStatusModel().setPending(0);
                current_order_model.getOrderStatusModel().setOnDelivered(current_order_model.getOrderStatusModel().getOnDelivered() + 1);

                batch.update(daoOrder.getReference().document(current_order_model.getId()), objectToHashMap(current_order_model));
//                daoOrder.update_qty(current_order_model.getId(), current_order_model, orderHistoryModel);
            } else {
                new CustomSnackUtil().showSnack(activity, "Incorrect Order Please Check It.", R.drawable.icon_warning);
            }
        }else if (position == 1) {
            if (current_order_model.getOrderStatusModel().getOnMachinePending() == 1) {
                OrderHistory orderHistoryModel = new OrderHistory(id, Const.ON_MACHINE_PENDING, Const.ON_DELIVERED, current_order_model.getOrder_no(), current_order_model.getSub_order_no(), current_order_model.getShade_no(), current_order_model.getDesign_no(), 1, new SimpleDateFormat("dd/MM/yy mm:hh").format(new Date().getTime()));

                current_order_model.getOrderStatusModel().setOnMachinePending(0);
                current_order_model.getOrderStatusModel().setOnDelivered(current_order_model.getOrderStatusModel().getOnDelivered() + 1);

                batch.update(daoOrder.getReference().document(current_order_model.getId()), objectToHashMap(current_order_model));
            } else {
                new CustomSnackUtil().showSnack(activity, "Incorrect Order Please Check It.", R.drawable.icon_warning);
            }
        }else if (position == 2) {
            if (current_order_model.getOrderStatusModel().getOnMachineCompleted() == 1) {
                OrderHistory orderHistoryModel = new OrderHistory(id, Const.ON_MACHINE_COMPLETED, Const.ON_DELIVERED, current_order_model.getOrder_no(), current_order_model.getSub_order_no(), current_order_model.getShade_no(), current_order_model.getDesign_no(), 1, new SimpleDateFormat("dd/MM/yy mm:hh").format(new Date().getTime()));

                current_order_model.getOrderStatusModel().setOnMachineCompleted(0);
                current_order_model.getOrderStatusModel().setOnDelivered(current_order_model.getOrderStatusModel().getOnDelivered() + 1);

                batch.update(daoOrder.getReference().document(current_order_model.getId()), objectToHashMap(current_order_model));
            } else {
                new CustomSnackUtil().showSnack(activity, "Incorrect Order Please Check It.", R.drawable.icon_warning);
            }
        }else if (position == 3) {
            if (current_order_model.getOrderStatusModel().getOnReadyToDispatch() == 1) {
                OrderHistory orderHistoryModel = new OrderHistory(id, Const.ON_READY_TO_DISPATCH, Const.ON_DELIVERED, current_order_model.getOrder_no(), current_order_model.getSub_order_no(), current_order_model.getShade_no(), current_order_model.getDesign_no(), 1, new SimpleDateFormat("dd/MM/yy mm:hh").format(new Date().getTime()));

                current_order_model.getOrderStatusModel().setOnReadyToDispatch(0);
                current_order_model.getOrderStatusModel().setOnDelivered(current_order_model.getOrderStatusModel().getOnDelivered() + 1);

                batch.update(daoOrder.getReference().document(current_order_model.getId()), objectToHashMap(current_order_model));
            } else {
                new CustomSnackUtil().showSnack(activity, "Incorrect Order Please Check It.", R.drawable.icon_warning);
            }
        }else if (position == 4) {
            if (current_order_model.getOrderStatusModel().getOnWarehouse() == 1) {
                OrderHistory orderHistoryModel = new OrderHistory(id, Const.ON_WAREHOUSE, Const.ON_DELIVERED, current_order_model.getOrder_no(), current_order_model.getSub_order_no(), current_order_model.getShade_no(), current_order_model.getDesign_no(), 1, new SimpleDateFormat("dd/MM/yy mm:hh").format(new Date().getTime()));

                current_order_model.getOrderStatusModel().setOnWarehouse(0);
                current_order_model.getOrderStatusModel().setOnDelivered(current_order_model.getOrderStatusModel().getOnDelivered() + 1);

                batch.update(daoOrder.getReference().document(current_order_model.getId()), objectToHashMap(current_order_model));
            } else {
                new CustomSnackUtil().showSnack(activity, "Incorrect Order Please Check It.", R.drawable.icon_warning);
            }
        } else if (position == 5) {
            if (current_order_model.getOrderStatusModel().getOnFinalDispatch() == 1) {
                OrderHistory orderHistoryModel = new OrderHistory(id, Const.ON_FINAL_DISPATCH, Const.ON_DELIVERED, current_order_model.getOrder_no(), current_order_model.getSub_order_no(), current_order_model.getShade_no(), current_order_model.getDesign_no(), 1, new SimpleDateFormat("dd/MM/yy mm:hh").format(new Date().getTime()));

                current_order_model.getOrderStatusModel().setOnFinalDispatch(0);
                current_order_model.getOrderStatusModel().setOnDelivered(current_order_model.getOrderStatusModel().getOnDelivered() + 1);

                batch.update(daoOrder.getReference().document(current_order_model.getId()), objectToHashMap(current_order_model));
            } else {
                new CustomSnackUtil().showSnack(activity, "Incorrect Order Please Check It.", R.drawable.icon_warning);
            }
        }else if (position == 7) {
            if (current_order_model.getOrderStatusModel().getOnDamage() == 1) {
                OrderHistory orderHistoryModel = new OrderHistory(id, Const.ON_DAMAGE, Const.ON_DELIVERED, current_order_model.getOrder_no(), current_order_model.getSub_order_no(), current_order_model.getShade_no(), current_order_model.getDesign_no(), 1, new SimpleDateFormat("dd/MM/yy mm:hh").format(new Date().getTime()));

                current_order_model.getOrderStatusModel().setOnDamage(0);
                current_order_model.getOrderStatusModel().setOnDelivered(current_order_model.getOrderStatusModel().getOnDelivered() + 1);

                batch.update(daoOrder.getReference().document(current_order_model.getId()), objectToHashMap(current_order_model));
            } else {
                new CustomSnackUtil().showSnack(activity, "Incorrect Order Please Check It.", R.drawable.icon_warning);
            }
        }
    }


    public void selectionModeUpdate() {
        int count = 0;
        for (int i = 0; i < itemModels.size(); i++) {
            if (itemModels.get(i).isSelected()) {
                count++;
            }
        }

        if (count == 0) {
            isSelectionMode = false;
            unlockAll();
            onSelectionCountListeners.onCountChanged(isSelectionMode, count, count == itemModels.size() ? true : false);
        } else {
            isSelectionMode = true;
            lockAll();
            onSelectionCountListeners.onCountChanged(isSelectionMode, count, count == itemModels.size() ? true : false);
        }
    }

    public void lockAll() {
        for (int i = 0; i < itemModels.size(); i++) {
            if (itemModels.get(i).isSwiped()) {
                binderHelper.closeLayout(String.valueOf(i));
            }
            binderHelper.lockSwipe(String.valueOf(i));
        }
    }

    public void unlockAll() {
        for (int i = 0; i < itemModels.size(); i++) {
            binderHelper.unlockSwipe(String.valueOf(i));
        }
    }

    public void select_all() {
        for (int i = 0; i < itemModels.size(); i++) {
            itemModels.get(i).setSelected(true);

            notifyItemChanged(i);
        }
        selectionModeUpdate();
    }

    public void unSelect_all() {
        for (int i = 0; i < itemModels.size(); i++) {
            itemModels.get(i).setSelected(false);
            notifyItemChanged(i);
        }
        selectionModeUpdate();
    }

    public void delete_all() {

        if (isUpdatePermissionGranted(PermissionState.NEW_ORDER.getValue())) {

                ArrayList<Integer> selected_item = new ArrayList<>();
                for (int i = 0; i < orderMasterList.size(); i++) {
                    if (itemModels.get(i).isSelected()) {
                        selected_item.add(itemModels.get(i).getPosition());
                    }
                }

            WriteBatch batch = FirebaseFirestore.getInstance().batch();
                DialogUtil.showProgressDialog(activity);
            for (int i = 0; i < selected_item.size(); i++) {
                if (itemModels.get(i).isSelected()) {
                    OrderMasterModel orderMasterModel = orderMasterList.get(selected_item.get(i));
                    if (orderMasterModel.getOrderStatusModel().getOnDelivered() == (orderMasterModel.getOrderStatusModel().getTotal() - 1)) {
                        if (orderMasterModel.getOrderStatusModel().getPending() == 1) {
                            select_dot(orderMasterModel, 0, batch);
                        } else if (orderMasterModel.getOrderStatusModel().getOnMachinePending() == 1) {
                            select_dot(orderMasterModel, 1, batch);
                        } else if (orderMasterModel.getOrderStatusModel().getOnMachineCompleted() == 1) {
                            select_dot(orderMasterModel, 2, batch);
                        } else if (orderMasterModel.getOrderStatusModel().getOnReadyToDispatch() == 1) {
                            select_dot(orderMasterModel, 3, batch);
                        } else if (orderMasterModel.getOrderStatusModel().getOnWarehouse() == 1) {
                            select_dot(orderMasterModel, 4, batch);
                        } else if (orderMasterModel.getOrderStatusModel().getOnFinalDispatch() == 1) {
                            select_dot(orderMasterModel, 5, batch);
                        } else if (orderMasterModel.getOrderStatusModel().getOnDamage() == 1) {
                            select_dot(orderMasterModel, 7, batch);
                        }
                    }
                }
            }
            batch.commit().addOnSuccessListener(aVoid -> {
                DialogUtil.hideProgressDialog();
                DialogUtil.showUpdateSuccessDialog(activity, false);
            }).addOnFailureListener(e -> {
                DialogUtil.hideProgressDialog();
                new CustomSnackUtil().showSnack(activity, "Something went wrong", R.drawable.error_msg_icon);
            });
        } else {
            DialogUtil.showAccessDeniedDialog(activity);
        }

    }

    @Override
    public int getItemCount() {
        return orderMasterList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        NearCompeleteOrderItemBinding binding;

        public ViewHolder(@Nonnull NearCompeleteOrderItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public void expand_item(int current_item, ImageView btn_down) {
        if (new_item != -1) {
            if (new_item != current_item) {
                old_item = new_item;

                itemModels.get(old_item).setExpanded(false);
                btn_down.setImageResource(R.drawable.down);
                new_item = current_item;
                itemModels.get(new_item).setExpanded(true);
                btn_down.setImageResource(R.drawable.up);
                notifyItemChanged(old_item);
                notifyItemChanged(new_item);

            } else {
                old_item = new_item;
                if (itemModels.get(new_item).isExpanded()) {
                    itemModels.get(new_item).setExpanded(false);
                    btn_down.setImageResource(R.drawable.down);
                } else {
                    itemModels.get(new_item).setExpanded(true);
                    btn_down.setImageResource(R.drawable.up);

                }
                new_item = current_item;
                notifyItemChanged(old_item);
                notifyItemChanged(new_item);
            }

        } else {
            new_item = current_item;
            itemModels.get(new_item).setExpanded(true);
            btn_down.setImageResource(R.drawable.up);
            notifyItemChanged(old_item);
            notifyItemChanged(new_item);
        }
    }
}