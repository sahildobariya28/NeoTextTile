package com.example.texttile.presentation.ui.interfaces;

import org.apache.poi.xssf.usermodel.XSSFSheet;

public interface AddOnReadySheet{
    void onReadySheet(XSSFSheet sheet, boolean ischecked);
}