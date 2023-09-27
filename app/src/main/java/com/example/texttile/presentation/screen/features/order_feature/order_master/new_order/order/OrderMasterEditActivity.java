package com.example.texttile.presentation.screen.features.order_feature.order_master.new_order.order;

import static com.example.texttile.presentation.ui.util.PermissionUtil.isInsertPermissionGranted;
import static com.example.texttile.presentation.ui.util.PermissionUtil.isPricePermissionGranted;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;

import com.example.texttile.R;
import com.example.texttile.databinding.ActivityOrderMasterEditBinding;
import com.example.texttile.presentation.screen.master.design_master.dialog.DesignMasterDialogFragment;
import com.example.texttile.presentation.ui.dialog.PartyDialog;
import com.example.texttile.presentation.ui.dialog.ShadeMasterDialog;
import com.example.texttile.data.dao.DAODesignMaster;
import com.example.texttile.data.dao.DAOOrder;
import com.example.texttile.data.dao.DAOPartyMaster;
import com.example.texttile.data.dao.DAOShadeMaster;
import com.example.texttile.presentation.ui.interfaces.AddOnSearchedItemSelectListener;
import com.example.texttile.data.model.DesignMasterModel;
import com.example.texttile.data.model.OrderMasterModel;
import com.example.texttile.data.model.PartyMasterModel;
import com.example.texttile.data.model.ShadeMasterModel;
import com.example.texttile.presentation.ui.lib.snackUtil.CustomSnackUtil;
import com.example.texttile.presentation.ui.util.Const;
import com.example.texttile.presentation.ui.util.DialogUtil;
import com.example.texttile.presentation.ui.util.ListShowDialog;
import com.example.texttile.presentation.ui.util.PermissionState;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class OrderMasterEditActivity extends AppCompatActivity implements AddOnSearchedItemSelectListener {

    String tracker;
    OrderMasterModel orderMasterModel;
    ActivityOrderMasterEditBinding binding;

    String[] category = {"Main Order", "Sample Order", "Self Order"};
    ArrayList<String> design_no_list = new ArrayList<>();
    ArrayList<String> party_name_list = new ArrayList<>();
    ArrayList<String> shade_no_list = new ArrayList<>();
    final Calendar myCalendar = Calendar.getInstance();
    DAOOrder dao;

    ListShowDialog partyListDialog, designListDialog, shadeListDialog;
    final int PARTY_REQUEST = 1, DESIGN_REQUEST = 2, SHADE_REQUEST = 3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderMasterEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dao = new DAOOrder(this);

        initToolbar();

        tracker = (getIntent().getStringExtra("tracker") != null) ? getIntent().getStringExtra("tracker") : "add";
        orderMasterModel = (tracker.equals("edit") && getIntent().getSerializableExtra(Const.ORDER_MASTER) != null) ? (OrderMasterModel) getIntent().getSerializableExtra(Const.ORDER_MASTER) : new OrderMasterModel();

        ArrayAdapter aa = new ArrayAdapter(this, R.layout.white_simple_spinner_item, category);
        aa.setDropDownViewResource(R.layout.white_simple_spinner_dropdown_item);
        binding.orderType.setAdapter(aa);
        binding.orderType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 2) {
                    binding.partyContainer.setVisibility(View.GONE);
                    binding.approxDateContainer.setVisibility(View.GONE);
                    binding.priceContainer.setVisibility(View.GONE);
                } else {
                    binding.partyContainer.setVisibility(View.VISIBLE);
                    binding.designNoContainer.setVisibility(View.VISIBLE);
                    binding.approxDateContainer.setVisibility(View.VISIBLE);
                    binding.priceContainer.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        if (isPricePermissionGranted(PermissionState.NEW_ORDER.getValue())) {
            binding.priceContainer.setVisibility(View.VISIBLE);
        } else {
            binding.priceContainer.setVisibility(View.GONE);
        }
        setAutoAdapter();

        if (tracker != null && tracker.equals("edit")) {
            if (orderMasterModel != null) {
                if (orderMasterModel.getOrder_type().equals("Main Order")) {
                    binding.orderType.setSelection(0);
                    binding.partyContainer.setVisibility(View.VISIBLE);
                    binding.designNoContainer.setVisibility(View.VISIBLE);
                    binding.approxDateContainer.setVisibility(View.VISIBLE);
                    binding.priceContainer.setVisibility(View.VISIBLE);
                } else if (orderMasterModel.getOrder_type().equals("Sample Order")){
                    binding.orderType.setSelection(1);
                    binding.partyContainer.setVisibility(View.VISIBLE);
                    binding.designNoContainer.setVisibility(View.VISIBLE);
                    binding.approxDateContainer.setVisibility(View.VISIBLE);
                    binding.priceContainer.setVisibility(View.VISIBLE);
                }else {
                    binding.orderType.setSelection(2);
                    binding.partyContainer.setVisibility(View.GONE);
                    binding.designNoContainer.setVisibility(View.VISIBLE);
                    binding.approxDateContainer.setVisibility(View.GONE);
                    binding.priceContainer.setVisibility(View.GONE);
                }
                binding.editOrderNo.setText(String.valueOf(orderMasterModel.getOrder_no()));
                binding.editDate.setText(String.valueOf(orderMasterModel.getDate()));
                binding.textPartyName.setText(String.valueOf(orderMasterModel.getParty_name()));
                binding.textDesignNo.setText(String.valueOf(orderMasterModel.getDesign_no()));
                binding.editApproxDays.setText(String.valueOf(orderMasterModel.getApprox_Delivery_date()));
                binding.textShadeNo.setText(String.valueOf(orderMasterModel.getShade_no()));
                binding.editQty.setText(String.valueOf(orderMasterModel.getQuantity()));

                if (!TextUtils.isEmpty(orderMasterModel.getPrice())) {
                    binding.editPrice.setText(orderMasterModel.getPrice());
                }
            }
        } else {
            if (orderMasterModel != null) {
                String date = String.format(new SimpleDateFormat("dd/MM/yy", Locale.US).format(myCalendar.getTime()));
                binding.editDate.setText(date);
                autoSetData();
            }
        }

        binding.editDate.setOnClickListener(view -> {
            DatePickerDialog.OnDateSetListener date = (view1, year, month, day) -> {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, day);
                String myFormat = "dd/MM/yy";
                SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.US);
                binding.editDate.setText(dateFormat.format(myCalendar.getTime()));
            };
            DatePickerDialog mDatePicker = new DatePickerDialog(this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
            mDatePicker.getDatePicker().setMaxDate(System.currentTimeMillis());
            mDatePicker.show();
        });
        binding.btnNext.setOnClickListener(view -> {
            DialogUtil.showProgressDialog(this);
            String order_no = binding.editOrderNo.getText().toString().trim();
            String party_name = binding.textPartyName.getText().toString().trim();
            String date = binding.editDate.getText().toString().trim();
            String design_name = binding.textDesignNo.getText().toString().trim();
            String shade_no = binding.textShadeNo.getText().toString().trim();
            String approx_date = binding.editApproxDays.getText().toString().trim();
            String price = binding.editPrice.getText().toString().trim();

            if (price.isEmpty()){
                price = "0";
            }

            if (binding.orderType.getSelectedItemPosition() == 2) {
                if (TextUtils.isEmpty(order_no)) {
                    new CustomSnackUtil().showSnack(this, "Please enter Order No", R.drawable.error_msg_icon);
                } else if (TextUtils.isEmpty(date)) {
                    new CustomSnackUtil().showSnack(this, "Please enter a Date", R.drawable.error_msg_icon);
                } else if (TextUtils.isEmpty(shade_no)) {
                    new CustomSnackUtil().showSnack(this, "Enter valid Shade no", R.drawable.error_msg_icon);
                }else if (binding.priceContainer.getVisibility() == View.VISIBLE && TextUtils.isEmpty(price)) {
                    new CustomSnackUtil().showSnack(this, "Please enter Price", R.drawable.error_msg_icon);
                } else {
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("order_type", category[binding.orderType.getSelectedItemPosition()]);
                    hashMap.put("order_no", order_no);
                    hashMap.put("sub_order_no", orderMasterModel.getSub_order_no());
                    hashMap.put("date", date);
                    hashMap.put("party_name", "self");
                    hashMap.put("design_no", design_name);
                    hashMap.put("shade_no", shade_no);
                    hashMap.put("price", price);
                    hashMap.put("approx_Delivery_date", date);

                    dao.update(orderMasterModel.getId(), hashMap).addOnSuccessListener(runnable -> {
                        DialogUtil.showUpdateSuccessDialog(this, true);
                    });
                }
            }else {
                if (TextUtils.isEmpty(party_name)) {
                    new CustomSnackUtil().showSnack(this, "Enter valid Party Name", R.drawable.error_msg_icon);
                } else if (TextUtils.isEmpty(order_no)) {
                    new CustomSnackUtil().showSnack(this, "Please enter Order No", R.drawable.error_msg_icon);
                } else if (TextUtils.isEmpty(date)) {
                    new CustomSnackUtil().showSnack(this, "Please enter a Date", R.drawable.error_msg_icon);
                } else if (TextUtils.isEmpty(design_name)) {
                    new CustomSnackUtil().showSnack(this, "Enter valid Design no", R.drawable.error_msg_icon);
                } else if (TextUtils.isEmpty(shade_no)) {
                    new CustomSnackUtil().showSnack(this, "Enter valid Shade no", R.drawable.error_msg_icon);
                } else if (TextUtils.isEmpty(approx_date)) {
                    new CustomSnackUtil().showSnack(this, "Please enter a Approx Date", R.drawable.error_msg_icon);
                } else if (binding.priceContainer.getVisibility() == View.VISIBLE && TextUtils.isEmpty(price)) {
                    new CustomSnackUtil().showSnack(this, "Please enter Price", R.drawable.error_msg_icon);
                } else {
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("order_type", category[binding.orderType.getSelectedItemPosition()]);
                    hashMap.put("order_no", order_no);
                    hashMap.put("sub_order_no", orderMasterModel.getSub_order_no());
                    hashMap.put("date", date);
                    hashMap.put("party_name", party_name);
                    hashMap.put("design_no", design_name);
                    hashMap.put("shade_no", shade_no);
                    hashMap.put("price", price);
                    hashMap.put("approx_Delivery_date", approx_date);

                    dao.update(orderMasterModel.getId(), hashMap).addOnSuccessListener(runnable -> {
                        DialogUtil.showUpdateSuccessDialog(this, true);
                    });
                }
            }

//
//            DialogUtil.showProgressDialog(this);
//            if (tracker.equals("edit")) {
//
//                HashMap<String, Object> hashMap = new HashMap<>();
//                hashMap.put("order_type", category[binding.orderType.getSelectedItemPosition()]);
//                hashMap.put("order_no", binding.editOrderNo.getText().toString());
//                hashMap.put("sub_order_no", orderMasterModel.getSub_order_no());
//                hashMap.put("date", binding.editDate.getText().toString());
//                hashMap.put("party_name", binding.textPartyName.getText().toString());
//                hashMap.put("design_no", binding.textDesignNo.getText().toString());
//                hashMap.put("shade_no", binding.textShadeNo.getText().toString());
//                hashMap.put("price", binding.editPrice.getText().toString());
//                hashMap.put("approx_Delivery_date", binding.editApproxDays.getText().toString());
//
//                dao.update(orderMasterModel.getId(), hashMap).addOnSuccessListener(runnable -> {
//                    DialogUtil.showUpdateSuccessDialog(this, true);
//                });
//            }
        });
        binding.textPartyName.setOnClickListener(view -> {
            if (partyListDialog != null) {
                partyListDialog.show_dialog();
            } else {
                partyListDialog = new ListShowDialog(this, party_name_list, OrderMasterEditActivity.this, PARTY_REQUEST);
                partyListDialog.show_dialog();
            }
        });
        binding.textDesignNo.setOnClickListener(view -> {
            if (designListDialog != null) {
                designListDialog.show_dialog();
            } else {
                designListDialog = new ListShowDialog(this, design_no_list, OrderMasterEditActivity.this, DESIGN_REQUEST);
                designListDialog.show_dialog();
            }
        });
        binding.textShadeNo.setOnClickListener(view -> {
            if (shadeListDialog != null) {
                shadeListDialog.show_dialog();
            } else {
                shadeListDialog = new ListShowDialog(this, shade_no_list, OrderMasterEditActivity.this, SHADE_REQUEST);
                shadeListDialog.show_dialog();
            }
        });
    }


    private void autoSetData() {

        new DAOOrder(this).getReference().addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                Log.w("TAG", "Listen failed.", e);
                return;
            }
            int max_no = 0;
            for (QueryDocumentSnapshot doc : snapshot) {
                OrderMasterModel orderMasterModel = doc.toObject(OrderMasterModel.class);
                int orderno = Integer.valueOf(orderMasterModel.getOrder_no());
                if (orderno > max_no) {
                    max_no = orderno;
                }
            }
            binding.editOrderNo.setText("" + (max_no + 1));
        });
    }

    private void setAutoAdapter() {

        getDesignList();
        getPartyList();
        getShadeList();

    }

    private void getShadeList() {
        new DAOShadeMaster(this).getReference().addSnapshotListener((snapshot, error) -> {
            if (error != null) {
                // Handle error
                return;
            }
            if (snapshot != null && !snapshot.isEmpty()) {
                shade_no_list.clear();
                for (QueryDocumentSnapshot documentSnapshot : snapshot) {
                    ShadeMasterModel shadeMasterModel = documentSnapshot.toObject(ShadeMasterModel.class);
//                    shade_no_list.add(shadeMasterModel.getShade_no());

                    shade_no_list.add(shadeMasterModel.getShade_no() + " (" + shadeMasterModel.getDesign_no() + ")");
                }
                if (shadeListDialog != null) {
                    shadeListDialog.update_list(shade_no_list);
                } else {
                    shadeListDialog = new ListShowDialog(OrderMasterEditActivity.this, shade_no_list, OrderMasterEditActivity.this, SHADE_REQUEST);
                }
                // Handle shadeMasterList
            }
        });
    }


    private void getPartyList() {
        new DAOPartyMaster(this).getReference().addSnapshotListener((querySnapshot, error) -> {

            if (error != null) {
                Log.w("djfksdsd", "Listen failed.", error);
                return;
            }

            party_name_list.clear();
            for (DocumentSnapshot doc : querySnapshot) {
              PartyMasterModel partyMasterModel = doc.toObject(PartyMasterModel.class);
                party_name_list.add(partyMasterModel.getParty_Name());
            }
            if (designListDialog != null) {
                designListDialog.update_list(design_no_list);
            } else {
                designListDialog = new ListShowDialog(OrderMasterEditActivity.this, design_no_list, OrderMasterEditActivity.this, DESIGN_REQUEST);
            }
        });

    }


    private void getDesignList() {
        new DAODesignMaster(this).getReference().get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                design_no_list.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    DesignMasterModel designMasterModel = document.toObject(DesignMasterModel.class);
                    design_no_list.add(designMasterModel.getDesign_no());
                }
                if (partyListDialog != null) {
                    partyListDialog.update_list(design_no_list);
                } else {
                    partyListDialog = new ListShowDialog(OrderMasterEditActivity.this, design_no_list, OrderMasterEditActivity.this, PARTY_REQUEST);
                }
            } else {
                Log.d("TAG", "Error getting documents: ", task.getException());
            }
        });

    }

    public void initToolbar() {
        binding.include.textTitle.setText("New Order");
        binding.include.btnBack.setOnClickListener(view -> {
            this.onBackPressed();
        });
    }

    @Override
    public void onSelected(int position, String name, int request_code) {
        if (request_code == PARTY_REQUEST) {
            binding.textPartyName.setText(name);
        } else if (request_code == DESIGN_REQUEST) {
            binding.textDesignNo.setText(name);
        } else if (request_code == SHADE_REQUEST) {
            binding.textShadeNo.setText(name.substring(0, 5));
        }
    }

    @Override
    public void onAddButtonClicked(int position, String name, int request_code) {
        if (request_code == PARTY_REQUEST) {

            if (isInsertPermissionGranted(PermissionState.PARTY_MASTER.getValue())) {
                PartyDialog partyDialog = new PartyDialog();
                Bundle bundle = new Bundle();
                bundle.putString("party_name", name);
                partyDialog.setArguments(bundle);
                partyDialog.show(getSupportFragmentManager(), "Party Dialog");
            } else {
                DialogUtil.showAccessDeniedDialog(this);
            }

        } else if (request_code == DESIGN_REQUEST) {

            if (isInsertPermissionGranted(PermissionState.DESIGN_MASTER.getValue())) {
                DesignMasterDialogFragment dialogFragment = new DesignMasterDialogFragment();
                Bundle bundle = new Bundle();
                bundle.putString("design_no", name);
                dialogFragment.setArguments(bundle);
                dialogFragment.show(getSupportFragmentManager(), "My Fragment");
            } else {
                DialogUtil.showAccessDeniedDialog(this);
            }

        } else if (request_code == SHADE_REQUEST) {

            if (isInsertPermissionGranted(PermissionState.SHADE_MASTER.getValue())) {
                ShadeMasterDialog shadeMasterDialog = new ShadeMasterDialog();
                Bundle bundle = new Bundle();
                bundle.putString("shade_no", name);
                shadeMasterDialog.setArguments(bundle);
                shadeMasterDialog.show(getSupportFragmentManager(), "My Fragment");
            } else {
                DialogUtil.showAccessDeniedDialog(this);
            }
        }
    }

    @Override
    public void onMultiSelected(ArrayList<String> shadeList, int request_code) {

    }
}