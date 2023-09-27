package com.example.texttile.data.model;

import javax.annotation.Nonnull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

public class ShadeMasterModel implements Serializable {

    private String id;
    private String shade_no;
    private String design_no;
    private String wrap_color;
    private ArrayList<String> f_list;

    public ShadeMasterModel() {
    }

    public ShadeMasterModel(String id, String shade_no, String design_no, String wrap_color, ArrayList<String> f_list) {
        this.id = id;
        this.shade_no = shade_no;
        this.design_no = design_no;
        this.wrap_color = wrap_color;
        this.f_list = f_list;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getShade_no() {
        return shade_no;
    }

    public void setShade_no(String shade_no) {
        this.shade_no = shade_no;
    }

    public String getDesign_no() {
        return design_no;
    }

    public void setDesign_no(String design_no) {
        this.design_no = design_no;
    }

    public String getWrap_color() {
        return wrap_color;
    }

    public void setWrap_color(String wrap_color) {
        this.wrap_color = wrap_color;
    }

    public ArrayList<String> getF_list() {
        return f_list;
    }

    public void setF_list(ArrayList<String> f_list) {
        this.f_list = f_list;
    }

    @Nonnull
    @Override
    public String toString() {
        return "ShadeMasterModel{" +
                "id='" + id + '\'' +
                ", shade_no='" + shade_no + '\'' +
                ", design_no='" + design_no + '\'' +
                ", wrap_color='" + wrap_color + '\'' +
                ", f_list=" + f_list +
                '}';
    }
}
