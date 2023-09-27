package com.example.texttile.presentation.ui.activity;



import static com.example.texttile.core.MyApplication.setUSERMODEL;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.texttile.core.MyApplication;
import com.example.texttile.R;
import com.example.texttile.databinding.ActivityLoginBinding;
import com.example.texttile.data.dao.DaoAuthority;
import com.example.texttile.data.model.UserDataModel;
import com.example.texttile.presentation.ui.lib.snackUtil.CustomSnackUtil;
import com.example.texttile.presentation.ui.util.PrefUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {

    CollectionReference collectionReference;
    ActivityLoginBinding binding;
    PrefUtil prefUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        collectionReference = new DaoAuthority(this).getReference();
        prefUtil = new PrefUtil(this);

        binding.btnLogin.setOnClickListener(view -> {
            checkUserList();
        });
    }


    private void checkUserList() {
        String userName = binding.editUsername.getText().toString().trim();
        String password = binding.editPassword.getText().toString().trim();

        if (TextUtils.isEmpty(userName)) {
            new CustomSnackUtil().showSnack(this, "Username is Empty", R.drawable.icon_warning);
        } else if (TextUtils.isEmpty(password)) {
            new CustomSnackUtil().showSnack(this, "password is Empty", R.drawable.icon_warning);
        } else {
            collectionReference.get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            ArrayList<UserDataModel> userDataModels = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                UserDataModel userModel = document.toObject(UserDataModel.class);
                                userDataModels.add(userModel);
                                Log.d("dfdkfksdf", "onDataChange: " + userModel.getU_name() + userModel.getPassword());
                            }
                            for (int i = 0; i < userDataModels.size(); i++) {
                                UserDataModel userModel = userDataModels.get(i);
                                if (userModel.getU_name().equals(userName) && userModel.getPassword().equals(password)) {
                                    binding.editPassword.clearFocus();
                                    binding.editUsername.clearFocus();

                                    prefUtil.setUser(userModel);
                                    setUSERMODEL(userModel);
                                    startActivity(new Intent(LoginActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                                    finish();
                                    break;

                                } else {
                                    if (i == (userDataModels.size() - 1)) {
                                        new CustomSnackUtil().showSnack(LoginActivity.this, "Incorrect UserName/Password", R.drawable.error_msg_icon);
                                    }
                                }

                            }
                        } else {
                            prefUtil.removeUser();
                            startActivity(new Intent(LoginActivity.this, SignupActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                            finish();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.w("Firestore", "Error getting documents.", e);
                    });


//            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@Nonnull DataSnapshot dataSnapshot) {
//
//
//                    ArrayList<UserDataModel> userDataModels = new ArrayList<>();
//                    if (dataSnapshot.exists()) {
//                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
//                            UserDataModel userModel = postSnapshot.getValue(UserDataModel.class);
//                            userDataModels.add(userModel);
//                            Log.d("dfdkfksdf", "onDataChange: " + userModel.getU_name() + userModel.getPassword());
//                        }
//                        for (int i = 0; i < userDataModels.size(); i++) {
//                            UserDataModel userModel = userDataModels.get(i);
//                            if (userModel.getU_name().equals(userName) && userModel.getPassword().equals(password)) {
//                                binding.editPassword.clearFocus();
//                                binding.editUsername.clearFocus();
//
//                                prefUtil.setUser(userModel);
//                                setUSERMODEL(userModel);
//                                startActivity(new Intent(LoginActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
//                                finish();
//                                break;
//
//                            } else {
//                                if (i == (userDataModels.size() - 1)) {
//                                    new CustomSnackUtil().showSnack(LoginActivity.this, "Incorrect UserName/Password", R.drawable.error_msg_icon);
//                                }
//                            }
//
//                        }
//
//                    } else {
//                        prefUtil.removeUser();
//                        startActivity(new Intent(LoginActivity.this, SignupActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
//                        finish();
//                    }
//                }
//
//                @Override
//                public void onCancelled(@Nonnull DatabaseError databaseError) {
//
//                }
//            });

        }

    }

    @Override
    protected void onResume() {
        new DaoAuthority(this).getReference().get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NotNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot snapshot = task.getResult();
                    if (snapshot.isEmpty()) {
                        new PrefUtil(LoginActivity.this).removeUser();
                        startActivity(new Intent(LoginActivity.this, SignupActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                        finish();
                    }
                }
            }
        });

        ((MyApplication) getApplicationContext()).setActivity(this, this);
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        finish();
        finishAffinity();
    }
}