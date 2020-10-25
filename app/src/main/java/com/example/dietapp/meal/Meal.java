package com.example.dietapp.meal;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Meal {
    private final Date date;
    private List<String> lunchFoods;
    private List<String> dinnerFoods;

    public Meal(Date date, List<String> lunchFoods, List<String> dinnerFoods) {
        this.date = date;
        this.lunchFoods = lunchFoods;
        this.dinnerFoods = dinnerFoods;
    }

    public Date getDate() {
        return date;
    }

    public List<String> getDinnerFoods() {
        return dinnerFoods;
    }

    public List<String> getLunchFoods() {
        return lunchFoods;
    }

    public void setLunchFoods(List<String> lunchFoods) {
        this.lunchFoods = lunchFoods;
    }

    public void setDinnerFoods(List<String> dinnerFoods) {
        this.dinnerFoods = dinnerFoods;
    }

    public static Meal todayEmptyMeal() {
        return new Meal(Date.today(), new ArrayList<>(), new ArrayList<>());
    }

    @NonNull
    @SuppressWarnings("all")
    public Meal clone() {
        final List<String> clonedLunchFoods = new ArrayList<>(this.lunchFoods.size());
        clonedLunchFoods.addAll(this.lunchFoods);

        final List<String> clonedDinnerFoods = new ArrayList<>(this.dinnerFoods.size());
        clonedDinnerFoods.addAll(this.dinnerFoods);

        return new Meal(this.date, clonedLunchFoods, clonedDinnerFoods);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;

        Meal meal = (Meal) o;
        return this.date.equals(meal.getDate()) &&
                this.lunchFoods.equals(meal.lunchFoods) &&
                this.dinnerFoods.equals(meal.dinnerFoods);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.date, this.lunchFoods, this.dinnerFoods);
    }
}
