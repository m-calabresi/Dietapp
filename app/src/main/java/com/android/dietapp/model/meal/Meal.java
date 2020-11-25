package com.android.dietapp.model.meal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Meal {
    private final Date date;
    private List<String> lunchFoods;
    private List<String> dinnerFoods;

    public Meal(final Date date, final List<String> lunchFoods, final List<String> dinnerFoods) {
        this.date = date;
        this.lunchFoods = lunchFoods;
        this.dinnerFoods = dinnerFoods;
    }

    public Date getDate() {
        return this.date;
    }

    public List<String> getDinnerFoods() {
        return this.dinnerFoods;
    }

    public List<String> getLunchFoods() {
        return this.lunchFoods;
    }

    void setLunchFoods(final List<String> lunchFoods) {
        this.lunchFoods = lunchFoods;
    }

    void setDinnerFoods(final List<String> dinnerFoods) {
        this.dinnerFoods = dinnerFoods;
    }

    @NonNull
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
    public boolean equals(@Nullable final Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;

        final Meal meal = (Meal) o;
        return this.date.equals(meal.getDate()) &&
                this.lunchFoods.equals(meal.lunchFoods) &&
                this.dinnerFoods.equals(meal.dinnerFoods);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.date, this.lunchFoods, this.dinnerFoods);
    }
}
