package com.example.texttile.presentation.screen.features.reading_feature.view_order;

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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.texttile.R;
import com.example.texttile.databinding.ActivityViewOrderBinding;
import com.example.texttile.presentation.ui.interfaces.EmptyDataListener;
import com.example.texttile.data.model.ItemState;
import com.example.texttile.presentation.ui.util.DialogUtil;

import java.util.ArrayList;

public class ViewOrderActivity extends AppCompatActivity implements EmptyDataListener {

    ActivityViewOrderBinding binding;

    ViewOrderViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewOrderBinding.inflate(getLayoutInflater());
        viewModel = new ViewModelProvider(this, new ViewOrderViewModelFactory(this)).get(ViewOrderViewModel.class);
        setContentView(binding.getRoot());

        fetchOrderData();

        initToolbar();


        viewModel.itemId.observe(this, itemId -> {
            viewModel.item_click();
        });


        binding.btnDelete.setOnClickListener(view -> {
            viewModel.moveToNextAll();
        });

    }


    public void fetchOrderData() {
        viewModel.orderMasterModels.observe(this, orderMasterModels -> {
            if (orderMasterModels.size() == 0) {
                binding.animationView.setVisibility(View.VISIBLE);
            } else {
                binding.animationView.setVisibility(View.GONE);
            }
            DialogUtil.hideProgressDialog();

            ArrayList<ItemState> itemModels = new ArrayList<>();
            for (int i = 0; i < viewModel.orderMasterModels.getValue().size(); i++) {
                itemModels.add(new ItemState(false, false, false, i));
            }
            Log.d("djslfkjjkw", "fetchOrderData: " + itemModels.size());
            viewModel.itemModels.setValue(itemModels);
            viewModel.selectionModeUpdate();
        });
        viewModel.daoOrder.getOrderListLiveData().observe(this, orderMasterModels -> {
            viewModel.orderMasterModels.setValue(orderMasterModels);
            viewModel.viewOrderAdapter = new ViewOrderAdapter(viewModel, this);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ViewOrderActivity.this);
            binding.recyclerView.setLayoutManager(linearLayoutManager);
            binding.recyclerView.setAdapter(viewModel.viewOrderAdapter);
            if (DialogUtil.isProgressDialogShowing()) {
                DialogUtil.hideProgressDialog();
            }
        });
    }


    private void initToolbar() {
        viewModel.selectionMode.observe(this, isSelectionMode -> {
            if (isSelectionMode) {
                binding.include.btnAddItem.setVisibility(View.GONE);
                binding.include.btnSelectAll.setVisibility(View.VISIBLE);
                binding.include.btnSearch.setVisibility(View.GONE);
                binding.include.viewSearch.setVisibility(View.GONE);
                binding.viewSelectionMode.setVisibility(View.VISIBLE);
            } else {
                binding.include.btnAddItem.setVisibility(View.VISIBLE);
                binding.include.btnSelectAll.setVisibility(View.GONE);
                binding.include.btnSearch.setVisibility(View.VISIBLE);
                binding.viewSelectionMode.setVisibility(View.GONE);
                binding.include.textTitle.setText("Design Master");
            }
        });
        viewModel.isAllSelected.observe(this, isAllSelected -> {
            if (isAllSelected) {
                binding.include.btnSelectAll.setImageResource(R.drawable.check_box_selected);
            } else {
                binding.include.btnSelectAll.setImageResource(R.drawable.check_box_unselected);
            }
        });
        binding.include.btnSelectAll.setOnClickListener(view -> {
            if (viewModel.isAllSelected.getValue()) {
                viewModel.unSelect_all();
            } else {
                viewModel.select_all();
            }
        });
        viewModel.selectedItemCount.observe(this, itemCount -> {
            if (viewModel.selectionMode.getValue()) {
                binding.include.textTitle.setText(itemCount + " Selected");
            } else {
                binding.include.textTitle.setText(viewModel.title.getValue());
            }

        });
        viewModel.title.observe(this, title -> {
            binding.include.textTitle.setText(title);
        });
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
                viewModel.filter.setValue(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        binding.include.btnAddItem.setOnClickListener(view -> {
            PopupMenu popup = new PopupMenu(this, view);
            popup.getMenuInflater().inflate(R.menu.pop_up, popup.getMenu());
            popup.setOnMenuItemClickListener(item -> {
                viewModel.itemId.setValue(item.getItemId());
                return true;
            });
            popup.show();
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
            DialogUtil.hideProgressDialog();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}