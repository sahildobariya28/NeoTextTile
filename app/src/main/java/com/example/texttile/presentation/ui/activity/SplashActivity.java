package com.example.texttile.presentation.ui.activity;

import static com.example.texttile.core.MyApplication.setUSERMODEL;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

import com.example.texttile.core.MyApplication;
import com.example.texttile.data.dao.DaoAuthority;
import com.example.texttile.data.model.UserDataModel;
import com.example.texttile.databinding.ActivitySplashBinding;
import com.example.texttile.presentation.ui.receivers.InternetReceiver;
import com.example.texttile.presentation.ui.util.PrefUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class SplashActivity extends AppCompatActivity {

    ArrayList<UserDataModel> userDataModels = new ArrayList<>();
    ActivitySplashBinding binding;
    boolean is_ready = false;
    BroadcastReceiver broadcastReceiver = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);

        SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        broadcastReceiver = new InternetReceiver();
        dismissSplashScreen();
    }

    public void InternetStatus() {
        registerReceiver(broadcastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    private void dismissSplashScreen() {

        new Handler().postDelayed(() -> {
            is_ready = true;
            PrefUtil prefUtil = new PrefUtil(this);

            new DaoAuthority(this).getReference().get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    ArrayList<UserDataModel> userDataModelList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        UserDataModel userModel = document.toObject(UserDataModel.class);
                        userDataModelList.add(userModel);
                    }
                    if (prefUtil.getUser() != null) {
                        for (int i = 0; i < userDataModelList.size(); i++) {
                            UserDataModel userModel = userDataModelList.get(i);
                            if (prefUtil.getUser().getU_name().equals(userModel.getU_name())) {
                                prefUtil.setUser(userModel);
                                setUSERMODEL(userModel);
                                startActivity(new Intent(SplashActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                                finish();
                                break;
                            } else {
                                if (i == (userDataModelList.size() - 1)) {
                                    prefUtil.removeUser();
                                    startActivity(new Intent(SplashActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                                    finish();
                                }
                            }
                        }
                    } else {
                        new DaoAuthority(this).getReference().get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NotNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    QuerySnapshot snapshot = task.getResult();
                                    if (snapshot.isEmpty()) {
                                        prefUtil.removeUser();
                                        startActivity(new Intent(SplashActivity.this, SignupActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                                        finish();
                                    }else {
                                        prefUtil.removeUser();
                                        startActivity(new Intent(SplashActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                                        finish();
                                    }
                                }
                            }
                        });
                    }
                } else {
                    prefUtil.removeUser();
                    startActivity(new Intent(SplashActivity.this, SignupActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                    finish();
                }
            }).addOnFailureListener(e -> {
                prefUtil.removeUser();
                startActivity(new Intent(SplashActivity.this, SignupActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                finish();
            });


//            new DaoAuthority(this).getReference().addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@Nonnull DataSnapshot snapshot) {
//                    ArrayList<UserDataModel> userDataModelList = new ArrayList<>();
//
//                    if (snapshot.exists()) {
//                        if (prefUtil.getUser() != null) {
//                            for (DataSnapshot postSnapshot : snapshot.getChildren()) {
//                                UserDataModel userModel = postSnapshot.getValue(UserDataModel.class);
//                                userDataModelList.add(userModel);
//                            }
//
//                            for (int i = 0; i < userDataModelList.size(); i++) {
//                                UserDataModel userModel = userDataModelList.get(i);
//                                if (prefUtil.getUser().getU_name().equals(userModel.getU_name())) {
//                                    prefUtil.setUser(userModel);
//                                    setUSERMODEL(userModel);
//                                    startActivity(new Intent(SplashActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
//                                    finish();
//                                    break;
//                                } else {
//                                    if (i == (userDataModelList.size() - 1)) {
//                                        prefUtil.removeUser();
//                                        startActivity(new Intent(SplashActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
//                                        finish();
//                                    }
//                                }
//                            }
//                        } else {
//                            prefUtil.removeUser();
//                            startActivity(new Intent(SplashActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
//                            finish();
//                        }
//                    } else {
//                        prefUtil.removeUser();
//                        startActivity(new Intent(SplashActivity.this, SignupActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
//                        finish();
//                    }
//                }
//
//                @Override
//                public void onCancelled(@Nonnull DatabaseError error) {
//
//                }
//            });

        }, 2000);
    }

    @Override
    protected void onStart() {
        InternetStatus();
        super.onStart();
    }

    @Override
    protected void onResume() {
        new DaoAuthority(this).getReference().get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NotNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot snapshot = task.getResult();
                    if (snapshot.isEmpty()) {
                        new PrefUtil(SplashActivity.this).removeUser();
                        startActivity(new Intent(SplashActivity.this, SignupActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                        finish();
                    }
                }
            }
        });


//        new DaoAuthority(this).getReference().addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@Nonnull DataSnapshot snapshot) {
//                if (!snapshot.exists()) {
//                    new PrefUtil(SplashActivity.this).removeUser();
//                    startActivity(new Intent(SplashActivity.this, SignupActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
//                    finish();
//                }
//            }
//
//            @Override
//            public void onCancelled(@Nonnull DatabaseError error) {
//
//            }
//        });
        ((MyApplication) getApplicationContext()).setActivity(this, this);
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
        }
        super.onDestroy();
    }
}