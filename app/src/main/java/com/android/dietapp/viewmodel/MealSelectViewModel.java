package com.android.dietapp.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import com.android.dietapp.model.Model;
import com.android.dietapp.model.meal.Cache;
import com.android.dietapp.model.meal.Date;
import com.android.dietapp.model.meal.Meal;

public class MealSelectViewModel extends ViewModel {
    public static final int TYPE_NEW_LIST_LUNCH = 1;
    public static final int TYPE_NEW_LIST_DINNER = 2;
    public static final int TYPE_EDIT_LIST_LUNCH = 3;
    public static final int TYPE_EDIT_LIST_DINNER = 4;
    public static final int TYPE_ERROR = -1;

    @NonNull
    public static final String EXTRA_REQUEST_TYPE = "com.example.dietapp.request_type";

    private final Model model;
    private final Cache cache;

    private boolean hasUserEditedMeal; // whether this is the first time a user sees the edit screen or not
    private final boolean todayNewMeal; // whether the current meal is the new created or the edited one (both today's and another day's)
    private final boolean todayMeal; // whether the current meal has today's date or not

    public MealSelectViewModel() {
        this.model = Model.getInstance();
        this.cache = Cache.getInstance();

        this.hasUserEditedMeal = false;

        // today's new meal has both food lists empty
        this.todayNewMeal = this.cache.getValue().getLunchFoods().size() == 0;
        this.todayMeal = this.cache.getValue().getDate().equals(Date.today());
    }

    public void notifyUserEditedMeal() {
        this.hasUserEditedMeal = true;
    }

    @NonNull
    public Meal getSelectedMeal() {
        return this.cache.getValue();
    }

    public boolean isTodayNewMeal() {
        return this.todayNewMeal;
    }

    public boolean isTodayMeal() {
        return this.todayMeal;
    }

    public boolean isMealReady() {
        return !this.cache.getValue().getLunchFoods().isEmpty()
                && this.hasUserEditedMeal
                && !this.cache.getValue().getDinnerFoods().isEmpty();
    }

    // get the temporary meal and finalize it
    public void finalizeSelectedMeal() {
        final Meal tempMeal = this.cache.commitAndGet();

        if (isTodayNewMeal())
            this.model.addNewMeal(0, tempMeal);
        else
            this.model.updateMeal(tempMeal);
    }

    public void clearSelectedMeal() {
        this.cache.rollBack();
    }
}
