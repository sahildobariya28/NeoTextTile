package com.example.texttile.presentation.screen.features.storage_feature.ready_stock.readystock;

import static com.example.texttile.presentation.ui.util.PermissionUtil.isInsertPermissionGranted;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;

import androidx.appcompat.app.AppCompatActivity;

import com.example.texttile.R;
import com.example.texttile.databinding.ActivityReadyStockBinding;
import com.example.texttile.presentation.screen.master.design_master.dialog.DesignMasterDialogFragment;
import com.example.texttile.presentation.ui.dialog.ShadeMasterDialog;
import com.example.texttile.data.dao.DAODesignMaster;
import com.example.texttile.data.dao.DAOExtraOrder;
import com.example.texttile.data.dao.DAOShadeMaster;
import com.example.texttile.presentation.ui.interfaces.AddOnSearchedItemSelectListener;
import com.example.texttile.data.model.DesignMasterModel;
import com.example.texttile.data.model.ReadyStockModel;
import com.example.texttile.data.model.ShadeMasterModel;
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
import java.util.HashMap;
import java.util.Locale;

import javax.annotation.Nullable;

public class ReadyStockActivity extends AppCompatActivity implements AddOnSearchedItemSelectListener {

    ArrayList<String> design_no_list = new ArrayList<>();
    ArrayList<String> shade_no_list = new ArrayList<>();

    final Calendar myCalendar = Calendar.getInstance();
    String tracker;
    ReadyStockModel readyStockModel;
    String[] category = {"Factory", "WareHouse"};
    DAOExtraOrder dao;

    ActivityReadyStockBinding binding;

    ListShowDialog designListDialog, shadeListDialog;
    final int DESIGN_REQUEST = 1, SHADE_REQUEST = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReadyStockBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dao = new DAOExtraOrder(this);

        setAutoAdapter();
        initToolbar();

        tracker = (getIntent().getStringExtra("tracker") != null) ? getIntent().getStringExtra("tracker") : "add";
        readyStockModel = (tracker.equals("edit") && getIntent().getSerializableExtra("extra_order_data") != null) ? (ReadyStockModel) getIntent().getSerializableExtra("extra_order_data") : new ReadyStockModel();

        if (tracker != null && tracker.equals("edit")) {
            if (readyStockModel != null) {
                binding.btnAdd.setText("Update");
                binding.textDesignNo.setText(String.valueOf(readyStockModel.getDesign_no()));
                binding.textShadeNo.setText(String.valueOf(readyStockModel.getShade_no()));
                binding.editCurrentDate.setText(String.valueOf(readyStockModel.getCurrent_date()));
                binding.editExtraQuantity.setText(String.valueOf(readyStockModel.getQuantity()));

            }
        } else {
            if (readyStockModel != null) {
                binding.editDesignName.requestFocus();
            }
        }
        binding.textDesignNo.setOnClickListener(view -> {
            if (designListDialog != null) {
                designListDialog.show_dialog();
            } else {
                designListDialog = new ListShowDialog(this, design_no_list, ReadyStockActivity.this, DESIGN_REQUEST);
                designListDialog.show_dialog();
            }
        });
        binding.textShadeNo.setOnClickListener(view -> {
            if (shadeListDialog != null) {
                shadeListDialog.show_dialog();
            } else {
                shadeListDialog = new ListShowDialog(this, shade_no_list, ReadyStockActivity.this, DESIGN_REQUEST);
                shadeListDialog.show_dialog();
            }
        });

        binding.editCurrentDate.setOnClickListener(view -> {
            if (tracker.equals("edit")) {
                DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, month);
                        myCalendar.set(Calendar.DAY_OF_MONTH, day);

                        String myFormat = "dd/MM/yy";
                        SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.US);
                        binding.editCurrentDate.setText(dateFormat.format(myCalendar.getTime()));
                    }
                };
                DatePickerDialog mDatePicker = new DatePickerDialog(this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
                mDatePicker.show();
            } else {
                DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, month);
                        myCalendar.set(Calendar.DAY_OF_MONTH, day);
                        String myFormat = "dd/MM/yy";
                        SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.US);
                        binding.editCurrentDate.setText(dateFormat.format(myCalendar.getTime()));
                    }
                };
                DatePickerDialog mDatePicker = new DatePickerDialog(this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
                mDatePicker.getDatePicker().setMinDate(System.currentTimeMillis());
                mDatePicker.getDatePicker().setMaxDate(System.currentTimeMillis());
                mDatePicker.show();
            }
        });

        binding.btnAdd.setOnClickListener(view -> {
            addArtist();
        });
    }


    private void setAutoAdapter() {
        ArrayAdapter aa = new ArrayAdapter(this, R.layout.white_simple_spinner_item, category);
        aa.setDropDownViewResource(R.layout.white_simple_spinner_dropdown_item);

        getDesignList();
        getShadeList();
    }

    private void getDesignList() {

        new DAODesignMaster(this).getReference().addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("TAG", "Listen failed.", e);
                    return;
                }

                if (querySnapshot != null && !querySnapshot.isEmpty()) {
                    design_no_list.clear();
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        DesignMasterModel artist = document.toObject(DesignMasterModel.class);

                        if (artist != null && artist.getDesign_no() != null) {
                            design_no_list.add(artist.getDesign_no());
                        }
                    }
                    if (designListDialog != null) {
                        designListDialog.update_list(design_no_list);
                    } else {
                        designListDialog = new ListShowDialog(ReadyStockActivity.this, design_no_list, ReadyStockActivity.this, DESIGN_REQUEST);
                    }
                } else {
                    new CustomSnackUtil().showSnack(ReadyStockActivity.this, "Design List is Empty", R.drawable.error_msg_icon);
                }
            }
        });

