package com.android.dietapp.model.parser;

import android.content.Context;

import androidx.annotation.NonNull;

import com.android.dietapp.ApplicationContext;
import com.android.dietapp.model.meal.Date;
import com.android.dietapp.model.meal.Meal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class JsonParser {
    @NonNull
    private static final String FOODS_ASSET_NAME = "foods.json";
    @NonNull
    private static final String FOODS_ARRAY_NAME = "foods";

    @NonNull
    private static final String MEALS_FILE_NAME = "meals.json";
    @NonNull
    private static final String MEALS_ARRAY_NAME = "meals";
    @NonNull
    private static final String DATE_NAME = "date";
    @NonNull
    private static final String LUNCH_LIST_ARRAY_NAME = "lunchList";
    @NonNull
    private static final String DINNER_LIST_ARRAY_NAME = "dinnerList";

    private static JSONObject jsonMeals;

    /**
     * -------- PUBLIC INTERFACE --------
     **/

    @NonNull
    public static List<String> loadFoodAsset() {
        try {
            final JSONObject jsonObject = JsonParser.loadJsonFromAssets(ApplicationContext.get(), FOODS_ASSET_NAME);
            final JSONArray foodsArray = jsonObject.getJSONArray(FOODS_ARRAY_NAME);
            return JsonParser.toStringList(foodsArray);
        } catch (JSONException e) {
            throw new RuntimeException("Unable to load foods: " + e.getLocalizedMessage());
        }
    }

    public static List<Meal> loadMealDataSet() {
        List<Meal> meals;

        try {
            JsonParser.jsonMeals = JsonParser.loadJsonFromFile(ApplicationContext.get(), MEALS_FILE_NAME);
            final JSONArray mealsArray = JsonParser.jsonMeals.getJSONArray(MEALS_ARRAY_NAME);
            meals = JsonParser.toMealsList(mealsArray);
        } catch (JSONException e) {
            // first time load: no data inside json file
            meals = new ArrayList<>();
        }
        return meals;
    }

    public static void saveMeals(@NonNull final List<Meal> meals) {
        JsonParser.jsonMeals = JsonParser.toJsonMeals(meals);
        JsonParser.storeJsonObject(ApplicationContext.get(), JsonParser.jsonMeals);
    }

    public static void initMealsFile() {
        try {
            final String emptyString = "{}";
            final String emptyStringName = "empty_string";

            final JSONObject emptyJsonObject = new JSONObject();
            emptyJsonObject.put(emptyStringName, emptyString);

            JsonParser.storeJsonObject(ApplicationContext.get(), emptyJsonObject);
        } catch (JSONException e) {
            throw new RuntimeException("Unable to init meals: " + e.getLocalizedMessage());
        }
    }

    /**
     * -------- CONVERSION FROM JSON FORMAT --------
     **/

    @NonNull
    private static List<Meal> toMealsList(@NonNull final JSONArray jsonArray) {
        try {
            final List<Meal> meals = new ArrayList<>(jsonArray.length());

            for (int i = 0; i < jsonArray.length(); i++) {
                final JSONObject jsonMeal = jsonArray.getJSONObject(i);
                final Meal meal = JsonParser.toMeal(jsonMeal);
                meals.add(meal);
            }
            return meals;
        } catch (JSONException e) {
            throw new RuntimeException("Unable to convert JSONObject to List<Meal>: " + e.getLocalizedMessage());
        }
    }

    @NonNull
    private static Meal toMeal(@NonNull final JSONObject jsonMeal) {
        try {
            final Date date = new Date(jsonMeal.getString(DATE_NAME));

            final JSONArray jsonLunchList = jsonMeal.getJSONArray(LUNCH_LIST_ARRAY_NAME);
            final List<String> lunchList = JsonParser.toStringList(jsonLunchList);

            final JSONArray jsonDinnerList = jsonMeal.getJSONArray(DINNER_LIST_ARRAY_NAME);
            final List<String> dinnerList = JsonParser.toStringList(jsonDinnerList);

            return new Meal(date, lunchList, dinnerList);
        } catch (JSONException e) {
            throw new RuntimeException("Unable to convert JSONObject to Meal: " + e.getLocalizedMessage());
        }
    }

    @NonNull
    private static List<String> toStringList(@NonNull final JSONArray jsonArray) {
        try {
            final List<String> list = new ArrayList<>(jsonArray.length());

            for (int i = 0; i < jsonArray.length(); i++)
                list.add(jsonArray.getString(i));
            return list;
        } catch (JSONException e) {
            throw new RuntimeException("Unable to convert JSONArray to List<String>: " + e.getLocalizedMessage());
        }
    }

    /**
     * -------- CONVERSION TO JSON FORMAT --------
     **/

    @NonNull
    private static JSONArray toJsonArray(@NonNull final List<String> stringList) {
        final JSONArray jsonArray = new JSONArray();

        for (final String string : stringList)
            jsonArray.put(string);

        return jsonArray;
    }

    @NonNull
    private static JSONObject toJsonMeal(@NonNull final Meal meal) {
        try {
            final JSONArray jsonLunchList = toJsonArray(meal.getLunchFoods());
            final JSONArray jsonDinnerList = toJsonArray(meal.getDinnerFoods());

            final JSONObject jsonMeal = new JSONObject();

            jsonMeal.put(DATE_NAME, meal.getDate());
            jsonMeal.put(LUNCH_LIST_ARRAY_NAME, jsonLunchList);
            jsonMeal.put(DINNER_LIST_ARRAY_NAME, jsonDinnerList);

            return jsonMeal;
        } catch (JSONException e) {
            throw new RuntimeException("Unable to convert Meal to JSONObject: " + e.getLocalizedMessage());
        }
    }

    @NonNull
    private static JSONObject toJsonMeals(@NonNull final List<Meal> meals) {
        try {
            final JSONObject jsonObject = new JSONObject();
            final JSONArray jsonArray = new JSONArray();

            for (final Meal meal : meals) {
                final JSONObject jsonMeal = JsonParser.toJsonMeal(meal);
                jsonArray.put(jsonMeal);
            }

            jsonObject.put(MEALS_ARRAY_NAME, jsonArray);
            return jsonObject;
        } catch (JSONException e) {
            throw new RuntimeException("Unable to convert List<Meal> to JSONObject: " + e.getLocalizedMessage());
        }
    }

    @NonNull
    private static JSONObject toJsonObject(@NonNull final String jsonString) {
        try {
            return new JSONObject(jsonString);
        } catch (JSONException e) {
            throw new RuntimeException("Unable to convert String to JSONObject: " + e.getLocalizedMessage());
        }
    }

    /**
     * -------- I/O FILE OPERATIONS --------
     **/

    private static void storeJsonObject(@NonNull final Context context, @NonNull final JSONObject jsonObject) {
        try (final FileOutputStream fos = context.openFileOutput(MEALS_FILE_NAME, Context.MODE_PRIVATE)) {
            final String fileContents = jsonObject.toString();
            fos.write(fileContents.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @NonNull
    @SuppressWarnings({"SameParameterValue", "ResultOfMethodCallIgnored"})
    private static JSONObject loadJsonFromAssets(@NonNull final Context context, @NonNull final String assetName) {
        try (final InputStream inputStream = context.getAssets().open(assetName)) {
            final int size = inputStream.available();
            final byte[] buffer = new byte[size];

            inputStream.read(buffer);
            final String jsonString = new String(buffer, StandardCharsets.UTF_8);

            return toJsonObject(jsonString);
        } catch (IOException e) {
            throw new RuntimeException("Unable to open InputStream: " + e.getLocalizedMessage());
        }
    }

    @NonNull
    @SuppressWarnings("SameParameterValue")
    private static JSONObject loadJsonFromFile(@NonNull final Context context, @NonNull final String fileName) {
        try (final InputStream inputStream = context.openFileInput(fileName)) {
            try (final InputStreamReader inputStreamReader = new InputStreamReader(inputStream)) {
                try (final BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                    final StringBuilder stringBuilder = new StringBuilder();
                    String receiveString;

                    while ((receiveString = bufferedReader.readLine()) != null)
                        stringBuilder.append(receiveString);

                    final String jsonString = stringBuilder.toString();
                    return JsonParser.toJsonObject(jsonString);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Unable to load JSONObject from file: " + e.getLocalizedMessage());
        }
    }
}
