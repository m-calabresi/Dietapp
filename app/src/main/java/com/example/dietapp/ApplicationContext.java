package com.example.dietapp;

import android.app.Application;
import android.content.Context;

public class ApplicationContext extends Application {
    private static ApplicationContext instance;

    @Override
    public void onCreate() {
        instance = this;
        super.onCreate();
    }

    public static Context get() {
        return instance.getApplicationContext();
    }
}
