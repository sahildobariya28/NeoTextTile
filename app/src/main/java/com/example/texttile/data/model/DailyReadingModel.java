package com.example.texttile.data.model;

import javax.annotation.Nonnull;

import java.io.Serializable;

public class DailyReadingModel implements Serializable {

    private String id;
    private String date;
    private String manager_name;
    private String machine_name;
    private String order_no;
    private int qty;

    public DailyReadingModel(String id, String date, String manager_name, String machine_name, String order_no, int qty) {
        this.id = id;
        this.date = date;
        this.manager_name = manager_name;
        this.machine_name = machine_name;
        this.order_no = order_no;
        this.qty = qty;
    }

    public DailyReadingModel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getManager_name() {
        return manager_name;
    }

    public void setManager_name(String manager_name) {
        this.manager_name = manager_name;
    }

    public String getMachine_name() {
        return machine_name;
    }

    public void setMachine_name(String machine_name) {
        this.machine_name = machine_name;
    }

    public String getOrder_no() {
        return order_no;
    }

    public void setOrder_no(String order_no) {
        this.order_no = order_no;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    @Nonnull
    @Override
    public String toString() {
        return "ReadingModel{" +
                "id='" + id + '\'' +
                ", date='" + date + '\'' +
                ", manager_name='" + manager_name + '\'' +
                ", order_no='" + order_no + '\'' +
                ", qty=" + qty +
                '}';
    }


}
