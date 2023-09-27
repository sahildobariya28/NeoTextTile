package com.example.texttile.presentation.screen.features.reading_feature.daily_reading.reading;

import static com.example.texttile.core.MyApplication.getUSERMODEL;
import static com.example.texttile.presentation.ui.util.PermissionUtil.isInsertPermissionGranted;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.texttile.R;
import com.example.texttile.databinding.ActivityDailyReadingBinding;
import com.example.texttile.presentation.ui.dialog.MachineMasterDialog;
import com.example.texttile.data.dao.DaoAuthority;
import com.example.texttile.presentation.ui.interfaces.AddOnSearchedItemSelectListener;
import com.example.texttile.data.model.DailyReadingModel;
import com.example.texttile.data.model.UserDataModel;
import com.example.texttile.presentation.ui.lib.snackUtil.CustomSnackUtil;
import com.example.texttile.presentation.ui.util.DialogUtil;
import com.example.texttile.presentation.ui.util.ListShowDialog;
import com.example.texttile.presentation.ui.util.PermissionState;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import javax.annotation.Nullable;

public class DailyReadingActivity extends AppCompatActivity implements AddOnSearchedItemSelectListener {


    DailyReadingModel dailyReadingModel;

    ActivityDailyReadingBinding binding;

    ListShowDialog orderListDialog, machineListDialog;

    DailyReadingViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDailyReadingBinding.inflate(getLayoutInflater());
        viewModel = new ViewModelProvider(this, new DailyReadingViewModelFactory((getIntent().getStringExtra("tracker") != null) ? getIntent().getStringExtra("tracker") : "add", this)).get(DailyReadingViewModel.class);
        setContentView(binding.getRoot());


        viewModel.orderNo = (getIntent().getStringExtra("order_no") != null) ? getIntent().getStringExtra("order_no") : "";

        ArrayAdapter aa = new ArrayAdapter(this, R.layout.white_simple_spinner_item, viewModel.category);
        aa.setDropDownViewResource(R.layout.white_simple_spinner_dropdown_item);
        binding.optionType.setAdapter(aa);

        initToolbar();
        setAutoAdapter();

        viewModel.order_no_list.observe(this, orderNoList -> {
            if (orderListDialog != null) {
                orderListDialog.update_list(orderNoList);
            } else {
                orderListDialog = new ListShowDialog(DailyReadingActivity.this, orderNoList, DailyReadingActivity.this, viewModel.ORDER_REQUEST);
            }
        });


        dailyReadingModel = (viewModel.tracker.equals("edit") && getIntent().getSerializableExtra("reading_data") != null) ? (DailyReadingModel) getIntent().getSerializableExtra("reading_data") : new DailyReadingModel();
        if (viewModel.tracker != null && viewModel.tracker.equals("edit")) {
            if (dailyReadingModel != null) {
                binding.btnAdd.setText("Update");
                dailyReadingModel = (DailyReadingModel) getIntent().getSerializableExtra("reading_data");

                binding.editCurrentDate.setText(String.valueOf(dailyReadingModel.getDate()));
                binding.editManagerName.setText(String.valueOf(dailyReadingModel.getManager_name()));
                binding.textOrderNo.setText(String.valueOf(dailyReadingModel.getOrder_no()));
                binding.editDailyReadingQty.setText(String.valueOf(dailyReadingModel.getQty()));
                binding.textMachine.setText(String.valueOf(dailyReadingModel.getMachine_name()));
                binding.btnAdd.setVisibility(View.GONE);
            }
        } else {
            if (dailyReadingModel != null) {
                if (!viewModel.orderNo.isEmpty()) {
                    binding.textOrderNo.setText(viewModel.orderNo);
                }
                binding.editCurrentDate.setText(new SimpleDateFormat("dd/MM/yy").format(new Date().getTime()));
            }
        }

        binding.textOrderNo.setOnClickListener(view -> {
            if (orderListDialog != null) {
                orderListDialog.show_dialog();
            } else {
                orderListDialog = new ListShowDialog(this, viewModel.order_no_list.getValue(), DailyReadingActivity.this, viewModel.ORDER_REQUEST);
                orderListDialog.show_dialog();
            }
        });

