package com.example.texttile.data.model;

import org.apache.poi.xssf.usermodel.XSSFSheet;

public class XSSFSheetModel {

    XSSFSheet xssfSheet;
    boolean isSelected = false;

    public XSSFSheetModel(XSSFSheet xssfSheet, boolean isSelected) {
        this.xssfSheet = xssfSheet;
        this.isSelected = isSelected;
    }

    public XSSFSheet getXssfSheet() {
        return xssfSheet;
    }

    public void setXssfSheet(XSSFSheet xssfSheet) {
        this.xssfSheet = xssfSheet;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
