package com.example.texttile.data.model;

import javax.annotation.Nonnull;

import java.io.Serializable;

public class YarnCompanyModel implements Serializable {

    String company_name;
    String company_shade_no;
    boolean fav;

    public YarnCompanyModel(String company_name, String company_shade_no, boolean fav) {
        this.company_name = company_name;
        this.company_shade_no = company_shade_no;
        this.fav = fav;
    }

    public YarnCompanyModel() {
    }

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public String getCompany_shade_no() {
        return company_shade_no;
    }

    public void setCompany_shade_no(String company_shade_no) {
        this.company_shade_no = company_shade_no;
    }

    public boolean isFav() {
        return fav;
    }

    public void setFav(boolean fav) {
        this.fav = fav;
    }

    @Nonnull
    @Override
    public String toString() {
        return "YarnCompanyModel{" +
                "company_name='" + company_name + '\'' +
                ", company_shade_no='" + company_shade_no + '\'' +
                '}';
    }
}
