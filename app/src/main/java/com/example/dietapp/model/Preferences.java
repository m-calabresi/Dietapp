package com.example.dietapp.model;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

import com.example.dietapp.ApplicationContext;

public class Preferences {
    private static Preferences instance;

    private Boolean firstTime = null;
    private static final String FIRST_TIME_PREF_NAME = "first_time_preference";

    private Integer themeIndex = null;
    private static final String THEME_PREF_NAME = "theme_preference";

    private Preferences() {
        if (isAppFirstTimeLaunch())
            initThemePreference();
    }

    public static Preferences getInstance() {
        if (instance == null)
            instance = new Preferences();
        return instance;
    }

    boolean isAppFirstTimeLaunch() {
        if (this.firstTime == null) {
            final SharedPreferences mPreferences = ApplicationContext.get().getSharedPreferences(FIRST_TIME_PREF_NAME, Context.MODE_PRIVATE);
            this.firstTime = mPreferences.getBoolean(FIRST_TIME_PREF_NAME, true);
            if (this.firstTime) {
                final SharedPreferences.Editor editor = mPreferences.edit();
                editor.putBoolean(FIRST_TIME_PREF_NAME, false);
                editor.apply();
            }
        }
        return this.firstTime;
    }

    private void initThemePreference() {
        this.setThemePreference(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
    }

    public void setThemePreference(int mode) {
        this.themeIndex = mode;

        final SharedPreferences mPreferences = ApplicationContext.get().getSharedPreferences(THEME_PREF_NAME, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.putInt(THEME_PREF_NAME, mode);
        editor.apply();

        AppCompatDelegate.setDefaultNightMode(mode);
    }

    public int getThemePreference() {
        if (this.themeIndex == null) {
            final SharedPreferences mPreferences = ApplicationContext.get().getSharedPreferences(THEME_PREF_NAME, Context.MODE_PRIVATE);
            this.themeIndex = mPreferences.getInt(THEME_PREF_NAME, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }
        return this.themeIndex;
    }
}