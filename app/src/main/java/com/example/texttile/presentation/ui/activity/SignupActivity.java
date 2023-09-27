package com.example.texttile.presentation.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;

import com.example.texttile.core.MyApplication;
import com.example.texttile.R;
import com.example.texttile.data.dao.DaoAuthority;
import com.example.texttile.data.model.PermissionCategory;
import com.example.texttile.data.model.UserDataModel;
import com.example.texttile.databinding.ActivitySignupBinding;
import com.example.texttile.presentation.ui.lib.snackUtil.CustomSnackUtil;
import com.example.texttile.presentation.ui.util.DialogUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignupActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    String[] category = {"Admin"};

    int spinner_position = 0;
    ArrayList<PermissionCategory> permissionList = new ArrayList<>();

    ActivitySignupBinding binding;
    DaoAuthority dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dao = new DaoAuthority(this);

        binding.spinner.setOnItemSelectedListener(SignupActivity.this);
        ArrayAdapter aa = new ArrayAdapter(SignupActivity.this, R.layout.simple_spinner_item, category);
        aa.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        binding.spinner.setAdapter(aa);

        binding.btnSignUp.setOnClickListener(view -> {
            addArtist();
        });

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        spinner_position = i;
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private void addArtist() {

        String username = binding.editUsername.getText().toString().trim();
        String phone = binding.editPhone.getText().toString().trim();
        String password = binding.editPassword.getText().toString().trim();
        String full_name = binding.editFullName.getText().toString().trim();
        String conform_password = binding.editConformPassword.getText().toString().trim();

        String expression = "^(?=[a-zA-Z0-9._]{8,20}$)(?!.*[_.]{2})[^_.].*[^_.]$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(username);

        dao.getReference().whereEqualTo("u_name", binding.editUsername.getText().toString().trim()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NotNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (querySnapshot.isEmpty()) {

                        if (TextUtils.isEmpty(username)) {
                            new CustomSnackUtil().showSnack(SignupActivity.this, "Please enter a Username", R.drawable.error_msg_icon);
                        } else if (TextUtils.isEmpty(phone)) {
                            new CustomSnackUtil().showSnack(SignupActivity.this, "Please enter a phone number", R.drawable.error_msg_icon);
                        } else if (TextUtils.isEmpty(password)) {
                            new CustomSnackUtil().showSnack(SignupActivity.this, "Please enter a Password", R.drawable.error_msg_icon);
                        } else if (TextUtils.isEmpty(full_name)) {
                            new CustomSnackUtil().showSnack(SignupActivity.this, "Please enter a Password", R.drawable.error_msg_icon);
                        } else if (TextUtils.isEmpty(conform_password)) {
                            new CustomSnackUtil().showSnack(SignupActivity.this, "Please enter a Password", R.drawable.error_msg_icon);
                        } else if (!password.equals(conform_password)) {
                            new CustomSnackUtil().showSnack(SignupActivity.this, "Password not match", R.drawable.error_msg_icon);
                        } else if (!matcher.matches()) {
                            new CustomSnackUtil().showSnack(SignupActivity.this, "Invalid Username", R.drawable.error_msg_icon);
                        } else {
                            String id = dao.getId();
                            UserDataModel orderData = new UserDataModel(id, username, full_name, category[spinner_position], phone, password);

                            //add default user
                            permissionList.add(new PermissionCategory(0, "Design Master", true, true, true, true, false));
                            permissionList.add(new PermissionCategory(1, "Design Shade Master", true, true, true, true, false));
                            permissionList.add(new PermissionCategory(2, "Yarn Master", true, true, true, true, false));
                            permissionList.add(new PermissionCategory(3, "Machine Master", true, true, true, true, false));
                            permissionList.add(new PermissionCategory(4, "Jala Master", true, true, true, true, false));
                            permissionList.add(new PermissionCategory(5, "Employee Master", true, true, true, true, false));
                            permissionList.add(new PermissionCategory(6, "Party Master", true, true, true, true, false));
                            permissionList.add(new PermissionCategory(7, "New Order", true, true, true, true, false));

                            permissionList.add(new PermissionCategory(8, "Pending", true, true, true, true, false));
                            permissionList.add(new PermissionCategory(9, "On Machine Pending", true, true, true, true, false));
                            permissionList.add(new PermissionCategory(10, "On Machine Completed", true, true, true, true, false));
                            permissionList.add(new PermissionCategory(11, "Ready to Dispatch", true, true, true, true, false));
                            permissionList.add(new PermissionCategory(12, "Warehouse", true, true, true, true, false));
                            permissionList.add(new PermissionCategory(13, "Final Dispatch", true, true, true, true, false));
                            permissionList.add(new PermissionCategory(14, "Delivered", true, true, true, true, false));
                            permissionList.add(new PermissionCategory(15, "Damage", true, true, true, true, false));

                            permissionList.add(new PermissionCategory(16, "Order Tracker", true, true, true, true, false));
                            permissionList.add(new PermissionCategory(17, "Allot Program", true, true, true, true, false));
                            permissionList.add(new PermissionCategory(18, "Employee Allotment", true, true, true, true, false));
                            permissionList.add(new PermissionCategory(19, "Daily Reading", true, true, true, true, false));
                            permissionList.add(new PermissionCategory(20, "Yarn Reading", true, true, true, true, false));
                            permissionList.add(new PermissionCategory(21, "Ready Stock", true, true, true, true, false));
                            permissionList.add(new PermissionCategory(22, "Storage File", true, true, true, true, false));
                            permissionList.add(new PermissionCategory(23, "Scanner", true, true, true, true, false));
                            permissionList.add(new PermissionCategory(24, "Excel to Firebase", true, true, true, true, false));
                            permissionList.add(new PermissionCategory(25, "Firebase to Excel", true, true, true, true, false));

                            orderData.setPermissionList(permissionList);
                            DialogUtil.showProgressDialog(SignupActivity.this);
                            dao.insert(id, orderData).addOnSuccessListener(runnable -> {
                                DialogUtil.showSuccessDialog(SignupActivity.this, false);
                                finish();
                                startActivity(new Intent(SignupActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                            });


                        }

                    }
                }
            }

        });


