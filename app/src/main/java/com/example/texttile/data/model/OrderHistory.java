package com.example.texttile.data.model;

import java.io.Serializable;

public class OrderHistory implements Serializable {

    private String id;
    private String source;
    private String destination;
    private String orderNo;
    private String subOrderNo;
    private String shadeNo;
    private String designNo;
    private int qty;
    private String dateTime;
    private String reason;
    private String slipNo;

    public OrderHistory(String id, String source, String destination, String orderNo, String subOrderNo, String shadeNo, String designNo, int qty, String dateTime) {
        this.id = id;
        this.source = source;
        this.destination = destination;
        this.orderNo = orderNo;
        this.subOrderNo = subOrderNo;
        this.shadeNo = shadeNo;
        this.designNo = designNo;
        this.qty = qty;
        this.dateTime = dateTime;
    }

    public OrderHistory() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getSubOrderNo() {
        return subOrderNo;
    }

    public void setSubOrderNo(String subOrderNo) {
        this.subOrderNo = subOrderNo;
    }

    public String getShadeNo() {
        return shadeNo;
    }

    public void setShadeNo(String shadeNo) {
        this.shadeNo = shadeNo;
    }

    public String getDesignNo() {
        return designNo;
    }

    public void setDesignNo(String designNo) {
        this.designNo = designNo;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getReason() {
        return reason;
    }

    public String getSlipNo() {
        return slipNo;
    }

    public void setSlipNo(String slipNo) {
        this.slipNo = slipNo;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    public String toString() {
        return "OrderHistory{" +
                "id='" + id + '\'' +
                ", source='" + source + '\'' +
                ", destination='" + destination + '\'' +
                ", orderNo='" + orderNo + '\'' +
                ", subOrderNo='" + subOrderNo + '\'' +
                ", shadeNo='" + shadeNo + '\'' +
                ", designNo='" + designNo + '\'' +
                ", qty=" + qty +
                ", dateTime='" + dateTime + '\'' +
                ", reason='" + reason + '\'' +
                ", slipNo='" + slipNo + '\'' +
                '}';
    }
}
