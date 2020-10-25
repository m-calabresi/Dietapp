package com.example.dietapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.dietapp.data.SharedLiveDataRepository;
import com.example.dietapp.food.FoodCheckViewAdapter;
import com.example.dietapp.food.RecyclerViewClickListener;
import com.example.dietapp.food.SelectableFood;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class FoodSelectActivity extends AppCompatActivity implements RecyclerViewClickListener {
    private static final int ADD_ACTIVITY_REQUEST_CODE = 1;
    public static final String ADD_ACTIVITY_RESULT_NAME = "custom_food";

    private final List<SelectableFood> foodsDataSet = new ArrayList<>();

    private RecyclerView recyclerView;
    private FoodCheckViewAdapter adapter;

    private ExtendedFloatingActionButton extendedFloatingActionButton;

    private int requestCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_select);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setElevation(0);

        requestCode = getIntent().getIntExtra(MealSelectActivity.MEAL_SELECT_ACTIVITY_REQUEST_CODE, MealSelectActivity.DATA_ERROR_CODE);

        // initialize the food set
        initFoodDataSet();

        recyclerView = findViewById(R.id.foods_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (extendedFloatingActionButton.isEnabled()) {
                    if (dy < 0 && !extendedFloatingActionButton.isExtended())
                        extendedFloatingActionButton.extend();
                    else if (dy > 0 && extendedFloatingActionButton.isExtended())
                        extendedFloatingActionButton.shrink();
                }
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new FoodCheckViewAdapter(foodsDataSet, this);
        recyclerView.setAdapter(adapter);

        extendedFloatingActionButton = findViewById(R.id.done_extended_fab);
        extendedFloatingActionButton.setEnabled(false);
        extendedFloatingActionButton.setExtended(false);
        extendedFloatingActionButton.setOnClickListener(view -> {
            final List<String> selectedFoods = foodsDataSet.stream().filter(SelectableFood::isSelected).map(SelectableFood::getFood).collect(Collectors.toList());

            switch (requestCode) {
                case MealSelectActivity.LUNCH_LIST_NEW_REQUEST_CODE:
                case MealSelectActivity.LUNCH_LIST_EDIT_REQUEST_CODE:
                    SharedLiveDataRepository.Cache.setValueLunchList(selectedFoods);
                    break;
                case MealSelectActivity.DINNER_LIST_NEW_REQUEST_CODE:
                case MealSelectActivity.DINNER_LIST_EDIT_REQUEST_CODE:
                    SharedLiveDataRepository.Cache.setValueDinnerList(selectedFoods);
                    break;
                default:
                    throw new NullPointerException("No intent found, activity started in an unintended way");
            }
            setResult(RESULT_OK);
            finish();
        });

        final Button customAddButton = findViewById(R.id.custom_add_button);
        customAddButton.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), AddCustomFoodActivity.class);
            startActivityForResult(intent, ADD_ACTIVITY_REQUEST_CODE); // TODO replace this
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                final String resultData = Objects.requireNonNull(data).getStringExtra(ADD_ACTIVITY_RESULT_NAME);

                final SelectableFood food = new SelectableFood(resultData, true);
                foodsDataSet.add(0, food);

                adapter.notifyItemInserted(0);
                adapter.notifyItemRangeChanged(0, foodsDataSet.size());

                //scroll to top, in order to see the newly added item
                Objects.requireNonNull(recyclerView.getLayoutManager()).scrollToPosition(0);
                // notify recyclerview that this item has been clicked (for selecting the checkbox state)
                recyclerViewItemClicked(adapter.selectedItemsCount());
            }
        }
    }

    @Override
    public void recyclerViewItemClicked(int selectedItemsCount) {
        if (selectedItemsCount > 0) {
            if (extendedFloatingActionButton.getVisibility() != View.VISIBLE) {
                extendedFloatingActionButton.show();
            }
        } else {
            if (extendedFloatingActionButton.getVisibility() == View.VISIBLE) {
                extendedFloatingActionButton.hide();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.onBackActionPerformed();
    }

    @Override
    public boolean onSupportNavigateUp() {
        this.onBackActionPerformed();
        this.finish();
        return true;
    }

    private void initFoodDataSet() {
        switch (requestCode) {
            case MealSelectActivity.LUNCH_LIST_NEW_REQUEST_CODE:
            case MealSelectActivity.DINNER_LIST_NEW_REQUEST_CODE:
                final List<String> allFoods = SharedLiveDataRepository.getFoodList();
                foodsDataSet.addAll(toDefaultSelectableFoods(allFoods));
                break;
            case MealSelectActivity.LUNCH_LIST_EDIT_REQUEST_CODE:
                final List<String> lunchFoods = SharedLiveDataRepository.Cache.getValue().getLunchFoods();
                foodsDataSet.addAll(toCustomSelectableFood(lunchFoods));
                break;
            case MealSelectActivity.DINNER_LIST_EDIT_REQUEST_CODE:
                final List<String> dinnerFoods = SharedLiveDataRepository.Cache.getValue().getDinnerFoods();
                foodsDataSet.addAll(toCustomSelectableFood(dinnerFoods));
                break;
            default:
                throw new NullPointerException("No intent found, activity started in an unintended way");
        }

    }

    private List<SelectableFood> toCustomSelectableFood(List<String> foods) {
        final List<SelectableFood> result = new ArrayList<>();

        final List<String> allFoods = SharedLiveDataRepository.getFoodList();
        int lastCustomItemIndex = -1;

        // check whether the list contains custom foods
        for (String food : foods) {
            if (!allFoods.contains(food))
                lastCustomItemIndex++;
            else
                break;
        }

        // add eventual custom food to data set
        for (int i = 0; i <= lastCustomItemIndex; i++)
            result.add(new SelectableFood(foods.get(i), true));

        // insert all the other foods accordingly
        for (String food : allFoods)
            result.add(new SelectableFood(food, foods.contains(food)));

        return result;
    }

    private List<SelectableFood> toDefaultSelectableFoods(List<String> foods) {
        final List<SelectableFood> selectableFoods = new ArrayList<>(foods.size());

        for (String food : foods)
            selectableFoods.add(new SelectableFood(food, false));
        return selectableFoods;
    }

    private void onBackActionPerformed() {
        Intent intent = new Intent();
        setResult(RESULT_CANCELED, intent);
    }
}