package com.example.texttile.presentation.screen.features.storage_feature.ready_stock.list;

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
import com.example.texttile.databinding.ActivityReadyStockListBinding;
import com.example.texttile.data.dao.DAOExtraOrder;
import com.example.texttile.presentation.screen.features.storage_feature.ready_stock.readystock.ReadyStockActivity;
import com.example.texttile.presentation.ui.interfaces.EmptyDataListener;
import com.example.texttile.presentation.ui.interfaces.LoadFragmentChangeListener;
import com.example.texttile.presentation.ui.interfaces.OnSelectionCountListeners;
import com.example.texttile.data.model.ReadyStockModel;
import com.example.texttile.presentation.ui.util.DialogUtil;
import com.example.texttile.presentation.ui.util.PermissionState;

import java.util.ArrayList;

public class ReadyStockListActivity extends AppCompatActivity implements EmptyDataListener, OnSelectionCountListeners {

    ReadyStockListAdapter readyStockAdapter;
    String tracker;
    ActivityReadyStockListBinding binding;
    LoadFragmentChangeListener loadFragmentChangeListener;
    boolean selectAll = false, selectionMode = false, isDesignQtyUp = false;
    DAOExtraOrder daoExtraOrder;

    public ReadyStockListActivity(){
        daoExtraOrder = new DAOExtraOrder(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReadyStockListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        tracker = getIntent().getStringExtra("tracker");

            if (isInsertPermissionGranted(PermissionState.READY_STOCK.getValue())){
                binding.include.btnAddItem.setVisibility(View.VISIBLE);
            }else {
                binding.include.btnAddItem.setVisibility(View.GONE);
            }


        initToolbar();
        ready_stock_list("");

        binding.btnDelete.setOnClickListener(view -> {
            readyStockAdapter.delete_all();
        });

        binding.include.btnSelectAll.setOnClickListener(view -> {
            if (selectAll) {
                readyStockAdapter.unSelect_all();
                binding.include.btnSelectAll.setImageResource(R.drawable.check_box_unselected);
                selectAll = false;
            } else {
                readyStockAdapter.select_all();
                binding.include.btnSelectAll.setImageResource(R.drawable.check_box_selected);
                selectAll = true;
            }
        });

        daoExtraOrder.getReadyStockListLiveData().observe(this, new Observer<ArrayList<ReadyStockModel>>() {
            @Override
            public void onChanged(ArrayList<ReadyStockModel> readyStockModels) {
                readyStockAdapter = new ReadyStockListAdapter(readyStockModels, ReadyStockListActivity.this, loadFragmentChangeListener, ReadyStockListActivity.this, ReadyStockListActivity.this);
                binding.recyclerView.setLayoutManager(new LinearLayoutManager(ReadyStockListActivity.this));
                binding.recyclerView.setAdapter(readyStockAdapter);
                if (DialogUtil.isProgressDialogShowing()){
                    DialogUtil.hideProgressDialog();
                }
            }
        });

        binding.btnCloseDesign.setOnClickListener(view -> {
            binding.editSearchDesign.setText("");
        });

        binding.btnCloseShade.setOnClickListener(view -> {
            binding.editSearchShade.setText("");
        });

//        binding.btnDesignQty.setOnClickListener(view -> {
//
//            if (isDesignQtyUp) {
//                isDesignQtyUp = false;
//                binding.btnDesignQty.setText(" Design Qty ▼ ");
//                binding.btnDesignQty.setBackgroundResource(R.drawable.border_bg_unselected);
//            } else {
//                isDesignQtyUp = true;
//                binding.btnDesignQty.setText(" Design Qty ▲ ");
//                binding.btnDesignQty.setBackgroundResource(R.drawable.border_bg_selected);
//            }
//
//            daoExtraOrder.sortDesignQty(isDesignQtyUp);
//
//        });


    }

    public void ready_stock_list(String filter) {

        if (readyStockAdapter == null) DialogUtil.showProgressDialog(this);
        daoExtraOrder.getReadyStockList(filter);
    }


    private void initToolbar() {

        binding.include.textTitle.setText("Ready Stock");
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
                ready_stock_list(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        binding.editSearchDesign.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                daoExtraOrder.getReadyStockList(charSequence.toString(), binding.editSearchShade.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        binding.editSearchShade.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                daoExtraOrder.getReadyStockList(binding.editSearchDesign.getText().toString(), charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        binding.include.btnAddItem.setOnClickListener(view -> {
            startActivity(new Intent(this, ReadyStockActivity.class).putExtra("tracker", "add"));
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
            binding.include.textTitle.setText(count + " Selected");
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
            readyStockAdapter.unSelect_all();
            binding.include.textTitle.setText("Ready Stock");
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        daoExtraOrder = null;
    }
}