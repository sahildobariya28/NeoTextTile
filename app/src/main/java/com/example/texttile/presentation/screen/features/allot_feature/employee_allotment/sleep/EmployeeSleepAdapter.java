package com.example.texttile.presentation.screen.features.allot_feature.employee_allotment.sleep;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.recyclerview.widget.RecyclerView;

import com.example.texttile.R;
import com.example.texttile.databinding.EmpAllotItemSleepBinding;
import com.example.texttile.presentation.ui.interfaces.EmptyDataListener;
import com.example.texttile.data.model.EmployeeReadingModel;

import java.util.ArrayList;

import javax.annotation.Nonnull;

public class EmployeeSleepAdapter extends RecyclerView.Adapter<EmployeeSleepAdapter.ViewHolder>{

    Activity activity;
    ArrayList<EmployeeReadingModel> employeeAllotmentHistoryList;
    EmptyDataListener emptyDataListener;

    public EmployeeSleepAdapter(ArrayList<EmployeeReadingModel> employeeAllotmentHistoryList, Activity activity, EmptyDataListener emptyDataListener) {
        this.activity = activity;
        this.employeeAllotmentHistoryList = employeeAllotmentHistoryList;
        this.emptyDataListener = emptyDataListener;

        emptyDataListener.onDataChanged(true, getItemCount());
    }

    @Nonnull
    @Override
    public ViewHolder onCreateViewHolder(@Nonnull ViewGroup parent, int viewType) {
        EmpAllotItemSleepBinding binding = EmpAllotItemSleepBinding.inflate(LayoutInflater.from(activity), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        EmployeeReadingModel employeeReadingModel = employeeAllotmentHistoryList.get(position);

        Animation animation = AnimationUtils.loadAnimation(activity, R.anim.scale_up);
        holder.binding.layoutMain.startAnimation(animation);


        holder.binding.txtEmpName.setText(employeeReadingModel.getEmp_name());
        holder.binding.txtMachineName.setText(String.valueOf(employeeReadingModel.getMachine_name()));
        holder.binding.txtDate.setText(employeeReadingModel.getDate());
        holder.binding.textQty.setText(employeeReadingModel.getQty());

    }

    @Override
    public int getItemCount() {
        return employeeAllotmentHistoryList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        EmpAllotItemSleepBinding binding;
        public ViewHolder(@Nonnull EmpAllotItemSleepBinding binding) {
            super(binding.getRoot());

            this.binding = binding;

        }
    }
}
