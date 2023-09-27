package com.example.texttile.data.model;

public class FListModel {
    int position;
    int f_no;

    public FListModel(int position, int f_no) {
        this.position = position;
        this.f_no = f_no;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getF_no() {
        return f_no;
    }

    public void setF_no(int f_no) {
        this.f_no = f_no;
    }
}
