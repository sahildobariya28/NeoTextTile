package com.example.texttile.data.model;

import javax.annotation.Nonnull;

import java.io.Serializable;

public class JalaMasterModel implements Serializable {

    private String id;
    private String jala_name;
    private String select_photo;

    public JalaMasterModel(String id, String jala_name, String select_photo) {
        this.id = id;
        this.jala_name = jala_name;
        this.select_photo = select_photo;
    }
    public JalaMasterModel(){

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getJala_name() {
        return jala_name;
    }

    public void setJala_name(String jala_name) {
        this.jala_name = jala_name;
    }

    public String getSelect_photo() {
        return select_photo;
    }

    public void setSelect_photo(String select_photo) {
        this.select_photo = select_photo;
    }

    @Nonnull
    @Override
    public String toString() {
        return "JalaMasterModel{" +
                "id='" + id + '\'' +
                ", jala_name='" + jala_name + '\'' +
                ", select_photo='" + select_photo + '\'' +
                '}';
    }
}
