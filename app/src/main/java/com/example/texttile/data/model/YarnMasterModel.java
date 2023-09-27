package com.example.texttile.data.model;

import javax.annotation.Nonnull;

import java.io.Serializable;
import java.util.ArrayList;

public class YarnMasterModel implements Serializable {

    private String id;
    private String yarn_name;
    private int denier;
    private String yarn_type;
    private int qty;
    ArrayList<YarnCompanyModel> yarnCompanyList = new ArrayList<>();

    public YarnMasterModel() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getYarn_name() {
        return yarn_name;
    }

    public void setYarn_name(String yarn_name) {
        this.yarn_name = yarn_name;
    }

    public int getDenier() {
        return denier;
    }

    public void setDenier(int denier) {
        this.denier = denier;
    }

    public String getYarn_type() {
        return yarn_type;
    }

    public void setYarn_type(String yarn_type) {
        this.yarn_type = yarn_type;
    }

    public ArrayList<YarnCompanyModel> getYarnCompanyList() {
        return yarnCompanyList;
    }

    public void setYarnCompanyList(ArrayList<YarnCompanyModel> yarnCompanyList) {
        this.yarnCompanyList = yarnCompanyList;
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
        return "YarnMasterModel{" +
                "id='" + id + '\'' +
                ", yarn_name='" + yarn_name + '\'' +
                ", denier=" + denier +
                ", yarn_type='" + yarn_type + '\'' +
                ", yarnCompanyList=" + yarnCompanyList.toString() +
                '}';
    }
}
