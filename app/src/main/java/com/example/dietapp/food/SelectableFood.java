package com.example.dietapp.food;

public class SelectableFood {
    private final String food;
    private boolean selected;

    public SelectableFood(String food, boolean selected) {
        this.food = food;
        this.selected = selected;
    }

    public String getFood() {
        return this.food;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