//        new DAODesignMaster(this).getReference().addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@Nonnull DataSnapshot dataSnapshot) {
//
//                if (dataSnapshot.exists()) {
//                    design_no_list.clear();
//                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
//                        DesignMasterModel artist = postSnapshot.getValue(DesignMasterModel.class);
//
//                        if (artist != null && artist.getDesign_no() != null) {
//                            design_no_list.add(artist.getDesign_no());
//                        }
//                    }
//                    if (designListDialog != null) {
//                        designListDialog.update_list(design_no_list);
//                    } else {
//                        designListDialog = new ListShowDialog(ReadyStockActivity.this, design_no_list, ReadyStockActivity.this, DESIGN_REQUEST);
//                    }
//
////                    new Util().setAutoAdapter(this, design_no_list, binding.autoDesignNo);
//                } else {
//                    new CustomSnackUtil().showSnack(ReadyStockActivity.this, "Design List is Empty", R.drawable.error_msg_icon);
//                }
//            }
//
//            @Override
//            public void onCancelled(@Nonnull DatabaseError databaseError) {
//
//            }
//        });
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
                    shade_no_list.add(shadeMasterModel.getShade_no());
                }
                if (shadeListDialog != null) {
                    shadeListDialog.update_list(shade_no_list);
                } else {
                    shadeListDialog = new ListShowDialog(ReadyStockActivity.this, shade_no_list, ReadyStockActivity.this, SHADE_REQUEST);
                }
                // Handle shadeMasterList
            }
        });

//        new DAOShadeMaster(this).getReference().addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@Nonnull DataSnapshot dataSnapshot) {
//
//                shade_no_list.clear();
//
//                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
//                    ShadeMasterModel shadeMasterModel = postSnapshot.getValue(ShadeMasterModel.class);
//                    shade_no_list.add(shadeMasterModel.getShade_no());
//                }
//                if (shadeListDialog != null) {
//                    shadeListDialog.update_list(shade_no_list);
//                } else {
//                    shadeListDialog = new ListShowDialog(ReadyStockActivity.this, shade_no_list, ReadyStockActivity.this, SHADE_REQUEST);
//                }
//            }
//
//            @Override
//            public void onCancelled(@Nonnull DatabaseError databaseError) {
//
//            }
//        });
    }

    private void addArtist() {
        String design_name = binding.textDesignNo.getText().toString().trim();
        String shade_no = binding.textShadeNo.getText().toString().trim();
        String date = binding.editCurrentDate.getText().toString().trim();
        String quantity = binding.editExtraQuantity.getText().toString().trim();


        if (TextUtils.isEmpty(design_name)) {
            new CustomSnackUtil().showSnack(this, "Please enter a Design no", R.drawable.error_msg_icon);
        } else if (TextUtils.isEmpty(shade_no)) {
            new CustomSnackUtil().showSnack(this, "Please enter Shade No", R.drawable.error_msg_icon);
        } else if (TextUtils.isEmpty(date)) {
            new CustomSnackUtil().showSnack(this, "Please enter a Current Date", R.drawable.error_msg_icon);
        } else if (TextUtils.isEmpty(quantity)) {
            new CustomSnackUtil().showSnack(this, "Please enter a Extra Quantity", R.drawable.error_msg_icon);
        } else {

            String id = dao.getId();
            DialogUtil.showProgressDialog(this);
            if (tracker.equals("edit")) {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("design_no", design_name);
                hashMap.put("shade_no", shade_no);
                hashMap.put("current_date", date);
                hashMap.put("quantity", Integer.parseInt(quantity));
                dao.update(readyStockModel.getId(), hashMap).addOnSuccessListener(runnable -> {
                    DialogUtil.showUpdateSuccessDialog(this, true);
                });
            } else {
                readyStockModel.setId(id);
                readyStockModel.setDesign_no(design_name);
                readyStockModel.setShade_no(shade_no);
                readyStockModel.setCurrent_date(date);
                readyStockModel.setQuantity(Integer.parseInt(quantity));
                dao.insert(id, readyStockModel).addOnSuccessListener(runnable -> {
                    DialogUtil.showSuccessDialog(this, true);

                });
            }
        }

    }

    public void initToolbar() {
        binding.include.textTitle.setText("Ready Stock");
        binding.include.btnBack.setOnClickListener(view -> {
            onBackPressed();
        });
    }

    @Override
    public void onSelected(int position, String name, int request_code) {
        if (request_code == DESIGN_REQUEST) {
            binding.textDesignNo.setText(name);
        } else if (request_code == SHADE_REQUEST) {
            binding.textShadeNo.setText(name);
        }
    }

    @Override
    public void onAddButtonClicked(int position, String name, int request_code) {
        if (request_code == DESIGN_REQUEST) {

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