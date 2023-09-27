package com.example.texttile.presentation.screen.master.party_master;

import android.os.Bundle;
import android.text.TextUtils;

import androidx.appcompat.app.AppCompatActivity;

import com.example.texttile.R;
import com.example.texttile.databinding.ActivityPartyMasterBinding;
import com.example.texttile.data.dao.DAOPartyMaster;
import com.example.texttile.data.model.PartyMasterModel;
import com.example.texttile.presentation.ui.lib.snackUtil.CustomSnackUtil;
import com.example.texttile.presentation.ui.util.DialogUtil;

import java.util.HashMap;

public class PartyMasterActivity extends AppCompatActivity {

    ActivityPartyMasterBinding binding;
    String tracker;
    PartyMasterModel partyMasterModel;
    DAOPartyMaster dao;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPartyMasterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dao = new DAOPartyMaster(this);

        initToolbar();

        tracker = (getIntent().getStringExtra("tracker") != null) ? getIntent().getStringExtra("tracker") : "add";
        partyMasterModel = (tracker.equals("edit") && getIntent().getSerializableExtra("party_data") != null) ? (PartyMasterModel) getIntent().getSerializableExtra("party_data") : new PartyMasterModel();

        if (tracker != null && tracker.equals("edit")) {
            if (partyMasterModel != null) {
                binding.btnAdd.setText("Update");

                binding.editPartyName.setText(String.valueOf(partyMasterModel.getParty_Name()));
                binding.editAddress.setText(String.valueOf(partyMasterModel.getAddress()));
            }
        } else {
            if (partyMasterModel != null) {
                binding.editPartyName.requestFocus();
            }
        }

        binding.btnAdd.setOnClickListener(view -> {
            addArtist();
        });
    }

    private void addArtist() {
        String partyName = binding.editPartyName.getText().toString().trim();
        String address = binding.editAddress.getText().toString().trim();

        if (TextUtils.isEmpty(partyName)) {
            new CustomSnackUtil().showSnack(this, "Party Name is Empty", R.drawable.error_msg_icon);
        } else if (TextUtils.isEmpty(address)) {
            new CustomSnackUtil().showSnack(this, "Address Name is Empty", R.drawable.error_msg_icon);
        } else {
            String id = dao.getId();
            DialogUtil.showProgressDialog(this);
            if (tracker.equals("edit")) {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("party_Name", partyName);
                hashMap.put("address", address);

                dao.update(partyMasterModel.getId(), hashMap).addOnSuccessListener(runnable -> {
                    DialogUtil.showUpdateSuccessDialog(this,true);
                });
            } else {
                partyMasterModel.setId(id);
                partyMasterModel.setParty_Name(partyName);
                partyMasterModel.setAddress(address);

                dao.insert(id, partyMasterModel).addOnSuccessListener(runnable -> {
                    DialogUtil.showSuccessDialog(this, true);
                });
            }
        }
    }

    public void initToolbar() {
        binding.include.textTitle.setText("Party Master");
        binding.include.btnBack.setOnClickListener(view -> {
            onBackPressed();
        });
    }
}