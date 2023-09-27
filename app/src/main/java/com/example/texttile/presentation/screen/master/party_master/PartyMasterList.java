package com.example.texttile.presentation.screen.master.party_master;

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
import com.example.texttile.databinding.ActivityPartyMasterListBinding;
import com.example.texttile.data.dao.DAOPartyMaster;
import com.example.texttile.presentation.ui.interfaces.EmptyDataListener;
import com.example.texttile.presentation.ui.interfaces.LoadFragmentChangeListener;
import com.example.texttile.presentation.ui.interfaces.OnSelectionCountListeners;
import com.example.texttile.data.model.PartyMasterModel;
import com.example.texttile.presentation.ui.util.DialogUtil;
import com.example.texttile.presentation.ui.util.PermissionState;

import java.util.ArrayList;

public class PartyMasterList extends AppCompatActivity implements EmptyDataListener, OnSelectionCountListeners {


    PartyMasterAdapter partyMasterAdapter;
    String tracker;
    ActivityPartyMasterListBinding binding;
    LoadFragmentChangeListener loadFragmentChangeListener;
    DAOPartyMaster daoPartyMaster;

    boolean selectAll = false, selectionMode = false;

    public PartyMasterList() {

        daoPartyMaster = new DAOPartyMaster(this);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPartyMasterListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        tracker = getIntent().getStringExtra("tracker");


        if (isInsertPermissionGranted(PermissionState.PARTY_MASTER.getValue())) {
            binding.include.btnAddItem.setVisibility(View.VISIBLE);
        } else {
            binding.include.btnAddItem.setVisibility(View.GONE);
        }
        initToolbar();
        party_master_list("");

        binding.btnDelete.setOnClickListener(view -> {
            partyMasterAdapter.delete_all();
        });

        binding.include.btnSelectAll.setOnClickListener(view -> {
            if (selectAll) {
                partyMasterAdapter.unSelect_all();
                binding.include.btnSelectAll.setImageResource(R.drawable.check_box_unselected);
                selectAll = false;
            } else {
                partyMasterAdapter.select_all();
                binding.include.btnSelectAll.setImageResource(R.drawable.check_box_selected);
                selectAll = true;
            }
        });

        daoPartyMaster.getPartyMasterListLiveData().observe(this, new Observer<ArrayList<PartyMasterModel>>() {
            @Override
            public void onChanged(ArrayList<PartyMasterModel> partyMasterModels) {
                partyMasterAdapter = new PartyMasterAdapter(partyMasterModels, PartyMasterList.this, loadFragmentChangeListener, PartyMasterList.this::onDataChanged, PartyMasterList.this::onCountChanged, daoPartyMaster);
                binding.recyclerView.setLayoutManager(new LinearLayoutManager(PartyMasterList.this));
                binding.recyclerView.setAdapter(partyMasterAdapter);
                partyMasterAdapter.selectionModeUpdate();
                if (DialogUtil.isProgressDialogShowing()) {
                    DialogUtil.hideProgressDialog();
                }
            }
        });
    }

    public void party_master_list(String filter) {
        if (partyMasterAdapter == null) DialogUtil.showProgressDialog(this);
        daoPartyMaster.getPartyMasterList(filter);
    }


    private void initToolbar() {

        binding.include.textTitle.setText("Party Master");
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

                party_master_list(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.include.btnAddItem.setOnClickListener(view -> {

            if (isInsertPermissionGranted(PermissionState.PARTY_MASTER.getValue())) {
                startActivity(new Intent(this, PartyMasterActivity.class).putExtra("tracker", "add"));
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
            binding.include.textTitle.setText("Party Master");
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
            partyMasterAdapter.unSelect_all();
            binding.include.textTitle.setText("Party Master");
        } else {
            super.onBackPressed();
        }
    }
}