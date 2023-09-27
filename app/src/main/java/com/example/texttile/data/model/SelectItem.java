package com.example.texttile.data.model;

public class SelectItem {

    String name;
    int position;
    boolean isSelected;

    public SelectItem(String name, int position, boolean isSelected) {
        this.name = name;
        this.position = position;
        this.isSelected = isSelected;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    @Override
    public String toString() {
        return "SelectItem{" +
                "name='" + name + '\'' +
                ", position=" + position +
                ", isSelected=" + isSelected +
                '}';
    }
}
