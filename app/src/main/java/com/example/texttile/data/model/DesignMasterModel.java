package com.example.texttile.data.model;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import javax.annotation.Nonnull;

public class DesignMasterModel implements Serializable {

    private String id;
    private String design_no;
    private String date;
    private int reed;
    private int base_pick;
    private int base_card;
    private int total_card;
    private int avg_pick;
    private String type;
    private ArrayList<Integer> f_list = new ArrayList<>();
    private float total_len;
    private String sample_card_len;
    private ArrayList<JalaWithFileModel> jalaWithFileModelList;

    public DesignMasterModel(String id, String design_no, String date, int reed, int base_pick, int base_card, int total_card, int avg_pick, String type, ArrayList<Integer> f_list, float total_len, String sample_card_len, ArrayList<JalaWithFileModel> jalaWithFileModelList) {
        this.id = id;
        this.design_no = design_no;
        this.date = date;
        this.reed = reed;
        this.base_pick = base_pick;
        this.base_card = base_card;
        this.total_card = total_card;
        this.avg_pick = avg_pick;
        this.type = type;
        this.f_list = f_list;
        this.total_len = total_len;
        this.sample_card_len = sample_card_len;
        this.jalaWithFileModelList = jalaWithFileModelList;
    }

    public DesignMasterModel() {
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getReed() {
        return reed;
    }

    public void setReed(int reed) {
        this.reed = reed;
    }

    public int getBase_pick() {
        return base_pick;
    }

    public void setBase_pick(int base_pick) {
        this.base_pick = base_pick;
    }

    public int getBase_card() {
        return base_card;
    }

    public void setBase_card(int base_card) {
        this.base_card = base_card;
    }

    public int getTotal_card() {
        return total_card;
    }

    public void setTotal_card(int total_card) {
        this.total_card = total_card;
    }

    public int getAvg_pick() {
        return avg_pick;
    }

    public void setAvg_pick(int avg_pick) {
        this.avg_pick = avg_pick;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ArrayList<Integer> getF_list() {
        return f_list;
    }

    public void setF_list(ArrayList<Integer> f_list) {
        this.f_list = f_list;
    }

    public float getTotal_len() {
        return total_len;
    }

    public void setTotal_len(float total_len) {
        this.total_len = total_len;
    }

    public String getSample_card_len() {
        return sample_card_len;
    }

    public void setSample_card_len(String sample_card_len) {
        this.sample_card_len = sample_card_len;
    }

    public ArrayList<JalaWithFileModel> getJalaWithFileModelList() {
        return jalaWithFileModelList;
    }

    public void setJalaWithFileModelList(ArrayList<JalaWithFileModel> jalaWithFileModelList) {
        this.jalaWithFileModelList = jalaWithFileModelList;
    }

    public boolean isFListItemVisible(int position){
        Log.d("dflkdslfkew2", "isFListItemVisible: " + f_list.size() +"    " + (position + 1) + "    " + (f_list.size() >= (position + 1)) );
        if (f_list.size() >= (position + 1)){
            return true;
        }else {
            return false;
        }
    }
    public String getFlistText(int position){
        if (position <= (f_list.size() - 1)){
            return Integer.toString(f_list.get(position));
        }else {
            return "null";
        }
    }

    public HashMap<String, Object> getDesignMasterHashMap() {

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id", getId());
        hashMap.put("design_no", getDesign_no());
        hashMap.put("date", getDate());
        hashMap.put("reed", getReed());
        hashMap.put("base_pick", getBase_pick());
        hashMap.put("base_card", getBase_card());
        hashMap.put("total_card", getTotal_card());
        hashMap.put("avg_pick", getAvg_pick());
        hashMap.put("type", getType());
        hashMap.put("f_list", getF_list());
        hashMap.put("total_len", getTotal_len());
        hashMap.put("sample_card_len", getSample_card_len());
        hashMap.put("jalaWithFileModelList", getJalaWithFileModelList());

        return hashMap;
    }

    @Nonnull
    @Override
    public String toString() {
        return "DesignMasterModel{" +
                "id='" + id + '\'' +
                ", design_no='" + design_no + '\'' +
                ", date='" + date + '\'' +
                ", reed=" + reed +
                ", base_pick=" + base_pick +
                ", base_card=" + base_card +
                ", total_card=" + total_card +
                ", avg_pick=" + avg_pick +
                ", f_list=" + f_list +
                ", total_len=" + total_len +
                ", sample_card_len='" + sample_card_len + '\'' +
                ", jalaWithFileModelList=" + jalaWithFileModelList +
                '}';
    }



}
