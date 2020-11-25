package com.android.dietapp.model.meal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Calendar;
import java.util.Objects;

public class Date {
    @NonNull
    private static final String SEPARATOR = "-";
    private final String date;

    public Date(final String date) {
        this.date = date;
    }

    @NonNull
    @Override
    public String toString() {
        return this.date;
    }

    @NonNull
    public static Date today() {
        final Calendar calendar = Calendar.getInstance();

        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        final int month = calendar.get(Calendar.MONTH) + 1;
        final int year = calendar.get(Calendar.YEAR);

        final String dateStr = day + SEPARATOR + month + SEPARATOR + year;
        return new Date(dateStr);
    }

    @Override
    public boolean equals(@Nullable final Object o) {
        if (o == null || this.getClass() != o.getClass()) return false;

        final Date date = (Date) o;
        return this.equals(date.toString());
    }

    public boolean equals(final String date) {
        return this.date.equals(date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.date);
    }
}
