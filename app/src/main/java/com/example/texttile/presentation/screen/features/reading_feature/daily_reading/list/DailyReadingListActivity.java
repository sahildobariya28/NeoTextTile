package com.example.texttile.presentation.screen.features.reading_feature.daily_reading.list;

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
import com.example.texttile.databinding.ActivityDailyReadingListBinding;
import com.example.texttile.data.dao.DAOReading;
import com.example.texttile.presentation.ui.interfaces.EmptyDataListener;
import com.example.texttile.presentation.ui.interfaces.OnSelectionCountListeners;
import com.example.texttile.presentation.screen.features.reading_feature.daily_reading.reading.DailyReadingActivity;
import com.example.texttile.data.model.DailyReadingModel;
import com.example.texttile.presentation.ui.util.DialogUtil;
import com.example.texttile.presentation.ui.util.PermissionState;

import java.util.ArrayList;

public class DailyReadingListActivity extends AppCompatActivity implements EmptyDataListener, OnSelectionCountListeners {

    DailyReadingAdapter dailyReadingAdapter;
    String tracker;
    ActivityDailyReadingListBinding binding;
    boolean selectAll = false, selectionMode = false;
    DAOReading daoReading;


    public DailyReadingListActivity(){
        daoReading = new DAOReading(this);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDailyReadingListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        tracker = getIntent().getStringExtra("tracker");

            if (isInsertPermissionGranted(PermissionState.DAILY_READING.getValue())){
                binding.include.btnAddItem.setVisibility(View.VISIBLE);
            }else {
                binding.include.btnAddItem.setVisibility(View.GONE);
            }
        initToolbar();
        daily_reading_list("");

        binding.btnDelete.setOnClickListener(view -> {
           dailyReadingAdapter.delete_all();
        });

        binding.include.btnSelectAll.setOnClickListener(view -> {
            if (selectAll){
                dailyReadingAdapter.unSelect_all();
                binding.include.btnSelectAll.setImageResource(R.drawable.check_box_unselected);
                selectAll = false;
            }else {
                dailyReadingAdapter.select_all();
                binding.include.btnSelectAll.setImageResource(R.drawable.check_box_selected);
                selectAll = true;
            }
        });

        daoReading.getDailyReadingListLiveData().observe(this, new Observer<ArrayList<DailyReadingModel>>() {
            @Override
            public void onChanged(ArrayList<DailyReadingModel> dailyReadingModels) {
                dailyReadingAdapter = new DailyReadingAdapter(dailyReadingModels, DailyReadingListActivity.this, DailyReadingListActivity.this, DailyReadingListActivity.this, daoReading);
                    binding.recyclerView.setLayoutManager(new LinearLayoutManager(DailyReadingListActivity.this));
                    binding.recyclerView.setAdapter(dailyReadingAdapter);
                    dailyReadingAdapter.selectionModeUpdate();
                if (DialogUtil.isProgressDialogShowing()){
                    DialogUtil.hideProgressDialog();
                }
            }
        });
    }


    public void daily_reading_list(String filter) {

        if (dailyReadingAdapter == null) DialogUtil.showProgressDialog(this);
        daoReading.getDailyReadingList(filter);

    }


    private void initToolbar() {

        binding.include.textTitle.setText("Daily Reading");
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
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                daily_reading_list(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) { }
        });

        binding.include.btnAddItem.setOnClickListener(view -> {
            startActivity(new Intent(this, DailyReadingActivity.class).putExtra("tracker", "add"));
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
        if (isSelectAll){
            binding.include.btnSelectAll.setImageResource(R.drawable.check_box_selected);
            selectAll = true;
        }else {
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
        if (selectionMode){
            selectionMode = false;
            binding.include.btnAddItem.setVisibility(View.VISIBLE);
            binding.include.btnSelectAll.setVisibility(View.GONE);
            binding.include.btnSearch.setVisibility(View.VISIBLE);
            binding.viewSelectionMode.setVisibility(View.GONE);
            if (!binding.include.editSearch.getText().toString().isEmpty()){
                binding.include.viewSearch.setVisibility(View.VISIBLE);
            }else {
                binding.include.viewSearch.setVisibility(View.GONE);
            }
            dailyReadingAdapter.unSelect_all();
            binding.include.textTitle.setText("Daily Reading");
        }else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        daoReading = null;
        super.onDestroy();
    }


}