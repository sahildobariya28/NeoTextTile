package com.example.texttile.presentation.screen.features.allot_feature.allot_program.activity.allot;

import android.app.Activity;
import android.util.Log;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.texttile.data.dao.DAODesignMaster;
import com.example.texttile.data.dao.DAOMachineMaster;
import com.example.texttile.data.dao.DAOOrder;
import com.example.texttile.data.dao.DAOShadeMaster;
import com.example.texttile.data.dao.DaoAllotProgram;
import com.example.texttile.data.model.AllotProgramModel;
import com.example.texttile.data.model.DesignMasterModel;
import com.example.texttile.data.model.MachineMasterModel;
import com.example.texttile.data.model.OrderMasterModel;
import com.example.texttile.data.model.ShadeMasterModel;
import com.example.texttile.presentation.ui.util.DialogUtil;
import com.example.texttile.presentation.ui.util.ListShowDialog;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nullable;

public class AllotProgramViewModel extends ViewModel {

    MutableLiveData<String> tracker = new MutableLiveData<>("add");
    MutableLiveData<String> order_no = new MutableLiveData<>("");
    AllotProgramModel allotProgramModel;
    OrderMasterModel orderModel;

    Activity activity;
    public MutableLiveData<ArrayList<String>> orderNoList = new MutableLiveData<>(new ArrayList<>());
    public MutableLiveData<ArrayList<OrderMasterModel>> orderMasterList = new MutableLiveData<>(new ArrayList<>());
    public MutableLiveData<ArrayList<String>> machine_name_list = new MutableLiveData<>(new ArrayList<>());
    public MutableLiveData<OrderMasterModel> orderMasterModel = new MutableLiveData<>();

    DAOOrder daoOrder;
    DaoAllotProgram dao;

    ListShowDialog machineListDialog, orderListDialog;
    final int MACHINE_REQUEST = 1, ORDER_REQUEST = 2;

    public AllotProgramViewModel(String tracker, Activity activity) {
        this.tracker.setValue(tracker);
        this.activity = activity;

        daoOrder = new DAOOrder(activity);
        dao = new DaoAllotProgram(activity);

        order_no.setValue((activity.getIntent().getStringExtra("order_no") != null) ? activity.getIntent().getStringExtra("order_no") : "");
        tracker = (activity.getIntent().getStringExtra("tracker") != null) ? activity.getIntent().getStringExtra("tracker") : "add";
        allotProgramModel = (tracker.equals("edit") && activity.getIntent().getSerializableExtra("allot_data") != null) ? (AllotProgramModel) activity.getIntent().getSerializableExtra("allot_data") : new AllotProgramModel();

        if (!order_no.getValue().isEmpty()){
            getMachineListWithOrderFilter(order_no.getValue());
        }else {
            getOrderList();
            getMachineList();
        }
    }

