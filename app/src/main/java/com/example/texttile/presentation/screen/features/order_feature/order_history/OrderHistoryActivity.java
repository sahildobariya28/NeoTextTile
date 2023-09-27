package com.example.texttile.presentation.screen.features.order_feature.order_history;

import static com.example.texttile.presentation.ui.util.DialogUtil.hideProgressDialog;
import static com.example.texttile.presentation.ui.util.PermissionUtil.isReadPermissionGranted;
import static com.example.texttile.presentation.ui.util.Util.convertDpToPixel;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.PopupMenu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.texttile.R;
import com.example.texttile.databinding.ActivityViewHistoryBinding;
import com.example.texttile.data.dao.DaoOrderHistory;
import com.example.texttile.presentation.ui.interfaces.EmptyDataListener;
import com.example.texttile.presentation.ui.interfaces.OnSelectionCountListeners;
import com.example.texttile.data.model.OrderHistory;
import com.example.texttile.presentation.ui.util.Const;
import com.example.texttile.presentation.ui.util.DialogUtil;
import com.example.texttile.presentation.ui.util.PermissionState;

import java.util.ArrayList;

public class OrderHistoryActivity extends AppCompatActivity implements EmptyDataListener, OnSelectionCountListeners {

    ActivityViewHistoryBinding binding;
    OrderHistoryAdapter viewHistoryAdapter;
    String item_id;
    String[] list = {"Pending", "On Machine Pending", "On Machine Completed", "Ready to Dispatch", "Warehouse", "Final Dispatch", "Delivered", "Damage", "See All"};
    boolean selectAll = false, selectionMode = false;
    DaoOrderHistory daoOrderHistory;

    public OrderHistoryActivity(){

        this.daoOrderHistory = new DaoOrderHistory(this);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        item_click("See All", "");

        initToolbar();

        binding.btnDelete.setOnClickListener(view1 -> {
            viewHistoryAdapter.delete_all();
        });
        binding.include.btnSelectAll.setOnClickListener(view1 -> {
            if (selectAll) {
                viewHistoryAdapter.unSelect_all();
                binding.include.btnSelectAll.setImageResource(R.drawable.check_box_unselected);
                selectAll = false;
            } else {
                viewHistoryAdapter.select_all();
                binding.include.btnSelectAll.setImageResource(R.drawable.check_box_selected);
                selectAll = true;
            }
        });

        daoOrderHistory.getOrderHistoryListLiveData().observe(this, new Observer<ArrayList<OrderHistory>>() {
            @Override
            public void onChanged(ArrayList<OrderHistory> employeeMasterModels) {
                Log.d("djfldsjfwer", "onEvent: " + employeeMasterModels);
                viewHistoryAdapter = new OrderHistoryAdapter(employeeMasterModels, OrderHistoryActivity.this, OrderHistoryActivity.this, OrderHistoryActivity.this, daoOrderHistory);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(OrderHistoryActivity.this);
                binding.recyclerView.setLayoutManager(linearLayoutManager);
                binding.recyclerView.setAdapter(viewHistoryAdapter);
                if (DialogUtil.isProgressDialogShowing()) {
                    DialogUtil.hideProgressDialog();
                }
            }
        });
    }


    public void item_click(String item_id, String filter) {
        this.item_id = item_id;
        switch (item_id) {
            case "Pending":
                binding.include.textTitle.setText("Pending");
                daoOrderHistory.getOrderHistoryList(filter, Const.ON_PENDING);
                break;
            case "On Machine Pending":
                binding.include.textTitle.setText("On Machine Pending");
                daoOrderHistory.getOrderHistoryList(filter, Const.ON_MACHINE_PENDING);
                break;
            case "On Machine Completed":
                binding.include.textTitle.setText("On Machine Completed");
                daoOrderHistory.getOrderHistoryList(filter, Const.ON_MACHINE_COMPLETED);
                break;
            case "Ready to Dispatch":
                binding.include.textTitle.setText("Ready to Dispatch");
                daoOrderHistory.getOrderHistoryList(filter, Const.ON_READY_TO_DISPATCH);
                break;
            case "Warehouse":
                binding.include.textTitle.setText("Warehouse");
                daoOrderHistory.getOrderHistoryList(filter, Const.ON_WAREHOUSE);
                break;
            case "Final Dispatch":
                binding.include.textTitle.setText("Final Dispatch");
                daoOrderHistory.getOrderHistoryList(filter, Const.ON_FINAL_DISPATCH);
                break;
            case "Delivered":
                binding.include.textTitle.setText("Delivered");
                daoOrderHistory.getOrderHistoryList(filter, Const.ON_DELIVERED);
                break;
            case "Damage":
                binding.include.textTitle.setText("Damage");
                daoOrderHistory.getOrderHistoryList(filter, Const.ON_DAMAGE);
                break;
            default:
                binding.include.textTitle.setText("All History");
                daoOrderHistory.getOrderHistoryList(filter, "all_history");
                break;
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
            hideProgressDialog();
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
            viewHistoryAdapter.unSelect_all();
            switch (item_id) {
                case "Pending":
                    binding.include.textTitle.setText("Pending");
                    break;
                case "On Machine Pending":
                    binding.include.textTitle.setText("On Machine Pending");
                    break;
                case "On Machine Completed":
                    binding.include.textTitle.setText("On Machine Completed");
                    break;
                case "Ready to Dispatch":
                    binding.include.textTitle.setText("Ready to Dispatch");
                    break;
                case "Warehouse":
                    binding.include.textTitle.setText("Warehouse");
                    break;
                case "Final Dispatch":
                    binding.include.textTitle.setText("Final Dispatch");
                    break;
                case "Delivered":
                    binding.include.textTitle.setText("Delivered");
                    break;
                case "Damage":
                    binding.include.textTitle.setText("Damage");
                    break;
                default:
                    binding.include.textTitle.setText("All History");
                    break;
            }

        } else {
            super.onBackPressed();
        }
    }

