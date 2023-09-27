package com.example.texttile.presentation.screen.near_complete_order;

import static com.example.texttile.presentation.ui.util.PermissionUtil.isInsertPermissionGranted;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.texttile.R;
import com.example.texttile.databinding.ActivityNearCompleteOrderBinding;
import com.example.texttile.data.dao.DAOOrder;
import com.example.texttile.presentation.ui.interfaces.EmptyDataListener;
import com.example.texttile.presentation.ui.interfaces.OnSelectionCountListeners;
import com.example.texttile.presentation.screen.features.order_feature.order_master.new_order.order.OrderMasterInsertActivity;
import com.example.texttile.data.model.OrderMasterModel;
import com.example.texttile.presentation.ui.util.DialogUtil;
import com.example.texttile.presentation.ui.util.PermissionState;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import javax.annotation.Nullable;

public class NearCompleteOrderActivity extends AppCompatActivity implements EmptyDataListener, OnSelectionCountListeners {

    String tracker, order_no;

    ActivityNearCompleteOrderBinding binding;
    Dialog progress_dialog;
    NearCompleteOrderAdapter nearCompleteOrderAdapter;
    MutableLiveData<ArrayList<OrderMasterModel>> nearCompleteOrderLiveList = new MutableLiveData<>(new ArrayList<>());
    ArrayList<OrderMasterModel> nearCompleteOrderList = new ArrayList<>();

    DAOOrder daoOrder;
    boolean selectAll = false, selectionMode = false;

    public NearCompleteOrderActivity() {
        daoOrder = new DAOOrder(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNearCompleteOrderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        tracker = getIntent().getStringExtra("tracker");
        if (getIntent().getStringExtra("order_no") != null) {
            order_no = getIntent().getStringExtra("order_no");
        }


        if (isInsertPermissionGranted(PermissionState.NEW_ORDER.getValue())) {
            binding.include.btnAddItem.setVisibility(View.VISIBLE);
        } else {
            binding.include.btnAddItem.setVisibility(View.GONE);
        }

        initToolbar();

        order_master_list("");

        binding.btnManage.setOnClickListener(view -> {
            nearCompleteOrderAdapter.delete_all();
        });
        binding.include.btnSelectAll.setOnClickListener(view -> {
            if (selectAll) {
                nearCompleteOrderAdapter.unSelect_all();
                binding.include.btnSelectAll.setImageResource(R.drawable.check_box_unselected);
                selectAll = false;
            } else {
                nearCompleteOrderAdapter.select_all();
                binding.include.btnSelectAll.setImageResource(R.drawable.check_box_selected);
                selectAll = true;
            }
        });

        nearCompleteOrderLiveList.observe(this, new Observer<ArrayList<OrderMasterModel>>() {
            @Override
            public void onChanged(ArrayList<OrderMasterModel> orderMasterModels) {
                nearCompleteOrderAdapter = new NearCompleteOrderAdapter(orderMasterModels, NearCompleteOrderActivity.this, tracker, NearCompleteOrderActivity.this, NearCompleteOrderActivity.this, daoOrder);
                binding.recyclerView.setLayoutManager(new LinearLayoutManager(NearCompleteOrderActivity.this));
                binding.recyclerView.setAdapter(nearCompleteOrderAdapter);
                nearCompleteOrderAdapter.selectionModeUpdate();
                if (DialogUtil.isProgressDialogShowing()) {
                    DialogUtil.hideProgressDialog();
                }
            }
        });

    }

    public void order_master_list(String filter) {
        daoOrder.getReference().addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.w("TAG", "Listen failed.", error);
                    return;
                }

                nearCompleteOrderList.clear();
                for (QueryDocumentSnapshot doc : value) {
                    OrderMasterModel orderMasterModel = doc.toObject(OrderMasterModel.class);
                    if (orderMasterModel.getSub_order_no().contains(filter)){
                        if (orderMasterModel.getOrderStatusModel().getOnDelivered() == (orderMasterModel.getOrderStatusModel().getTotal() - 1)){
                            nearCompleteOrderList.add(orderMasterModel);
                        }
                    }
                }
                nearCompleteOrderLiveList.setValue(nearCompleteOrderList);
            }
        });
    }

    private void initToolbar() {

        binding.include.textTitle.setText("Near Complete Order");
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
                order_master_list(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        binding.include.btnAddItem.setOnClickListener(view -> {
            if (isInsertPermissionGranted(PermissionState.NEW_ORDER.getValue())) {
                startActivity(new Intent(this, OrderMasterInsertActivity.class).putExtra("tracker", "add"));
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
            hideProgressDialog();
        }
    }

    public void showProgressDialog(Activity activity) {
        if (activity != null) {
            progress_dialog = new Dialog(activity);
            progress_dialog.setContentView(R.layout.dialog_progress);
            progress_dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT);
            progress_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            progress_dialog.show();
        }
    }

    public void hideProgressDialog() {
        if (progress_dialog != null && progress_dialog.isShowing()) {
            progress_dialog.dismiss();
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
            binding.include.textTitle.setText("Sub Order");
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
            nearCompleteOrderAdapter.unSelect_all();
            binding.include.textTitle.setText("Sub Order");
        } else {
            super.onBackPressed();
        }
    }
}