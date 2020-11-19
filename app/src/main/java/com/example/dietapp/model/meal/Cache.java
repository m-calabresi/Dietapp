package com.example.dietapp.model.meal;

import java.util.ArrayList;
import java.util.List;

public class Cache {
    private static Cache instance;

    private Meal editableCachedMeal; // meal affected by edit operations (to be saved in case of conform operations)
    private Meal fixedCachedMeal; // copy of the original meal (to be restored in case of undo operations)

    private Cache() {
        this.invalidate();
    }

    public static Cache getInstance() {
        if (instance == null)
            instance = new Cache();
        return instance;
    }

    // invalidates the cache (all operations on the stored value will be lost)
    private void invalidate() {
        this.editableCachedMeal = null;
        this.fixedCachedMeal = null;
    }

    // initializes the cache with a clone of the given meal
    public void init(Meal meal) {
        this.editableCachedMeal = meal.clone();
        this.fixedCachedMeal = meal.clone();
    }

    // clones the given list and updates the cached meal
    public void updateLunchFoods(List<String> lunchFoods) {
        this.editableCachedMeal.setLunchFoods(new ArrayList<>(lunchFoods));
    }

    // clones the given list and updates the cached meal
    public void updateDinnerFoods(List<String> dinnerFoods) {
        this.editableCachedMeal.setDinnerFoods(new ArrayList<>(dinnerFoods));
    }

    // returned the last edited value without invalidating the cache
    public Meal getValue() {
        return this.editableCachedMeal;
    }

    // return the last edited value, then invalidates the cache
    public Meal commitAndGet() {
        final Meal result = this.editableCachedMeal;
        this.invalidate();
        return result;
    }

    // rolls back to initial inserted value: this is not returned
    public void rollBack() {
        this.invalidate();
    }

    // rolls back to initial inserted value: this is returned
    public Meal rollBackAndGet() {
        final Meal result = this.fixedCachedMeal;
        this.invalidate();
        return result;
    }
}
