package com.example.texttile.presentation.ui.util;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;

import com.example.texttile.R;
import com.example.texttile.data.dao.DAODesignMaster;
import com.example.texttile.data.dao.DAOEmployeeMaster;
import com.example.texttile.data.dao.DAOJalaMaster;
import com.example.texttile.data.dao.DAOMachineMaster;
import com.example.texttile.data.dao.DAOPartyMaster;
import com.example.texttile.data.dao.DAOShadeMaster;
import com.example.texttile.data.dao.DaoYarnMaster;
import com.example.texttile.presentation.ui.interfaces.AddOnReadySheet;
import com.example.texttile.data.model.DesignMasterModel;
import com.example.texttile.data.model.EmployeeMasterModel;
import com.example.texttile.data.model.JalaMasterModel;
import com.example.texttile.data.model.JalaWithFileModel;
import com.example.texttile.data.model.MachineMasterModel;
import com.example.texttile.data.model.PartyMasterModel;
import com.example.texttile.data.model.ShadeMasterModel;
import com.example.texttile.data.model.XSSFSheetModel;
import com.example.texttile.data.model.YarnCompanyModel;
import com.example.texttile.data.model.YarnMasterModel;
import com.example.texttile.presentation.ui.lib.snackUtil.CustomSnackUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.annotation.Nonnull;

public class ReadExcelUtils {
    public static final String TAG = "ExcelUtil";
    Activity activity;
    AddOnReadySheet addOnReadySheet;

    public ReadExcelUtils(Activity activity, AddOnReadySheet addOnReadySheet) {
        this.activity = activity;
        this.addOnReadySheet = addOnReadySheet;
    }

