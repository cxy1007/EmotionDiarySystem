package com.example.emotiondiarysystem.bean;

public class Tag {
    private String name;
    private boolean isSelected;
    private boolean isCustom;

    public Tag(String name) {
        this.name = name;
        this.isSelected = false;
        this.isCustom = false;
    }

    public Tag(String name, boolean isCustom) {
        this.name = name;
        this.isSelected = false;
        this.isCustom = isCustom;
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

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isCustom() {
        return isCustom;
    }

    public void setCustom(boolean custom) {
        isCustom = custom;
    }
}
