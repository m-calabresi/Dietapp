package com.example.dietapp.data;

import android.content.Context;

import com.example.dietapp.meal.Date;
import com.example.dietapp.meal.Meal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

class JsonParser {
    private static final String FOODS_ASSET_NAME = "foods.json";
    private static final String FOODS_ARRAY_NAME = "foods";

    private static final String MEALS_FILE_NAME = "meals.json";
    private static final String MEALS_ARRAY_NAME = "meals";
    private static final String DATE_NAME = "date";
    private static final String LUNCH_LIST_ARRAY_NAME = "lunchList";
    private static final String DINNER_LIST_ARRAY_NAME = "dinnerList";

    private static JSONObject jsonMeals;

    /**
     * -------- PUBLIC INTERFACE --------
     **/

    public static List<String> loadFoodAsset(Context context) {
        final JSONObject jsonObject = JsonParser.loadJsonFromAssets(context, FOODS_ASSET_NAME);

        JSONArray foodsArray = null;
        try {
            foodsArray = jsonObject.getJSONArray(FOODS_ARRAY_NAME);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        assert foodsArray != null;
        return JsonParser.toStringList(foodsArray);
    }

    public static List<Meal> loadMealDataSet(Context context) {
        List<Meal> meals;

        try {
            JsonParser.jsonMeals = JsonParser.loadJsonFromFile(context, MEALS_FILE_NAME);
            final JSONArray mealsArray = JsonParser.jsonMeals.getJSONArray(MEALS_ARRAY_NAME);
            meals = JsonParser.toMealsList(mealsArray);
        } catch (JSONException e) {
            // first time load: no data inside json file
            meals = new ArrayList<>();
        }
        return meals;
    }

    public static void saveMeals(Context context, List<Meal> meals) {
        JsonParser.jsonMeals = JsonParser.toJsonMeals(meals);
        JsonParser.storeJsonObject(context, JsonParser.jsonMeals);
    }

    @SuppressWarnings("all")
    public static void initMealsFile(Context context) {
        final File file = new File(context.getFilesDir() + File.separator + MEALS_FILE_NAME);
        try {
            file.createNewFile();

            final FileWriter writer = new FileWriter(file);
            writer.append("{}");
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * -------- CONVERSION FROM JSON FORMAT --------
     **/

    private static List<Meal> toMealsList(JSONArray jsonArray) {
        final List<Meal> meals = new ArrayList<>(jsonArray.length());

        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                final JSONObject jsonMeal = jsonArray.getJSONObject(i);
                final Meal meal = JsonParser.toMeal(jsonMeal);
                meals.add(meal);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return meals;
    }

    private static Meal toMeal(JSONObject jsonMeal) {
        Meal meal = null;
        try {
            final Date date = new Date(jsonMeal.getString(DATE_NAME));

            final JSONArray jsonLunchList = jsonMeal.getJSONArray(LUNCH_LIST_ARRAY_NAME);
            final List<String> lunchList = JsonParser.toStringList(jsonLunchList);

            final JSONArray jsonDinnerList = jsonMeal.getJSONArray(DINNER_LIST_ARRAY_NAME);
            final List<String> dinnerList = JsonParser.toStringList(jsonDinnerList);

            meal = new Meal(date, lunchList, dinnerList);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return meal;
    }

    private static List<String> toStringList(JSONArray jsonArray) {
        final List<String> list = new ArrayList<>(jsonArray.length());

        try {
            for (int i = 0; i < jsonArray.length(); i++)
                list.add(jsonArray.getString(i));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return list;
    }

    /**
     * -------- CONVERSION TO JSON FORMAT --------
     **/

    private static JSONArray toJsonArray(List<String> stringList) {
        final JSONArray jsonArray = new JSONArray();

        for (final String string : stringList)
            jsonArray.put(string);

        return jsonArray;
    }

    private static JSONObject toJsonMeal(Meal meal) {
        final JSONArray jsonLunchList = toJsonArray(meal.getLunchFoods());
        final JSONArray jsonDinnerList = toJsonArray(meal.getDinnerFoods());

        final JSONObject jsonMeal = new JSONObject();
        try {
            jsonMeal.put(DATE_NAME, meal.getDate());
            jsonMeal.put(LUNCH_LIST_ARRAY_NAME, jsonLunchList);
            jsonMeal.put(DINNER_LIST_ARRAY_NAME, jsonDinnerList);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonMeal;
    }

    private static JSONObject toJsonMeals(List<Meal> meals) {
        final JSONObject jsonObject = new JSONObject();
        final JSONArray jsonArray = new JSONArray();

        for (final Meal meal : meals) {
            final JSONObject jsonMeal = JsonParser.toJsonMeal(meal);
            jsonArray.put(jsonMeal);
        }

        try {
            jsonObject.put(MEALS_ARRAY_NAME, jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    private static JSONObject toJsonObject(String jsonString) {
        JSONObject jsonObject = null;

        try {
            jsonObject = new JSONObject(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    /**
     * -------- I/O FILE OPERATIONS --------
     **/

    private static void storeJsonObject(Context context, JSONObject jsonObject) {
        try (final FileOutputStream fos = context.openFileOutput(MEALS_FILE_NAME, Context.MODE_PRIVATE)) {
            final String fileContents = jsonObject.toString();
            fos.write(fileContents.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static JSONObject loadJsonFromAssets(Context context, String assetName) {
        String jsonString = "";

        try (final InputStream inputStream = context.getAssets().open(assetName)) {
            final int size = inputStream.available();
            final byte[] buffer = new byte[size];

            inputStream.read(buffer);
            jsonString = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return toJsonObject(jsonString);
    }

    private static JSONObject loadJsonFromFile(Context context, String fileName) {
        String jsonString = "";

        try (final InputStream inputStream = context.openFileInput(fileName)) {
            if (inputStream != null) {
                try (final InputStreamReader inputStreamReader = new InputStreamReader(inputStream)) {
                    try (final BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                        final StringBuilder stringBuilder = new StringBuilder();
                        String receiveString;

                        while ((receiveString = bufferedReader.readLine()) != null)
                            stringBuilder.append(receiveString);

                        jsonString = stringBuilder.toString();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return JsonParser.toJsonObject(jsonString);
    }
}
