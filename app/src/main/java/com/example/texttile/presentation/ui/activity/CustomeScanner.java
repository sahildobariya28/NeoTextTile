package com.example.texttile.presentation.ui.activity;

import static com.example.texttile.presentation.ui.util.FileUtils.getPath;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.View;

import javax.annotation.Nonnull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.texttile.core.MyApplication;
import com.example.texttile.R;
import com.example.texttile.databinding.ActivityCustomeScannerBinding;
import com.example.texttile.presentation.ui.lib.snackUtil.CustomSnackUtil;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.ViewfinderView;
import com.journeyapps.barcodescanner.camera.CameraSettings;


public class CustomeScanner extends AppCompatActivity implements DecoratedBarcodeView.TorchListener, ImageEditing.OnSomeEventListener{

    CaptureManager capture;
    boolean isflash = false;
    Uri uri;
    ViewfinderView viewfinderView;

    public static ImageEditing.OnSomeEventListener SomeEventListener;
    ActivityCustomeScannerBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCustomeScannerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.zxingBarcodeScanner.setTorchListener(this);
        viewfinderView = findViewById(R.id.zxing_viewfinder_view);

        SomeEventListener = this::someEvent;


        capture = new CaptureManager(this, binding.zxingBarcodeScanner);
        capture.initializeFromIntent(getIntent(), savedInstanceState);
        capture.setShowMissingCameraPermissionDialog(false);
        capture.decode();

        changeMaskColor(null);
        changeLaserVisibility(true);
        binding.zxingBarcodeScanner.setTorchOff();

        binding.flashOnOff.setOnClickListener(v -> {
            if (isflash) {
                binding.zxingBarcodeScanner.setTorchOff();
                isflash = false;
            } else {
                binding.zxingBarcodeScanner.setTorchOn();
                isflash = true;
            }
        });

        binding.cameraRotate.setOnClickListener(v -> {
            CameraSettings settings = binding.zxingBarcodeScanner.getBarcodeView().getCameraSettings();

            if (binding.zxingBarcodeScanner.getBarcodeView().isPreviewActive()) {
                binding.zxingBarcodeScanner.pause();
            }

            //swap the id of the camera to be used
            if (settings.getRequestedCameraId() == Camera.CameraInfo.CAMERA_FACING_BACK) {
                settings.setRequestedCameraId(Camera.CameraInfo.CAMERA_FACING_FRONT);
            } else {
                settings.setRequestedCameraId(Camera.CameraInfo.CAMERA_FACING_BACK);
            }
            binding.zxingBarcodeScanner.getBarcodeView().setCameraSettings(settings);

            binding.zxingBarcodeScanner.resume();
        });

