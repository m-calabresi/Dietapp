package com.android.dietapp.model;

public class SelectableFood {
    private final String name;
    private boolean selected;

    public SelectableFood(String name, boolean selected) {
        this.name = name;
        this.selected = selected;
    }

    public String getName() {
        return this.name;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(final boolean selected) {
        this.selected = selected;
    }
}
