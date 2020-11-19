package com.example.dietapp.model.meal;

import androidx.annotation.NonNull;

import java.util.Calendar;
import java.util.Objects;

public class Date {
    private static final String SEPARATOR = "-";
    private final String date;

    public Date(String date) {
        this.date = date;
    }

    @NonNull
    @Override
    public String toString() {
        return this.date;
    }

    public static Date today() {
        final Calendar calendar = Calendar.getInstance();

        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        final int month = calendar.get(Calendar.MONTH) + 1;
        final int year = calendar.get(Calendar.YEAR);

        final String dateStr = day + SEPARATOR + month + SEPARATOR + year;
        return new Date(dateStr);
    }

    public boolean isToday() {
        return this.equals(Date.today());
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || this.getClass() != o.getClass()) return false;

        Date date = (Date) o;
        return this.equals(date.toString());
    }

    public boolean equals(String date) {
        return this.date.equals(date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.date);
    }
}
