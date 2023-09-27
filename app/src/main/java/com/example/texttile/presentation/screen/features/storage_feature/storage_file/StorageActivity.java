package com.example.texttile.presentation.screen.features.storage_feature.storage_file;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.texttile.R;
import com.example.texttile.databinding.ActivityStorageBinding;
import com.example.texttile.data.dao.DAODesignMaster;
import com.example.texttile.presentation.ui.interfaces.EmptyDataListener;
import com.example.texttile.data.model.DesignMasterModel;
import com.example.texttile.presentation.ui.lib.snackUtil.CustomSnackUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import javax.annotation.Nonnull;

import java.util.ArrayList;

public class StorageActivity extends AppCompatActivity implements EmptyDataListener {

    ArrayList<DesignMasterModel> designMasterModelsList = new ArrayList<>();
    ActivityStorageBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStorageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initToolbar();

        if (!isPermission()){
            requestPermission();
        }

        new DAODesignMaster(this).getReference().get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@Nonnull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<DesignMasterModel> designMasterModelsList = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                DesignMasterModel designMasterModel = document.toObject(DesignMasterModel.class);
                                designMasterModelsList.add(designMasterModel);
                            }
                            StorageAdapter storageAdapter = new StorageAdapter(StorageActivity.this, designMasterModelsList);
                            binding.recyclerView.setLayoutManager(new LinearLayoutManager(StorageActivity.this));
                            binding.recyclerView.setAdapter(storageAdapter);
                        } else {
                            Log.w("TAG", "Error getting documents.", task.getException());
                        }
                    }
                });

//        new DAODesignMaster(this).getReference().addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@Nonnull DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists()) {
//                    designMasterModelsList.clear();
//                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
//                        DesignMasterModel designMasterModel = postSnapshot.getValue(DesignMasterModel.class);
//
//                        designMasterModelsList.add(designMasterModel);
//                    }
//
//                    StorageAdapter storageAdapter = new StorageAdapter(StorageActivity.this, designMasterModelsList);
//                    binding.recyclerView.setLayoutManager(new LinearLayoutManager(StorageActivity.this));
//                    binding.recyclerView.setAdapter(storageAdapter);
//                }else {
//                    onDataChanged(true, 0);
//                }
//            }
//
//            @Override
//            public void onCancelled(@Nonnull DatabaseError databaseError) {
//
//            }
//        });
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
        binding.include.textTitle.setText("Storage File");
        binding.include.btnBack.setOnClickListener(view -> {
            onBackPressed();
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
}