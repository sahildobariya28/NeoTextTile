package com.example.texttile.presentation.screen.features.allot_feature.employee_allotment.sleep;

import static com.example.texttile.presentation.ui.util.PrinterUtils.isBluetoothPermission;
import static com.example.texttile.presentation.ui.util.PrinterUtils.printBluetooth;
import static com.example.texttile.presentation.ui.util.PrinterUtils.requestBluetoothPermission;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.texttile.R;
import com.example.texttile.databinding.ActivityEmployeeAllotmentSleepBinding;
import com.example.texttile.data.dao.DAOEmployeeReading;
import com.example.texttile.presentation.ui.interfaces.EmptyDataListener;
import com.example.texttile.data.model.EmployeeReadingModel;
import com.example.texttile.presentation.ui.lib.snackUtil.CustomSnackUtil;
import com.example.texttile.presentation.ui.util.DialogUtil;

import java.util.ArrayList;

public class EmployeeAllotmentSleepActivity extends AppCompatActivity implements EmptyDataListener {

    ActivityEmployeeAllotmentSleepBinding binding;
    ArrayList<EmployeeReadingModel> employeeReadingHistoryList = new ArrayList<>();
    EmployeeSleepAdapter employeeSleepAdapter;

    String year, month;
    DAOEmployeeReading daoEmployeeReading;

    public EmployeeAllotmentSleepActivity() {
        daoEmployeeReading = new DAOEmployeeReading(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEmployeeAllotmentSleepBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        year = getIntent().getStringExtra("year");
        month = getIntent().getStringExtra("month");

        initToolbar();

        employee_allotment_list("");

        daoEmployeeReading.getEmployeeReadingListLiveData().observe(this, new Observer<ArrayList<EmployeeReadingModel>>() {
            @Override
            public void onChanged(ArrayList<EmployeeReadingModel> employeeMasterModels) {
                employeeReadingHistoryList = employeeMasterModels;
                employeeSleepAdapter = new EmployeeSleepAdapter(employeeReadingHistoryList, EmployeeAllotmentSleepActivity.this, EmployeeAllotmentSleepActivity.this);
                binding.recyclerView.setLayoutManager(new LinearLayoutManager(EmployeeAllotmentSleepActivity.this));
                binding.recyclerView.setAdapter(employeeSleepAdapter);
                if (DialogUtil.isProgressDialogShowing()) {
                    DialogUtil.hideProgressDialog();
                }
            }
        });

        binding.btnPrint.setOnClickListener(view -> {

            if (isBluetoothPermission(EmployeeAllotmentSleepActivity.this)) {
                setupPrinter("Employee Reading", employeeReadingHistoryList, year, month);
            } else {
                requestBluetoothPermission(EmployeeAllotmentSleepActivity.this);
            }

        });

        binding.btnBack.setOnClickListener(view1 -> {
            onBackPressed();
        });
    }

    public void employee_allotment_list(String filter) {
        if (employeeSleepAdapter == null) DialogUtil.showProgressDialog(EmployeeAllotmentSleepActivity.this);
        daoEmployeeReading.getEmployeeReadingList(filter);


    }


    public void initToolbar() {
        binding.textTitle.setText("Employee Reading History");

        binding.btnBack.setOnClickListener(view -> {
            onBackPressed();
        });
    }

    public void setupPrinter(String heading, ArrayList<EmployeeReadingModel> employeeAllotmentHistoryModels, String year, String month) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("[C]<b><font size='big'>" + heading + "</font></b>\n\n\n");

        StringBuilder employee_allot_item_list = new StringBuilder();
        for (int i = 0; i < employeeAllotmentHistoryModels.size(); i++) {
            if (employeeAllotmentHistoryModels.get(i).getDate().contains(month + "/" + year) || employeeAllotmentHistoryModels.get(i).getDate().contains(month + "/" + year.replace("20", ""))) {
                employee_allot_item_list.append("[L]<font size='normal'>" + employeeAllotmentHistoryModels.get(i).getEmp_name() + "</font> " + "[L]<font size='normal'>" + employeeAllotmentHistoryModels.get(i).getQty() + "</font> " + "[L]<font size='normal'>" + employeeAllotmentHistoryModels.get(i).getMachine_name() + "      " + "</font> " + "<font size='normal'>" + employeeAllotmentHistoryModels.get(i).getDate() + "</font>[L]\n");
            }
        }
        if (!employee_allot_item_list.toString().isEmpty()) {
            stringBuilder.append("[L]<font size='normal'><u><b>Emp Name</b></u></font>  [L]<font size='normal'><u><b>Qty</b></u></font> [L]<font size='normal'><u><b>Machine</b></u></font>  " + "<font size='normal'><u><b>Date(mm:hh)</b></u></font> [L]\n");
            stringBuilder.append("[C]------------------------------------------------\n");

            stringBuilder.append(employee_allot_item_list.toString());
            stringBuilder.append("[C]\n");
            stringBuilder.append("[C]\n");
            stringBuilder.append("[C]\n");
            stringBuilder.append("[C]\n");
            stringBuilder.append("[C]\n");
            stringBuilder.append("[C]\n");

            printBluetooth(this, stringBuilder.toString());
        } else {
            new CustomSnackUtil().showSnack(this, "No Data Found", R.drawable.icon_warning);
        }
    }

    @Override
    public void onDataChanged(boolean isDataChanged, int size) {
        if (isDataChanged) {
            if (size == 0) {
                binding.animationView.setVisibility(View.VISIBLE);
            } else {
                binding.animationView.setVisibility(View.GONE);
            }
        }
    }
}