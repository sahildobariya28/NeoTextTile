package com.example.texttile.data.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

public class MachineMasterModel implements Serializable {

    private String id;
    private String machine_name;
    private String jala_name;
    private String reed;
    private String wrap_color;
    private String wrap_thread;
    private boolean status;
    private float labour_charge;
    private List<AllotProgramModel> allotList = new ArrayList<>();
    private List<DailyReadingModel> readingList = new ArrayList<>();

    public MachineMasterModel() {

    }

    public MachineMasterModel(String id, String machine_name, String jala_name, String reed, String wrap_color, String wrap_thread, boolean status, float labour_charge) {
        this.id = id;
        this.machine_name = machine_name;
        this.jala_name = jala_name;
        this.reed = reed;
        this.wrap_color = wrap_color;
        this.wrap_thread = wrap_thread;
        this.status = status;
        this.labour_charge = labour_charge;
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

    public String getJala_name() {
        return jala_name;
    }

    public void setJala_name(String jala_name) {
        this.jala_name = jala_name;
    }

    public String getReed() {
        return reed;
    }

    public void setReed(String reed) {
        this.reed = reed;
    }

    public String getWrap_color() {
        return wrap_color;
    }

    public void setWrap_color(String wrap_color) {
        this.wrap_color = wrap_color;
    }

    public String getWrap_thread() {
        return wrap_thread;
    }

    public void setWrap_thread(String wrap_thread) {
        this.wrap_thread = wrap_thread;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public float getLabour_charge() {
        return labour_charge;
    }

    public void setLabour_charge(float labour_charge) {
        this.labour_charge = labour_charge;
    }

    public List<AllotProgramModel> getAllotList() {
        return allotList;
    }

    public void setAllotList(List<AllotProgramModel> allotList) {
        this.allotList = allotList;
    }

    public List<DailyReadingModel> getReadingList() {
        return readingList;
    }

    public void setReadingList(List<DailyReadingModel> readingList) {
        this.readingList = readingList;
    }

    @Nonnull
    @Override
    public String toString() {
        return "MachineMasterModel{" +
                "id='" + id + '\'' +
                ", machine_name='" + machine_name + '\'' +
                ", jala_name='" + jala_name + '\'' +
                ", reed='" + reed + '\'' +
                ", wrap_color='" + wrap_color + '\'' +
                ", wrap_thread='" + wrap_thread + '\'' +
                ", status=" + status +
                ", allotList=" + allotList +
                ", readingList=" + readingList +
                '}';
    }
}
