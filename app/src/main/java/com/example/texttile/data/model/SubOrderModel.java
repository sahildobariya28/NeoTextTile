package com.example.texttile.data.model;

import java.io.Serializable;

public class SubOrderModel implements Serializable {

    String id;
    String shadeNo;
    int qty;

    public SubOrderModel(String id, String shadeNo, int qty) {
        this.id = id;
        this.shadeNo = shadeNo;
        this.qty = qty;
    }

    public SubOrderModel() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getShadeNo() {
        return shadeNo;
    }

    public void setShadeNo(String shadeNo) {
        this.shadeNo = shadeNo;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }
}
