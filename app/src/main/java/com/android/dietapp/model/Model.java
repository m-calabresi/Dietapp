package com.android.dietapp.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.dietapp.model.meal.Meal;
import com.android.dietapp.model.parser.JsonParser;
import com.android.dietapp.viewmodel.livedata.MutableListLiveData;

import java.util.List;

public class Model {
    private static Model instance;
    @Nullable
    private MutableListLiveData<Meal> meals;
    @Nullable
    private List<String> foods;

    public final Preferences preferences;

    private Model() {
        this.meals = null;
        this.foods = null;
        this.preferences = Preferences.getInstance();

        if (preferences.isAppFirstTimeLaunch())
            JsonParser.initMealsFile();
    }

    public static Model getInstance() {
        if (instance == null)
            instance = new Model();
        return instance;
    }

    public void loadFoods(final boolean force) {
        if (this.foods == null || force)
            this.foods = JsonParser.loadFoodAsset();
    }

    @NonNull
    public List<String> getFoods() {
        if (this.foods == null)
            loadFoods(true);
        return this.foods;
    }

    @NonNull
    public MutableListLiveData<Meal> getMeals() {
        if (this.meals == null)
            loadMeals(true);
        return this.meals;
    }

    public void loadMeals(final boolean force) {
        if (this.meals == null || force)
            this.meals = new MutableListLiveData<>(JsonParser.loadMealDataSet());
    }

    public void addNewMeal(final int position, @NonNull final Meal meal) {
        if (this.meals == null)
            throw new NullPointerException("Attempt to add new Meal to empty list");

        this.meals.addItem(meal, position);
        JsonParser.saveMeals(this.meals.asList());
    }

    public void updateMeal(@NonNull final Meal meal) {
        if (this.meals == null)
            throw new NullPointerException("Attempt to update Meal in an empty list");

        for (int i = 0; i < this.meals.size(); i++) {
            if (meal.getDate().equals(this.meals.get(i).getDate())) {
                this.meals.setItem(meal, i);

                JsonParser.saveMeals(this.meals.asList());
                return;
            }
        }
        throw new NullPointerException("No Meal found with date '" + meal.getDate() + "'");
    }
}