    public ArrayList<XSSFSheetModel> getSheet(String fileName) {

        ArrayList<XSSFSheetModel> sheetList = new ArrayList<>();

//        String path = Environment.getExternalStorageDirectory() + "/Download/" + "MyData.xls";
//        Log.d("dfsdfsdfwer23", "onActivityResult: 00" + path);
        File file = new File(fileName);
        FileInputStream fileInputStream = null;

        try {
            fileInputStream = new FileInputStream(file);
            Log.e(TAG, "Reading from Excel" + file);

            XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);

            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                sheetList.add(new XSSFSheetModel(workbook.getSheetAt(i), true));
            }

        } catch (IOException e) {
            Log.e(TAG, "Error Reading Exception: ", e);

        } catch (Exception e) {
            Log.e(TAG, "Failed to read file due to Exception: ", e);

        } finally {
            try {
                if (null != fileInputStream) {
                    fileInputStream.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return sheetList;

    }

    public void setEmployeeMasterData(XSSFSheet sheet) {
        int batchSize = 500;
        int batchCount = 0;
        DialogUtil.showProgressDialog(activity);
        WriteBatch batch = FirebaseFirestore.getInstance().batch();

        DAOEmployeeMaster daoEmployeeMaster = new DAOEmployeeMaster(activity);
        CollectionReference designMasterRef = daoEmployeeMaster.getReference();

        for (Row row : sheet) {
            if (row.getRowNum() > 0) {
                EmployeeMasterModel employeeMasterModel = new EmployeeMasterModel();
                String id = daoEmployeeMaster.getNewId();
                DocumentReference docRef = designMasterRef.document(id);
                employeeMasterModel.setId(id);
                int index = 0;

                Iterator<Cell> cellIterator = row.cellIterator();
                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();

                    if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
                        switch (index) {
                            case 0:
                                employeeMasterModel.setEmployee_name(cell.getStringCellValue());
                                break;
                            case 1:
                                employeeMasterModel.setPhone_no(cell.getStringCellValue());
                                break;
                            case 2:
                                employeeMasterModel.setAlter_phone_no(cell.getStringCellValue());
                                break;
                            case 3:
                                employeeMasterModel.setAccount_no(cell.getStringCellValue());
                                break;
                            case 4:
                                employeeMasterModel.setIfsc_code(cell.getStringCellValue());
                                break;
                            case 5:
                                employeeMasterModel.setBank_name(cell.getStringCellValue());
                                break;
                            case 6:
                                employeeMasterModel.setBank_holder_name(cell.getStringCellValue());
                                break;
                            case 7:
                                employeeMasterModel.setEmployee_photo(cell.getStringCellValue());
                                break;
                            case 8:
                                employeeMasterModel.setId_proof(cell.getStringCellValue());
                                break;
                            case 9:
                                employeeMasterModel.setEmployee_allot_status(cell.getStringCellValue());
                                batch.set(docRef, employeeMasterModel);
                                if (++batchCount == batchSize) {
                                    batch.commit();
                                    batch = FirebaseFirestore.getInstance().batch();
                                    batchCount = 0;
                                }
                                break;
                        }
                        index++;
                    }
                }
            }
        }
        if (batchCount > 0) {
            batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@Nonnull Task<Void> task) {
                    DialogUtil.hideProgressDialog();
                    DialogUtil.showSuccessDialog(activity, false);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@Nonnull Exception e) {
                    DialogUtil.hideProgressDialog();
                    new CustomSnackUtil().showSnack(activity, "Something Want Wrong", R.drawable.error_msg_icon);
                }
            });
        } else {
            DialogUtil.hideProgressDialog();
            DialogUtil.showSuccessDialog(activity, false);
        }


    }

    public void setPartyMasterData(XSSFSheet sheet) {
        int batchSize = 500;
        int batchCount = 0;
        DialogUtil.showProgressDialog(activity);
        WriteBatch batch = FirebaseFirestore.getInstance().batch();

        DAOPartyMaster daoPartyMaster = new DAOPartyMaster(activity);
        CollectionReference collectionRef = daoPartyMaster.getReference();

        for (Row row : sheet) {
            if (row.getRowNum() > 0) {
                PartyMasterModel partyMasterModel = new PartyMasterModel();
                String id = collectionRef.document().getId(); // Generate a new ID for each document
                partyMasterModel.setId(id);
                int index = 0;

                Iterator<Cell> cellIterator = row.cellIterator();
                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();

                    if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
                        switch (index) {
                            case 0:
                                partyMasterModel.setAddress(cell.getStringCellValue());
                                break;
                            case 1:
                                partyMasterModel.setParty_Name(cell.getStringCellValue());
                                break;
                        }
                        index++;
                    }
                }

                DocumentReference documentRef = collectionRef.document(id);
                batch.set(documentRef, partyMasterModel);
                if (++batchCount == batchSize) {
                    batch.commit();
                    batch = FirebaseFirestore.getInstance().batch();
                    batchCount = 0;
                }
            }
        }
        if (batchCount > 0) {
            batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(Task<Void> task) {
                    DialogUtil.hideProgressDialog();
                    DialogUtil.showSuccessDialog(activity, false);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@Nonnull Exception e) {
                    DialogUtil.hideProgressDialog();
                    Log.w(TAG, "Batch write failed.", e);
                }
            });
        } else {
            DialogUtil.hideProgressDialog();
            DialogUtil.showSuccessDialog(activity, false);
        }
    }

    public void setYarnMasterData(XSSFSheet sheet, XSSFSheet companyListSheet) {
        int batchSize = 500;
        int batchCount = 0;
        DialogUtil.showProgressDialog(activity);
        WriteBatch batch = FirebaseFirestore.getInstance().batch();

        DaoYarnMaster daoYarnMaster = new DaoYarnMaster(activity);
        CollectionReference collectionRef = daoYarnMaster.getReference();

        for (Row row : sheet) {
            if (row.getRowNum() > 0) {
                YarnMasterModel yarnMasterModel = new YarnMasterModel();
                String id = daoYarnMaster.getId();
                DocumentReference docRef = collectionRef.document(id);
                yarnMasterModel.setId(id);
                int index = 0;

                Iterator<Cell> cellIterator = row.cellIterator();
                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();

                    if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                        switch (index) {
                            case 0:
                                yarnMasterModel.setDenier((int) cell.getNumericCellValue());
                                break;
                            case 1:
                                yarnMasterModel.setQty((int) cell.getNumericCellValue());

                                break;
                        }
                        index++;
                    } else if (cell.getCellType() == Cell.CELL_TYPE_STRING) {

                        switch (index) {

                            case 2:
                                yarnMasterModel.setYarn_name(cell.getStringCellValue());
                                break;
                            case 3:
                                yarnMasterModel.setYarn_type(cell.getStringCellValue());
                                setYarnCompanyListData(companyListSheet, yarnMasterModel, batch, docRef);
                                if (++batchCount == batchSize) {
                                    batch.commit();
                                    batch = FirebaseFirestore.getInstance().batch();
                                    batchCount = 0;
                                }
                                break;
//                                daoYarnMaster.addWithoutDialog(id, yarnMasterModel);

                        }
                        index++;
                    }
                }
            }
        }
        if (batchCount > 0) {
            batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(Task<Void> task) {
                    DialogUtil.hideProgressDialog();
                    DialogUtil.showSuccessDialog(activity, false);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@Nonnull Exception e) {
                    DialogUtil.hideProgressDialog();
                    DialogUtil.showErrorDialog(activity);
                }
            });
        } else {
            DialogUtil.hideProgressDialog();
            DialogUtil.showSuccessDialog(activity, false);
        }

    }

    public void setYarnCompanyListData(XSSFSheet sheet, YarnMasterModel yarnMasterModel, WriteBatch batch, DocumentReference documentReference) {

        boolean ischild = false;
        ArrayList<YarnCompanyModel> yarnCompanyModels = new ArrayList<>();
        for (Row row : sheet) {
            if (row.getRowNum() > 0) {
                YarnCompanyModel yarnCompanyModel = new YarnCompanyModel();
                int index = 0;

                Iterator<Cell> cellIterator = row.cellIterator();
                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();

                    if (index == 0) {
                        if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
                            if (cell.getStringCellValue().equals(yarnMasterModel.getYarn_name())) {
                                ischild = true;
                            } else {
                                ischild = false;
                            }
                        }
                    }
                    if (ischild) {
                        if (cell.getCellType() == Cell.CELL_TYPE_STRING) {

                            switch (index) {
                                case 1:
                                    yarnCompanyModel.setCompany_name(cell.getStringCellValue());
                                    break;
                                case 2:
                                    yarnCompanyModel.setCompany_shade_no(cell.getStringCellValue());
                                    break;
                            }
                            index++;
                        } else if (cell.getCellType() == Cell.CELL_TYPE_BOOLEAN) {
                            switch (index) {
                                case 3:
                                    yarnCompanyModel.setFav(cell.getBooleanCellValue());
                                    yarnCompanyModels.add(yarnCompanyModel);
                                    break;
                            }
                            index++;
                        }
                    }
                }
            }
        }

        yarnMasterModel.setYarnCompanyList(yarnCompanyModels);
        batch.set(documentReference, yarnMasterModel);


    }

    public void setJalaMasterData(XSSFSheet sheet) {

        int batchSize = 500;
        int batchCount = 0;
        DialogUtil.showProgressDialog(activity);
        WriteBatch batch = FirebaseFirestore.getInstance().batch();

        DAOJalaMaster daoJalaMaster = new DAOJalaMaster(activity);
        CollectionReference jalaMasterReference = daoJalaMaster.getReference();

        for (Row row : sheet) {

            if (row.getRowNum() > 0) {
                JalaMasterModel jalaMasterModel = new JalaMasterModel();
                String id = daoJalaMaster.getNewId();
                DocumentReference docRef = jalaMasterReference.document(id);
                jalaMasterModel.setId(id);
                int index = 0;

                Iterator<Cell> cellIterator = row.cellIterator();
                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();

                    if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
                        switch (index) {
                            case 0:
                                jalaMasterModel.setJala_name(cell.getStringCellValue());
                                break;
                            case 1:
                                if (cell.getStringCellValue().equals("null")) {
                                    jalaMasterModel.setSelect_photo(cell.getStringCellValue());
                                } else {
                                    jalaMasterModel.setSelect_photo(cell.getStringCellValue());
                                }
                                daoJalaMaster.insert(id, jalaMasterModel);
                                batch.set(docRef, jalaMasterModel);

                                if (++batchCount == batchSize) {
                                    batch.commit();
                                    batch = FirebaseFirestore.getInstance().batch();
                                    batchCount = 0;
                                }
                                break;
                        }
                        index++;
                    }
                }
            }
        }
        if (batchCount > 0) {
            batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@Nonnull Task<Void> task) {
                    DialogUtil.hideProgressDialog();
                    DialogUtil.showSuccessDialog(activity, false);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@Nonnull Exception e) {
                    DialogUtil.hideProgressDialog();
                    new CustomSnackUtil().showSnack(activity, "Something Want Wrong", R.drawable.error_msg_icon);
                }
            });
        } else {
            DialogUtil.hideProgressDialog();
            DialogUtil.showSuccessDialog(activity, false);
        }
    }

    public void setMachineMasterData(XSSFSheet sheet) {
        int batchSize = 500;
        int batchCount = 0;
        DialogUtil.showProgressDialog(activity);
        WriteBatch batch = FirebaseFirestore.getInstance().batch();

        DAOMachineMaster daoMachineMaster = new DAOMachineMaster(activity);
        CollectionReference jalaMasterReference = daoMachineMaster.getReference();

        for (Row row : sheet) {
            if (row.getRowNum() > 0) {
                MachineMasterModel machineMasterModel = new MachineMasterModel();
                String id = daoMachineMaster.getId();
                DocumentReference docRef = jalaMasterReference.document(id);
                machineMasterModel.setId(id);
                int index = 0;

                for (int i = 0; i < 6; i++) {
                    Log.d(TAG, "setMachineMasterData:  " + i + "    " + row.getCell(i).getCellType());
                    if (row.getCell(i).getCellType() == Cell.CELL_TYPE_STRING) {
                        switch (i) {
                            case 0:
                                machineMasterModel.setMachine_name(row.getCell(i).getStringCellValue());
                                break;
                            case 1:
                                machineMasterModel.setJala_name(row.getCell(i).getStringCellValue());
                                break;
                            case 2:
                                machineMasterModel.setReed(String.valueOf(row.getCell(i).getStringCellValue()));
                                break;
                            case 3:
                                machineMasterModel.setWrap_color(row.getCell(i).getStringCellValue());
                                break;
                            case 4:
                                machineMasterModel.setWrap_thread(String.valueOf(row.getCell(i).getStringCellValue()));
                                break;
                        }

                    } else if (row.getCell(i).getCellType() == Cell.CELL_TYPE_NUMERIC) {
                        switch (i) {
                            case 2:
                                machineMasterModel.setReed(String.valueOf((int) row.getCell(i).getNumericCellValue()));
                                break;
                            case 4:
                                machineMasterModel.setWrap_thread(String.valueOf((int) row.getCell(i).getNumericCellValue()));
                                break;
                        }

                    } else if (row.getCell(i).getCellType() == Cell.CELL_TYPE_BOOLEAN) {

                        switch (i) {
                            case 5:
                                machineMasterModel.setStatus(row.getCell(i).getBooleanCellValue());
                                batch.set(docRef, machineMasterModel);
                                if (++batchCount == batchSize) {
                                    batch.commit();
                                    batch = FirebaseFirestore.getInstance().batch();
                                    batchCount = 0;
                                }
                                break;
                        }
                    }
                }
            }
        }
        if (batchCount > 0) {
            batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(Task<Void> task) {
                    DialogUtil.hideProgressDialog();
                    DialogUtil.showSuccessDialog(activity, false);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@Nonnull Exception e) {
                    DialogUtil.hideProgressDialog();
                    DialogUtil.showErrorDialog(activity);
                }
            });
        } else {
            DialogUtil.hideProgressDialog();
            DialogUtil.showSuccessDialog(activity, false);
        }
    }

    public void setDesignMasterData(Activity activity, XSSFSheet sheet, XSSFSheet fListSheet, XSSFSheet jalaWithFileModelListSheet) {

        int batchSize = 500;
        int batchCount = 0;
        DialogUtil.showProgressDialog(activity);
        WriteBatch batch = FirebaseFirestore.getInstance().batch();

        DAODesignMaster daoDesignMaster = new DAODesignMaster(activity);
        CollectionReference designMasterRef = daoDesignMaster.getReference();

        for (Row row : sheet) {
            if (row.getRowNum() > 0) {
                DesignMasterModel designMasterModel = new DesignMasterModel();
                String id = daoDesignMaster.getNewId();
                DocumentReference docRef = designMasterRef.document(id);
                designMasterModel.setId(id);

                for (int i = 0; i < 11; i++) {

                    if (row.getCell(i).getCellType() == Cell.CELL_TYPE_NUMERIC) {
                        switch (i) {
                            case 0:
                                designMasterModel.setAvg_pick((int) row.getCell(i).getNumericCellValue());
                                break;
                            case 1:
                                designMasterModel.setBase_card((int) row.getCell(i).getNumericCellValue());
                                break;
                            case 2:
                                designMasterModel.setBase_pick((int) row.getCell(i).getNumericCellValue());
                                break;
                            case 6:
                                designMasterModel.setReed((int) row.getCell(i).getNumericCellValue());
                                break;
                            case 8:
                                designMasterModel.setTotal_card((int) row.getCell(i).getNumericCellValue());
                                break;
                            case 9:
                                designMasterModel.setTotal_len((float) row.getCell(i).getNumericCellValue());
                                break;

                        }
                    } else if (row.getCell(i).getCellType() == Cell.CELL_TYPE_STRING) {

                        switch (i) {
                            case 4:
                                designMasterModel.setDate(row.getCell(i).getStringCellValue());
                                break;
                            case 5:
                                designMasterModel.setDesign_no(row.getCell(i).getStringCellValue());
                                break;
                            case 7:
                                designMasterModel.setSample_card_len(row.getCell(i).getStringCellValue());
                                break;
                            case 10:
                                designMasterModel.setType(row.getCell(i).getStringCellValue());
                                setFListData(fListSheet, designMasterModel);
                                setJalaWithFileModelListSheet(jalaWithFileModelListSheet, designMasterModel, batch, docRef);

                                if (++batchCount == batchSize) {
                                    batch.commit();
                                    batch = FirebaseFirestore.getInstance().batch();
                                    batchCount = 0;
                                }
                                break;
                        }
                    } else if (row.getCell(i).getCellType() == Cell.CELL_TYPE_FORMULA) {
                        switch (i) {
                            case 4:
                                designMasterModel.setDate(row.getCell(i).getStringCellValue());
                                break;
                        }
                    }

                }
            }
        }
        if (batchCount > 0) {
            batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@Nonnull Task<Void> task) {
                    DialogUtil.hideProgressDialog();
                    DialogUtil.showSuccessDialog(activity, false);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@Nonnull Exception e) {
                    DialogUtil.hideProgressDialog();
                    new CustomSnackUtil().showSnack(activity, "Something Want Wrong", R.drawable.error_msg_icon);
                }
            });
        } else {
            DialogUtil.hideProgressDialog();
            DialogUtil.showSuccessDialog(activity, false);
        }
    }

    public void setFListData(XSSFSheet sheet, DesignMasterModel model) {

        boolean ischild = false;
        ArrayList<Integer> flist = new ArrayList<>();
        for (Row row : sheet) {
            if (row.getRowNum() > 0) {
                int index = 0;

                Iterator<Cell> cellIterator = row.cellIterator();
                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();

                    if (index == 0) {
                        if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
                            if (cell.getStringCellValue().equals(model.getDesign_no())) {
                                ischild = true;
                            } else {
                                ischild = false;
                            }
                        }
                    }


                    if (ischild) {
                        if (cell != null && cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {


                            flist.add((int) cell.getNumericCellValue());


                        }
                    }
                }
            }
        }

        model.setF_list(flist);
    }

    public void setJalaWithFileModelListSheet(XSSFSheet sheet, DesignMasterModel model, WriteBatch batch, DocumentReference documentReference) {

        boolean ischild = false;
        ArrayList<JalaWithFileModel> jalaWithFileModels = new ArrayList<>();
        for (Row row : sheet) {
            if (row.getRowNum() > 0) {
                JalaWithFileModel jalaWithFileModel = new JalaWithFileModel();
                int index = 0;

                Iterator<Cell> cellIterator = row.cellIterator();
                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();

                    if (index == 0) {
                        if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
                            if (cell.getStringCellValue().equals(model.getDesign_no())) {
                                ischild = true;
                            } else {
                                ischild = false;
                            }
                        }
                    }

                    if (ischild) {
                        if (cell.getCellType() == Cell.CELL_TYPE_STRING) {

                            switch (index) {
                                case 1:
                                    jalaWithFileModel.setJala_name(cell.getStringCellValue());
                                    break;
                                case 2:
                                    if (cell.getStringCellValue().equals("null")) {
                                        StorageReference ref = FirebaseStorage.getInstance().getReference().child("default").child("no_image.webp");
                                        ref.getDownloadUrl().addOnSuccessListener(uri -> {
                                            jalaWithFileModel.setFile_uri(uri.toString());
                                        }).addOnFailureListener(e -> {

                                            Bitmap bitmap = BitmapFactory.decodeResource(activity.getResources(), R.drawable.no_image);
                                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                            byte[] data = baos.toByteArray();
                                            ref.putBytes(data).addOnSuccessListener(taskSnapshot -> {
                                                StorageReference ref1 = FirebaseStorage.getInstance().getReference().child("default").child("no_image.webp");
                                                ref1.getDownloadUrl().addOnSuccessListener(uri -> {
                                                    jalaWithFileModel.setFile_uri(uri.toString());
                                                });

                                            }).addOnFailureListener(e1 -> {

                                            });
                                        });

                                    } else {
                                        jalaWithFileModel.setFile_uri(cell.getStringCellValue());
                                    }
                                    break;
                            }
                            index++;
                        } else if (cell.getCellType() == Cell.CELL_TYPE_BOOLEAN) {
                            switch (index) {
                                case 3:
                                    jalaWithFileModel.setReady(cell.getBooleanCellValue());
                                    break;
                                case 4:
                                    jalaWithFileModel.setFirebaseURI(cell.getBooleanCellValue());
                                    jalaWithFileModels.add(jalaWithFileModel);
                                    break;
                            }
                            index++;
                        }
                    }
                }
            }
        }
        model.setJalaWithFileModelList(jalaWithFileModels);
        batch.set(documentReference, model);

    }

    public void setShadeMasterData(XSSFSheet sheet) {


        DAOShadeMaster daoShadeMaster = new DAOShadeMaster(activity);
        CollectionReference designMasterRef = daoShadeMaster.getReference();

        ArrayList<String> shadeFlist = new ArrayList<>();
        ArrayList<ShadeMasterModel> shadeMasterList = new ArrayList<>();

        int batchSize = 500;
        int batchCount = 0;
        DialogUtil.showProgressDialog(activity);
        WriteBatch batch = FirebaseFirestore.getInstance().batch();

        for (Row row : sheet) {
            if (row.getRowNum() > 0) {
                ShadeMasterModel shadeMasterModel = new ShadeMasterModel();
                String id = daoShadeMaster.getId();
                DocumentReference docRef = designMasterRef.document(id);
                shadeMasterModel.setId(id);
                int index = 0;

                shadeFlist.clear();
                for (int i = 0; i <= 11; i++) {
                    if (row.getCell(i) != null && row.getCell(i).getCellType() == Cell.CELL_TYPE_STRING) {
                        switch (i) {
                            case 0:
                                shadeMasterModel.setShade_no(row.getCell(i).getStringCellValue());
                                break;
                            case 1:
                                shadeMasterModel.setDesign_no(row.getCell(i).getStringCellValue());
                                break;
                            case 2:
                                shadeMasterModel.setWrap_color(row.getCell(i).getStringCellValue());
                                break;
                            case 3:
                            case 4:
                            case 5:
                            case 6:
                            case 7:
                            case 8:
                            case 9:
                            case 10:
                                if (row.getCell(i + 1) != null && !TextUtils.isEmpty(row.getCell(i + 1).getStringCellValue())) {
                                    shadeFlist.add(row.getCell(i).getStringCellValue());
                                    break;
                                } else {
                                    shadeFlist.add(row.getCell(i).getStringCellValue());
                                    shadeMasterModel.setF_list(shadeFlist);
                                    batch.set(docRef, shadeMasterModel);

                                    // Commit the batch once it reaches the maximum size
                                    if (++batchCount == batchSize) {
                                        batch.commit();
                                        batch = FirebaseFirestore.getInstance().batch();
                                        batchCount = 0;
                                    }

                                    break;
                                }
                        }
                    }
                }
            }
        }

        // Commit the remaining batch
        if (batchCount > 0) {
            batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    DialogUtil.hideProgressDialog();
                    DialogUtil.showSuccessDialog(activity, false);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@Nonnull Exception e) {
                    DialogUtil.hideProgressDialog();
                    DialogUtil.showErrorDialog(activity);
                    Log.d(TAG, "onFailure: " + e.getMessage() + "\n" + e.getLocalizedMessage());
                    new CustomSnackUtil().showSnack(activity, "Something Want Wrong" + e.getMessage(), R.drawable.error_msg_icon);
                }
            });
        } else {
            DialogUtil.hideProgressDialog();
            DialogUtil.showSuccessDialog(activity, false);
        }
    }
}