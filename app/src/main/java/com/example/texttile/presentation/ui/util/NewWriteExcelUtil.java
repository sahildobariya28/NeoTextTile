package com.example.texttile.presentation.ui.util;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.example.texttile.R;
import com.example.texttile.data.dao.DAODesignMaster;
import com.example.texttile.data.dao.DAOEmployeeMaster;
import com.example.texttile.data.dao.DAOMachineMaster;
import com.example.texttile.data.dao.DAOPartyMaster;
import com.example.texttile.data.dao.DAOShadeMaster;
import com.example.texttile.data.dao.DaoYarnMaster;
import com.example.texttile.presentation.ui.interfaces.AddOnReadySheet;
import com.example.texttile.data.model.DesignMasterModel;
import com.example.texttile.data.model.EmployeeMasterModel;
import com.example.texttile.data.model.JalaMasterModel;
import com.example.texttile.data.model.MachineMasterModel;
import com.example.texttile.data.model.PartyMasterModel;
import com.example.texttile.data.model.ShadeMasterModel;
import com.example.texttile.data.model.XSSFSheetModel;
import com.example.texttile.data.model.YarnMasterModel;
import com.example.texttile.presentation.ui.lib.snackUtil.CustomSnackUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.annotation.Nonnull;

public class NewWriteExcelUtil {

    public static final String TAG = "WriteExcelUtil";

    private static Cell cell;
    private static XSSFWorkbook workbook;
    Activity activity;
    ArrayList<XSSFSheetModel> sheetList = new ArrayList<>();
    AddOnReadySheet addOnReadySheet;

    XSSFSheet party_sheet, jala_sheet, employee_sheet, yarn_sheet, machine_sheet, design_sheet, shade_sheet;

    public NewWriteExcelUtil(Activity activity, AddOnReadySheet addOnReadySheet) {
        this.addOnReadySheet = addOnReadySheet;
        this.activity = activity;
    }

    public void setUpExport() {
        workbook = new XSSFWorkbook();

        party_sheet = workbook.createSheet(Const.PARTY_MASTER);
        addOnReadySheet.onReadySheet(party_sheet, false);
        jala_sheet = workbook.createSheet(Const.JALA_MASTER);
        addOnReadySheet.onReadySheet(jala_sheet, false);
        employee_sheet = workbook.createSheet(Const.EMPLOYEE_MASTER);
        addOnReadySheet.onReadySheet(employee_sheet, false);
        yarn_sheet = workbook.createSheet(Const.YARN_MASTER);
        addOnReadySheet.onReadySheet(yarn_sheet, false);
        machine_sheet = workbook.createSheet(Const.MACHINE_MASTER);
        addOnReadySheet.onReadySheet(machine_sheet, false);
        design_sheet = workbook.createSheet(Const.DESIGN_MASTER);
        addOnReadySheet.onReadySheet(design_sheet, false);
        shade_sheet = workbook.createSheet(Const.SHADE_MASTER);
        addOnReadySheet.onReadySheet(shade_sheet, false);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(Const.JALA_MASTER).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ArrayList<JalaMasterModel> jalaList = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    JalaMasterModel jalaMasterModel = document.toObject(JalaMasterModel.class);
                    jalaList.add(jalaMasterModel);
                }
                exportData(activity, "MyJala.xls", jalaList);

