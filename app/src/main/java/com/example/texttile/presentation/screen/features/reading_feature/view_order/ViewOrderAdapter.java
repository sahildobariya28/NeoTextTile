package com.example.texttile.presentation.screen.features.reading_feature.view_order;

import static com.example.texttile.presentation.ui.util.PermissionUtil.isReadPermissionGranted;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.example.texttile.R;
import com.example.texttile.databinding.ItemViewOrderBinding;
import com.example.texttile.presentation.screen.features.order_feature.order_master.new_order.order_tracker.OrderTrackerActivity;
import com.example.texttile.data.model.OrderMasterModel;
import com.example.texttile.presentation.ui.util.DialogUtil;
import com.example.texttile.presentation.ui.util.PermissionState;

import java.util.Objects;

import javax.annotation.Nonnull;

public class ViewOrderAdapter extends RecyclerView.Adapter<ViewOrderAdapter.ViewHolder> {

    ViewOrderViewModel viewModel;
    LifecycleOwner lifecycleOwner;


    public ViewOrderAdapter(ViewOrderViewModel viewModel, LifecycleOwner lifecycleOwner) {

        this.viewModel = viewModel;
        this.lifecycleOwner = lifecycleOwner;

        viewModel.binderHelper.setOpenOnlyOne(true);

    }

    @Nonnull
    @Override
    public ViewOrderAdapter.ViewHolder onCreateViewHolder(@Nonnull ViewGroup parent, int viewType) {
        ItemViewOrderBinding binding = ItemViewOrderBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewOrderAdapter.ViewHolder(binding);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@Nonnull ViewHolder holder, int position) {

        OrderMasterModel orderModel = Objects.requireNonNull(viewModel.orderMasterModels.getValue()).get(position);

        if (orderModel != null) {
            holder.binding.setOrderModel(orderModel);

            if (viewModel.itemModels.getValue().get(position).isExpanded()) {
                holder.binding.detailContainer.setVisibility(View.VISIBLE);
                holder.binding.btnDown.setImageResource(R.drawable.up);
            } else {
                holder.binding.detailContainer.setVisibility(View.GONE);
                holder.binding.btnDown.setImageResource(R.drawable.down);
            }
            holder.binding.viewMain.setOnClickListener(view -> {
                if (viewModel.selectionPermission) {
                    if (viewModel.selectionMode.getValue()) {
                        if (viewModel.itemModels.getValue().get(holder.getAdapterPosition()).isSelected()) {
                            holder.binding.viewMain.setBackground(viewModel.activity.getDrawable(R.drawable.border_bg_unselected));
                            viewModel.itemModels.getValue().get(holder.getAdapterPosition()).setSelected(false);
                            viewModel.itemModels.setValue(viewModel.itemModels.getValue());
                            viewModel.selectionModeUpdate();
                        } else {
                            viewModel.binderHelper.closeLayout(String.valueOf(holder.getAdapterPosition()));
                            holder.binding.viewMain.setBackground(viewModel.activity.getDrawable(R.drawable.border_bg_selected));
                            viewModel.itemModels.getValue().get(holder.getAdapterPosition()).setSelected(true);
                            viewModel.itemModels.setValue(viewModel.itemModels.getValue());
                            viewModel.selectionModeUpdate();
                        }
                    } else {
                        viewModel.expand_item(holder.getAdapterPosition(), holder.binding.btnDown);
                    }

                } else {

                    viewModel.expand_item(holder.getAdapterPosition(), holder.binding.btnDown);
                }
            });

            holder.binding.viewMain.setOnLongClickListener(view -> {
                if (viewModel.selectionPermission) {
                    if (viewModel.itemModels.getValue().get(holder.getAdapterPosition()).isSelected()) {
                        holder.binding.viewMain.setBackground(viewModel.activity.getDrawable(R.drawable.border_bg_unselected));
                        viewModel.itemModels.getValue().get(holder.getAdapterPosition()).setSelected(false);
                        viewModel.itemModels.setValue(viewModel.itemModels.getValue());
                        viewModel.selectionModeUpdate();
                    } else {
                        viewModel.binderHelper.closeLayout(String.valueOf(holder.getAdapterPosition()));
                        holder.binding.viewMain.setBackground(viewModel.activity.getDrawable(R.drawable.border_bg_selected));
                        viewModel.itemModels.getValue().get(holder.getAdapterPosition()).setSelected(true);
                        viewModel.itemModels.setValue(viewModel.itemModels.getValue());
                        viewModel.selectionModeUpdate();
                    }
                }
                return true;
            });


            Animation animation = AnimationUtils.loadAnimation(viewModel.activity, R.anim.scale_up);
            holder.binding.layoutMain.startAnimation(animation);


            switch (viewModel.itemId.getValue()) {
                case R.id.btn_padding:
                    holder.binding.setTotal("\\" + orderModel.getOrderStatusModel().getPending());
                    break;
                case R.id.btn_on_machine_padding:
                    holder.binding.setTotal("\\" + orderModel.getOrderStatusModel().getOnMachinePending());
                    break;
                case R.id.btn_on_machine_completed:
                    holder.binding.setTotal("\\" + orderModel.getOrderStatusModel().getOnMachineCompleted());
                    break;
                case R.id.btn_ready_to_dispatch:
                    holder.binding.setTotal("\\" + orderModel.getOrderStatusModel().getOnReadyToDispatch());
                    break;
                case R.id.btn_warehouse:
                    holder.binding.setTotal("\\" + orderModel.getOrderStatusModel().getOnWarehouse());
                    break;
                case R.id.btn_final_dispatch:
                    holder.binding.setTotal("\\" + orderModel.getOrderStatusModel().getOnFinalDispatch());
                    break;
                case R.id.btn_delivered:
                    holder.binding.setTotal("\\" + orderModel.getOrderStatusModel().getOnDelivered());
                    break;
                case R.id.btn_damage:
                    holder.binding.setTotal("\\" + orderModel.getOrderStatusModel().getOnDamage());
                    break;
            }


            holder.binding.btnTracker.setOnClickListener(view -> {

                if (isReadPermissionGranted(PermissionState.ORDER_TRACKER.getValue())) {
                    Bundle bundle = new Bundle();
                    bundle.putString("order_no", orderModel.getSub_order_no());
                    viewModel.activity.startActivity(new Intent(viewModel.activity, OrderTrackerActivity.class).putExtras(bundle));
                } else {
                    DialogUtil.showAccessDeniedDialog(viewModel.activity);
                }

            });
        }


    }


    @Override
    public int getItemCount() {
        return viewModel.orderMasterModels.getValue().size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        ItemViewOrderBinding binding;

        public ViewHolder(@Nonnull ItemViewOrderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }


}
