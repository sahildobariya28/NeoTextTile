package com.example.texttile.data.model;

import javax.annotation.Nonnull;

import java.io.Serializable;

public class AllotProgramModel implements Serializable {

     private String id;
     private String machine_name;
     private String order_no;
     private String party_name;
     private String design_no;
     private String shade_no;
     private int qty;

    public AllotProgramModel() {
    }

    public AllotProgramModel(String id, String machine_name, String order_no, String party_name, String design_no, String shade_no, int qty) {
        this.id = id;
        this.machine_name = machine_name;
        this.order_no = order_no;
        this.party_name = party_name;
        this.design_no = design_no;
        this.shade_no = shade_no;
        this.qty = qty;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
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

    public String getParty_name() {
        return party_name;
    }

    public void setParty_name(String party_name) {
        this.party_name = party_name;
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

    @Nonnull
    @Override
    public String toString() {
        return "AllotProgramModel{" +
                "id='" + id + '\'' +
                ", machine_name='" + machine_name + '\'' +
                ", order_no='" + order_no + '\'' +
                ", party_name='" + party_name + '\'' +
                ", design_no='" + design_no + '\'' +
                ", shade_no='" + shade_no + '\'' +
                ", qty=" + qty +
                '}';
    }
}