        binding.imgSelect.setOnClickListener(v -> {
            Intent pickIntent = new Intent(Intent.ACTION_PICK);
            pickIntent.setDataAndType(Uri.parse(MediaStore.Images.Media.DATA), "image/*");

            startActivityForResult(pickIntent, 999);
        });

    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 999 && resultCode == -1 && data != null) {
            uri = data.getData();
            startActivity(new Intent(this, ImageEditing.class).putExtra("path", getPath(this, uri)));

        }
    }



    @Override
    protected void onResume() {
        ((MyApplication) getApplicationContext()).setActivity(this, this);
        capture.onResume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        capture.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        capture.onDestroy();
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        capture.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        return binding.zxingBarcodeScanner.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }

    private boolean hasFlash() {
        return getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }

    public void changeMaskColor(View view) {
        int col = Color.argb(100, 0, 0, 0);
//        int color = Color.argb(100, rnd.nextInt(1), rnd.nextInt(214), rnd.nextInt(82));
        viewfinderView.setMaskColor(col);
    }

    public void changeLaserVisibility(boolean visible) {
        viewfinderView.setLaserVisibility(visible);
    }

    @Override
    public void onTorchOn() {

        binding.imgFlash.setImageResource(R.drawable.flash_off);
    }

    @Override
    public void onTorchOff() {
        binding.imgFlash.setImageResource(R.drawable.flash_on);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @Nonnull String[] permissions, @Nonnull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        capture.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void someEvent(Bitmap bitmap) {
        if (bitmap == null) {
            Log.e("TAG", "uri is not a bitmap," + uri.toString());
            return;
        }
        Frame frame = new Frame.Builder().setBitmap(bitmap).build();
        BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(this)
                .build();
        SparseArray<Barcode> sparseArray = barcodeDetector.detect(frame);


        if (sparseArray != null && sparseArray.size() > 0) {
            ScanQR scanQR = new ScanQR();

            for (int i = 0; i < sparseArray.size(); i++) {
                switch (sparseArray.valueAt(0).format) {
                    case 32:
                        scanQR.onCreate(getSupportFragmentManager(),this, sparseArray.valueAt(i).rawValue, "EAN_13");
                        startActivity(new Intent(this, ScanQR.class).putExtra("result_code", sparseArray.valueAt(i).rawValue).putExtra("result_formate", "EAN_13"));
                        break;
                    case 64:
                        scanQR.onCreate(getSupportFragmentManager(),this, sparseArray.valueAt(i).rawValue, "EAN_8");
//                        startActivity(new Intent(this, ScanQR.class).putExtra("result_code", sparseArray.valueAt(i).rawValue).putExtra("result_formate", "EAN_8"));
                        break;
                    case 1024:
                        scanQR.onCreate(getSupportFragmentManager(),this, sparseArray.valueAt(i).rawValue, "UPC_E");
//                        startActivity(new Intent(this, ScanQR.class).putExtra("result_code", sparseArray.valueAt(i).rawValue).putExtra("result_formate", "UPC_E"));
                        break;
                    case 512:
                        scanQR.onCreate(getSupportFragmentManager(),this, sparseArray.valueAt(i).rawValue, "UPC_A");
//                        startActivity(new Intent(this, ScanQR.class).putExtra("result_code", sparseArray.valueAt(i).rawValue).putExtra("result_formate", "UPC_A"));
                        break;
                    case 1:
                        scanQR.onCreate(getSupportFragmentManager(),this, sparseArray.valueAt(i).rawValue, "CODE_128");
//                        startActivity(new Intent(this, ScanQR.class).putExtra("result_code", sparseArray.valueAt(i).rawValue).putExtra("result_formate", "CODE_128"));
                        break;
                    case 4:
                        scanQR.onCreate(getSupportFragmentManager(),this, sparseArray.valueAt(i).rawValue, "CODE_93");
//                        startActivity(new Intent(this, ScanQR.class).putExtra("result_code", sparseArray.valueAt(i).rawValue).putExtra("result_formate", "CODE_93"));
                        break;
                    case 2:
                        scanQR.onCreate(getSupportFragmentManager(),this, sparseArray.valueAt(i).rawValue, "CODE_39");
//                        startActivity(new Intent(this, ScanQR.class).putExtra("result_code", sparseArray.valueAt(i).rawValue).putExtra("result_formate", "CODE_39"));
                        break;
                    case 8:
                        scanQR.onCreate(getSupportFragmentManager(),this, sparseArray.valueAt(i).rawValue, "CODABAR");
//                        startActivity(new Intent(this, ScanQR.class).putExtra("result_code", sparseArray.valueAt(i).rawValue).putExtra("result_formate", "CODABAR"));
                        break;
                    case 128:
                        scanQR.onCreate(getSupportFragmentManager(),this, sparseArray.valueAt(i).rawValue, "ITF");
//                        startActivity(new Intent(this, ScanQR.class).putExtra("result_code", sparseArray.valueAt(i).rawValue).putExtra("result_formate", "ITF"));
                        break;
                    case 256:
                        scanQR.onCreate(getSupportFragmentManager(), this, sparseArray.valueAt(i).rawValue, "QR_CODE");
//                        startActivity(new Intent(this, ScanQR.class).putExtra("result_code", sparseArray.valueAt(i).rawValue).putExtra("result_formate", "QR_CODE"));
                        break;

                }
            }
        }else {
            new CustomSnackUtil().showSnack(this, "No QR Code Found", R.drawable.error_msg_icon);
        }
    }
}