    public void getOrderList(String machine_name) {
        ArrayList<DesignMasterModel> designMasterModels = new ArrayList<>();
        ArrayList<ShadeMasterModel> shadeMasterModels = new ArrayList<>();


        new DAOMachineMaster(activity).getReference().whereEqualTo("machine_name", machine_name).addSnapshotListener((value, error) -> {
            if (error == null && value != null && !value.isEmpty()) {
                for (QueryDocumentSnapshot documentSnapshot : value) {
                    MachineMasterModel machineMasterModel = documentSnapshot.toObject(MachineMasterModel.class);
                    String jala = machineMasterModel.getJala_name();
                    String warp = machineMasterModel.getWrap_color();


                    // kya design no ma jala use thay che
                    new DAODesignMaster(activity).getReference().addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot snapshot,
                                            @Nullable FirebaseFirestoreException e) {
                            if (e != null) {
                                Log.w("TAG", "Listen failed.", e);
                                return;
                            }

                            for (QueryDocumentSnapshot document : snapshot) {
                                DesignMasterModel designMasterModel = document.toObject(DesignMasterModel.class);
                                for (int i = 0; i < designMasterModel.getJalaWithFileModelList().size(); i++) {
                                    if (designMasterModel.getJalaWithFileModelList().get(i).getJala_name().equals(jala)) {
                                        designMasterModels.add(designMasterModel);
                                    }
                                }
                            }
                            new DAOShadeMaster(activity).getReference().whereEqualTo("wrap_color", warp).addSnapshotListener((snapshot1, error) -> {
                                if (error != null) {
                                    // Handle error
                                    return;
                                }
                                if (snapshot1 != null && !snapshot1.isEmpty()) {
                                    for (QueryDocumentSnapshot documentSnapshot : snapshot1) {
                                        ShadeMasterModel shadeMasterModel = documentSnapshot.toObject(ShadeMasterModel.class);
                                        if (shadeMasterModel != null) {
                                            shadeMasterModels.add(shadeMasterModel);
                                        }
                                    }
                                    // Handle shadeMasterList

                                    new DAOOrder(activity).getReference().addSnapshotListener(new EventListener<QuerySnapshot>() {
                                        @Override
                                        public void onEvent(@Nullable QuerySnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                                            if (e != null) {
                                                Log.w("TAG", "Listen failed.", e);
                                                return;
                                            }
                                            orderNoList.getValue().clear();
                                            orderMasterList.getValue().clear();

                                            for (QueryDocumentSnapshot doc : snapshot) {
                                                OrderMasterModel orderMasterModel = doc.toObject(OrderMasterModel.class);

                                                for (int i = 0; i < designMasterModels.size(); i++) {
                                                    if (orderMasterModel.getDesign_no().equals(designMasterModels.get(i).getDesign_no())) {

                                                        for (int j = 0; j < shadeMasterModels.size(); j++) {
                                                            if (orderMasterModel.getShade_no().equals(shadeMasterModels.get(j).getShade_no())) {
                                                                if (orderMasterModel.getOrderStatusModel().getPending() != 0) {
                                                                    String orderNo_withDetail = orderMasterModel.getSub_order_no() + " (" + orderMasterModel.getParty_name() + ", " + orderMasterModel.getDesign_no() + ", " + orderMasterModel.getQuantity() + ", " + orderMasterModel.getShade_no() + ")";
                                                                    orderNoList.getValue().add(orderNo_withDetail);
                                                                    orderMasterList.getValue().add(orderMasterModel);
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            Set<String> set = new HashSet<>(orderNoList.getValue());
                                            orderNoList.getValue().clear();
                                            orderNoList.getValue().addAll(set);


//                                                new Util().setAutoAdapter(getContext(), order_no_list, binding.autoOrderNo);
                                        }
                                    });
                                }
                            });

                        }
                    });
                }
            } else {
            }
        });
    }

    public void getMachineListWithOrderFilter(String order_no) {
        ArrayList<String> design_jala_list = new ArrayList<>();
        ArrayList<String> shade_list = new ArrayList<>();

        new DAOOrder(activity).getReference().whereEqualTo("sub_order_no", order_no).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot orderSnapShot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("TAG", "Listen failed.", e);
                    return;
                }

                for (QueryDocumentSnapshot doc : orderSnapShot) {
                    orderMasterModel.setValue(doc.toObject(OrderMasterModel.class));

                    String design_no = orderMasterModel.getValue().getDesign_no();
                    String shade_no = orderMasterModel.getValue().getShade_no();

                    new DAODesignMaster(activity).getReference().whereEqualTo("design_no", design_no).addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot designSnapShots, @Nullable FirebaseFirestoreException e) {
                            design_jala_list.clear();
                            if (e != null) {
                                return;
                            }

                            for (QueryDocumentSnapshot designSnapshot : designSnapShots) {
                                DesignMasterModel designMasterModel = designSnapshot.toObject(DesignMasterModel.class);
                                for (int i = 0; i < designMasterModel.getJalaWithFileModelList().size(); i++) {
                                    design_jala_list.add(designMasterModel.getJalaWithFileModelList().get(i).getJala_name());
                                }
                            }


                            new DAOShadeMaster(activity).getReference().whereEqualTo("shade_no", shade_no).addSnapshotListener((shadeSnapShots, error) -> {
                                if (error != null) {
                                    // Handle error
                                    return;
                                }
                                if (shadeSnapShots != null && !shadeSnapShots.isEmpty()) {
                                    for (QueryDocumentSnapshot shadeSnapshot : shadeSnapShots) {

                                        ShadeMasterModel shadeMasterModel = shadeSnapshot.toObject(ShadeMasterModel.class);
                                        if (shadeMasterModel != null) {
                                            shade_list.add(shadeMasterModel.getWrap_color());
                                        }
                                    }
                                    // Handle shadeMasterList

                                    new DAOMachineMaster(activity).getReference().addSnapshotListener((machineSnapshot, error1) -> {
                                        if (error != null) {
                                            DialogUtil.showErrorDialog(activity);
                                            return;
                                        }
                                        machine_name_list.getValue().clear();

                                        for (QueryDocumentSnapshot MachineItem : machineSnapshot) {
                                            MachineMasterModel machineMasterModel = MachineItem.toObject(MachineMasterModel.class);
                                            for (int i = 0; i < design_jala_list.size(); i++) {
                                                if (machineMasterModel.getJala_name().equals(design_jala_list.get(i))) {
                                                    for (int j = 0; j < shade_list.size(); j++) {
                                                        if (machineMasterModel.getWrap_color().equals(shade_list.get(j))) {
                                                            if (machineMasterModel.isStatus()) {
                                                                machine_name_list.getValue().add(machineMasterModel.getMachine_name());
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                        Set<String> set = new HashSet<>(machine_name_list.getValue());
                                        machine_name_list.getValue().clear();
                                        machine_name_list.getValue().addAll(set);
                                    });
                                }
                            });

                        }
                    });
                }
            }
        });
    }

    public void getMachineList() {
        new DAOMachineMaster(activity).getReference().addSnapshotListener((snapshot, error) -> {
            machine_name_list.getValue().clear();
            if (snapshot != null && !snapshot.isEmpty()) {
                for (QueryDocumentSnapshot documentSnapshot : snapshot) {
                    MachineMasterModel machineMasterModel = documentSnapshot.toObject(MachineMasterModel.class);
                    if (machineMasterModel != null && machineMasterModel.isStatus()) {
                        machine_name_list.getValue().add(machineMasterModel.getMachine_name());
                    }
                }
            }
        });
    }

    public void getOrderList() {
        new DAOOrder(activity).getReference().addSnapshotListener((snapshot, error) -> {
            orderNoList.getValue().clear();
            orderMasterList.getValue().clear();
            if (snapshot != null && !snapshot.isEmpty()) {
                for (QueryDocumentSnapshot documentSnapshot : snapshot) {
                    OrderMasterModel orderMasterModel1 = documentSnapshot.toObject(OrderMasterModel.class);
                    if (orderMasterModel1.getOrderStatusModel().getPending() > 0) {
                        orderNoList.getValue().add(orderMasterModel1.getSub_order_no() + "(" + orderMasterModel1.getOrderStatusModel().getPending() + ")");
                        orderMasterList.getValue().add(orderMasterModel1);
                    }
                }
            }
        });
    }

    public OrderMasterModel getOrderModelById(String orderId) {
        OrderMasterModel orderMasterModel = null;
        for (int i = 0; i < orderMasterList.getValue().size(); i++) {
            if (orderMasterList.getValue().get(i).getSub_order_no().equals(orderId)) {
                orderMasterModel = orderMasterList.getValue().get(i);
                break;
            }
        }
        return orderMasterModel;
    }
}
