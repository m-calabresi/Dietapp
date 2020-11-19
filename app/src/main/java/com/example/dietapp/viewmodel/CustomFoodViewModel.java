package com.example.dietapp.viewmodel;

import androidx.lifecycle.ViewModel;

public class CustomFoodViewModel extends ViewModel {
    private String foodText;

    public CustomFoodViewModel() {
        this.foodText = "";
    }

    public String getFoodText() {
        return this.foodText;
    }

    public void setFoodText(String foodText) {
        this.foodText = foodText;
    }
}
