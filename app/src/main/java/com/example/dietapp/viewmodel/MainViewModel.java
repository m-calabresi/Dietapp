package com.example.dietapp.viewmodel;

import androidx.lifecycle.ViewModel;

import com.example.dietapp.model.Model;
import com.example.dietapp.model.meal.Cache;
import com.example.dietapp.model.meal.Date;
import com.example.dietapp.model.meal.Meal;
import com.example.dietapp.viewmodel.livedata.MutableListLiveData;

public class MainViewModel extends ViewModel {
    private final Model model;
    private final Cache cache;

    private boolean isToday;

    public MainViewModel() {
        this.model = Model.getInstance();
        this.model.loadMeals(true);
        this.cache = Cache.getInstance();
    }

    public MutableListLiveData<Meal> getMeals() {
        return this.model.getMeals();
    }

    public int getThemePreference() {
        return this.model.preferences.getThemePreference();
    }

    public void setThemePreference(int mode) {
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

    public void shareSelectedMeal(int selectedMealPosition) {
        final Meal meal = this.model.getMeals().get(selectedMealPosition);
        this.cache.init(meal);
        this.isToday = false;
    }
}
