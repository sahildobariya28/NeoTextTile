package com.example.texttile.presentation.screen.features.order_feature.order_master.new_order.order;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.util.Log;
import android.widget.ArrayAdapter;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.texttile.R;
import com.example.texttile.data.dao.DAODesignMaster;
import com.example.texttile.data.dao.DAOOrder;
import com.example.texttile.data.dao.DAOPartyMaster;
import com.example.texttile.data.dao.DAOShadeMaster;
import com.example.texttile.presentation.ui.interfaces.AddOnSearchedItemSelectListener;
import com.example.texttile.data.model.DesignMasterModel;
import com.example.texttile.data.model.OrderMasterModel;
import com.example.texttile.data.model.PartyMasterModel;
import com.example.texttile.data.model.ShadeMasterModel;
import com.example.texttile.data.model.SubOrderModel;
import com.example.texttile.presentation.ui.util.ListShowDialog;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class OrderViewModel extends ViewModel {

    final int PARTY_REQUEST = 1, DESIGN_REQUEST = 2, SHADE_REQUEST = 3;

    Activity activity;
    final Calendar myCalendar = Calendar.getInstance();
    String[] category = {"Main Order", "Sample Order", "Self Order"};
    public MutableLiveData<OrderStepState> currentState = new MutableLiveData<>(OrderStepState.STEP1);
    public MutableLiveData<Boolean> isProgressVisible = new MutableLiveData<>(false);
    public MutableLiveData<String> currentDate = new MutableLiveData<>(String.format(new SimpleDateFormat("dd/MM/yy", Locale.US).format(myCalendar.getTime())));
    OrderMasterModel orderMasterModel = new OrderMasterModel();


    //dao
    DAOPartyMaster daoPartyMaster;
    DAODesignMaster daoDesignMaster;
    DAOOrder dao;

    ArrayList<SubOrderModel> subOrderList = new ArrayList<>();

    //list Dialog
    ArrayList<String> design_no_list = new ArrayList<>();
    ArrayList<String> party_name_list = new ArrayList<>();
    ArrayList<String> shade_no_list = new ArrayList<>();
    ListShowDialog partyListDialog, designListDialog, shadeListDialog;

    public OrderViewModel(Activity activity){
        this.activity = activity;
        daoPartyMaster = new DAOPartyMaster(this.activity);
        daoDesignMaster = new DAODesignMaster(this.activity);
        dao = new DAOOrder(activity);

    }

    public void onBackPress(){
        if (currentState.getValue() == OrderStepState.STEP3){
            currentState.setValue(OrderStepState.STEP2);
        }else if (currentState.getValue() == OrderStepState.STEP2){
            currentState.setValue(OrderStepState.STEP1);
        }else {
            currentState.setValue(OrderStepState.STEP1);
        }
    }

    public void onPartyNameClicked(AddOnSearchedItemSelectListener addOnSearchedItemSelectListener){
        if (partyListDialog != null) {
            partyListDialog.show_dialog();
        } else {
            partyListDialog = new ListShowDialog(activity, party_name_list, addOnSearchedItemSelectListener, PARTY_REQUEST);
            partyListDialog.show_dialog();
        }
    }
    public void onDesignNoClicked(AddOnSearchedItemSelectListener addOnSearchedItemSelectListener){
        if (designListDialog != null) {
            designListDialog.show_dialog();
        } else {
            designListDialog = new ListShowDialog(activity, design_no_list, addOnSearchedItemSelectListener, DESIGN_REQUEST);
            designListDialog.show_dialog();
        }
    }

    public void getDesignMasterList(androidx.lifecycle.LifecycleOwner owner, AddOnSearchedItemSelectListener addOnSearchedItemSelectListener){
        daoDesignMaster.getDesignMasterListLiveData().observe(owner, designMasterModels -> {
            design_no_list.clear();

            for (DesignMasterModel designMasterModel : designMasterModels) {
                design_no_list.add(designMasterModel.getDesign_no());
            }

            isProgressVisible.setValue(false);

            if (designListDialog != null) {
                designListDialog.update_list(design_no_list);
            } else {
                designListDialog = new ListShowDialog(activity, design_no_list, addOnSearchedItemSelectListener, DESIGN_REQUEST);
            }
        });
    }
    public void getPartyMasterList(androidx.lifecycle.LifecycleOwner owner, AddOnSearchedItemSelectListener addOnSearchedItemSelectListener){
        daoPartyMaster.getPartyMasterListLiveData().observe(owner, partyMasterModels -> {
            party_name_list.clear();

            for (PartyMasterModel partyMasterModel : partyMasterModels) {
                party_name_list.add(partyMasterModel.getParty_Name());
            }

            isProgressVisible.setValue(false);
            if (partyListDialog != null) {
                partyListDialog.update_list(party_name_list);
            } else {
                partyListDialog = new ListShowDialog(activity, party_name_list, addOnSearchedItemSelectListener, PARTY_REQUEST);
            }
        });
    }

    public void getShadeMasterList(String design_no, AddOnSearchedItemSelectListener addOnSearchedItemSelectListener){
        shade_no_list.clear();
        new DAOShadeMaster(activity).getReference().addSnapshotListener((snapshot, error) -> {
            if (snapshot != null && !snapshot.isEmpty()) {
                List<ShadeMasterModel> shadeMasterList = new ArrayList<>();
                for (QueryDocumentSnapshot documentSnapshot : snapshot) {
                    ShadeMasterModel shadeMasterModel = documentSnapshot.toObject(ShadeMasterModel.class);
                    if (shadeMasterModel != null) {

                        if (!design_no.isEmpty()) {
                            if (design_no.equals(shadeMasterModel.getDesign_no())) {
//                                        Log.d("jfkdslf", "getDesign: " + shadeMasterModel.getDesign_no() +"   getShade: " + shadeMasterModel.getShade_no() +"   getDesignText : " + binding.textDesignNo.getText().toString());
                                new DAOOrder(activity).getReference().whereEqualTo("shade_no", shadeMasterModel.getShade_no()).addSnapshotListener((task, taskError) -> {

                                    if (task != null && !task.isEmpty()) {
                                        int orderQty = 0, orderPendingQty = 0;
                                        for (QueryDocumentSnapshot documentOrderSnapshot : task) {
                                            OrderMasterModel orderMasterModel1 = documentOrderSnapshot.toObject(OrderMasterModel.class);
                                            if (orderMasterModel1.getShade_no().equals(shadeMasterModel.getShade_no())){
                                                orderQty += orderMasterModel1.getQuantity();
                                                orderPendingQty += orderMasterModel1.getOrderStatusModel().getPending();
                                            }
                                        }
                                        Log.d("jfkdslf", "getListSize: " + shade_no_list.size());
                                        shade_no_list.add(shadeMasterModel.getShade_no() + " (" + "Qty = " + orderQty + ", " + "Pending Qty = "+ orderPendingQty + ")");
                                        Log.d("jfkdslf", "getDesign: " + shadeMasterModel.getDesign_no() +"   getShade: " + shadeMasterModel.getShade_no() +"   getDesignText : " + shade_no_list.size());
                                    }else{
                                        shade_no_list.add(shadeMasterModel.getShade_no() + " (" + "Qty = " + 0 + ", " + "Pending Qty = " + 0 + ")");
                                    }
                                });
//                                        orderViewModel.shade_no_list.add(shadeMasterModel.getShade_no());
//                                        orderViewModel.shade_no_list.add(shadeMasterModel.getShade_no() + " (" + shadeMasterModel.getDesign_no() + ")");
                            }
                        }

                    }
                }


                if (shadeListDialog != null) {
                    shadeListDialog.update_list(shade_no_list);
                } else {
                    shadeListDialog = new ListShowDialog(activity, shade_no_list, addOnSearchedItemSelectListener, SHADE_REQUEST);
                }
                // Handle shadeMasterList
            }
        });
    }
    public void openDatePicker(){
        DatePickerDialog.OnDateSetListener date = (view1, year, month, day) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, month);
            myCalendar.set(Calendar.DAY_OF_MONTH, day);
            String myFormat = "dd/MM/yy";
            SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.US);
            currentDate.setValue(dateFormat.format(myCalendar.getTime()));
        };
        DatePickerDialog mDatePicker = new DatePickerDialog(activity, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
        mDatePicker.getDatePicker().setMaxDate(System.currentTimeMillis());
        mDatePicker.show();
    }

    public ArrayAdapter getCategoryAdapter(){
        ArrayAdapter aa = new ArrayAdapter(activity, R.layout.white_simple_spinner_item, category);
        aa.setDropDownViewResource(R.layout.white_simple_spinner_dropdown_item);
        return aa;
    }

}
