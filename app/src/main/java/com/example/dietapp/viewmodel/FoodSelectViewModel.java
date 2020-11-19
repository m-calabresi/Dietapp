package com.example.dietapp.viewmodel;

import android.view.View;

import androidx.lifecycle.ViewModel;

import com.example.dietapp.model.Model;
import com.example.dietapp.model.SelectableFood;
import com.example.dietapp.model.meal.Cache;
import com.example.dietapp.viewmodel.livedata.MutableListLiveData;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FoodSelectViewModel extends ViewModel {
    public static final String EXTRA_CUSTOM_FOOD = "com.example.dietapp.custom_food";
    private int requestType;

    private final Model model;
    private final Cache cache;

    private MutableListLiveData<SelectableFood> selectableFoods;

    public FoodSelectViewModel() {
        this.model = Model.getInstance();
        this.model.loadFoods(true);

        this.cache = Cache.getInstance();
        this.selectableFoods = null;
    }

    public void setRequestType(int requestType) {
        this.requestType = requestType;
    }

    public int getRequestType() {
        return this.requestType;
    }

    public MutableListLiveData<SelectableFood> getFoods() {
        return this.selectableFoods;
    }

    public void insertCustomFood(String name) {
        final SelectableFood selectableFood = new SelectableFood(name, true);
        this.selectableFoods.addItem(selectableFood, 0);
    }

    public void initFoods() {
        switch (this.requestType) {
            case MealSelectViewModel.TYPE_NEW_LIST_LUNCH:
            case MealSelectViewModel.TYPE_NEW_LIST_DINNER:
                loadEmptySelection();
                break;
            case MealSelectViewModel.TYPE_EDIT_LIST_LUNCH:
                loadFilteredSelection(this.cache.getValue().getLunchFoods());
                break;
            case MealSelectViewModel.TYPE_EDIT_LIST_DINNER:
                loadFilteredSelection(this.cache.getValue().getDinnerFoods());
                break;
            default:
                throw new NullPointerException("No intent found, activity started in an unintended way");
        }
    }

    private void loadEmptySelection() {
        if (this.selectableFoods == null) {
            final List<SelectableFood> selectableFoods = model.getFoods()
                    .stream()
                    .map(food -> new SelectableFood(food, false))
                    .collect(Collectors.toList());

            this.selectableFoods = new MutableListLiveData<>(selectableFoods);
        }
    }

    private void loadFilteredSelection(List<String> foods) {
        if (this.selectableFoods == null) {
            final List<String> customFoods = new ArrayList<>();
            final List<String> selectedFoods = new ArrayList<>();

            // distinguish between custom foods and normal ones
            for (String food : foods) {
                if (model.getFoods().contains(food))
                    selectedFoods.add(food);
                else
                    customFoods.add(food);
            }

            // add normal foods properly selected
            final List<SelectableFood> allFoods = this.model.getFoods()
                    .stream()
                    .map(name -> new SelectableFood(name, selectedFoods.contains(name)))
                    .collect(Collectors.toList());

            // add all custom foods on top (all marked as selected)
            for (int i = customFoods.size() - 1; i >= 0; i--) {
                final SelectableFood customFood = new SelectableFood(customFoods.get(i), true);
                allFoods.add(0, customFood);
            }

            this.selectableFoods = new MutableListLiveData<>(allFoods);
        }
    }

    public void saveSelectedFoods() {
        // get the selected food names
        final List<String> selectedFoodNames = this.selectableFoods
                .asList()
                .stream()
                .filter(SelectableFood::isSelected)
                .map(SelectableFood::getName)
                .collect(Collectors.toList());

        switch (this.requestType) {
            case MealSelectViewModel.TYPE_NEW_LIST_LUNCH:
            case MealSelectViewModel.TYPE_EDIT_LIST_LUNCH:
                this.cache.updateLunchFoods(selectedFoodNames);
                break;
            case MealSelectViewModel.TYPE_NEW_LIST_DINNER:
            case MealSelectViewModel.TYPE_EDIT_LIST_DINNER:
                this.cache.updateDinnerFoods(selectedFoodNames);
                break;
            default:
                throw new NullPointerException("No intent found, activity started in an unintended way");
        }
    }

    public boolean isAnyItemSelected() {
        final int selectedItemsCount = (int) this.selectableFoods.asList()
                .stream()
                .filter(SelectableFood::isSelected)
                .count();

        return selectedItemsCount > 0;
    }
}
