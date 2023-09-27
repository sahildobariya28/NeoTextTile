package com.example.texttile.presentation.screen.master.shade_master.list;

import static com.example.texttile.presentation.ui.util.DialogUtil.showProgressDialog;
import static com.example.texttile.presentation.ui.util.PermissionUtil.isInsertPermissionGranted;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.example.texttile.R;
import com.example.texttile.data.dao.DAOShadeMaster;
import com.example.texttile.databinding.ActivityShadeMasterSubListBinding;
import com.example.texttile.presentation.screen.master.shade_master.shade_master.ShadeMultipleActivity;
import com.example.texttile.presentation.ui.interfaces.EmptyDataListener;
import com.example.texttile.presentation.ui.interfaces.LoadFragmentChangeListener;
import com.example.texttile.presentation.ui.interfaces.OnSelectionCountListeners;
import com.example.texttile.presentation.ui.util.DialogUtil;
import com.example.texttile.presentation.ui.util.PermissionState;

public class ShadeMasterSubListActivity extends AppCompatActivity implements EmptyDataListener, OnSelectionCountListeners {

    ActivityShadeMasterSubListBinding binding;
    FilterShadeMasterAdapter filterShadeMasterAdapter;
    String tracker;
    LoadFragmentChangeListener loadFragmentChangeListener;
    DAOShadeMaster daoShadeMaster;
    boolean selectAll = false, selectionMode = false;

    public ShadeMasterSubListActivity() {
        daoShadeMaster = new DAOShadeMaster(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityShadeMasterSubListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        tracker = getIntent().getStringExtra("tracker");

        initToolbar();

        shade_master_list("");

        daoShadeMaster.groupShadeMasterListLiveData().observe(this, shadeMasterModels -> {

            filterShadeMasterAdapter = new FilterShadeMasterAdapter(shadeMasterModels, ShadeMasterSubListActivity.this, loadFragmentChangeListener, ShadeMasterSubListActivity.this::onDataChanged, ShadeMasterSubListActivity.this::onCountChanged, daoShadeMaster);
            binding.recyclerView.setLayoutManager(new LinearLayoutManager(ShadeMasterSubListActivity.this));
            binding.recyclerView.setAdapter(filterShadeMasterAdapter);

            if (DialogUtil.isProgressDialogShowing()) {
                DialogUtil.hideProgressDialog();
            }
        });
    }

    private void initToolbar() {

        binding.include.textTitle.setText("Shade Master");
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

                shade_master_list(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.include.btnAddItem.setOnClickListener(view -> {

            if (isInsertPermissionGranted(PermissionState.SHADE_MASTER.getValue())) {
                startActivity(new Intent(this, ShadeMultipleActivity.class).putExtra("tracker", "add"));
            } else {
                DialogUtil.showAccessDeniedDialog(this);
            }
        });
    }

    public void shade_master_list(String filter) {

        if (filterShadeMasterAdapter == null) showProgressDialog(this);
        daoShadeMaster.getGroupShadeMaster(filter);
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

        selectionMode = isSelectionMode;
        if (isSelectionMode) {
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
            binding.include.textTitle.setText("Shade Master");
        }
    }
}