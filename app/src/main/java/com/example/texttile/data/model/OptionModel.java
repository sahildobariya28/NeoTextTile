package com.example.texttile.data.model;

import javax.annotation.Nonnull;

public class OptionModel {

    private int id;
    private String name;
    private int image;

    public OptionModel(String name, int image) {
        this.name = name;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    @Nonnull
    @Override
    public String toString() {
        return "OptionModel{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", image=" + image +
                '}';
    }
}
