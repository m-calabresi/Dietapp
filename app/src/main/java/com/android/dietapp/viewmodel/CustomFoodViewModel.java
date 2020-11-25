package com.android.dietapp.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

public class CustomFoodViewModel extends ViewModel {
    private String foodText;

    public CustomFoodViewModel() {
        this.foodText = "";
    }

    public String getFoodText() {
        return this.foodText;
    }

    public void setFoodText(@NonNull final String foodText) {
        this.foodText = foodText;
    }
}
