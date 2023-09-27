package com.example.texttile.data.model;

import javax.annotation.Nonnull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

public class UserDataModel implements Serializable {
    private String id;
    private String u_name;
    private String f_name;
    private String type;
    private String phone_number;
    private String password;
    private ArrayList<PermissionCategory> permissionList;

    public UserDataModel() {
    }


    public UserDataModel(String id, String u_name, String f_name, String type, String phone_number, String password) {
        this.id = id;
        this.u_name = u_name;
        this.f_name = f_name;
        this.type = type;
        this.phone_number = phone_number;
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getU_name() {
        return u_name;
    }

    public void setU_name(String u_name) {
        this.u_name = u_name;
    }

    public String getF_name() {
        return f_name;
    }

    public void setF_name(String f_name) {
        this.f_name = f_name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ArrayList<PermissionCategory> getPermissionList() {
        return permissionList;
    }

    public void setPermissionList(ArrayList<PermissionCategory> permissionList) {
        this.permissionList = permissionList;
    }

    @Nonnull
    @Override
    public String toString() {
        return "UserDataModel{" +
                "id='" + id + '\'' +
                ", u_name='" + u_name + '\'' +
                ", f_name='" + f_name + '\'' +
                ", type='" + type + '\'' +
                ", phone_number='" + phone_number + '\'' +
                ", password='" + password + '\'' +
                ", permissionList=" + permissionList.toString() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserDataModel that = (UserDataModel) o;
        return Objects.equals(id, that.id) && Objects.equals(u_name, that.u_name) && Objects.equals(f_name, that.f_name) && Objects.equals(type, that.type) && Objects.equals(phone_number, that.phone_number) && Objects.equals(password, that.password) && Objects.equals(permissionList, that.permissionList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, u_name, f_name, type, phone_number, password, permissionList);
    }
}