//        dao.getReference().
//
//                orderByChild("u_name").
//
//                equalTo(binding.editUsername.getText().
//
//                        toString().
//
//                        trim()).
//
//                addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@Nonnull DataSnapshot snapshot) {
//                        if (!snapshot.exists()) {
//
//                            if (TextUtils.isEmpty(username)) {
//                                new CustomSnackUtil().showSnack(SignupActivity.this, "Please enter a Username", R.drawable.error_msg_icon);
//                            } else if (TextUtils.isEmpty(phone)) {
//                                new CustomSnackUtil().showSnack(SignupActivity.this, "Please enter a phone number", R.drawable.error_msg_icon);
//                            } else if (TextUtils.isEmpty(password)) {
//                                new CustomSnackUtil().showSnack(SignupActivity.this, "Please enter a Password", R.drawable.error_msg_icon);
//                            } else if (TextUtils.isEmpty(full_name)) {
//                                new CustomSnackUtil().showSnack(SignupActivity.this, "Please enter a Password", R.drawable.error_msg_icon);
//                            } else if (TextUtils.isEmpty(conform_password)) {
//                                new CustomSnackUtil().showSnack(SignupActivity.this, "Please enter a Password", R.drawable.error_msg_icon);
//                            } else if (!password.equals(conform_password)) {
//                                new CustomSnackUtil().showSnack(SignupActivity.this, "Password not match", R.drawable.error_msg_icon);
//                            } else if (!matcher.matches()) {
//                                new CustomSnackUtil().showSnack(SignupActivity.this, "Invalid Username", R.drawable.error_msg_icon);
//                            } else {
//                                String id = dao.getId();
//                                UserDataModel orderData = new UserDataModel(id, username, full_name, category[spinner_position], phone, password);
//
//                                //add default user
//                                permissionList.add(new PermissionCategory(0, "Design Master", true, true, true, true, false));
//                                permissionList.add(new PermissionCategory(1, "Design Shade Master", true, true, true, true, false));
//                                permissionList.add(new PermissionCategory(2, "Yarn Master", true, true, true, true, false));
//                                permissionList.add(new PermissionCategory(3, "Machine Master", true, true, true, true, false));
//                                permissionList.add(new PermissionCategory(4, "Jala Master", true, true, true, true, false));
//                                permissionList.add(new PermissionCategory(5, "Employee Master", true, true, true, true, false));
//                                permissionList.add(new PermissionCategory(6, "Party Master", true, true, true, true, false));
//                                permissionList.add(new PermissionCategory(7, "New Order", true, true, true, true, false));
//
//                                permissionList.add(new PermissionCategory(8, "Pending", true, true, true, true, false));
//                                permissionList.add(new PermissionCategory(9, "On Machine Pending", true, true, true, true, false));
//                                permissionList.add(new PermissionCategory(10, "On Machine Completed", true, true, true, true, false));
//                                permissionList.add(new PermissionCategory(11, "Ready to Dispatch", true, true, true, true, false));
//                                permissionList.add(new PermissionCategory(12, "Warehouse", true, true, true, true, false));
//                                permissionList.add(new PermissionCategory(13, "Final Dispatch", true, true, true, true, false));
//                                permissionList.add(new PermissionCategory(14, "Delivered", true, true, true, true, false));
//                                permissionList.add(new PermissionCategory(15, "Damage", true, true, true, true, false));
//
//                                permissionList.add(new PermissionCategory(16, "Order Tracker", true, true, true, true, false));
//                                permissionList.add(new PermissionCategory(17, "Allot Program", true, true, true, true, false));
//                                permissionList.add(new PermissionCategory(18, "Employee Allotment", true, true, true, true, false));
//                                permissionList.add(new PermissionCategory(19, "Daily Reading", true, true, true, true, false));
//                                permissionList.add(new PermissionCategory(20, "Yarn Reading", true, true, true, true, false));
//                                permissionList.add(new PermissionCategory(21, "Ready Stock", true, true, true, true, false));
//                                permissionList.add(new PermissionCategory(22, "Storage File", true, true, true, true, false));
//                                permissionList.add(new PermissionCategory(23, "Scanner", true, true, true, true, false));
//                                permissionList.add(new PermissionCategory(24, "Excel to Firebase", true, true, true, true, false));
//                                permissionList.add(new PermissionCategory(25, "Firebase to Excel", true, true, true, true, false));
//
//                                orderData.setPermissionList(permissionList);
//                                DialogUtil.showProgressDialog(SignupActivity.this);
//                                dao.add(id, orderData).addOnSuccessListener(runnable -> {
//                                    DialogUtil.showSuccessDialog(SignupActivity.this, false);
//                                });
//
//                                finish();
//                                startActivity(new Intent(SignupActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
//                                finish();
//                            }
//
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@Nonnull DatabaseError error) {
//
//                    }
//                });
    }

    @Override
    public void onBackPressed() {
        finish();
        finishAffinity();
    }

    @Override
    protected void onResume() {
        ((MyApplication) getApplicationContext()).setActivity(this, this);
        super.onResume();
    }
}