package com.example.texttile.presentation.screen.features.reading_feature.yarn_reading.yarn_company_reading_list;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.texttile.R;
import com.example.texttile.databinding.ActivityCompanyListBinding;
import com.example.texttile.data.dao.DaoYarnMaster;
import com.example.texttile.data.model.YarnCompanyModel;
import com.example.texttile.presentation.ui.lib.snackUtil.CustomSnackUtil;
import com.example.texttile.presentation.ui.util.DialogUtil;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;

import javax.annotation.Nullable;

public class YarnCompanyListActivity extends AppCompatActivity {

    String yarn_id;
    ActivityCompanyListBinding binding;

    YarnCompanyListAdapter yarnCompanyListAdapter;
    ArrayList<YarnCompanyModel> yarnCompanyList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCompanyListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        yarn_id = getIntent().getStringExtra("yarn_id");

        initToolbar("Yarn Company List");



        yarn_master_list();
    }

    public void yarn_master_list() {
        if (yarnCompanyListAdapter == null) DialogUtil.showProgressDialog(this);


        new DaoYarnMaster(this).getReference().document(yarn_id).collection("yarnCompanyList").orderBy("fav", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        yarnCompanyList.clear();
                        if (e != null) {
                            Log.w("TAG", "Listen failed.", e);
                            return;
                        }
                        if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                YarnCompanyModel yarnCompanyModel = documentSnapshot.toObject(YarnCompanyModel.class);
                                if (yarnCompanyModel != null) {
                                    yarnCompanyList.add(yarnCompanyModel);
                                }
                            }
                            DialogUtil.hideProgressDialog();
                            Collections.reverse(yarnCompanyList);
                            yarnCompanyListAdapter = new YarnCompanyListAdapter(yarnCompanyList, yarn_id, YarnCompanyListActivity.this);
                            binding.recyclerView.setLayoutManager(new LinearLayoutManager(YarnCompanyListActivity.this));
                            binding.recyclerView.setAdapter(yarnCompanyListAdapter);
                        } else {
                            DialogUtil.hideProgressDialog();
                            yarnCompanyListAdapter = new YarnCompanyListAdapter(yarnCompanyList, yarn_id, YarnCompanyListActivity.this);
                            binding.recyclerView.setLayoutManager(new LinearLayoutManager(YarnCompanyListActivity.this));
                            binding.recyclerView.setAdapter(yarnCompanyListAdapter);
                            new CustomSnackUtil().showSnack(YarnCompanyListActivity.this, "Yarn company List is Empty", R.drawable.error_msg_icon);
                        }
                    }
                });

//        new DaoYarnMaster(this).getReference().child(yarn_id).child("yarnCompanyList").orderByChild("fav").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@Nonnull DataSnapshot dataSnapshot) {
//                yarnCompanyList.clear();
//
//
//                if (dataSnapshot.exists()) {
//                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
//                        YarnCompanyModel yarnCompanyModel = postSnapshot.getValue(YarnCompanyModel.class);
//                        if (yarnCompanyModel != null) {
//                            yarnCompanyList.add(yarnCompanyModel);
//                        }
//                    }
//                    DialogUtil.hideProgressDialog();
//                    Collections.reverse(yarnCompanyList);
//                    yarnCompanyListAdapter = new YarnCompanyListAdapter(yarnCompanyList, yarn_id, YarnCompanyListActivity.this);
//                    binding.recyclerView.setLayoutManager(new LinearLayoutManager(YarnCompanyListActivity.this));
//                    binding.recyclerView.setAdapter(yarnCompanyListAdapter);
//                } else {
//                    DialogUtil.hideProgressDialog();
//                    yarnCompanyListAdapter = new YarnCompanyListAdapter(yarnCompanyList, yarn_id, YarnCompanyListActivity.this);
//                    binding.recyclerView.setLayoutManager(new LinearLayoutManager(YarnCompanyListActivity.this));
//                    binding.recyclerView.setAdapter(yarnCompanyListAdapter);
//                    new CustomSnackUtil().showSnack(YarnCompanyListActivity.this, "Yarn company List is Empty", R.drawable.error_msg_icon);
//                }
//            }
//
//            @Override
//            public void onCancelled(@Nonnull DatabaseError error) {
//            }
//        });
    }

    public void initToolbar(String name) {
        binding.include.textTitle.setText(name);
        binding.include.btnBack.setOnClickListener(view -> {
            onBackPressed();
        });
    }
}