        binding.editCurrentDate.setOnClickListener(view -> {

            DatePickerDialog.OnDateSetListener date1 = (view1, year, month, day) -> {
                viewModel.myCalendar.set(Calendar.YEAR, year);
                viewModel.myCalendar.set(Calendar.MONTH, month);
                viewModel.myCalendar.set(Calendar.DAY_OF_MONTH, day);
                String myFormat = "dd/MM/yy";
                SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.US);
                binding.editCurrentDate.setText(dateFormat.format(viewModel.myCalendar.getTime()));
            };
            DatePickerDialog mDatePicker = new DatePickerDialog(this, date1, viewModel.myCalendar.get(Calendar.YEAR), viewModel.myCalendar.get(Calendar.MONTH), viewModel.myCalendar.get(Calendar.DAY_OF_MONTH));

            mDatePicker.getDatePicker().setMaxDate(System.currentTimeMillis());
            mDatePicker.show();
        });

        binding.editDailyReadingQty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (viewModel.machineMasterList.getValue().size() != 0) {
                    for (int j = 0; j < viewModel.machineMasterList.getValue().size(); j++) {
                        if (viewModel.machineMasterList.getValue().get(j).getMachine_name().equals(binding.textMachine.getText().toString().replaceAll("\\(.*\\)", ""))) {
                            if (!charSequence.toString().isEmpty()) {
                                int reading_qty = Integer.parseInt(charSequence.toString());
                                float labour_charge = viewModel.machineMasterList.getValue().get(j).getLabour_charge();
                                binding.editLabourCharge.setText("" + (reading_qty * labour_charge));
                            }
                        }
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.textMachine.setOnClickListener(view -> {
            if (machineListDialog != null) {
                machineListDialog.show_dialog();
            } else {
                machineListDialog = new ListShowDialog(this, viewModel.machineList.getValue(), DailyReadingActivity.this, viewModel.MACHINE_REQUEST);
                machineListDialog.show_dialog();
            }
        });
        binding.btnAdd.setOnClickListener(view -> {
            addArtist();
        });
    }


    private void setAutoAdapter() {

        new DaoAuthority(this).getReference().whereEqualTo("u_name", getUSERMODEL().getU_name()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.w("Firestore", "Listen failed.", error);
                    return;
                }

                for (QueryDocumentSnapshot document : snapshot) {
                    UserDataModel userModel = document.toObject(UserDataModel.class);
                    binding.editManagerName.setText(userModel.getF_name());
                }
            }
        });


        viewModel.getOrderList();
        viewModel.getMachineList(viewModel.orderNo);
    }

    private void addArtist() {
        String machine_name = binding.textMachine.getText().toString().trim();
        String currentDate = binding.editCurrentDate.getText().toString().trim();
        String managerName = binding.editManagerName.getText().toString().trim();
        String orderNo = binding.textOrderNo.getText().toString().trim();
        String readingQty = binding.editDailyReadingQty.getText().toString().trim();

        if (TextUtils.isEmpty(currentDate)) {
            new CustomSnackUtil().showSnack(this, "Please enter a Current Date", R.drawable.error_msg_icon);
        } else if (TextUtils.isEmpty(managerName)) {
            new CustomSnackUtil().showSnack(this, "Please enter a Manager Name", R.drawable.error_msg_icon);
        } else if (TextUtils.isEmpty(orderNo)) {
            new CustomSnackUtil().showSnack(this, "Please enter a Order No", R.drawable.error_msg_icon);
        } else if (binding.optionType.getSelectedItemPosition() == 0) {
            new CustomSnackUtil().showSnack(this, "Please enter a Reading Type", R.drawable.error_msg_icon);
        } else if (TextUtils.isEmpty(readingQty)) {
            new CustomSnackUtil().showSnack(this, "Please enter a Reading Qty", R.drawable.error_msg_icon);
        } else if (TextUtils.isEmpty(machine_name)) {
            new CustomSnackUtil().showSnack(this, "Please enter a Machine_name", R.drawable.error_msg_icon);
        } else if (viewModel.getOrderModel(orderNo) != null && Integer.parseInt(readingQty) > viewModel.getOrderModel(orderNo).getOrderStatusModel().getOnMachinePending()) {
            new CustomSnackUtil().showSnack(this, "Select Maximum " + viewModel.getOrderModel(orderNo).getOrderStatusModel().getOnMachinePending() + " order", R.drawable.error_msg_icon);
        } else {

            String id = viewModel.dao.getId();
            DialogUtil.showProgressDialog(this);
            if (viewModel.tracker.equals("edit")) {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("date", currentDate);
                hashMap.put("manager_name", managerName);
                hashMap.put("order_no", orderNo);
                hashMap.put("qty", Integer.valueOf(readingQty));

                viewModel.updateOrderStatus(orderNo, Integer.parseInt(readingQty), machine_name, binding.optionType.getSelectedItemPosition());
                viewModel.dao.update(dailyReadingModel.getId(), hashMap).addOnSuccessListener(runnable -> {
                    DialogUtil.showUpdateSuccessDialog(this, true);
                });
            } else {
                DailyReadingModel dailyReadingModel = new DailyReadingModel(id, currentDate, managerName, machine_name, orderNo, Integer.parseInt(readingQty));
                viewModel.updateOrderStatus(orderNo, Integer.parseInt(readingQty), machine_name, binding.optionType.getSelectedItemPosition());
                viewModel.dao.insert(id, dailyReadingModel).addOnSuccessListener(runnable -> {
                    DialogUtil.showSuccessDialog(this, true);
                });
            }
        }
    }

    public void initToolbar() {
        binding.include.textTitle.setText("Daily Reading");
        binding.include.btnBack.setOnClickListener(view -> {
            this.onBackPressed();
        });
    }

    @Override
    public void onSelected(int position, String name, int request_code) {
        if (request_code == viewModel.MACHINE_REQUEST) {
            binding.textMachine.setText(name);
        } else if (request_code == viewModel.ORDER_REQUEST) {
            viewModel.getMachineList(name);
            binding.textOrderNo.setText(name);
        }
    }

    @Override
    public void onAddButtonClicked(int position, String name, int request_code) {
        if (request_code == viewModel.MACHINE_REQUEST) {
            if (isInsertPermissionGranted(PermissionState.MACHINE_MASTER.getValue())) {
                MachineMasterDialog machineMasterDialog = new MachineMasterDialog();
                Bundle bundle = new Bundle();
                bundle.putString("tracker", "add");
                bundle.putString("machine_name", name);
                machineMasterDialog.setArguments(bundle);
                machineMasterDialog.show(getSupportFragmentManager(), "My Fragment");
            } else {
                DialogUtil.showAccessDeniedDialog(this);
            }

        } else if (request_code == viewModel.ORDER_REQUEST) {
            new CustomSnackUtil().showSnack(this, "Please Create Order Manually", R.drawable.icon_warning);
        }
    }

    @Override
    public void onMultiSelected(ArrayList<String> shadeList, int request_code) {

    }
}