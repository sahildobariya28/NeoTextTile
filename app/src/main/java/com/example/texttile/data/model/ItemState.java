package com.example.texttile.data.model;

public class ItemState {
    boolean isExpanded = false;
    boolean isSwiped = false;
    boolean isSelected = false;
    int position;

    public ItemState(boolean isExpanded, boolean isSwiped, boolean isSelected, int position) {
        this.isExpanded = isExpanded;
        this.isSwiped = isSwiped;
        this.isSelected = isSelected;
        this.position = position;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }

    public boolean isSwiped() {
        return isSwiped;
    }

    public void setSwiped(boolean swiped) {
        isSwiped = swiped;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return "ItemState{" +
                "isExpanded=" + isExpanded +
                ", isSwiped=" + isSwiped +
                ", isSelected=" + isSelected +
                ", position=" + position +
                '}';
    }
}
