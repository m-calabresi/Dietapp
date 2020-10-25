package com.example.dietapp.data;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.MutableLiveData;

import com.example.dietapp.meal.Meal;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SharedLiveDataRepository {
    private static final MutableLiveData<List<Meal>> mealListLiveData = new MutableLiveData<>();
    private static final MutableLiveData<List<String>> foodListLiveData = new MutableLiveData<>();

    /**
     * -------- CACHE OPERATIONS --------
     **/

    public static class Cache {
        private static Meal editableCachedMeal; // meal affected by edit operations (to be saved in case of conform operations)
        private static Meal fixedCachedMeal; // copy of the original meal (to be restored in case of undo operations)

        private static void invalidate() {
            Cache.editableCachedMeal = null;
            Cache.fixedCachedMeal = null;
        }

        public static void init(Meal meal) {
            Cache.editableCachedMeal = meal.clone();
            Cache.fixedCachedMeal = meal.clone();
        }

        public static void setValueLunchList(List<String> lunchList) {
            Cache.editableCachedMeal.setLunchFoods(new ArrayList<>(lunchList));
        }

        public static void setValueDinnerList(List<String> dinnerList) {
            Cache.editableCachedMeal.setDinnerFoods(new ArrayList<>(dinnerList));
        }

        public static Meal getValue() {
            return Cache.editableCachedMeal;
        }

        public static Meal commitAndGet() {
            Meal result = Cache.editableCachedMeal;
            Cache.invalidate();
            return result;
        }

        public static void rollBack() {
            Cache.invalidate();
        }

        public static Meal rollBackAndGet() {
            Meal result = Cache.fixedCachedMeal;
            Cache.invalidate();
            return result;
        }
    }

    public static class Preferences {
        private static Boolean firstTime = null;
        private static final String FIRST_TIME_PREF_NAME = "first_time_preference";

        private static Integer themeIndex = null;
        private static final String THEME_PREF_NAME = "theme_preference";

        private static boolean isAppFirstTimeLaunch(Context context) {
            if (firstTime == null) {
                SharedPreferences mPreferences = context.getSharedPreferences(FIRST_TIME_PREF_NAME, Context.MODE_PRIVATE);
                firstTime = mPreferences.getBoolean(FIRST_TIME_PREF_NAME, true);
                if (firstTime) {
                    SharedPreferences.Editor editor = mPreferences.edit();
                    editor.putBoolean(FIRST_TIME_PREF_NAME, false);
                    editor.apply();
                }
            }
            return firstTime;
        }

        private static void initThemePreference(Context context) {
            Preferences.setThemePreference(context, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }

        public static void setThemePreference(Context context, int mode) {
            Preferences.themeIndex = mode;

            SharedPreferences mPreferences = context.getSharedPreferences(THEME_PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = mPreferences.edit();
            editor.putInt(THEME_PREF_NAME, mode);
            editor.apply();

            AppCompatDelegate.setDefaultNightMode(mode);
        }

        public static int getThemePreference(Context context) {
            if (themeIndex == null) {
                SharedPreferences mPreferences = context.getSharedPreferences(THEME_PREF_NAME, Context.MODE_PRIVATE);
                Preferences.themeIndex = mPreferences.getInt(THEME_PREF_NAME, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
            }
            return Preferences.themeIndex;
        }
    }

    /**
     * -------- GETTERS --------
     **/

    public static List<Meal> getMealsList() {
        return Objects.requireNonNull(SharedLiveDataRepository.mealListLiveData.getValue());
    }

    public static List<String> getFoodList() {
        return Objects.requireNonNull(SharedLiveDataRepository.foodListLiveData.getValue());
    }

    /**
     * -------- LOAD OPERATIONS --------
     **/

    public static void loadMealList(Context context) {
        final List<Meal> meals = JsonParser.loadMealDataSet(context);
        SharedLiveDataRepository.mealListLiveData.setValue(meals);
    }

    public static void loadFoodList(Context context) {
        final List<String> foods = JsonParser.loadFoodAsset(context);
        SharedLiveDataRepository.foodListLiveData.postValue(foods);
    }

    /**
     * -------- EDIT OPERATIONS --------
     **/

    public static void saveNewMeal(Context context, Meal meal) {
        SharedLiveDataRepository.getMealsList().add(meal);
        JsonParser.saveMeals(context, SharedLiveDataRepository.getMealsList());
    }

    public static void updateMeal(Context context, Meal meal) {
        final List<Meal> meals = SharedLiveDataRepository.getMealsList();
        boolean update = false;

        for (int i = 0; i < meals.size(); i++) {
            final Meal m = meals.get(i);
            if (m.getDate().equals(meal.getDate())) {
                meals.set(i, meal);
                update = true;
                break;
            }
        }

        if (!update)
            throw new NullPointerException("No Meal found with date '" + meal.getDate() + "'");
        JsonParser.saveMeals(context, SharedLiveDataRepository.getMealsList());
    }

    /**
     * -------- PREFERENCES TIME OPERATIONS --------
     **/

    public static void initEnvironment(Context context) {
        if (Preferences.isAppFirstTimeLaunch(context)) {
            SharedLiveDataRepository.Preferences.initThemePreference(context);
            JsonParser.initMealsFile(context);
        }
    }
}
