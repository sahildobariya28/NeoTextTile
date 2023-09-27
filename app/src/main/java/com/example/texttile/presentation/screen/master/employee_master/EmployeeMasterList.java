package com.example.texttile.presentation.screen.master.employee_master;

import static com.example.texttile.presentation.ui.util.PermissionUtil.isInsertPermissionGranted;

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

import com.example.texttile.R;
import com.example.texttile.databinding.ActivityEmployeeMasterListBinding;
import com.example.texttile.data.dao.DAOEmployeeMaster;
import com.example.texttile.presentation.ui.interfaces.EmptyDataListener;
import com.example.texttile.presentation.ui.interfaces.LoadFragmentChangeListener;
import com.example.texttile.presentation.ui.interfaces.OnSelectionCountListeners;
import com.example.texttile.data.model.EmployeeMasterModel;
import com.example.texttile.presentation.ui.util.DialogUtil;
import com.example.texttile.presentation.ui.util.PermissionState;

import java.util.ArrayList;

public class EmployeeMasterList extends AppCompatActivity implements EmptyDataListener, OnSelectionCountListeners {

    EmployeeMasterAdapter employeeMasterAdapter;
    String tracker;
    ActivityEmployeeMasterListBinding binding;
    LoadFragmentChangeListener loadFragmentChangeListener;
    DAOEmployeeMaster daoEmployeeMaster;
    boolean selectAll = false, selectionMode = false;

    public EmployeeMasterList() {
        daoEmployeeMaster = new DAOEmployeeMaster(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEmployeeMasterListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        tracker = getIntent().getStringExtra("tracker");


        if (isInsertPermissionGranted(PermissionState.EMPLOYEE_MASTER.getValue())) {
            binding.include.btnAddItem.setVisibility(View.VISIBLE);
        } else {
            binding.include.btnAddItem.setVisibility(View.GONE);
        }
        initToolbar();
        employee_master_list("");

        binding.btnDelete.setOnClickListener(view -> {
            employeeMasterAdapter.delete_all();
        });
        binding.include.btnSelectAll.setOnClickListener(view -> {
            if (selectAll) {
                employeeMasterAdapter.unSelect_all();
                binding.include.btnSelectAll.setImageResource(R.drawable.check_box_unselected);
                selectAll = false;
            } else {
                employeeMasterAdapter.select_all();
                binding.include.btnSelectAll.setImageResource(R.drawable.check_box_selected);
                selectAll = true;
            }
        });
        daoEmployeeMaster.getEmployeeMasterListLiveData().observe(this, new Observer<ArrayList<EmployeeMasterModel>>() {
            @Override
            public void onChanged(ArrayList<EmployeeMasterModel> employeeMasterModels) {
                employeeMasterAdapter = new EmployeeMasterAdapter(employeeMasterModels, EmployeeMasterList.this, loadFragmentChangeListener, EmployeeMasterList.this::onDataChanged, EmployeeMasterList.this, daoEmployeeMaster);
                binding.recyclerView.setLayoutManager(new LinearLayoutManager(EmployeeMasterList.this));
                binding.recyclerView.setAdapter(employeeMasterAdapter);
                employeeMasterAdapter.selectionModeUpdate();
                if (DialogUtil.isProgressDialogShowing()) {
                    DialogUtil.hideProgressDialog();
                }
            }
        });
    }

    public void employee_master_list(String filter) {
        if (employeeMasterAdapter == null) DialogUtil.showProgressDialog(this);
        daoEmployeeMaster.getEmployeeMasterList(filter);
    }

    private void initToolbar() {

        binding.include.textTitle.setText("Employee Master");
        binding.include.btnSearch.setVisibility(View.VISIBLE);

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
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                employee_master_list(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.include.btnAddItem.setOnClickListener(view -> {

            if (isInsertPermissionGranted(PermissionState.EMPLOYEE_MASTER.getValue())) {
                startActivity(new Intent(this, EmployeeMasterActivity.class).putExtra("tracker", "add"));
            } else {
                DialogUtil.showAccessDeniedDialog(this);
            }


        });
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
    public void onCountChanged(boolean isSelectionMode, int count, boolean isSelectAll) {
        if (isSelectAll) {
            binding.include.btnSelectAll.setImageResource(R.drawable.check_box_selected);
            selectAll = true;
        } else {
            binding.include.btnSelectAll.setImageResource(R.drawable.check_box_unselected);
            selectAll = false;
        }

        if (isSelectionMode) {

            selectionMode = true;
            binding.include.btnAddItem.setVisibility(View.GONE);
            binding.include.btnSelectAll.setVisibility(View.VISIBLE);
            binding.include.btnSearch.setVisibility(View.GONE);
            binding.include.viewSearch.setVisibility(View.GONE);
            binding.viewSelectionMode.setVisibility(View.VISIBLE);
            binding.include.textTitle.setText("Selected " + "(" + count + ")");
        } else {
            binding.include.btnAddItem.setVisibility(View.VISIBLE);
            binding.include.btnSelectAll.setVisibility(View.GONE);
            binding.viewSelectionMode.setVisibility(View.GONE);
            binding.include.btnSearch.setVisibility(View.VISIBLE);
            binding.include.textTitle.setText("Employee Master");
        }

//        if (isSelectionMode) {
//            binding.include.textTitle.setText(String.valueOf(count));
//        } else {
//            binding.include.textTitle.setText("Employee Master");
//        }
    }

    @Override
    public void onBackPressed() {
        if (selectionMode) {
            selectionMode = false;
            binding.include.btnAddItem.setVisibility(View.VISIBLE);
            binding.include.btnSelectAll.setVisibility(View.GONE);
            binding.include.btnSearch.setVisibility(View.VISIBLE);
            binding.viewSelectionMode.setVisibility(View.GONE);
            if (!binding.include.editSearch.getText().toString().isEmpty()) {
                binding.include.viewSearch.setVisibility(View.VISIBLE);
            } else {
                binding.include.viewSearch.setVisibility(View.GONE);
            }
            employeeMasterAdapter.unSelect_all();
            binding.include.textTitle.setText("Employee Master");
        } else {
            super.onBackPressed();
        }
    }
}