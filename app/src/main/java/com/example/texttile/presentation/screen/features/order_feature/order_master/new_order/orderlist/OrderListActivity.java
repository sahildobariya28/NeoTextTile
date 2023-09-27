package com.example.texttile.presentation.screen.features.order_feature.order_master.new_order.orderlist;

import static com.example.texttile.presentation.ui.util.PermissionUtil.isInsertPermissionGranted;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.texttile.R;
import com.example.texttile.databinding.ActivityOrderListBinding;
import com.example.texttile.data.dao.DAOOrder;
import com.example.texttile.presentation.screen.features.order_feature.order_master.new_order.order.OrderMasterInsertActivity;
import com.example.texttile.presentation.ui.util.DialogUtil;
import com.example.texttile.presentation.ui.util.KeyboardUtils;
import com.example.texttile.presentation.ui.util.PermissionState;

public class OrderListActivity extends AppCompatActivity {

    NewOrderAdapter newOrderAdapter;
    String tracker, order_no;
    DAOOrder daoOrder;

    ActivityOrderListBinding binding;

    public OrderListActivity() {

        daoOrder = new DAOOrder(this);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        tracker = getIntent().getStringExtra("tracker");
        if (getIntent().getStringExtra("order_no") != null) {
            order_no = getIntent().getStringExtra("order_no");
        }
        initToolbar("New Order");
        design_master_list("");


        daoOrder.getFilterOrderListLiveData().observe(this, orderMasterModels -> {
            newOrderAdapter = new NewOrderAdapter(OrderListActivity.this, orderMasterModels);
            binding.recyclerView.setLayoutManager(new LinearLayoutManager(OrderListActivity.this));
            binding.recyclerView.setAdapter(newOrderAdapter);
            if (DialogUtil.isProgressDialogShowing()) {
                DialogUtil.hideProgressDialog();
            }
        });
    }

    public void design_master_list(String filter) {
        if (newOrderAdapter == null) DialogUtil.showProgressDialog(this);
        daoOrder.getFilterOrderList(filter);
    }


    public void initToolbar(String name) {
        binding.include.textTitle.setText(name);
        if (isInsertPermissionGranted(PermissionState.NEW_ORDER.getValue())) {
            binding.include.btnAddItem.setVisibility(View.VISIBLE);
        } else {
            binding.include.btnAddItem.setVisibility(View.GONE);
        }

        binding.include.btnAddItem.setVisibility(View.VISIBLE);
        binding.include.btnSearch.setVisibility(View.VISIBLE);
        binding.include.btnSearch.setImageResource(R.drawable.search);


        binding.include.btnSearch.setOnClickListener(view -> {
            if (binding.include.viewSearch.getVisibility() == View.VISIBLE) {
                binding.include.viewSearch.setVisibility(View.GONE);
            } else {
                binding.include.viewSearch.setVisibility(View.VISIBLE);
                binding.include.btnSearch.setVisibility(View.GONE);
                binding.include.editSearch.requestFocus();
                KeyboardUtils.forceOpenKeyboard(OrderListActivity.this);
            }
        });

        binding.include.btnClose.setOnClickListener(view -> {
            if (binding.include.editSearch.getText().toString().isEmpty()) {
                binding.include.viewSearch.setVisibility(View.GONE);
                binding.include.btnSearch.setVisibility(View.VISIBLE);
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
                design_master_list(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.include.btnBack.setOnClickListener(view -> {
            onBackPressed();
        });

        binding.include.btnAddItem.setOnClickListener(view1 -> {
            if (isInsertPermissionGranted(PermissionState.NEW_ORDER.getValue())) {
                startActivity(new Intent(this, OrderMasterInsertActivity.class).putExtra("tracker", "add"));
            } else {
                DialogUtil.showAccessDeniedDialog(this);
            }
        });
    }
}