package com.example.texttile.presentation.ui.activity;

import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.texttile.core.MyApplication;
import com.example.texttile.databinding.ActivityImagePreviewBinding;

public class ImagePreviewActivity extends AppCompatActivity {

    ActivityImagePreviewBinding binding;
    Uri uri;
    String tracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityImagePreviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initToolbar();

        String path = getIntent().getStringExtra("img_uri");
        tracker = getIntent().getStringExtra("tracker");

        try {
            Glide.with(this).load(path).into(binding.imagePreview);
        }catch (Exception e){
            uri = Uri.parse(path);
            Glide.with(this).load(uri).into(binding.imagePreview);
        }

    }

    public void initToolbar() {
        binding.include.textTitle.setText("Image Preview");
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