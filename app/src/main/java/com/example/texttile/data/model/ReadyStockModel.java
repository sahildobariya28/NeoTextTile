package com.example.texttile.data.model;

import javax.annotation.Nonnull;

import java.io.Serializable;

public class ReadyStockModel implements Serializable {

    private String id;
    private String design_no;
    private String shade_no;
    private String current_date;
    private int quantity;

    public ReadyStockModel(String id, String design_no, String shade_no, String current_date, int quantity) {
        this.id = id;
        this.design_no = design_no;
        this.shade_no = shade_no;
        this.current_date = current_date;
        this.quantity = quantity;
    }

    public ReadyStockModel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDesign_no() {
        return design_no;
    }

    public void setDesign_no(String design_no) {
        this.design_no = design_no;
    }

    public String getShade_no() {
        return shade_no;
    }

    public void setShade_no(String shade_no) {
        this.shade_no = shade_no;
    }

    public String getCurrent_date() {
        return current_date;
    }

    public void setCurrent_date(String current_date) {
        this.current_date = current_date;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Nonnull
    @Override
    public String toString() {
        return "ExtraOrderModel{" +
                "id='" + id + '\'' +
                ", design_no='" + design_no + '\'' +
                ", shade_no='" + shade_no + '\'' +
                ", current_date='" + current_date + '\'' +
                ", quantity=" + quantity +
                '}';
    }
}
