package com.example.texttile.presentation.screen.features.order_feature.order_history;

import static com.example.texttile.presentation.ui.util.PermissionUtil.isDeletePermissionGranted;
import static com.example.texttile.presentation.ui.util.PermissionUtil.isReadPermissionGranted;

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
import android.widget.ImageView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.texttile.R;
import com.example.texttile.databinding.ItemViewHistoryBinding;
import com.example.texttile.data.dao.DaoOrderHistory;
import com.example.texttile.presentation.ui.interfaces.EmptyDataListener;
import com.example.texttile.presentation.ui.interfaces.OnSelectionCountListeners;
import com.example.texttile.presentation.screen.features.order_feature.order_master.new_order.order_tracker.OrderTrackerActivity;
import com.example.texttile.data.model.ItemState;
import com.example.texttile.data.model.OrderHistory;
import com.example.texttile.presentation.ui.lib.new_swipe_feature.SwipeRevealLayout;
import com.example.texttile.presentation.ui.lib.new_swipe_feature.ViewBinderHelper;
import com.example.texttile.presentation.ui.lib.snackUtil.CustomSnackUtil;
import com.example.texttile.presentation.ui.util.DialogUtil;
import com.example.texttile.presentation.ui.util.PermissionState;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;

import javax.annotation.Nonnull;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.ViewHolder> {

    Activity activity;
    int old_item = -1, new_item = -1;
    EmptyDataListener emptyDataListener;

    int item_id;
    ArrayList<OrderHistory> orderHistoryList;

    ArrayList<ItemState> itemModels = new ArrayList<>();
    private final ViewBinderHelper binderHelper = new ViewBinderHelper();

    public boolean isSelectionMode = false;
    OnSelectionCountListeners onSelectionCountListeners;
    DaoOrderHistory daoOrderHistory;

    public OrderHistoryAdapter(ArrayList<OrderHistory> orderHistoryList, Activity activity, EmptyDataListener emptyDataListener, OnSelectionCountListeners onSelectionCountListeners, DaoOrderHistory daoOrderHistory) {

        this.orderHistoryList = orderHistoryList;
        this.activity = activity;
        this.emptyDataListener = emptyDataListener;
        this.onSelectionCountListeners = onSelectionCountListeners;
        this.daoOrderHistory = daoOrderHistory;

        emptyDataListener.onDataChanged(true, getItemCount());
        itemModels.clear();
        for (int i = 0; i < orderHistoryList.size(); i++) {
            itemModels.add(new ItemState(false, false, false, i));
        }
        binderHelper.setOpenOnlyOne(true);
    }

    @Nonnull
    @Override
    public OrderHistoryAdapter.ViewHolder onCreateViewHolder(@Nonnull ViewGroup parent, int viewType) {
        ItemViewHistoryBinding binding = ItemViewHistoryBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new OrderHistoryAdapter.ViewHolder(binding);
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
                Log.d("jkdksdf324", "onSlide: " + "yes view is slide");
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

        OrderHistory orderHistory = orderHistoryList.get(position);
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

        holder.binding.textOrderNo.setText(orderHistory.getSubOrderNo());
        holder.binding.textDesignNo.setText(orderHistory.getShadeNo());
        holder.binding.textShadeNo.setText(orderHistory.getShadeNo());
        holder.binding.textQty.setText("\\" + orderHistory.getQty());
        holder.binding.textSource.setText(orderHistory.getSource());
        holder.binding.textDestination.setText(orderHistory.getDestination());
        holder.binding.textDate.setText(orderHistory.getDateTime());
        holder.binding.textSlip.setText(orderHistory.getSlipNo());
        holder.binding.textReasons.setText(orderHistory.getReason());



        holder.binding.btnTracker.setOnClickListener(view -> {

            if (isReadPermissionGranted(PermissionState.ORDER_TRACKER.getValue())){
                Bundle bundle = new Bundle();
                bundle.putString("order_no", orderHistory.getSubOrderNo());
                activity.startActivity(new Intent(activity, OrderTrackerActivity.class).putExtras(bundle));
            }else {
                DialogUtil.showAccessDeniedDialog(activity);
            }


        });
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

        if (isDeletePermissionGranted(PermissionState.NEW_ORDER.getValue())) {
            final Dialog dialog = new Dialog(activity);
            dialog.setContentView(R.layout.delete_conform);
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            CardView btn_cancel = dialog.findViewById(R.id.btn_cancel);
            CardView btn_delete = dialog.findViewById(R.id.btn_delete);

            btn_cancel.setOnClickListener(v1 -> dialog.dismiss());
            btn_delete.setOnClickListener(v1 -> {
                dialog.dismiss();

                ArrayList<Integer> selected_item = new ArrayList<>();
                for (int i = 0; i < orderHistoryList.size(); i++) {
                    if (itemModels.get(i).isSelected()) {
                        selected_item.add(itemModels.get(i).getPosition());
                    }
                }
                daoOrderHistory.removeSelectedItem(selected_item, new OnSuccessListener() {
                    @Override
                    public void onSuccess(Object o) {
                        DialogUtil.hideProgressDialog();
                        DialogUtil.showDeleteDialog(activity);
                        onSelectionCountListeners.onCountChanged(true, 0, false);
                    }
                }, new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        new CustomSnackUtil().showSnack(activity, "Something Want Wrong", R.drawable.error_msg_icon);
                    }
                });
            });

            dialog.show();
        } else {
            DialogUtil.showAccessDeniedDialog(activity);
        }
    }

    @Override
    public int getItemCount() {
        return orderHistoryList.size();
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

    public class ViewHolder extends RecyclerView.ViewHolder {

        ItemViewHistoryBinding binding;

        public ViewHolder(@Nonnull ItemViewHistoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}

