package com.example.texttile.presentation.screen.features.order_feature.order_master.new_order.orderlist;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import javax.annotation.Nonnull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.texttile.R;
import com.example.texttile.databinding.NewOrderItemBinding;
import com.example.texttile.presentation.screen.features.order_feature.order_master.new_order.order_sublist.SubOrderListActivity;
import com.example.texttile.data.model.OrderMasterModel;

import java.util.ArrayList;

public class NewOrderAdapter extends RecyclerView.Adapter<NewOrderAdapter.ViewHolder> {

    Activity activity;
    ArrayList<OrderMasterModel> orderMasterModelList;

    public NewOrderAdapter(Activity activity, ArrayList<OrderMasterModel> orderMasterModelList) {
        this.activity = activity;
        this.orderMasterModelList = orderMasterModelList;
    }


    @Nonnull
    @Override
    public ViewHolder onCreateViewHolder(@Nonnull ViewGroup parent, int viewType) {
        NewOrderItemBinding binding = NewOrderItemBinding.inflate(LayoutInflater.from(activity), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@Nonnull ViewHolder holder, int position) {
        Animation animation = AnimationUtils.loadAnimation(activity, R.anim.scale_up);
        holder.binding.layoutMain.startAnimation(animation);

        OrderMasterModel orderMasterModel = orderMasterModelList.get(position);

        holder.binding.textOrderNo.setText(orderMasterModel.getOrder_no());
        holder.binding.textPartyName.setText(orderMasterModel.getParty_name());
        holder.binding.textDesignNo.setText(orderMasterModel.getDesign_no());

        holder.itemView.setOnClickListener(view -> {
            Bundle bundle = new Bundle();
            bundle.putString("tracker", "sub_order");
            bundle.putString("order_no", orderMasterModel.getOrder_no());
            activity.startActivity(new Intent(activity, SubOrderListActivity.class).putExtras(bundle));
        });
    }

    @Override
    public int getItemCount() {
        return orderMasterModelList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {


        NewOrderItemBinding binding;

        public ViewHolder(@Nonnull NewOrderItemBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
        }
    }
}

