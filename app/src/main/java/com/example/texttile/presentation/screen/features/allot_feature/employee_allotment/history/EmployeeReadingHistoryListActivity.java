package com.example.texttile.presentation.screen.features.allot_feature.employee_allotment.history;

import static com.example.texttile.presentation.ui.util.PrinterUtils.printBluetooth;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.texttile.R;
import com.example.texttile.databinding.ActivityEmployeeAllotListBinding;
import com.example.texttile.presentation.ui.interfaces.EmptyDataListener;
import com.example.texttile.data.model.EmployeeReadingModel;
import com.example.texttile.presentation.ui.lib.snackUtil.CustomSnackUtil;
import com.example.texttile.presentation.ui.util.Const;
import com.example.texttile.presentation.ui.util.DialogUtil;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import javax.annotation.Nullable;

public class EmployeeReadingHistoryListActivity extends AppCompatActivity implements EmptyDataListener {

    ActivityEmployeeAllotListBinding binding;
    ArrayList<EmployeeReadingModel> employeeAllotmentHistoryList = new ArrayList<>();
    EmployeeReadingHistoryAdapter employeeReadingHistoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEmployeeAllotListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initToolbar();

        employee_allotment_list("");

    }

    public void employee_allotment_list(String filter) {
        if (employeeReadingHistoryAdapter == null)
            DialogUtil.showProgressDialog(EmployeeReadingHistoryListActivity.this);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(Const.EMPLOYEE_READING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("TAG", "Listen failed.", e);
                    return;
                }

                if (snapshot != null && !snapshot.isEmpty()) {
                    employeeAllotmentHistoryList.clear();
                    for (QueryDocumentSnapshot document : snapshot) {
                        EmployeeReadingModel employeeReadingModel = document.toObject(EmployeeReadingModel.class);
                        if (employeeReadingModel != null && employeeReadingModel.getEmp_name().toLowerCase().contains(filter.toLowerCase())) {
                            employeeAllotmentHistoryList.add(employeeReadingModel);
                        }
                    }
                    DialogUtil.hideProgressDialog();
                    Collections.reverse(employeeAllotmentHistoryList);
                    employeeReadingHistoryAdapter = new EmployeeReadingHistoryAdapter(employeeAllotmentHistoryList, EmployeeReadingHistoryListActivity.this, EmployeeReadingHistoryListActivity.this);
                    binding.recyclerView.setLayoutManager(new LinearLayoutManager(EmployeeReadingHistoryListActivity.this));
                    binding.recyclerView.setAdapter(employeeReadingHistoryAdapter);
                } else {
                    DialogUtil.hideProgressDialog();
                    onDataChanged(true, 0);
                    employeeReadingHistoryAdapter = new EmployeeReadingHistoryAdapter(employeeAllotmentHistoryList, EmployeeReadingHistoryListActivity.this, EmployeeReadingHistoryListActivity.this);
                    binding.recyclerView.setLayoutManager(new LinearLayoutManager(EmployeeReadingHistoryListActivity.this));
                    binding.recyclerView.setAdapter(employeeReadingHistoryAdapter);
                    new CustomSnackUtil().showSnack(EmployeeReadingHistoryListActivity.this, "Employee Reading history is Empty", R.drawable.error_msg_icon);
                }
            }
        });
    }

    public void initToolbar() {
        binding.include.textTitle.setText("Employee Reading History");

        binding.include.btnHistory.setVisibility(View.VISIBLE);
        binding.include.btnHistory.setImageResource(R.drawable.printer);


        binding.include.btnHistory.setOnClickListener(view -> {
            final Dialog dialog = new Dialog(EmployeeReadingHistoryListActivity.this);
            dialog.setContentView(R.layout.employee_print_history);
            dialog.setCancelable(true);
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            CardView btn_cancel = dialog.findViewById(R.id.btn_cancel);
            CardView btn_print = dialog.findViewById(R.id.btn_exit);
            EditText edit_month = dialog.findViewById(R.id.edit_month);
            EditText edit_year = dialog.findViewById(R.id.edit_year);


            edit_year.setOnClickListener(view1 -> {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);

// Create a DatePickerDialog with year-only selection
                DatePickerDialog dialogPicker = new DatePickerDialog(
                        this,
                        android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                // Do something with the selected year
                                Log.d("Selected Year", String.valueOf(year));
                            }
                        },
                        year,
                        0, // Set the initial month to January (0-based)
                        1 // Set the initial day to the 1st of January
                );

                dialogPicker.getDatePicker().setCalendarViewShown(false);
                dialogPicker.getDatePicker().setSpinnersShown(true);
// Hide the day and month spinners
                ((ViewGroup) dialogPicker.getDatePicker()).findViewById(Resources.getSystem().getIdentifier("day", "id", "android")).setVisibility(View.GONE);
                ((ViewGroup) dialogPicker.getDatePicker()).findViewById(Resources.getSystem().getIdentifier("month", "id", "android")).setVisibility(View.GONE);

                dialogPicker.show();
            });


        });

        binding.include.btnBack.setOnClickListener(view -> {
            onBackPressed();
        });
    }

    public void setupPrinter(String
                                     heading, ArrayList<EmployeeReadingModel> employeeAllotmentHistoryModels, String
                                     year, String month) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("[C]<b><font size='big'>" + heading + "</font></b>\n\n\n");

        StringBuilder employee_allot_item_list = new StringBuilder();
        for (int i = 0; i < employeeAllotmentHistoryModels.size(); i++) {
            if (employeeAllotmentHistoryModels.get(i).getDate().contains(month + "/" + year) || employeeAllotmentHistoryModels.get(i).getDate().contains(month + "/" + year.replace("20", ""))) {
                employee_allot_item_list.append("[L]<font size='normal'>" + employeeAllotmentHistoryModels.get(i).getEmp_name() + "</font> " + "[L]<font size='normal'>" + employeeAllotmentHistoryModels.get(i).getQty() + "</font> " + "[L]<font size='normal'>" + employeeAllotmentHistoryModels.get(i).getMachine_name() + "      " + "</font> " + "<font size='normal'>" + employeeAllotmentHistoryModels.get(i).getDate() + "</font>[L]\n");
            }
        }
        if (!employee_allot_item_list.toString().isEmpty()) {
            stringBuilder.append("[L]<font size='normal'><u><b>Emp Name</b></u></font>  [L]<font size='normal'><u><b>Qty</b></u></font> [L]<font size='normal'><u><b>Machine</b></u></font>  " + "<font size='normal'><u><b>Date</b></u></font> [L]\n");
            stringBuilder.append("[C]------------------------------------------------\n");

            stringBuilder.append(employee_allot_item_list.toString());
            stringBuilder.append("[C]\n");
            stringBuilder.append("[C]\n");
            stringBuilder.append("[C]\n");
            stringBuilder.append("[C]\n");
            stringBuilder.append("[C]\n");
            stringBuilder.append("[C]\n");

            printBluetooth(this, stringBuilder.toString());
        } else {
            new CustomSnackUtil().showSnack(this, "No Data Found", R.drawable.icon_warning);
        }
    }

    @Override
    public void onDataChanged(boolean isDataChanged, int size) {
        if (isDataChanged) {
            if (size == 0) {
                binding.animationView.setVisibility(View.VISIBLE);
            } else {
                binding.animationView.setVisibility(View.GONE);
            }
        }
    }
}