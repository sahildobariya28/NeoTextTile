package com.example.texttile.data.model;

import java.io.Serializable;
import java.util.ArrayList;

public class EmployeeMasterModel implements Serializable {

    private String id;
    private String employee_name;
    private String employee_photo;
    private String id_proof;
    private String phone_no;
    private String alter_phone_no;
    private String account_no;
    private String ifsc_code;
    private String bank_name;
    private String bank_holder_name;
    private String employee_allot_status = "null";
    private ArrayList<String> machine_name;



    public EmployeeMasterModel(String id, String employee_name, String employee_photo, String id_proof, String phone_no,String alter_phone_no,String account_no,String ifsc_code,String bank_name,String bank_holder_name) {
        this.id = id;
        this.employee_name = employee_name;
        this.employee_photo = employee_photo;
        this.id_proof = id_proof;
        this.phone_no = phone_no;
        this.alter_phone_no = alter_phone_no;
        this.account_no = account_no;
        this.ifsc_code = ifsc_code;
        this.bank_name = bank_name;
        this.bank_holder_name = bank_holder_name;
    }

    public EmployeeMasterModel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmployee_name() {
        return employee_name;
    }

    public void setEmployee_name(String employee_name) {
        this.employee_name = employee_name;
    }

    public String getEmployee_photo() {
        return employee_photo;
    }

    public void setEmployee_photo(String employee_photo) {
        this.employee_photo = employee_photo;
    }

    public String getId_proof() {
        return id_proof;
    }

    public void setId_proof(String id_proof) {
        this.id_proof = id_proof;
    }

    public String getEmployee_allot_status() {
        return employee_allot_status;
    }

    public void setEmployee_allot_status(String employee_allot_status) {
        this.employee_allot_status = employee_allot_status;
    }

    public ArrayList<String> getMachine_name() {
        return machine_name;
    }

    public void setMachine_name(ArrayList<String> machine_name) {
        this.machine_name = machine_name;
    }

    public String getPhone_no() {
        return phone_no;
    }

    public void setPhone_no(String phone_no) {
        this.phone_no = phone_no;
    }

    public String getAlter_phone_no() {
        return alter_phone_no;
    }

    public void setAlter_phone_no(String alter_phone_no) {
        this.alter_phone_no = alter_phone_no;
    }

    public String getAccount_no() {
        return account_no;
    }

    public void setAccount_no(String account_no) {
        this.account_no = account_no;
    }

    public String getIfsc_code() {
        return ifsc_code;
    }

    public void setIfsc_code(String ifsc_code) {
        this.ifsc_code = ifsc_code;
    }

    public String getBank_name() {
        return bank_name;
    }

    public void setBank_name(String bank_name) {
        this.bank_name = bank_name;
    }

    public String getBank_holder_name() {
        return bank_holder_name;
    }

    public void setBank_holder_name(String bank_holder_name) {
        this.bank_holder_name = bank_holder_name;
    }

    @Override
    public String toString() {
        return "EmployeeMasterModel{" +
                "id='" + id + '\'' +
                ", employee_name='" + employee_name + '\'' +
                ", employee_photo='" + employee_photo + '\'' +
                ", id_proof='" + id_proof + '\'' +
                ", phone_no='" + phone_no + '\'' +
                ", alter_phone_no='" + alter_phone_no + '\'' +
                ", account_no='" + account_no + '\'' +
                ", ifsc_code='" + ifsc_code + '\'' +
                ", bank_name='" + bank_name + '\'' +
                ", bank_holder_name='" + bank_holder_name + '\'' +
                ", employee_allot_status='" + employee_allot_status + '\'' +
                ", machine_name='" + machine_name + '\'' +
                '}';
    }
}
