package com.android.dietapp.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import com.android.dietapp.model.Model;
import com.android.dietapp.model.meal.Cache;
import com.android.dietapp.model.meal.Date;
import com.android.dietapp.model.meal.Meal;
import com.android.dietapp.viewmodel.livedata.MutableListLiveData;

public class MainViewModel extends ViewModel {
    private final Model model;
    private final Cache cache;

    private boolean isToday;
    private boolean extended;

    public MainViewModel() {
        this.model = Model.getInstance();
        this.model.loadMeals(true);
        this.cache = Cache.getInstance();

        this.extended = true;
    }

    @NonNull
    public MutableListLiveData<Meal> getMeals() {
        return this.model.getMeals();
    }

    public int getThemePreference() {
        return this.model.preferences.getThemePreference();
    }

    public void setThemePreference(final int mode) {
        this.model.preferences.setThemePreference(mode);
    }

    public boolean canAddTodayMeal() {
        if (this.model.getMeals().asList().isEmpty())
            return true;

        final boolean todayMealAlreadyAdded = this.model.getMeals()
                .get(0).getDate().equals(Date.today());

        return !todayMealAlreadyAdded;
    }

    public boolean isSharedMealToday() {
        return this.isToday;
    }

    public void shareNewMeal() {
        final Meal meal = Meal.todayEmptyMeal();
        this.cache.init(meal);
        this.isToday = true;
    }

    public void shareSelectedMeal(final int selectedMealPosition) {
        final Meal meal = this.model.getMeals().get(selectedMealPosition);
        this.cache.init(meal);
        this.isToday = false;
    }

    public boolean getButtonState() {
        return this.extended;
    }

    public void setButtonState(final boolean extended) {
        this.extended = extended;
    }
}
