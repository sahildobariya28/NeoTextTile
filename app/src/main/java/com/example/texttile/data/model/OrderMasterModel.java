package com.example.texttile.data.model;

import javax.annotation.Nonnull;

import java.io.Serializable;

public class OrderMasterModel implements Serializable {

    private String id;
    private String order_type;
    private String order_no;
    private String sub_order_no;
    private String date;
    private String party_name;
    private String design_no;
    private String shade_no;
    private int quantity;
    private String price;
    private String approx_Delivery_date;
    private OrderStatusModel orderStatusModel;

    public OrderMasterModel() {
    }

    public OrderMasterModel(String id, String order_type, String order_no, String sub_order_no, String date, String party_name, String design_no, String shade_no, int qty, String approx_delivery_date, OrderStatusModel orderStatusModel, String price) {
        this.id = id;
        this.order_type = order_type;
        this.order_no = order_no;
        this.sub_order_no = sub_order_no;
        this.date = date;
        this.party_name = party_name;
        this.design_no = design_no;
        this.shade_no = shade_no;
        this.quantity = qty;
        this.approx_Delivery_date = approx_delivery_date;
        this.orderStatusModel = orderStatusModel;
        this.price = price;

    }

    public OrderStatusModel getOrderStatusModel() {
        return orderStatusModel;
    }

    public void setOrderStatusModel(OrderStatusModel orderStatusModel) {
        this.orderStatusModel = orderStatusModel;
    }

    public String getOrder_no() {
        return order_no;
    }

    public void setOrder_no(String order_no) {
        this.order_no = order_no;
    }

    public String getSub_order_no() {
        return sub_order_no;
    }

    public void setSub_order_no(String sub_order_no) {
        this.sub_order_no = sub_order_no;
    }

    public String getOrder_type() {
        return order_type;
    }

    public void setOrder_type(String order_type) {
        this.order_type = order_type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getParty_name() {
        return party_name;
    }

    public void setParty_name(String party_name) {
        this.party_name = party_name;
    }

    public String getDesign_no() {
        return design_no;
    }

    public void setDesign_no(String design_no) {
        this.design_no = design_no;
    }

    public String getShade_no() {
        return shade_no;
    }

    public void setShade_no(String shade_no) {
        this.shade_no = shade_no;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getApprox_Delivery_date() {
        return approx_Delivery_date;
    }

    public void setApprox_Delivery_date(String approx_Delivery_date) {
        this.approx_Delivery_date = approx_Delivery_date;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    @Nonnull
    @Override
    public String toString() {
        return "OrderModel{" +
                "id='" + id + '\'' +
                ", order_type='" + order_type + '\'' +
                ", order_no='" + order_no + '\'' +
                ", sub_order_no='" + sub_order_no + '\'' +
                ", date='" + date + '\'' +
                ", party_name='" + party_name + '\'' +
                ", design_no='" + design_no + '\'' +
                ", approx_Delivery_date='" + approx_Delivery_date + '\'' +
                ", orderStatusModel=" + orderStatusModel.toString() +
                '}';
    }
}
