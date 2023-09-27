package com.example.texttile.presentation.ui.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.texttile.core.MyApplication;
import com.example.texttile.databinding.ActivityImageEditingBinding;

public class ImageEditing extends AppCompatActivity {


    Bitmap bitmap;

    ActivityImageEditingBinding binding;

    public interface OnSomeEventListener {
        void someEvent(Bitmap bitmap);
    }
    OnSomeEventListener someEventListener;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityImageEditingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        someEventListener = CustomeScanner.SomeEventListener;

        String path = getIntent().getStringExtra("path");



        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bitmap = BitmapFactory.decodeFile(path, bmOptions);
        binding.imageMain.setImageBitmap(bitmap);



        binding.imgHorizontal.setOnClickListener(v -> {
            bitmap = RotateBitmap(bitmap, 270);
            binding.imageMain.setImageBitmap(bitmap);
        });


        binding.imgVertical.setOnClickListener(v -> {
            bitmap = RotateBitmap(bitmap, 90);
            binding.imageMain.setImageBitmap(bitmap);
        });
        binding.btnClose.setOnClickListener(v -> {
            onBackPressed();
        });

        binding.btnDone.setOnClickListener(v -> {

            someEventListener.someEvent(bitmap);
            onBackPressed();

        });
    }
    public static Bitmap scaleDownBitmap(Bitmap photo, int newHeight, Context context) {

        final float densityMultiplier = context.getResources().getDisplayMetrics().density;

        int h= (int) (newHeight*densityMultiplier);
        int w= (int) (h * photo.getWidth()/((double) photo.getHeight()));

        photo=Bitmap.createScaledBitmap(photo, w, h, true);

        return photo;
    }


    public static Bitmap RotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        ((MyApplication) getApplicationContext()).setActivity(this, this);
        super.onResume();
    }
}