                new DAOPartyMaster(activity).getReference().get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.isEmpty()) {
                            ArrayList<PartyMasterModel> partyList = new ArrayList<>();
                            for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                                PartyMasterModel partyMasterModel = queryDocumentSnapshot.toObject(PartyMasterModel.class);
                                partyList.add(partyMasterModel);
                                // Access document data here using documentSnapshot.getData()
                            }
                            exportData(activity, "MyJala.xls", partyList);

                            new DAOEmployeeMaster(activity).getReference().get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        ArrayList<EmployeeMasterModel> employeeList = new ArrayList<>();
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            EmployeeMasterModel employeeMasterModel = document.toObject(EmployeeMasterModel.class);
                                            employeeList.add(employeeMasterModel);
                                        }
                                        exportData(activity, "MyJala.xls", employeeList);


                                    } else {
                                        // Handle errors here
                                    }
                                }
                            });

                            new DAOEmployeeMaster(activity).getReference().get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        ArrayList<EmployeeMasterModel> employeeList = new ArrayList<>();

                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            EmployeeMasterModel employeeMasterModel = document.toObject(EmployeeMasterModel.class);
                                            employeeList.add(employeeMasterModel);
                                        }
                                        exportData(activity, "MyJala.xls", employeeList);

                                        new DaoYarnMaster(activity).getReference().get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    ArrayList<YarnMasterModel> yarnList = new ArrayList<>();
                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        YarnMasterModel yarnMasterModel = document.toObject(YarnMasterModel.class);
                                                        yarnList.add(yarnMasterModel);
                                                    }
                                                    exportData(activity, "MyJala.xls", yarnList);



                                                    new DAOMachineMaster(activity).getReference().get().addOnCompleteListener(task1 -> {
                                                        if (task1.isSuccessful()) {
                                                            ArrayList<MachineMasterModel> machineList = new ArrayList<>();
                                                            for (QueryDocumentSnapshot document : task1.getResult()) {
                                                                MachineMasterModel machineMasterModel = document.toObject(MachineMasterModel.class);
                                                                machineList.add(machineMasterModel);
                                                            }
                                                            exportData(activity, "MyJala.xls", machineList);


                                                            new DAODesignMaster(activity).getReference().get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                @Override
                                                                public void onComplete(@Nonnull Task<QuerySnapshot> task) {
                                                                    if (task.isSuccessful()) {
                                                                        ArrayList<DesignMasterModel> designList = new ArrayList<>();
                                                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                                                            DesignMasterModel designMasterModel = document.toObject(DesignMasterModel.class);
                                                                            designList.add(designMasterModel);
                                                                        }
                                                                        exportData(activity, "MyJala.xls", designList);

                                                                        new DAOShadeMaster(activity).getReference().get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                                if (task.isSuccessful()) {
                                                                                    ArrayList<ShadeMasterModel> shadeList = new ArrayList<>();
                                                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                                                        ShadeMasterModel shadeMasterModel = document.toObject(ShadeMasterModel.class);
                                                                                        shadeList.add(shadeMasterModel);
                                                                                    }
                                                                                    exportData(activity, "MyJala.xls", shadeList);
                                                                                } else {
                                                                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                                                                    new CustomSnackUtil().showSnack(activity, "Error getting documents", R.drawable.error_msg_icon);
                                                                                }
                                                                            }
                                                                        });

