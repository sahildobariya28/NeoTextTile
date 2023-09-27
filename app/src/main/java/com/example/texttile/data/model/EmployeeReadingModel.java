package com.example.texttile.data.model;

import java.io.Serializable;

public class EmployeeReadingModel implements Serializable {

    String id;
    String emp_name;
    String machine_name;
    String date;
    int qty;
    String reason;

    public EmployeeReadingModel(String id, String emp_name, String machine_name, String date, int qty, String reason) {
        this.id = id;
        this.emp_name = emp_name;
        this.machine_name = machine_name;
        this.date = date;
        this.qty = qty;
        this.reason = reason;
    }

    public EmployeeReadingModel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmp_name() {
        return emp_name;
    }

    public void setEmp_name(String emp_name) {
        this.emp_name = emp_name;
    }

    public String getMachine_name() {
        return machine_name;
    }

    public void setMachine_name(String machine_name) {
        this.machine_name = machine_name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    public String toString() {
        return "EmployeeReadingModel{" +
                "id='" + id + '\'' +
                ", emp_name='" + emp_name + '\'' +
                ", machine_name=" + machine_name +
                ", date='" + date + '\'' +
                ", qty=" + qty +
                ", reason='" + reason + '\'' +
                '}';
    }
}
