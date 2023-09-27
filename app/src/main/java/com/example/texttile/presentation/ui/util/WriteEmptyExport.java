package com.example.texttile.presentation.ui.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.example.texttile.R;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class WriteEmptyExport {

    public static final String TAG = "WriteExcelUtil";
    private static XSSFWorkbook workbook;
    private static Cell cell;
    Activity activity;
    XSSFSheet party_sheet, jala_sheet, employee_sheet, yarn_sheet, machine_sheet, design_sheet, shade_sheet;

    public WriteEmptyExport(Activity activity) {
        this.activity = activity;
    }

    public void setUpExport() {
        workbook = new XSSFWorkbook();

        party_sheet = workbook.createSheet(Const.PARTY_MASTER);
        jala_sheet = workbook.createSheet(Const.JALA_MASTER);
        employee_sheet = workbook.createSheet(Const.EMPLOYEE_MASTER);
        yarn_sheet = workbook.createSheet(Const.YARN_MASTER);
        machine_sheet = workbook.createSheet(Const.MACHINE_MASTER);
        design_sheet = workbook.createSheet(Const.DESIGN_MASTER);
        shade_sheet = workbook.createSheet(Const.SHADE_MASTER);

        getPartySheet();
        getJalaSheet();
        getEmployeeSheet();
        getYarnSheet();
        getMachineSheet();
        getDesignSheet();
        getShadeSheet();
        showDialog();
    }

    public void showDialog() {
        final Dialog dialog = new Dialog(activity);
        dialog.getWindow().setWindowAnimations(R.style.DialogAnimation);
        dialog.setContentView(R.layout.read_excel_data_dialog);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        CardView btn_yes = dialog.findViewById(R.id.btn_yes);
        CardView btn_no = dialog.findViewById(R.id.btn_no);
        TextView text_title = dialog.findViewById(R.id.text_title);
        TextView text_description = dialog.findViewById(R.id.text_description);
        TextView text_no = dialog.findViewById(R.id.text_no);
        TextView text_yes = dialog.findViewById(R.id.text_yes);
        LinearLayout view_file_name = dialog.findViewById(R.id.view_file_name);
        EditText edit_file_name = dialog.findViewById(R.id.edit_file_name);

        text_title.setText("Export Firebase ?");
        text_description.setVisibility(View.GONE);
        view_file_name.setVisibility(View.VISIBLE);
        text_yes.setText("Export");
        text_no.setText("Cancel");

        btn_yes.setOnClickListener(v1 -> {
            storeExcelInStorage(activity, edit_file_name.getText().toString().trim() + ".xlsx");
            dialog.dismiss();
        });
        btn_no.setOnClickListener(v1 -> {
            dialog.dismiss();
        });

        dialog.show();
    }

    public void getPartySheet() {
        Row headerRow = party_sheet.createRow(0);
        (cell = headerRow.createCell(0)).setCellValue("address");
        (cell = headerRow.createCell(1)).setCellValue("party_Name");
    }

    public void getJalaSheet() {
        Row headerRow = jala_sheet.createRow(0);
        (cell = headerRow.createCell(0)).setCellValue("jala_name");
        (cell = headerRow.createCell(1)).setCellValue("select_photo");
    }

    public void getEmployeeSheet() {
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
    }

    public void getYarnSheet() {
        XSSFSheet yarnCompanySheet;
        yarnCompanySheet = workbook.createSheet("yarnCompanyList");

        //Set Header Row
        Row headerRow = yarn_sheet.createRow(0);
        (cell = headerRow.createCell(0)).setCellValue("denier");
        (cell = headerRow.createCell(1)).setCellValue("qty");
        (cell = headerRow.createCell(2)).setCellValue("yarn_name");
        (cell = headerRow.createCell(3)).setCellValue("yarn_type");

        Row headerYarnCompanyRow = yarnCompanySheet.createRow(0);
        (cell = headerYarnCompanyRow.createCell(0)).setCellValue("yarn_name");
        (cell = headerYarnCompanyRow.createCell(1)).setCellValue("company_name");
        (cell = headerYarnCompanyRow.createCell(2)).setCellValue("company_shade_no");
        (cell = headerYarnCompanyRow.createCell(3)).setCellValue("fav");
    }

    public void getMachineSheet() {
        Row headerRow = machine_sheet.createRow(0);
        (cell = headerRow.createCell(0)).setCellValue("machine_name");
        (cell = headerRow.createCell(1)).setCellValue("jala_name");
        (cell = headerRow.createCell(2)).setCellValue("reed");
        (cell = headerRow.createCell(3)).setCellValue("wrap_color");
        (cell = headerRow.createCell(4)).setCellValue("wrap_thread");
        (cell = headerRow.createCell(5)).setCellValue("status");
    }

    public void getDesignSheet() {
        XSSFSheet fListSheet, jalaWithFileModelSheet;
        fListSheet = workbook.createSheet("f_list");
        jalaWithFileModelSheet = workbook.createSheet("jalaWithFileModelList");

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
    }

    public void getShadeSheet() {
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
}
