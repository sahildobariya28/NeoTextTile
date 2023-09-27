package com.example.texttile.data.model;

import java.io.Serializable;
import java.util.ArrayList;

public class OrderStatusModel implements Serializable {

    private int pending;
    private int onMachinePending;
    private int onMachineCompleted;
    private int onReadyToDispatch;
    private int onWarehouse;
    private int onFinalDispatch;
    private int onDelivered;
    private int onDamage;
    private int total;

    public OrderStatusModel(int pending, int onMachinePending, int onMachineCompleted, int onReadyToDispatch, int onWarehouse, int onFinalDispatch, int onDelivered, int onDamage, int total) {
        this.pending = pending;
        this.onMachinePending = onMachinePending;
        this.onMachineCompleted = onMachineCompleted;
        this.onReadyToDispatch = onReadyToDispatch;
        this.onWarehouse = onWarehouse;
        this.onFinalDispatch = onFinalDispatch;
        this.onDelivered = onDelivered;
        this.onDamage = onDamage;
        this.total = total;
    }

    public OrderStatusModel() {
    }

    public int getOnMachinePending() {
        return onMachinePending;
    }

    public void setOnMachinePending(int onMachine) {
        this.onMachinePending = onMachine;
    }

    public int getOnMachineCompleted() {
        return onMachineCompleted;
    }

    public void setOnMachineCompleted(int onMachineCompleted) {
        this.onMachineCompleted = onMachineCompleted;
    }

    public int getOnReadyToDispatch() {
        return onReadyToDispatch;
    }

    public void setOnReadyToDispatch(int ready_to_dispatch) {
        this.onReadyToDispatch = ready_to_dispatch;
    }

    public int getPending() {
        return pending;
    }

    public void setPending(int pending) {
        this.pending = pending;
    }

    public int getOnWarehouse() {
        return onWarehouse;
    }

    public void setOnWarehouse(int warehouse) {
        this.onWarehouse = warehouse;
    }

    public int getOnFinalDispatch() {
        return onFinalDispatch;
    }

    public void setOnFinalDispatch(int final_dispatch) {
        this.onFinalDispatch = final_dispatch;
    }

    public int getOnDelivered() {
        return onDelivered;
    }

    public void setOnDelivered(int delivered) {
        this.onDelivered = delivered;
    }

    public int getOnDamage() {
        return onDamage;
    }

    public void setOnDamage(int damage) {
        this.onDamage = damage;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    @Override
    public String toString() {
        return "OrderStatusModel{" +
                "onMachine=" + onMachinePending +
                ", onMachineCompleted=" + onMachineCompleted +
                ", ready_to_dispatch=" + onReadyToDispatch +
                ", padding=" + pending +
                ", warehouse=" + onWarehouse +
                ", final_dispatch=" + onFinalDispatch +
                ", damage=" + onDamage+
                ", total=" + total +
                '}';
    }
}
