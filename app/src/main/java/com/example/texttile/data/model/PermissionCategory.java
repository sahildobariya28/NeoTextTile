package com.example.texttile.data.model;

import javax.annotation.Nonnull;

import java.io.Serializable;

public class PermissionCategory implements Serializable {

    private int id;
    private String name;
    private boolean read;
    private boolean insert;
    private boolean update;
    private boolean delete;
    private boolean isPrice;

    public PermissionCategory() {
    }

    public PermissionCategory(int id, String name, boolean read, boolean insert, boolean update, boolean delete, boolean isPrice) {
        this.id = id;
        this.name = name;
        this.read = read;
        this.insert = insert;
        this.update = update;
        this.delete = delete;
        this.isPrice = isPrice;
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

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public boolean isInsert() {
        return insert;
    }

    public void setInsert(boolean insert) {
        this.insert = insert;
    }

    public boolean isUpdate() {
        return update;
    }

    public void setUpdate(boolean update) {
        this.update = update;
    }

    public boolean isDelete() {
        return delete;
    }

    public void setDelete(boolean delete) {
        this.delete = delete;
    }

    public boolean isPrice() {
        return isPrice;
    }

    public void setPrice(boolean price) {
        isPrice = price;
    }

    @Nonnull
    @Override
    public String toString() {
        return "PermissionCategory{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", read=" + read +
                ", insert=" + insert +
                ", update=" + update +
                ", delete=" + delete +
                '}';
    }
}
