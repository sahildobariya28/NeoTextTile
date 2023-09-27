package com.example.texttile.presentation.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.texttile.databinding.ActivityEmptyExportBinding;
import com.example.texttile.presentation.ui.util.WriteEmptyExport;

public class EmptyExportActivity extends AppCompatActivity {
    ActivityEmptyExportBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEmptyExportBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnExportEmpty.setOnClickListener(view -> {
            WriteEmptyExport writeExcelUtil= new WriteEmptyExport(this);
            writeExcelUtil.setUpExport();
        });
    }
}