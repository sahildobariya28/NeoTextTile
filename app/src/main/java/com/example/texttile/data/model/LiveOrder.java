package com.example.texttile.data.model;

public class LiveOrder {

    String id;
    String machine_name;
    String order_no;
    int qty;

    public LiveOrder(String id, String machine_name,String order_no, int qty) {
        this.id = id;
        this.machine_name = machine_name;
        this.order_no = order_no;
        this.qty = qty;
    }

    public LiveOrder() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
}
