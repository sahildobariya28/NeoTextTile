package com.example.texttile.presentation.ui.adapter;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import javax.annotation.Nonnull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.texttile.R;
import com.example.texttile.databinding.MachineReadingItemBinding;

import java.util.ArrayList;

public class MachineReadingAdapter extends RecyclerView.Adapter<MachineReadingAdapter.ViewHolder> {

    Context context;
    ArrayList<String> orderList;
    Dialog dialog;
    MachineReadingClickListener machineReadingClickListener;

    public MachineReadingAdapter(Context context, ArrayList<String> orderList, Dialog dialog, MachineReadingClickListener machineReadingClickListener) {
        this.context = context;
        this.orderList = orderList;
        this.dialog = dialog;
        this.machineReadingClickListener = machineReadingClickListener;
    }

    public interface MachineReadingClickListener {
        void onclick(int position, String orderNo);
    }

    @Nonnull
    @Override
    public ViewHolder onCreateViewHolder(@Nonnull ViewGroup parent, int viewType) {
        MachineReadingItemBinding binding = MachineReadingItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@Nonnull ViewHolder holder, int position) {
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.scale_up);
        holder.binding.layoutMain.startAnimation(animation);

        holder.binding.txtOrderNo.setText(orderList.get(position));

        holder.itemView.setOnClickListener(view -> {
            dialog.dismiss();
            machineReadingClickListener.onclick(position, orderList.get(position));
        });

    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        MachineReadingItemBinding binding;

        public ViewHolder(@Nonnull MachineReadingItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }
}
