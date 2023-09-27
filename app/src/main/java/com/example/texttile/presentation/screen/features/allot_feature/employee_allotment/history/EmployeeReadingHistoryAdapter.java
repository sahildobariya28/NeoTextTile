package com.example.texttile.presentation.screen.features.allot_feature.employee_allotment.history;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import javax.annotation.Nonnull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.texttile.presentation.ui.lib.new_swipe_feature.ViewBinderHelper;
import com.example.texttile.R;
import com.example.texttile.databinding.EmployeeAllotItemBinding;
import com.example.texttile.presentation.ui.interfaces.EmptyDataListener;
import com.example.texttile.data.model.EmployeeReadingModel;

import java.util.ArrayList;

public class EmployeeReadingHistoryAdapter extends RecyclerView.Adapter<EmployeeReadingHistoryAdapter.ViewHolder>{

    Activity activity;
    ArrayList<EmployeeReadingModel> employeeAllotmentHistoryList;
    EmptyDataListener emptyDataListener;
    private final ViewBinderHelper binderHelper = new ViewBinderHelper();

    public EmployeeReadingHistoryAdapter(ArrayList<EmployeeReadingModel> employeeAllotmentHistoryList, Activity activity, EmptyDataListener emptyDataListener) {
        this.activity = activity;
        this.employeeAllotmentHistoryList = employeeAllotmentHistoryList;
        this.emptyDataListener = emptyDataListener;

        emptyDataListener.onDataChanged(true, getItemCount());
        binderHelper.setOpenOnlyOne(true);
    }

    @Nonnull
    @Override
    public ViewHolder onCreateViewHolder(@Nonnull ViewGroup parent, int viewType) {
        EmployeeAllotItemBinding binding = EmployeeAllotItemBinding.inflate(LayoutInflater.from(activity), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        binderHelper.bind(holder.binding.swipeRevealLayout, String.valueOf(position));
        binderHelper.lockSwipe(String.valueOf(position));

        EmployeeReadingModel employeeReadingModel = employeeAllotmentHistoryList.get(position);

        Animation animation = AnimationUtils.loadAnimation(activity, R.anim.scale_up);
        holder.binding.layoutMain.startAnimation(animation);


        holder.binding.txtEmployeeName.setText(employeeReadingModel.getEmp_name());
        holder.binding.txtMachineName.setText(String.valueOf(employeeReadingModel.getMachine_name()));
        holder.binding.textDate.setText(employeeReadingModel.getDate());
        holder.binding.textQty.setText("\\" + employeeReadingModel.getQty());


    }

    @Override
    public int getItemCount() {
        return employeeAllotmentHistoryList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        EmployeeAllotItemBinding binding;
        public ViewHolder(@Nonnull EmployeeAllotItemBinding binding) {
            super(binding.getRoot());

            this.binding = binding;

        }
    }
}