//                                                                        new DAOShadeMaster(activity).getReference().addListenerForSingleValueEvent(new ValueEventListener() {
//                                                                            @Override
//                                                                            public void onDataChange(@Nonnull DataSnapshot snapshot) {
//
//                                                                                if (snapshot.exists()) {
//                                                                                    Log.d(TAG, "onDataChange: " + snapshot.getChildrenCount());
//
//                                                                                    ArrayList<ShadeMasterModel> shadeList = new ArrayList<>();
//                                                                                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
//                                                                                        ShadeMasterModel shadeMasterModel = postSnapshot.getValue(ShadeMasterModel.class);
//                                                                                        shadeList.add(shadeMasterModel);
//                                                                                    }
//                                                                                    exportData(activity, "MyJala.xls", shadeList);
//                                                                                } else {
//                                                                                    new CustomSnackUtil().showSnack(activity, "Shade List is Empty", R.drawable.error_msg_icon);
//                                                                                }
//                                                                            }
//
//                                                                            @Override
//                                                                            public void onCancelled(@Nonnull DatabaseError error) {
//
//                                                                            }
//                                                                        });

                                                                    } else {
                                                                        new CustomSnackUtil().showSnack(activity, "Design List is Empty", R.drawable.error_msg_icon);
                                                                    }
                                                                }
                                                            });

                                                        } else {
                                                            new CustomSnackUtil().showSnack(activity, "Machine List is Empty", R.drawable.error_msg_icon);
                                                        }
                                                    });
                                                } else {
                                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                                    new CustomSnackUtil().showSnack(activity, "Error getting documents", R.drawable.error_msg_icon);
                                                }
                                            }
                                        });


                                    } else {
                                        // Handle errors here
                                    }
                                }
                            });
                        } else {
                            new CustomSnackUtil().showSnack(activity, "Party List is Empty", R.drawable.error_msg_icon);
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        // Handle errors here
                    }
                });

            } else {
                new CustomSnackUtil().showSnack(activity, "Jala List is Empty", R.drawable.error_msg_icon);
            }
        });
    }

    public void exportData(Activity activity, String fileName, ArrayList dataList) {
        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
            Log.e(TAG, "Storage not available or read only");
        }
        if (dataList != null && dataList.get(0) != null && dataList.size() > 0) {
            if (dataList.get(0) instanceof PartyMasterModel) {
                getPartySheet(dataList);
            } else if (dataList.get(0) instanceof JalaMasterModel) {
                getJalaSheet(dataList);
            } else if (dataList.get(0) instanceof EmployeeMasterModel) {
                getEmployeeSheet(dataList);
            } else if (dataList.get(0) instanceof YarnMasterModel) {
                getYarnSheet(dataList);
            } else if (dataList.get(0) instanceof MachineMasterModel) {
                getMachineSheet(dataList);
            } else if (dataList.get(0) instanceof DesignMasterModel) {
                getDesignSheet(dataList);
            } else if (dataList.get(0) instanceof ShadeMasterModel) {
                getShadeSheet(dataList);
            }
        }
    }

    public void getPartySheet(ArrayList dataList) {
        ArrayList<PartyMasterModel> partyList = dataList;

        //Set Header Row
        Row headerRow = party_sheet.createRow(0);
        (cell = headerRow.createCell(0)).setCellValue("address");
        (cell = headerRow.createCell(1)).setCellValue("party_Name");

        //Set all data row
        for (int i = 0; i < partyList.size(); i++) {
            // Create a New Row for every new entry in list
            Row rowData = party_sheet.createRow(i + 1);

            // Create Cells for each row
            (cell = rowData.createCell(0)).setCellValue(partyList.get(i).getAddress());
            (cell = rowData.createCell(1)).setCellValue(partyList.get(i).getParty_Name());
            if (i == (partyList.size() - 1)) {
                addOnReadySheet.onReadySheet(party_sheet, true);
            }
        }
    }

    public void getJalaSheet(ArrayList dataList) {
        ArrayList<JalaMasterModel> jalaList = dataList;

        //Set Header Row
        Row headerRow = jala_sheet.createRow(0);
        (cell = headerRow.createCell(0)).setCellValue("jala_name");
        (cell = headerRow.createCell(1)).setCellValue("select_photo");

        //Set all data row
        for (int i = 0; i < jalaList.size(); i++) {
            Row rowData = jala_sheet.createRow(i + 1);
            (cell = rowData.createCell(0)).setCellValue(jalaList.get(i).getJala_name());
            (cell = rowData.createCell(1)).setCellValue(jalaList.get(i).getSelect_photo());
            if (i == (jalaList.size() - 1)) {
                addOnReadySheet.onReadySheet(jala_sheet, true);
            }
        }

    }

    public void getEmployeeSheet(ArrayList dataList) {
        ArrayList<EmployeeMasterModel> employeeList = dataList;

        //Set Header Row
        Row headerRow = employee_sheet.createRow(0);
        (cell = headerRow.createCell(0)).setCellValue("employee_name");
        (cell = headerRow.createCell(1)).setCellValue("phone_no");
        (cell = headerRow.createCell(2)).setCellValue("alter_phone_no");
        (cell = headerRow.createCell(3)).setCellValue("account_no");
        (cell = headerRow.createCell(4)).setCellValue("ifsc_code");
        (cell = headerRow.createCell(5)).setCellValue("bank_name");
        (cell = headerRow.createCell(6)).setCellValue("bank_holder_name");
        (cell = headerRow.createCell(7)).setCellValue("employee_photo");
        (cell = headerRow.createCell(8)).setCellValue("id_proof");
        (cell = headerRow.createCell(9)).setCellValue("employee_allot_status");

        //Set all data row
        for (int i = 0; i < employeeList.size(); i++) {
            Row rowData = employee_sheet.createRow(i + 1);
            (cell = rowData.createCell(0)).setCellValue(employeeList.get(i).getEmployee_name());
            (cell = rowData.createCell(1)).setCellValue(employeeList.get(i).getPhone_no());
            (cell = rowData.createCell(2)).setCellValue(employeeList.get(i).getAlter_phone_no());
            (cell = rowData.createCell(3)).setCellValue(employeeList.get(i).getAccount_no());
            (cell = rowData.createCell(4)).setCellValue(employeeList.get(i).getIfsc_code());
            (cell = rowData.createCell(5)).setCellValue(employeeList.get(i).getBank_name());
            (cell = rowData.createCell(6)).setCellValue(employeeList.get(i).getBank_holder_name());
            (cell = rowData.createCell(7)).setCellValue(employeeList.get(i).getEmployee_photo());
            (cell = rowData.createCell(8)).setCellValue(employeeList.get(i).getId_proof());
            (cell = rowData.createCell(9)).setCellValue(employeeList.get(i).getEmployee_allot_status());

            if (i == (employeeList.size() - 1)) {
                addOnReadySheet.onReadySheet(employee_sheet, true);
            }
        }

    }

    public void getYarnSheet(ArrayList dataList) {
        ArrayList<YarnMasterModel> yarnList = dataList;
        XSSFSheet yarnCompanySheet;
        int yarnCompanyCount = 1;


        yarnCompanySheet = workbook.createSheet("yarnCompanyList");


        //Set Header Row
        Row headerRow = yarn_sheet.createRow(0);
        (cell = headerRow.createCell(0)).setCellValue("denier");
        (cell = headerRow.createCell(1)).setCellValue("qty");
        (cell = headerRow.createCell(2)).setCellValue("yarn_name");
        (cell = headerRow.createCell(3)).setCellValue("yarn_type");
//Set Header Row
        Row headerYarnCompanyRow = yarnCompanySheet.createRow(0);
        (cell = headerYarnCompanyRow.createCell(0)).setCellValue("yarn_name");
        (cell = headerYarnCompanyRow.createCell(1)).setCellValue("company_name");
        (cell = headerYarnCompanyRow.createCell(2)).setCellValue("company_shade_no");
        (cell = headerYarnCompanyRow.createCell(3)).setCellValue("fav");

        //Set all data row
        for (int i = 0; i < yarnList.size(); i++) {
            Row rowData = yarn_sheet.createRow(i + 1);
            (cell = rowData.createCell(0)).setCellValue(yarnList.get(i).getDenier());
            (cell = rowData.createCell(1)).setCellValue(yarnList.get(i).getQty());
            (cell = rowData.createCell(2)).setCellValue(yarnList.get(i).getYarn_name());
            (cell = rowData.createCell(3)).setCellValue(yarnList.get(i).getYarn_type());

            if (yarnList.get(i).getYarnCompanyList() != null && yarnList.get(i).getYarnCompanyList().size() != 0) {
                for (int j = 0; j < yarnList.get(i).getYarnCompanyList().size(); j++) {
                    Row rowYarnCompanyData = yarnCompanySheet.createRow(yarnCompanyCount++);
                    (cell = rowYarnCompanyData.createCell(0)).setCellValue(yarnList.get(i).getYarn_name());
                    (cell = rowYarnCompanyData.createCell(1)).setCellValue(yarnList.get(i).getYarnCompanyList().get(j).getCompany_name());
                    (cell = rowYarnCompanyData.createCell(2)).setCellValue(yarnList.get(i).getYarnCompanyList().get(j).getCompany_shade_no());
                    (cell = rowYarnCompanyData.createCell(3)).setCellValue(yarnList.get(i).getYarnCompanyList().get(j).isFav());
                }
            }
            if (i == (yarnList.size() - 1)) {
                addOnReadySheet.onReadySheet(yarn_sheet, true);
            }
        }
    }

    public void getMachineSheet(ArrayList dataList) {
        ArrayList<MachineMasterModel> machineList = dataList;

        //Set Header Row
        Row headerRow = machine_sheet.createRow(0);
        (cell = headerRow.createCell(0)).setCellValue("machine_name");
        (cell = headerRow.createCell(1)).setCellValue("jala_name");
        (cell = headerRow.createCell(2)).setCellValue("reed");
        (cell = headerRow.createCell(3)).setCellValue("wrap_color");
        (cell = headerRow.createCell(4)).setCellValue("wrap_thread");
        (cell = headerRow.createCell(5)).setCellValue("status");

        //Set all data row
        for (int i = 0; i < machineList.size(); i++) {
            Row rowData = machine_sheet.createRow(i + 1);
            (cell = rowData.createCell(0)).setCellValue(machineList.get(i).getMachine_name());
            (cell = rowData.createCell(1)).setCellValue(machineList.get(i).getJala_name());
            (cell = rowData.createCell(2)).setCellValue(machineList.get(i).getReed());
            (cell = rowData.createCell(3)).setCellValue(machineList.get(i).getWrap_color());
            (cell = rowData.createCell(4)).setCellValue(machineList.get(i).getWrap_thread());
            (cell = rowData.createCell(5)).setCellValue(machineList.get(i).isStatus());

            if (i == (machineList.size() - 1)) {
                addOnReadySheet.onReadySheet(machine_sheet, true);
            }
        }
    }

    public void getDesignSheet(ArrayList dataList) {
        ArrayList<DesignMasterModel> desingList = dataList;
        XSSFSheet fListSheet, jalaWithFileModelSheet;
        int jalaWithFileCount = 1;


        fListSheet = workbook.createSheet("f_list");
        jalaWithFileModelSheet = workbook.createSheet("jalaWithFileModelList");

        //Set Header Row
        Row headerRow = design_sheet.createRow(0);
        (cell = headerRow.createCell(0)).setCellValue("avg_pick");
        (cell = headerRow.createCell(1)).setCellValue("base_card");
        (cell = headerRow.createCell(2)).setCellValue("base_pick");
        (cell = headerRow.createCell(3)).setCellValue("temp_date");
        (cell = headerRow.createCell(4)).setCellValue("date");
        (cell = headerRow.createCell(5)).setCellValue("design_no");
        (cell = headerRow.createCell(6)).setCellValue("reed");
        (cell = headerRow.createCell(7)).setCellValue("sample_card_len");
        (cell = headerRow.createCell(8)).setCellValue("total_card");
        (cell = headerRow.createCell(9)).setCellValue("total_len");
        (cell = headerRow.createCell(10)).setCellValue("type");

        Row fList_headerRow = fListSheet.createRow(0);
        (cell = fList_headerRow.createCell(0)).setCellValue("id");
        (cell = fList_headerRow.createCell(1)).setCellValue("0");
        (cell = fList_headerRow.createCell(2)).setCellValue("1");
        (cell = fList_headerRow.createCell(3)).setCellValue("2");
        (cell = fList_headerRow.createCell(4)).setCellValue("3");
        (cell = fList_headerRow.createCell(5)).setCellValue("4");
        (cell = fList_headerRow.createCell(6)).setCellValue("5");
        (cell = fList_headerRow.createCell(7)).setCellValue("6");
        (cell = fList_headerRow.createCell(8)).setCellValue("7");


        Row jalaWithFileHeaderRow = jalaWithFileModelSheet.createRow(0);
        (cell = jalaWithFileHeaderRow.createCell(0)).setCellValue("id");
        (cell = jalaWithFileHeaderRow.createCell(1)).setCellValue("jala_name");
        (cell = jalaWithFileHeaderRow.createCell(2)).setCellValue("file_uri");
        (cell = jalaWithFileHeaderRow.createCell(3)).setCellValue("ready");
        (cell = jalaWithFileHeaderRow.createCell(4)).setCellValue("isFirebaseURI");

        //Set all data row
        for (int i = 0; i < desingList.size(); i++) {
            Row rowData = design_sheet.createRow(i + 1);
            Row rowFListData = fListSheet.createRow(i + 1);
            (cell = rowData.createCell(0)).setCellValue(desingList.get(i).getAvg_pick());
            (cell = rowData.createCell(1)).setCellValue(desingList.get(i).getBase_card());
            (cell = rowData.createCell(2)).setCellValue(desingList.get(i).getBase_pick());
            (cell = rowData.createCell(3)).setCellValue(desingList.get(i).getDate());
            (cell = rowData.createCell(4)).setCellValue(desingList.get(i).getDate());
            (cell = rowData.createCell(5)).setCellValue(desingList.get(i).getDesign_no());
            (cell = rowData.createCell(6)).setCellValue(desingList.get(i).getReed());
            (cell = rowData.createCell(7)).setCellValue(desingList.get(i).getSample_card_len());
            (cell = rowData.createCell(8)).setCellValue(desingList.get(i).getTotal_card());
            (cell = rowData.createCell(9)).setCellValue(desingList.get(i).getTotal_len());
            (cell = rowData.createCell(10)).setCellValue(desingList.get(i).getType());

            (cell = rowFListData.createCell(0)).setCellValue(desingList.get(i).getDesign_no());
            for (int j = 0; j < desingList.get(i).getF_list().size(); j++) {
                (cell = rowFListData.createCell(j + 1)).setCellValue(desingList.get(i).getF_list().get(j));
            }

            for (int j = 0; j < desingList.get(i).getJalaWithFileModelList().size(); j++) {
                Row rowJalawithFileData = jalaWithFileModelSheet.createRow(jalaWithFileCount++);

                (cell = rowJalawithFileData.createCell(0)).setCellValue(desingList.get(i).getDesign_no());
                (cell = rowJalawithFileData.createCell(1)).setCellValue(desingList.get(i).getJalaWithFileModelList().get(j).getJala_name());
                if (desingList.get(i).getJalaWithFileModelList().get(j).getFile_uri() == null || desingList.get(i).getJalaWithFileModelList().get(j).getFile_uri().isEmpty()) {
                    (cell = rowJalawithFileData.createCell(2)).setCellValue("null");
                } else {
                    (cell = rowJalawithFileData.createCell(2)).setCellValue(desingList.get(i).getJalaWithFileModelList().get(j).getFile_uri());
                }
                (cell = rowJalawithFileData.createCell(3)).setCellValue(desingList.get(i).getJalaWithFileModelList().get(j).isReady());
                (cell = rowJalawithFileData.createCell(4)).setCellValue(desingList.get(i).getJalaWithFileModelList().get(j).isFirebaseURI());
            }
            if (i == (desingList.size() - 1)) {
                addOnReadySheet.onReadySheet(design_sheet, true);
            }

        }
    }


    public void getShadeSheet(ArrayList dataList) {
        ArrayList<ShadeMasterModel> shadeList = dataList;

        //Set Header Row
        Row headerRow = shade_sheet.createRow(0);
        (cell = headerRow.createCell(0)).setCellValue("shade_no");
        (cell = headerRow.createCell(1)).setCellValue("design_no");
        (cell = headerRow.createCell(2)).setCellValue("wrap_color");
        (cell = headerRow.createCell(3)).setCellValue("1");
        (cell = headerRow.createCell(4)).setCellValue("2");
        (cell = headerRow.createCell(5)).setCellValue("3");
        (cell = headerRow.createCell(6)).setCellValue("4");
        (cell = headerRow.createCell(7)).setCellValue("5");
        (cell = headerRow.createCell(8)).setCellValue("6");
        (cell = headerRow.createCell(9)).setCellValue("7");
        (cell = headerRow.createCell(10)).setCellValue("8");

        //Set all data row
        for (int i = 0; i < shadeList.size(); i++) {
            Row rowData = shade_sheet.createRow(i + 1);
            (cell = rowData.createCell(0)).setCellValue(shadeList.get(i).getShade_no());
            (cell = rowData.createCell(1)).setCellValue(shadeList.get(i).getDesign_no());
            (cell = rowData.createCell(2)).setCellValue(shadeList.get(i).getWrap_color());

            if (shadeList.get(i).getF_list().size() > 0) {
                Log.d("kdlfkke2w3rnm", "getShadeSheet: " + shadeList.get(i).getShade_no() + "    " + i);
                (cell = rowData.createCell(3)).setCellValue(shadeList.get(i).getF_list().get(0));
                if (shadeList.get(i).getF_list().size() > 1) {
                    (cell = rowData.createCell(4)).setCellValue(shadeList.get(i).getF_list().get(1));
                    if (shadeList.get(i).getF_list().size() > 2) {
                        (cell = rowData.createCell(5)).setCellValue(shadeList.get(i).getF_list().get(2));
                        if (shadeList.get(i).getF_list().size() > 3) {
                            (cell = rowData.createCell(6)).setCellValue(shadeList.get(i).getF_list().get(3));
                            if (shadeList.get(i).getF_list().size() > 4) {
                                (cell = rowData.createCell(7)).setCellValue(shadeList.get(i).getF_list().get(4));
                                if (shadeList.get(i).getF_list().size() > 5) {
                                    (cell = rowData.createCell(8)).setCellValue(shadeList.get(i).getF_list().get(5));
                                    if (shadeList.get(i).getF_list().size() > 6) {
                                        (cell = rowData.createCell(9)).setCellValue(shadeList.get(i).getF_list().get(6));
                                        if (shadeList.get(i).getF_list().size() > 7) {
                                            (cell = rowData.createCell(10)).setCellValue(shadeList.get(i).getF_list().get(7));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (i == shadeList.size() - 1) {
                addOnReadySheet.onReadySheet(shade_sheet, true);
                new CustomSnackUtil().showSnack(activity, "Success", R.drawable.img_true);
            }
        }
    }

    public boolean storeExcelInStorage(Context context, String fileName) {
        boolean isSuccess;
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);
//        File file = new File(context.getExternalFilesDir(null), fileName);
        FileOutputStream fileOutputStream = null;
        Log.e(TAG, "Writing file" + file.canWrite());
        try {
            fileOutputStream = new FileOutputStream(file);
            workbook.write(fileOutputStream);
            Log.e(TAG, "Writing file" + file);
            isSuccess = true;
        } catch (IOException e) {
            Log.e(TAG, "Error writing Exception: ", e);
            isSuccess = false;
        } catch (Exception e) {
            Log.e(TAG, "Failed to save file due to Exception: ", e);
            isSuccess = false;
        } finally {
            try {
                if (null != fileOutputStream) {
                    fileOutputStream.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return isSuccess;
    }

    private static boolean isExternalStorageReadOnly() {
        String externalStorageState = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED_READ_ONLY.equals(externalStorageState);
    }

    /**
     * Checks if Storage is Available
     *
     * @return boolean
     */
    private static boolean isExternalStorageAvailable() {
        String externalStorageState = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(externalStorageState);
    }
}
