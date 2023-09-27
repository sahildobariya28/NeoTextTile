package com.example.texttile.presentation.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.annotation.Nonnull;
import androidx.fragment.app.Fragment;

import com.example.texttile.presentation.ui.activity.CustomeScanner;
import com.example.texttile.databinding.FragmentScannerBinding;
import com.example.texttile.presentation.ui.util.DialogUtil;
import com.example.texttile.presentation.ui.util.PermissionState;
import com.example.texttile.presentation.ui.util.PermissionUtil;
import com.google.zxing.integration.android.IntentIntegrator;


public class ScannerFragment extends Fragment {


    FragmentScannerBinding binding;
    IntentIntegrator intentIntegrator;


    @Override
    public View onCreateView(@Nonnull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentScannerBinding.inflate(getLayoutInflater());

        initToolbar();

        binding.btnScanner.setOnClickListener(view -> {
            if (PermissionUtil.isReadPermissionGranted(PermissionState.SCANNER.getValue())){
                setZxing(0);
            }else {
                DialogUtil.showAccessDeniedDialog(getActivity());
            }
        });

        return binding.getRoot();
    }


    public void setZxing(int camera_rotation) {

        intentIntegrator = new IntentIntegrator(getActivity());
        intentIntegrator.setPrompt("Scan a barcode or QR Code");
        intentIntegrator.setCameraId(camera_rotation);
        intentIntegrator.setOrientationLocked(true);
        intentIntegrator.setCaptureActivity(CustomeScanner.class);
        intentIntegrator.setBeepEnabled(true);
        intentIntegrator.initiateScan();

    }

    public void initToolbar() {
        binding.include.textTitle.setText("Scanner");
        binding.include.btnBack.setOnClickListener(view -> {
            getActivity().onBackPressed();
        });
    }
}