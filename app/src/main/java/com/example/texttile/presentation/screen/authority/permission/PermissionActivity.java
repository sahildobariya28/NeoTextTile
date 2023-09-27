package com.example.texttile.presentation.screen.authority.permission;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;

import com.example.texttile.databinding.ActivityPermissionBinding;
import com.example.texttile.data.dao.DaoAuthority;
import com.example.texttile.data.model.PermissionCategory;
import com.example.texttile.data.model.UserDataModel;
import com.example.texttile.presentation.ui.util.Const;
import com.example.texttile.presentation.ui.util.DialogUtil;

import java.util.ArrayList;
import java.util.HashMap;

public class PermissionActivity extends AppCompatActivity {


    ActivityPermissionBinding binding;
    ArrayList<PermissionCategory> permissionList = new ArrayList<>();
    PermissionCategoryAdapter permissionAdapter;
    UserDataModel userDataModel;
    String tracker;

    DaoAuthority dao;


    public PermissionActivity(){
        dao = new DaoAuthority(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPermissionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initToolbar();

        tracker = (getIntent().getStringExtra("tracker") != null) ? getIntent().getStringExtra("tracker") : "add";
        userDataModel = (getIntent().getSerializableExtra(Const.USER_DATA) != null) ? (UserDataModel) getIntent().getSerializableExtra(Const.USER_DATA) : new UserDataModel();

        if (tracker != null && tracker.equals("edit")) {
            if (userDataModel != null) {
                binding.btnAddUser.setText("Update User");

                permissionAdapter = new PermissionCategoryAdapter(this, userDataModel);
                binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
                binding.recyclerView.setAdapter(permissionAdapter);
            }
        } else {
            if (userDataModel != null) {
                binding.btnAddUser.setText("Add User");

                permissionList.add(new PermissionCategory(0,"Design Master", false, false,false ,false, false));
                permissionList.add(new PermissionCategory(1,"Design Shade Master", false, false,false ,false, false));
                permissionList.add(new PermissionCategory(2,"Yarn Master", false, false,false ,false, false));
                permissionList.add(new PermissionCategory(3,"Machine Master", false, false,false ,false, false));
                permissionList.add(new PermissionCategory(4,"Jala Master", false, false,false ,false, false));
                permissionList.add(new PermissionCategory(5,"Employee Master", false, false,false ,false, false));
                permissionList.add(new PermissionCategory(6,"Party Master", false, false,false ,false, false));
                permissionList.add(new PermissionCategory(7,"New Order", false, false,false ,false, false));

                permissionList.add(new PermissionCategory(8,"Pending", false, false,false ,false, false));
                permissionList.add(new PermissionCategory(9,"On Machine Pending", false, false,false ,false, false));
                permissionList.add(new PermissionCategory(10,"On Machine Completed", false, false,false ,false, false));
                permissionList.add(new PermissionCategory(11,"Ready to Dispatch", false, false,false ,false, false));
                permissionList.add(new PermissionCategory(12,"Warehouse", false, false,false ,false, false));
                permissionList.add(new PermissionCategory(13,"Final Dispatch", false, false,false ,false, false));
                permissionList.add(new PermissionCategory(14,"Delivered", false, false,false ,false, false));
                permissionList.add(new PermissionCategory(15,"Damage", false, false,false ,false, false));

                permissionList.add(new PermissionCategory(16,"Order Tracker", false, false,false ,false, false));
                permissionList.add(new PermissionCategory(17,"Allot Program", false, false,false ,false, false));
                permissionList.add(new PermissionCategory(18,"Employee Allotment", false, false,false ,false, false));
                permissionList.add(new PermissionCategory(19,"Daily Reading", false, false,false ,false, false));
                permissionList.add(new PermissionCategory(20,"Yarn Reading", false, false,false ,false, false));
                permissionList.add(new PermissionCategory(21,"Ready Stock", false, false,false ,false, false));
                permissionList.add(new PermissionCategory(22,"Storage File", false, false,false ,false, false));
                permissionList.add(new PermissionCategory(23,"Scanner", false, false,false ,false, false));
                permissionList.add(new PermissionCategory(24,"Excel to Firebase", false, false,false ,false, false));
                permissionList.add(new PermissionCategory(25,"Firebase to Excel", false, false,false ,false, false));



                userDataModel.setPermissionList(permissionList);

                permissionAdapter = new PermissionCategoryAdapter(this, userDataModel);
                binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
                binding.recyclerView.setAdapter(permissionAdapter);
            }
        }


        binding.btnAddUser.setOnClickListener(view -> {

            addArtist();

        });
    }


    private void addArtist() {

        if (tracker.equals("edit")){
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("permissionList", permissionAdapter.getList().getPermissionList());
            hashMap.put("f_name", permissionAdapter.getList().getF_name());
            hashMap.put("password", permissionAdapter.getList().getPassword());
            hashMap.put("phone_number", permissionAdapter.getList().getPhone_number());
            hashMap.put("u_name", permissionAdapter.getList().getU_name());
            hashMap.put("type", permissionAdapter.getList().getType());

            dao.update(userDataModel.getId(), hashMap).addOnSuccessListener(runnable -> {
                DialogUtil.showUpdateSuccessDialog(this, true);
            });

        }else {
            userDataModel = permissionAdapter.getList();
            dao.insert(userDataModel.getId(), userDataModel).addOnSuccessListener(runnable -> {
                DialogUtil.showSuccessDialog(this, true);
            });
        }

    }

    public void initToolbar(){
        binding.textTitle.setText("Permission");
        binding.btnBack.setOnClickListener(view -> {
            onBackPressed();
        });
    }
}