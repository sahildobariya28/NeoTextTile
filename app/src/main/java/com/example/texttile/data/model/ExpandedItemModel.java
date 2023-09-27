package com.example.texttile.data.model;

public class ExpandedItemModel {
    boolean isExpanded = false;

    public ExpandedItemModel(boolean isExpanded) {
        this.isExpanded = isExpanded;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }
}
