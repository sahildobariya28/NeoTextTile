package com.example.texttile.presentation.screen.features.allot_feature.allot_program.activity.list;

import static com.example.texttile.presentation.ui.util.PermissionUtil.isInsertPermissionGranted;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.texttile.R;
import com.example.texttile.databinding.ActivityAllotProgramListBinding;
import com.example.texttile.data.dao.DaoAllotProgram;
import com.example.texttile.presentation.ui.interfaces.EmptyDataListener;
import com.example.texttile.presentation.ui.interfaces.OnSelectionCountListeners;
import com.example.texttile.presentation.screen.features.allot_feature.allot_program.activity.allot.AllotProgramActivity;
import com.example.texttile.presentation.ui.util.DialogUtil;
import com.example.texttile.presentation.ui.util.PermissionState;

public class AllotProgramListActivity extends AppCompatActivity implements EmptyDataListener, OnSelectionCountListeners {

    AllotProgramAdapter allotProgramAdapter;
    String tracker;
    ActivityAllotProgramListBinding binding;
    DaoAllotProgram daoAllotProgram;
    boolean selectAll = false, selectionMode = false;

    public AllotProgramListActivity() {
        this.daoAllotProgram = new DaoAllotProgram(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAllotProgramListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        tracker = getIntent().getStringExtra("tracker");


        if (isInsertPermissionGranted(PermissionState.ALLOT_PROGRAM.getValue())) {
            binding.include.btnAddItem.setVisibility(View.VISIBLE);
        } else {
            binding.include.btnAddItem.setVisibility(View.GONE);
        }
        initToolbar();
        allot_program_list("");

        binding.btnDelete.setOnClickListener(view -> {
            allotProgramAdapter.delete_all();
        });
        binding.include.btnSelectAll.setOnClickListener(view -> {
            if (selectAll) {
                allotProgramAdapter.unSelect_all();
                binding.include.btnSelectAll.setImageResource(R.drawable.check_box_unselected);
                selectAll = false;
            } else {
                allotProgramAdapter.select_all();
                binding.include.btnSelectAll.setImageResource(R.drawable.check_box_selected);
                selectAll = true;
            }
        });

        daoAllotProgram.getAllotProgramListLiveData().observe(this, allotProgramModels -> {

            allotProgramAdapter = new AllotProgramAdapter(allotProgramModels, AllotProgramListActivity.this, AllotProgramListActivity.this, AllotProgramListActivity.this);
            binding.recyclerView.setLayoutManager(new LinearLayoutManager(AllotProgramListActivity.this));
            binding.recyclerView.setAdapter(allotProgramAdapter);
            allotProgramAdapter.selectionModeUpdate();
            if (DialogUtil.isProgressDialogShowing()) {
                DialogUtil.hideProgressDialog();
            }
        });
    }

    public void allot_program_list(String filter) {
        if (allotProgramAdapter == null)
            DialogUtil.showProgressDialog(AllotProgramListActivity.this);
        daoAllotProgram.read(filter);
    }


    private void initToolbar() {

        binding.include.textTitle.setText("Allot Program");
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
                allot_program_list(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        binding.include.btnAddItem.setOnClickListener(view -> {
            if (isInsertPermissionGranted(PermissionState.ALLOT_PROGRAM.getValue())) {
                startActivity(new Intent(this, AllotProgramActivity.class).putExtra("tracker", "add"));
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
            binding.include.textTitle.setText(count + " Selected");
        } else {
            binding.include.btnAddItem.setVisibility(View.VISIBLE);
            binding.include.btnSelectAll.setVisibility(View.GONE);
            binding.include.btnSearch.setVisibility(View.VISIBLE);
            binding.viewSelectionMode.setVisibility(View.GONE);
            binding.include.textTitle.setText("Allot Program");
        }
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
            allotProgramAdapter.unSelect_all();
            binding.include.textTitle.setText("Allot Program");
        } else {
            super.onBackPressed();
        }
    }
}