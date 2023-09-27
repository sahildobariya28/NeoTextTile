package com.example.texttile.presentation.ui.util;

public enum PermissionState {
    DESIGN_MASTER(0),
    SHADE_MASTER(1),
    YARN_MASTER(2),
    MACHINE_MASTER(3),
    JALA_MASTER(4),
    EMPLOYEE_MASTER(5),
    PARTY_MASTER(6),
    NEW_ORDER(7),
    PENDING(8),
    ON_MACHINE_PENDING(9),
    ON_MACHINE_COMPLETED(10),
    READY_TO_DISPATCH(11),
    WAREHOUSE(12),
    FINAL_DISPATCH(13),
    DELIVERED(14),
    DAMAGE(15),
    ORDER_TRACKER(16),
    ALLOT_PROGRAM(17),
    EMPLOYEE_ALLOTMENT(18),
    DAILY_READING(19),
    YARN_READING(20),
    READY_STOCK(21),
    STORAGE_FILE(22),
    SCANNER(23),
    IMPORT(24),
    EXPORT(25);

    private final int value;

    PermissionState(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
