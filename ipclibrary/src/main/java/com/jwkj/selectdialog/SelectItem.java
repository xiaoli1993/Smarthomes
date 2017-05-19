package com.jwkj.selectdialog;

/**
 * Created by dxs on 2016/1/14.
 */
public class SelectItem {
    private String name;
    private boolean isSelected=false;

    public SelectItem() {
    }

    public SelectItem(String name, boolean isSelected) {
        this.name = name;
        this.isSelected = isSelected;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }
}
