package com.example.texttile.presentation.screen.features.allot_feature.employee_allotment.list;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.texttile.databinding.ActivityEmployeeAllotListBinding;
import com.example.texttile.data.dao.DAOEmployeeMaster;
import com.example.texttile.presentation.screen.features.allot_feature.employee_allotment.history.EmployeeReadingHistoryListActivity;
import com.example.texttile.presentation.ui.interfaces.EmptyDataListener;
import com.example.texttile.data.model.EmployeeMasterModel;
import com.example.texttile.presentation.ui.util.DialogUtil;

import java.util.ArrayList;

public class EmployeeAllotListActivity extends AppCompatActivity implements EmptyDataListener {

    EmployeeAllotmentAdapter employeeAllotmentAdapter;
    ActivityEmployeeAllotListBinding binding;
    DAOEmployeeMaster daoEmployeeMaster;

    public EmployeeAllotListActivity(){
        daoEmployeeMaster = new DAOEmployeeMaster(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEmployeeAllotListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initToolbar();
        employee_allotment_list("");

        daoEmployeeMaster.getEmployeeMasterListLiveData().observe(this, new Observer<ArrayList<EmployeeMasterModel>>() {
            @Override
            public void onChanged(ArrayList<EmployeeMasterModel> employeeMasterModels) {
                employeeAllotmentAdapter = new EmployeeAllotmentAdapter(employeeMasterModels, EmployeeAllotListActivity.this,EmployeeAllotListActivity.this);
                binding.recyclerView.setLayoutManager(new LinearLayoutManager(EmployeeAllotListActivity.this));
                binding.recyclerView.setAdapter(employeeAllotmentAdapter);
                if (DialogUtil.isProgressDialogShowing()){
                    DialogUtil.hideProgressDialog();
                }
            }
        });
    }

    public void employee_allotment_list(String filter) {
        if (employeeAllotmentAdapter == null) DialogUtil.showProgressDialog(EmployeeAllotListActivity.this);
        daoEmployeeMaster.getEmployeeMasterList(filter);


    }


    private void initToolbar() {
        binding.include.btnHistory.setVisibility(View.VISIBLE);
        binding.include.btnSearch.setVisibility(View.VISIBLE);
        binding.include.btnAddItem.setVisibility(View.GONE);

        binding.include.textTitle.setText("Employee Allotment");

        binding.include.btnBack.setOnClickListener(view -> {
            onBackPressed();
        });

        binding.include.btnSearch.setOnClickListener(view -> {
            if (binding.include.viewSearch.getVisibility() == View.VISIBLE) {
                binding.include.viewSearch.setVisibility(View.GONE);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(binding.include.editSearch.getWindowToken(), 0);
            } else {
                binding.include.viewSearch.setVisibility(View.VISIBLE);
                binding.include.editSearch.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(binding.include.editSearch, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        binding.include.btnClose.setOnClickListener(view -> {
            if (binding.include.editSearch.getText().toString().isEmpty()) {
                binding.include.viewSearch.setVisibility(View.GONE);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(binding.include.editSearch.getWindowToken(), 0);

            } else {
                binding.include.editSearch.setText("");
            }
        });
        binding.include.editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                employee_allotment_list(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) { }
        });

        binding.include.btnHistory.setOnClickListener(view -> {
            startActivity(new Intent(this, EmployeeReadingHistoryListActivity.class).putExtra("tracker", "add"));
        });

//        binding.include.btnAddItem.setOnClickListener(view -> {
//            if (isInsertPermissionGranted(PermissionState.DESIGN_MASTER.getValue())) {
//                startActivity(new Intent(this, EmployeeReadingHistoryListActivity.class).putExtra("tracker", "add"));
//            } else {
//                DialogUtil.showAccessDeniedDialog(this);
//            }
//
//        });

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

    @Override
    public void onBackPressed() {
            super.onBackPressed();
    }
}