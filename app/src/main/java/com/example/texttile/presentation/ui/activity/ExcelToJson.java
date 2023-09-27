package com.example.texttile.presentation.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.texttile.core.MyApplication;
import com.example.texttile.R;
import com.example.texttile.databinding.ActivityExcelToJsonBinding;
import com.example.texttile.presentation.ui.lib.snackUtil.CustomSnackUtil;
import com.example.texttile.presentation.ui.util.DialogUtil;
import com.example.texttile.presentation.ui.util.PermissionState;
import com.example.texttile.presentation.ui.util.PermissionUtil;

public class ExcelToJson extends AppCompatActivity {

    ActivityExcelToJsonBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityExcelToJsonBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initToolbar();

        if (!isPermission()){
            requestPermission();
        }

        binding.btnImport.setOnClickListener(view -> {
            if (PermissionUtil.isReadPermissionGranted(PermissionState.IMPORT.getValue())){
                startActivity(new Intent(this, ImportExportDataActivity.class).putExtra("tracker", "import"));
            }else {
                DialogUtil.showAccessDeniedDialog(this);
            }

        });
        binding.btnExport.setOnClickListener(view -> {
            if (PermissionUtil.isReadPermissionGranted(PermissionState.EXPORT.getValue())){
                startActivity(new Intent(this, ImportExportDataActivity.class).putExtra("tracker", "export"));
            }else {
                DialogUtil.showAccessDeniedDialog(this);
            }
        });

        binding.btnEmptyImport.setOnClickListener(view -> {
            if (PermissionUtil.isReadPermissionGranted(PermissionState.EXPORT.getValue())){
                startActivity(new Intent(this, EmptyExportActivity.class).putExtra("tracker", "export"));
            }else {
                DialogUtil.showAccessDeniedDialog(this);
            }
        });
    }


    public void requestPermission(){

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE }, 100);
        }else {
            new CustomSnackUtil().showSnack(this, "Storage Permission Already Granted", R.drawable.error_msg_icon);

        }

    }
    public boolean isPermission()
    {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            return false;
        }else {
            return true;
        }
    }
    public void initToolbar() {
        binding.include.textTitle.setText("Import / Export");
        binding.include.btnBack.setOnClickListener(view -> {
            onBackPressed();
        });
    }


    @Override
    protected void onResume() {
        ((MyApplication) getApplicationContext()).setActivity(this, this);
        super.onResume();
    }
}