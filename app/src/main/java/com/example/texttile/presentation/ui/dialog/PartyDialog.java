package com.example.texttile.presentation.ui.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.WindowManager;

import androidx.fragment.app.DialogFragment;

import com.example.texttile.R;
import com.example.texttile.databinding.PartyMasterDialogBinding;
import com.example.texttile.data.dao.DAOPartyMaster;
import com.example.texttile.data.model.PartyMasterModel;
import com.example.texttile.presentation.ui.lib.snackUtil.CustomSnackUtil;
import com.example.texttile.presentation.ui.util.DialogUtil;
import com.example.texttile.presentation.ui.util.KeyboardUtils;

import javax.annotation.Nullable;

public class PartyDialog extends DialogFragment {

    PartyMasterDialogBinding binding;
    PartyMasterModel partyMasterModel;
    DAOPartyMaster dao;
    Dialog dialog;
    String party_name;

    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        dialog = new Dialog(getActivity());
        binding = PartyMasterDialogBinding.inflate(getLayoutInflater());
        dialog.getWindow().setWindowAnimations(R.style.DialogAnimation);
        dialog.setContentView(binding.getRoot());
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        KeyboardUtils.addKeyboardToggleListener(getActivity(), new KeyboardUtils.SoftKeyboardToggleListener() {
            @Override
            public void onToggleSoftKeyboard(boolean isVisible) {
                if (isVisible) {
                    binding.guideline3.setGuidelinePercent(0.10f);
                    binding.guideline4.setGuidelinePercent(0.90f);
                } else {
                    binding.guideline3.setGuidelinePercent(0.25f);
                    binding.guideline4.setGuidelinePercent(0.75f);
                }
            }
        });

        if (getArguments().getString("party_name") != null) {
            party_name = getArguments().getString("party_name");
            binding.editPartyName.setText(party_name);
        }

        binding.btnAdd.setOnClickListener(view -> {
            addArtist();
        });
        binding.btnClose.setOnClickListener(view -> {
            dialog.dismiss();
        });
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dao = new DAOPartyMaster(getActivity());

    }

    private void addArtist() {

        if (partyMasterModel == null) {
            partyMasterModel = new PartyMasterModel();
        }
        String partyName = binding.editPartyName.getText().toString().trim();
        String address = binding.editAddress.getText().toString().trim();

        if (TextUtils.isEmpty(partyName)) {
            new CustomSnackUtil().showSnack(getActivity(), "Party Name is Empty", R.drawable.error_msg_icon);
        } else if (TextUtils.isEmpty(address)) {
            new CustomSnackUtil().showSnack(getActivity(), "Address Name is Empty", R.drawable.error_msg_icon);
        } else {
            String id = dao.getId();
            DialogUtil.showProgressDialog(getActivity());

            partyMasterModel.setId(id);
            partyMasterModel.setParty_Name(partyName);
            partyMasterModel.setAddress(address);

            dao.insert(id, partyMasterModel).addOnSuccessListener(runnable -> {
                DialogUtil.showSuccessDialog(getActivity(), false);
                dialog.dismiss();
            });
        }
    }
}
