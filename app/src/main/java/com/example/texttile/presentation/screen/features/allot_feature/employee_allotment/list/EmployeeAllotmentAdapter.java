package com.example.texttile.presentation.screen.features.allot_feature.employee_allotment.list;

import static com.example.texttile.presentation.ui.util.PermissionUtil.isInsertPermissionGranted;
import static com.example.texttile.presentation.ui.util.PermissionUtil.isUpdatePermissionGranted;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.texttile.R;
import com.example.texttile.databinding.EmployeeAllotItemBinding;
import com.example.texttile.presentation.ui.dialog.MachineMasterDialog;
import com.example.texttile.data.dao.DAOEmployeeMaster;
import com.example.texttile.data.dao.DAOEmployeeReading;
import com.example.texttile.data.dao.DAOMachineMaster;
import com.example.texttile.presentation.ui.interfaces.AddOnSearchedItemSelectListener;
import com.example.texttile.presentation.ui.interfaces.EmptyDataListener;
import com.example.texttile.presentation.ui.interfaces.SwipeListener;
import com.example.texttile.data.model.EmployeeMasterModel;
import com.example.texttile.data.model.EmployeeReadingModel;
import com.example.texttile.data.model.ItemState;
import com.example.texttile.data.model.MachineMasterModel;
import com.example.texttile.presentation.ui.lib.new_swipe_feature.SwipeRevealLayout;
import com.example.texttile.presentation.ui.lib.new_swipe_feature.ViewBinderHelper;
import com.example.texttile.presentation.ui.lib.snackUtil.CustomSnackUtil;
import com.example.texttile.presentation.ui.util.DialogUtil;
import com.example.texttile.presentation.ui.util.ListShowDialog;
import com.example.texttile.presentation.ui.util.PermissionState;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class EmployeeAllotmentAdapter extends RecyclerView.Adapter<EmployeeAllotmentAdapter.ViewHolder> implements SwipeListener, AddOnSearchedItemSelectListener {

    Activity activity;
    EmptyDataListener emptyDataListener;
    DAOEmployeeMaster dao;
    ArrayList<EmployeeMasterModel> employeeMasterList;
    ArrayList<String> machine_name = new ArrayList<>();
    ArrayList<String> machine_name_by_emp = new ArrayList<>();
    ListShowDialog machineListDialog, machineListDialogForReading;
    final int MACHINE_REQUEST = 1, MACHINE_REQUEST_READING = 2;
    ArrayList<String> selected_machine_name = new ArrayList<>();
    ChipGroup chipGroup;
    Dialog dialog;
    FragmentManager manager;

    ArrayList<ItemState> itemModels = new ArrayList<>();
    private final ViewBinderHelper binderHelper = new ViewBinderHelper();


    public EmployeeAllotmentAdapter(ArrayList<EmployeeMasterModel> employeeMasterList, Activity activity, EmptyDataListener emptyDataListener) {
        this.employeeMasterList = employeeMasterList;
        this.activity = activity;
        this.emptyDataListener = emptyDataListener;
        dao = new DAOEmployeeMaster(activity);

        emptyDataListener.onDataChanged(true, getItemCount());
        itemModels.clear();
        for (int i = 0; i < employeeMasterList.size(); i++) {
            itemModels.add(new ItemState(false, false, false, i));
        }
        binderHelper.setOpenOnlyOne(true);
    }

    @Nonnull
    @Override
    public ViewHolder onCreateViewHolder(@Nonnull ViewGroup parent, int viewType) {
        EmployeeAllotItemBinding binding = EmployeeAllotItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@Nonnull ViewHolder holder, int position) {

        holder.binding.swipeRevealLayout.setSwipeListener(new SwipeRevealLayout.SwipeListener() {
            @Override
            public void onClosed(SwipeRevealLayout view) {
                itemModels.get(holder.getAdapterPosition()).setSwiped(false);
            }

            @Override
            public void onOpened(SwipeRevealLayout view) {
                itemModels.get(holder.getAdapterPosition()).setSwiped(true);

            }

            @Override
            public void onSlide(SwipeRevealLayout view, float slideOffset) {
                itemModels.get(holder.getAdapterPosition()).setSwiped(false);
                Log.d("jkdksdf324", "onSlide: " + "yes view is slide");
            }
        });

        holder.binding.viewMain.setOnClickListener(view -> {
            if (holder.binding.swipeRevealLayout.isOpened()) {
                binderHelper.closeLayout(String.valueOf(holder.getAdapterPosition()));
                itemModels.get(holder.getAdapterPosition()).setSwiped(false);
            }

        });

        binderHelper.bind(holder.binding.swipeRevealLayout, String.valueOf(position));

        EmployeeMasterModel employeeMasterModel = employeeMasterList.get(position);
        Animation animation = AnimationUtils.loadAnimation(activity, R.anim.scale_up);
        holder.binding.layoutMain.startAnimation(animation);


        if (itemModels.get(position).isSelected()) {
            holder.binding.viewMain.setBackground(activity.getDrawable(R.drawable.border_bg_selected));
            itemModels.get(holder.getAdapterPosition()).setSelected(true);
        } else {
            holder.binding.viewMain.setBackground(activity.getDrawable(R.drawable.border_bg_unselected));
            itemModels.get(holder.getAdapterPosition()).setSelected(false);
        }
        if (itemModels.get(position).isSwiped()) {
            binderHelper.openLayout(String.valueOf(holder.getAdapterPosition()));
        } else {
            binderHelper.closeLayout(String.valueOf(holder.getAdapterPosition()));
        }


        holder.binding.textQty.setVisibility(View.GONE);
        holder.binding.txtEmployeeName.setText(String.valueOf(employeeMasterModel.getEmployee_name()));
        holder.binding.txtMachineName.setText(String.valueOf(employeeMasterModel.getMachine_name()));


//        holder.binding.btnEdit.setOnClickListener(view -> {
//            Fragment fragment = new EmployeeAllotmentFragment();
//            Bundle bundle = new Bundle();
//            bundle.putString("tracker", "edit");
//            bundle.putSerializable("employee_allot_data", employeeMasterModel);
//            fragment.setArguments(bundle);
//            loadFragmentChangeListener.onLoadFragment(fragment,1);
//        });
        holder.binding.btnReading.setOnClickListener(view -> {
            switch (employeeMasterModel.getEmployee_allot_status()) {
                case "new":
                case "null":
                case "reading_completed":
                    showAllotDialog(employeeMasterModel);
                    break;
                case "allot_completed":
                    showReadingDialog(employeeMasterModel);
                    break;
            }

        });
    }


    public void lockAll() {
        for (int i = 0; i < itemModels.size(); i++) {
            if (itemModels.get(i).isSwiped()) {
                binderHelper.closeLayout(String.valueOf(i));
            }
            binderHelper.lockSwipe(String.valueOf(i));
        }
    }

    public void unlockAll() {
        for (int i = 0; i < itemModels.size(); i++) {
            binderHelper.unlockSwipe(String.valueOf(i));
        }
    }

    @Override
    public int getItemCount() {
        return employeeMasterList.size();
    }


    public SwipeListener getListener() {
        return this::onSwipeListener;
    }

    @Override
    public void onSwipeListener(String title, int pos) {
        if (title.equals("Reading")) {
            if (isUpdatePermissionGranted(PermissionState.EMPLOYEE_ALLOTMENT.getValue())) {
                switch (employeeMasterList.get(pos).getEmployee_allot_status()) {
                    case "new":
                    case "null":
                    case "reading_completed":
                        showAllotDialog(employeeMasterList.get(pos));
                        break;
                    case "allot_completed":
                        showReadingDialog(employeeMasterList.get(pos));
                        break;
                }
            } else {
                DialogUtil.showAccessDeniedDialog(activity);
            }
        }
    }

    private void getMachineList() {
        new DAOMachineMaster(activity).getReference().addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.w("fjskdfsd", "Listen failed.", error);
                    return;
                }

                if (value != null && !value.isEmpty()) {
                    machine_name.clear();
                    for (QueryDocumentSnapshot document : value) {
                        MachineMasterModel machineMasterModel = document.toObject(MachineMasterModel.class);
                        machine_name.add(machineMasterModel.getMachine_name());
                    }

                    if (machineListDialog != null) {
                        machineListDialog.update_list(machine_name);
                    } else {
                        machineListDialog = new ListShowDialog(activity, machine_name, EmployeeAllotmentAdapter.this, MACHINE_REQUEST);
                    }
//            new Util().setAutoAdapter(activity, machine_name, txtMachine);
                } else {
                    new CustomSnackUtil().showSnack(activity, "Machine List is Empty", R.drawable.error_msg_icon);
                }
            }
        });
    }

    private void getEmployeeList(String employee_name_filter) {
        new DAOEmployeeMaster(activity).getReference().addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.w("fjskdfsd", "Listen failed.", error);
                    return;
                }

                if (value != null && !value.isEmpty()) {
                    machine_name_by_emp.clear();
                    for (QueryDocumentSnapshot document : value) {
                        EmployeeMasterModel employeeMasterModel = document.toObject(EmployeeMasterModel.class);

                        if (employeeMasterModel.getEmployee_name().equals(employee_name_filter)) {
                            machine_name_by_emp.addAll(employeeMasterModel.getMachine_name());
                            break;
                        }
                    }

                    if (machineListDialogForReading != null) {
                        machineListDialogForReading.update_list(machine_name_by_emp);
                    } else {
                        machineListDialogForReading = new ListShowDialog(activity, machine_name_by_emp, EmployeeAllotmentAdapter.this, MACHINE_REQUEST_READING);
                    }
                } else {
                    new CustomSnackUtil().showSnack(activity, "Machine List is Empty", R.drawable.error_msg_icon);
                }
            }
        });
    }

    public void showAllotDialog(EmployeeMasterModel employeeMasterModel) {
        dialog = new Dialog(activity);
        dialog.setContentView(R.layout.dialog_emp_allot);
        dialog.setCancelable(true);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        final Calendar myCalendar = Calendar.getInstance();
        TextView edit_emp_name = dialog.findViewById(R.id.edit_emp_name);
        TextView text_machine_name = dialog.findViewById(R.id.text_machine_name);
        TextView btn_add = dialog.findViewById(R.id.btn_add);
        TextView btn_cancel = dialog.findViewById(R.id.btn_cancel);
        chipGroup = dialog.findViewById(R.id.chip_group);

        edit_emp_name.setText(employeeMasterModel.getEmployee_name());

        getMachineList();

        text_machine_name.setOnClickListener(view -> {
            if (machineListDialog != null) {
                machineListDialog.show_dialog();
            } else {
                machineListDialog = new ListShowDialog(activity, machine_name, EmployeeAllotmentAdapter.this, MACHINE_REQUEST);
                machineListDialog.show_dialog();
            }
        });

        text_machine_name.setEnabled(true);

        btn_add.setOnClickListener(view -> {

            String emp_name = edit_emp_name.getText().toString().trim();
            addAllotArtist(employeeMasterModel, emp_name, getChipToName(getAllChip(chipGroup)), dialog);

        });
        btn_cancel.setOnClickListener(view -> {
            dialog.dismiss();
        });

        dialog.show();
    }

    TextView text_machine_name;

    public void showReadingDialog(EmployeeMasterModel model) {
        dialog = new Dialog(activity);
        dialog.setContentView(R.layout.dialog_emp_reading);
        dialog.setCancelable(true);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        getEmployeeList(model.getEmployee_name());

        final Calendar myCalendar = Calendar.getInstance();
        TextView edit_current_date = dialog.findViewById(R.id.edit_current_date);
        TextView edit_emp_name = dialog.findViewById(R.id.edit_emp_name);
        TextView edit_qty = dialog.findViewById(R.id.edit_qty);
        text_machine_name = dialog.findViewById(R.id.text_machine_name);
        TextView btn_add = dialog.findViewById(R.id.btn_add);
        TextView btn_cancel = dialog.findViewById(R.id.btn_cancel);
        TextView txt_qty = dialog.findViewById(R.id.txt_qty);
        TextView text_title = dialog.findViewById(R.id.text_title);
        EditText edit_reason = dialog.findViewById(R.id.edit_reason);
        LinearLayout reason_container = dialog.findViewById(R.id.reason_container);
        chipGroup = dialog.findViewById(R.id.chip_group);

        edit_current_date.setText(new SimpleDateFormat("dd/MM/yy").format(new Date().getTime()));
        edit_emp_name.setText(model.getEmployee_name());

        getMachineList();

        text_machine_name.setOnClickListener(view -> {
            if (machineListDialogForReading != null) {
                machineListDialogForReading.show_dialog();
            } else {
                machineListDialogForReading = new ListShowDialog(activity, machine_name_by_emp, EmployeeAllotmentAdapter.this, MACHINE_REQUEST_READING);
                machineListDialogForReading.show_dialog();
            }
        });

        text_machine_name.setEnabled(true);
        text_title.setText("Employee Reading");
        edit_qty.setVisibility(View.VISIBLE);
        txt_qty.setVisibility(View.VISIBLE);


        edit_current_date.setOnClickListener(view -> {

            DatePickerDialog.OnDateSetListener date1 = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int day) {
                    myCalendar.set(Calendar.YEAR, year);
                    myCalendar.set(Calendar.MONTH, month);
                    myCalendar.set(Calendar.DAY_OF_MONTH, day);
                    String myFormat = "dd/MM/yy";
                    SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.US);
                    edit_current_date.setText(dateFormat.format(myCalendar.getTime()));
                }
            };
            DatePickerDialog mDatePicker = new DatePickerDialog(activity, date1, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
            mDatePicker.getDatePicker().setMaxDate(System.currentTimeMillis());
            mDatePicker.show();
        });


        btn_add.setOnClickListener(view -> {


            if (!edit_qty.getText().toString().isEmpty()) {
                if (Integer.parseInt(edit_qty.getText().toString()) < 50000 || Integer.parseInt(edit_qty.getText().toString()) > 90000) {

                    if (reason_container.getVisibility() == View.VISIBLE) {
                        reason_container.setVisibility(View.VISIBLE);
                        String currentDate = edit_current_date.getText().toString().trim();
                        String emp_name = edit_emp_name.getText().toString().trim();
                        String readingQty = edit_qty.getText().toString().trim();
                        String editReason = edit_reason.getText().toString().trim();

                        addReadingArtist(model, currentDate, emp_name, text_machine_name.getText().toString(), readingQty, dialog, editReason);
                    } else {
                        reason_container.setVisibility(View.VISIBLE);
                        edit_reason.requestFocus();
                    }

                } else {
                    reason_container.setVisibility(View.GONE);
                    String currentDate = edit_current_date.getText().toString().trim();
                    String emp_name = edit_emp_name.getText().toString().trim();
                    String readingQty = edit_qty.getText().toString().trim();
                    String editReason = "-";

                    addReadingArtist(model, currentDate, emp_name, text_machine_name.getText().toString(), readingQty, dialog, editReason);
                }
            } else {

            }

        });
        btn_cancel.setOnClickListener(view -> {
            dialog.dismiss();
        });


        dialog.show();
    }

    public Chip createChip(String label) {
        Chip chip = new Chip(activity, null, R.style.Widget_MaterialComponents_Chip_Entry);
        chip.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        chip.setText(label);
        chip.setCloseIconVisible(true);
        chip.setChipIconVisible(true);
        chip.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChipGroup parent = (ChipGroup) chip.getParent();
                parent.removeView(chip);
                new CustomSnackUtil().showSnack(activity, "Chip deleted successfully", R.drawable.img_true);
            }
        });
        return chip;
    }

    public ArrayList<Chip> getAllChip(ChipGroup chipGroup) {
        ArrayList<Chip> chip_list = new ArrayList<>();
        for (int i = 0; i < chipGroup.getChildCount(); i++) {
            Chip chip = (Chip) chipGroup.getChildAt(i);
            chip_list.add(chip);

        }
        return chip_list;
    }

    public ArrayList<String> getChipToName(ArrayList<Chip> chipList) {
        selected_machine_name.clear();
        for (int i = 0; i < chipList.size(); i++) {
            selected_machine_name.add(chipList.get(i).getText().toString());
        }
        return selected_machine_name;
    }

    private void addReadingArtist(EmployeeMasterModel model, String currentDate, String emp_name, String machine_name, String readingQty, Dialog dialog, String reason) {

        if (TextUtils.isEmpty(currentDate)) {
            new CustomSnackUtil().showSnack(activity, "Please enter a Current Date", R.drawable.error_msg_icon);
        } else if (TextUtils.isEmpty(emp_name)) {
            new CustomSnackUtil().showSnack(activity, "Please enter a Employee Name", R.drawable.error_msg_icon);
        } else if (TextUtils.isEmpty(readingQty)) {
            new CustomSnackUtil().showSnack(activity, "Please enter a Reading Qty", R.drawable.error_msg_icon);
        } else if (TextUtils.isEmpty(reason)) {
            new CustomSnackUtil().showSnack(activity, "Please enter a Reason", R.drawable.error_msg_icon);
        } else {

            DialogUtil.showProgressDialog(activity);
            DAOEmployeeReading daoEmployeeReading = new DAOEmployeeReading(activity);

            String id = daoEmployeeReading.getNewId();
            EmployeeReadingModel employeeReadingModel = new EmployeeReadingModel(id, emp_name, machine_name, currentDate, Integer.parseInt(readingQty), reason);
            daoEmployeeReading.insert(id, employeeReadingModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    for (int i = 0; i < model.getMachine_name().size(); i++) {
                        Log.d("kdsfkpweng", "onSuccess: " + model.getMachine_name().get(i) + "   " + machine_name + "\n" + model.getEmployee_name() + "   " + emp_name);
                        if (model.getMachine_name().get(i).equals(machine_name) && model.getEmployee_name().equals(emp_name)) {
                            model.getMachine_name().remove(i);
                            if (model.getMachine_name().isEmpty()) {
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("machine_name", model.getMachine_name());
                                hashMap.put("employee_allot_status", "reading_completed");

                                new DAOEmployeeMaster(activity).getReference().document(model.getId()).update(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        DialogUtil.hideProgressDialog();
                                        DialogUtil.showUpdateSuccessDialog(activity, false);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(Exception e) {
                                        DialogUtil.hideProgressDialog();
                                        DialogUtil.showErrorDialog(activity);
                                    }
                                });
                            } else {
                                new DAOEmployeeMaster(activity).getReference().document(model.getId()).update("machine_name", model.getMachine_name()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        DialogUtil.hideProgressDialog();
                                        DialogUtil.showUpdateSuccessDialog(activity, false);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(Exception e) {
                                        DialogUtil.hideProgressDialog();
                                        DialogUtil.showErrorDialog(activity);
                                    }
                                });
                            }
                        }
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception e) {
                    DialogUtil.hideProgressDialog();
                    DialogUtil.emptyRecordDialog(activity);
                }
            });

            dialog.dismiss();
        }
    }

    private void addAllotArtist(EmployeeMasterModel model, String emp_name, ArrayList<String> machine_name, Dialog dialog) {
        if (TextUtils.isEmpty(emp_name)) {
            new CustomSnackUtil().showSnack(activity, "Please Enter Employee Name", R.drawable.error_msg_icon);
        } else {
            boolean isValid = true;
            for (int i = 0; i < machine_name.size(); i++) {
                if (machine_name.get(i).isEmpty()) {
                    isValid = false;
                    break;
                }
            }
            if (isValid) {
                for (int i = 0; i < machine_name.size(); i++) {
                    int pos = i;


                    new DAOMachineMaster(activity).getReference().whereEqualTo("machine_name", machine_name.get(pos)).get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                model.setEmployee_name(emp_name);
                                model.setMachine_name(machine_name);
                                model.setEmployee_allot_status("allot_completed");

                                if (pos == machine_name.size() - 1) {
                                    HashMap<String, Object> hashMap = new HashMap<>();
                                    hashMap.put("employee_name", emp_name);
                                    hashMap.put("machine_name", machine_name);
                                    hashMap.put("employee_allot_status", "allot_completed");

                                    new DAOEmployeeMaster(activity).update(model.getId(), hashMap).addOnSuccessListener(runnable -> {
                                        dialog.dismiss();
                                        DialogUtil.showUpdateSuccessDialog(activity, false);
                                    });
                                }
                            } else {
                                new CustomSnackUtil().showSnack(activity, "Party List is Empty", R.drawable.error_msg_icon);
                            }
                        } else {
                            Log.d("dsfsfew", "Error getting documents: ", task.getException());
                        }
                    });

                }
            }

        }
    }

    @Override
    public void onSelected(int position, String name, int request_code) {
        if (request_code == MACHINE_REQUEST) {
            chipGroup.addView(createChip(name));
        }
        if (request_code == MACHINE_REQUEST_READING) {
            if (text_machine_name.getText().toString() != null) {
                text_machine_name.setText(name);
            }
        }
    }

    @Override
    public void onAddButtonClicked(int position, String name, int request_code) {


        if (request_code == MACHINE_REQUEST || request_code == MACHINE_REQUEST_READING) {

            if (isInsertPermissionGranted(PermissionState.MACHINE_MASTER.getValue())) {
                MachineMasterDialog machineMasterDialog = new MachineMasterDialog();
                Bundle bundle = new Bundle();
                bundle.putString("tracker", "add");
                bundle.putString("machine_name", name);
                machineMasterDialog.setArguments(bundle);
                machineMasterDialog.show(manager, "My Fragment");
            } else {
                DialogUtil.showAccessDeniedDialog(activity);
            }
        }
    }

    @Override
    public void onMultiSelected(ArrayList<String> shadeList, int request_code) {

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        EmployeeAllotItemBinding binding;

        public ViewHolder(@Nonnull EmployeeAllotItemBinding binding) {
            super(binding.getRoot());

            this.binding = binding;

        }
    }
}