    private void initToolbar() {

        binding.include.textTitle.setText("All History");
        binding.include.btnSearch.setVisibility(View.VISIBLE);
        binding.include.btnAddItem.setVisibility(View.VISIBLE);
        binding.include.btnAddItem.setImageResource(R.drawable.dots);
        binding.include.btnAddItem.setPadding(convertDpToPixel(10, this), convertDpToPixel(30, this), convertDpToPixel(10, this), convertDpToPixel(30, this));

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
                item_click(item_id, charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        binding.include.btnAddItem.setOnClickListener(view -> {

            PopupMenu popup = new PopupMenu(this, view);
            for (int i = 0; i < list.length; i++) {

                switch (list[i]) {
                    case "Pending":

                        if (isReadPermissionGranted(PermissionState.PENDING.getValue())) {
                            popup.getMenu().add("Pending");
                        }

                        break;
                    case "On Machine Pending":

                        if (isReadPermissionGranted(PermissionState.ON_MACHINE_PENDING.getValue())) {
                            popup.getMenu().add("On Machine Pending");
                        }

                        break;
                    case "On Machine Completed":

                        if (isReadPermissionGranted(PermissionState.ON_MACHINE_COMPLETED.getValue())) {
                            popup.getMenu().add("On Machine Completed");
                        }

                        break;
                    case "Ready to Dispatch":

                        if (isReadPermissionGranted(PermissionState.READY_TO_DISPATCH.getValue())) {
                            popup.getMenu().add("Ready to Dispatch");
                        }

                        break;
                    case "Warehouse":

                        if (isReadPermissionGranted(PermissionState.WAREHOUSE.getValue())) {
                            popup.getMenu().add("Warehouse");
                        }

                        break;
                    case "Final Dispatch":

                        if (isReadPermissionGranted(PermissionState.FINAL_DISPATCH.getValue())) {
                            popup.getMenu().add("Final Dispatch");
                        }

                        break;
                    case "Delivered":

                        if (isReadPermissionGranted(PermissionState.DELIVERED.getValue())) {
                            popup.getMenu().add("Delivered");
                        }

                        break;
                    case "Damage":

                        if (isReadPermissionGranted(PermissionState.DAMAGE.getValue())) {
                            popup.getMenu().add("Damage");
                        }

                        break;
                    case "See All":
                        if (isReadPermissionGranted(PermissionState.PENDING.getValue()) && isReadPermissionGranted(PermissionState.ON_MACHINE_PENDING.getValue()) && isReadPermissionGranted(PermissionState.ON_MACHINE_COMPLETED.getValue()) && isReadPermissionGranted(PermissionState.READY_TO_DISPATCH.getValue()) && isReadPermissionGranted(PermissionState.WAREHOUSE.getValue()) && isReadPermissionGranted(PermissionState.FINAL_DISPATCH.getValue()) && isReadPermissionGranted(PermissionState.DELIVERED.getValue()) && isReadPermissionGranted(PermissionState.DAMAGE.getValue())) {
                            popup.getMenu().add("See All");
                        }
                        break;
                }
            }
            popup.setOnMenuItemClickListener(item -> {

                item_click(item.getTitle().toString(), "");
                return true;
            });
            popup.show();
        });
    }
}