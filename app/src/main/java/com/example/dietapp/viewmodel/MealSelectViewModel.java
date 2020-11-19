package com.example.dietapp.viewmodel;

import androidx.lifecycle.ViewModel;

import com.example.dietapp.model.Model;
import com.example.dietapp.model.meal.Cache;
import com.example.dietapp.model.meal.Meal;

public class MealSelectViewModel extends ViewModel {
    public static final int TYPE_NEW_LIST_LUNCH = 1;
    public static final int TYPE_NEW_LIST_DINNER = 2;
    public static final int TYPE_EDIT_LIST_LUNCH = 3;
    public static final int TYPE_EDIT_LIST_DINNER = 4;
    public static final int TYPE_ERROR = -1;

    public static final String EXTRA_REQUEST_TYPE = "com.example.dietapp.request_type";

    private final Model model;
    private final Cache cache;

    public MealSelectViewModel() {
        this.model = Model.getInstance();
        this.cache = Cache.getInstance();
    }

    public Meal getSelectedMeal() {
        return this.cache.getValue();
    }

    public boolean isMealReady() {
        return !this.cache.getValue().getLunchFoods().isEmpty()
                && !this.cache.getValue().getDinnerFoods().isEmpty();
    }

    // get the temporary meal and finalize it
    public void finalizeSelectedMeal() {
        final Meal tempMeal = this.cache.commitAndGet();

        if (tempMeal.getDate().isToday())
            this.model.addNewMeal(0, tempMeal);
        else
            this.model.updateMeal(tempMeal);
    }

    public void clearSelectedMeal() {
        this.cache.rollBack();
    }
}
