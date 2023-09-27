package com.example.texttile.presentation.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.texttile.databinding.FragmentDashbardBinding;
import com.example.texttile.data.dao.DAOExtraOrder;
import com.example.texttile.data.dao.DAOMachineMaster;
import com.example.texttile.data.dao.DAOOrder;
import com.example.texttile.presentation.screen.near_complete_order.NearCompleteOrderActivity;
import com.example.texttile.data.model.MachineMasterModel;
import com.example.texttile.data.model.OrderMasterModel;
import com.example.texttile.data.model.ReadyStockModel;
import com.example.texttile.presentation.ui.util.Const;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public class DashboardFragment extends Fragment{

    FragmentDashbardBinding binding;

    DAOOrder daoOrder;
    DAOExtraOrder daoExtraOrder;
    DAOMachineMaster daoMachineMaster;
    public ArrayList<OrderMasterModel> nearCompleteOrderList = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        daoOrder = new DAOOrder(getActivity());
        daoExtraOrder = new DAOExtraOrder(getActivity());
        daoMachineMaster = new DAOMachineMaster(getActivity());
    }

    @Override
    public View onCreateView(@Nonnull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDashbardBinding.inflate(getLayoutInflater());

        initToolbar();

        getMachineList();
        getReturnStock();
        getExtraStock();
        getNearComplete();

        binding.btnNearComplete.setOnClickListener(view -> {
            startActivity(new Intent(getActivity(), NearCompleteOrderActivity.class));
        });

        return binding.getRoot();
    }

    private void getNearComplete() {
        daoOrder.getReference().addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.w("TAG", "Listen failed.", error);
                    return;
                }

                int count = 0;
                nearCompleteOrderList.clear();
                for (QueryDocumentSnapshot doc : value) {
                    Log.d("jdksjklfweee2", "onEvent: " + doc.getData());
                    OrderMasterModel orderMasterModel = doc.toObject(OrderMasterModel.class);
                    if (orderMasterModel.getOrderStatusModel().getOnDelivered() == (orderMasterModel.getOrderStatusModel().getTotal() - 1)){
                        nearCompleteOrderList.add(orderMasterModel);
                        count++;
                    }
                }
                binding.txtNearComplete.setText("" + count);
            }
        });
    }

    private void getReturnStock() {
        new DAOOrder(getActivity()).getReference().whereGreaterThanOrEqualTo("orderStatusModel." + Const.ON_DAMAGE, 1).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("TAG", "Listen failed.", e);
                    return;
                }

                int return_count = 0;
                for (QueryDocumentSnapshot doc : snapshot) {
                    OrderMasterModel orderMasterModel = doc.toObject(OrderMasterModel.class);
                    return_count += orderMasterModel.getOrderStatusModel().getOnDamage();
                }
                binding.txtReturnStatus.setText(String.valueOf(return_count));
            }
        });
    }

    private void getExtraStock() {
        daoExtraOrder.getReference().addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.w("TAG", "Listen failed.", error);
                    return;
                }

                int count = 0;
                for (QueryDocumentSnapshot doc : value) {
                    ReadyStockModel readyStockModel = doc.toObject(ReadyStockModel.class);
                    count = count + readyStockModel.getQuantity();
                }
                binding.txtExtraStock.setText("" + count);
            }
        });
    }

    private void getMachineList() {
        daoMachineMaster.getReference().addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                int isTrue = 0;
                int isFalse = 0;
                if (error != null) {
                    return;
                }
                for (QueryDocumentSnapshot doc : value) {
                    MachineMasterModel machineMasterModel = doc.toObject(MachineMasterModel.class);
                    if (machineMasterModel.isStatus()) {
                        isTrue++;
                    } else {
                        isFalse++;
                    }
                }
                binding.txtMachineStatus.setText("" + isTrue + "/" + (isTrue + isFalse));
            }
        });
    }

    public void initToolbar(){
        binding.textTitle.setText("DashBoard");
        binding.btnBack.setOnClickListener(view -> {
            getActivity().onBackPressed();
        });
    }

}