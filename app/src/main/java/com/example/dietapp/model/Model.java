package com.example.dietapp.model;

import com.example.dietapp.model.meal.Meal;
import com.example.dietapp.model.parser.JsonParser;
import com.example.dietapp.viewmodel.livedata.MutableListLiveData;

import java.util.List;

public class Model {
    private static Model instance;
    private MutableListLiveData<Meal> meals;
    private List<String> foods;

    public Preferences preferences;

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

    public void loadFoods(boolean force) {
        if (this.foods == null || force)
            this.foods = JsonParser.loadFoodAsset();
    }

    public List<String> getFoods() {
        if (this.foods == null)
            loadFoods(true);
        return this.foods;
    }

    public MutableListLiveData<Meal> getMeals() {
        if (this.meals == null)
            loadMeals(true);
        return this.meals;
    }

    public void loadMeals(boolean force) {
        if (this.meals == null || force)
            this.meals = new MutableListLiveData<>(JsonParser.loadMealDataSet());
    }

    public void addNewMeal(int position, Meal meal) {
        this.meals.addItem(meal, position);
        JsonParser.saveMeals(this.meals.asList());
    }

    public void updateMeal(Meal meal) {
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
