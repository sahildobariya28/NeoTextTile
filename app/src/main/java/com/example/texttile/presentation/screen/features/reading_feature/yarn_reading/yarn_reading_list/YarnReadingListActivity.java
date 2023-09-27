package com.example.texttile.presentation.screen.features.reading_feature.yarn_reading.yarn_reading_list;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.texttile.R;
import com.example.texttile.databinding.ActivityYarnMasterListBinding;
import com.example.texttile.data.dao.DaoYarnMaster;
import com.example.texttile.presentation.ui.interfaces.EmptyDataListener;
import com.example.texttile.presentation.ui.interfaces.OnSelectionCountListeners;
import com.example.texttile.data.model.YarnMasterModel;
import com.example.texttile.presentation.ui.util.DialogUtil;

import java.util.ArrayList;

public class YarnReadingListActivity extends AppCompatActivity implements EmptyDataListener, OnSelectionCountListeners {


    YarnReadingAdapter yarnReadingAdapter;
    String tracker;
    ActivityYarnMasterListBinding binding;
    DaoYarnMaster daoYarnMaster;
    boolean selectAll = false, selectionMode = false;

    public YarnReadingListActivity(){
        daoYarnMaster = new DaoYarnMaster(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityYarnMasterListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        tracker = getIntent().getStringExtra("tracker");

        initToolbar();
        yarn_reading_list("");

        daoYarnMaster.getYarnMasterListLiveData().observe(this, new Observer<ArrayList<YarnMasterModel>>() {
            @Override
            public void onChanged(ArrayList<YarnMasterModel> yarnMasterModels) {
                yarnReadingAdapter = new YarnReadingAdapter(yarnMasterModels, YarnReadingListActivity.this, YarnReadingListActivity.this);
                binding.recyclerView.setLayoutManager(new LinearLayoutManager(YarnReadingListActivity.this));
                binding.recyclerView.setAdapter(yarnReadingAdapter);
                if (DialogUtil.isProgressDialogShowing()){
                    DialogUtil.hideProgressDialog();
                }
            }
        });
    }

    public void yarn_reading_list(String filter) {
        if (yarnReadingAdapter == null) DialogUtil.showProgressDialog(YarnReadingListActivity.this);
        daoYarnMaster.getYarnMasterList(filter);
    }


    private void initToolbar() {

        binding.include.textTitle.setText("Yarn Reading");
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
                yarn_reading_list(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
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
            binding.include.btnSelectAll.setVisibility(View.VISIBLE);
            binding.include.btnSearch.setVisibility(View.GONE);
            binding.include.viewSearch.setVisibility(View.GONE);
            binding.include.textTitle.setText(count + " Selected");
        } else {
            binding.include.textTitle.setText(count + " Selected");
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}