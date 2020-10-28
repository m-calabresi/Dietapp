package com.example.dietapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dietapp.data.SharedLiveDataRepository;
import com.example.dietapp.food.FoodCheckViewAdapter;
import com.example.dietapp.food.RecyclerViewClickListener;
import com.example.dietapp.food.SelectableFood;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class FoodSelectActivity extends AppCompatActivity implements RecyclerViewClickListener {
    public static final String EXTRA_CUSTOM_FOOD = "com.example.dietapp.custom_food";

    private final List<SelectableFood> foodsDataSet = new ArrayList<>();

    private RecyclerView recyclerView; // TODO fix boxes unchecked, after theme changed
    private FoodCheckViewAdapter adapter;

    private Button addButton;

    private int requestType;

    private final ActivityResultLauncher<Intent> addActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    final String resultData = Objects.requireNonNull(result.getData()).getStringExtra(EXTRA_CUSTOM_FOOD);

                    final SelectableFood food = new SelectableFood(resultData, true);
                    foodsDataSet.add(0, food);

                    adapter.notifyItemInserted(0);

                    //scroll to top, in order to see the newly added item
                    Objects.requireNonNull(recyclerView.getLayoutManager()).scrollToPosition(0);
                    // notify recyclerview that this item has been clicked (for selecting the checkbox state)
                    recyclerViewItemClicked(adapter.selectedItemsCount());
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_select);

        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        requestType = getIntent().getIntExtra(MealSelectActivity.EXTRA_REQUEST_TYPE, MealSelectActivity.TYPE_ERROR);

        // initialize the food set
        initFoodDataSet();

        recyclerView = findViewById(R.id.foods_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new FoodCheckViewAdapter(foodsDataSet, this);
        recyclerView.setAdapter(adapter);

        final Button customAddButton = findViewById(R.id.custom_add_button);
        customAddButton.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), AddCustomFoodActivity.class);
            addActivityResultLauncher.launch(intent);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_menu, menu);

        final ConstraintLayout constraintLayout = (ConstraintLayout) menu.findItem(R.id.item_action_button).getActionView();
        addButton = constraintLayout.findViewById(R.id.action_button);
        addButton.setText(R.string.action_add);
        addButton.setOnClickListener(view -> {
            final List<String> selectedFoods = foodsDataSet.stream()
                    .filter(SelectableFood::isSelected)
                    .map(SelectableFood::getFood)
                    .collect(Collectors.toList());

            switch (requestType) {
                case MealSelectActivity.TYPE_NEW_LIST_LUNCH:
                case MealSelectActivity.TYPE_EDIT_LIST_LUNCH:
                    SharedLiveDataRepository.Cache.setValueLunchList(selectedFoods);
                    break;
                case MealSelectActivity.TYPE_NEW_LIST_DINNER:
                case MealSelectActivity.TYPE_EDIT_LIST_DINNER:
                    SharedLiveDataRepository.Cache.setValueDinnerList(selectedFoods);
                    break;
                default:
                    throw new NullPointerException("No intent found, activity started in an unintended way");
            }
            Intent intent = new Intent();
            intent.putExtra(MealSelectActivity.EXTRA_REQUEST_TYPE, requestType);
            setResult(RESULT_OK, intent);
            finish();
        });
        return true;
    }

    @Override
    public void recyclerViewItemClicked(int selectedItemsCount) {
        if (selectedItemsCount > 0) {
            if (addButton.getVisibility() != View.VISIBLE) {
                addButton.setVisibility(View.VISIBLE);
            }
        } else {
            if (addButton.getVisibility() == View.VISIBLE) {
                addButton.setVisibility(View.GONE);
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
        switch (requestType) {
            case MealSelectActivity.TYPE_NEW_LIST_LUNCH:
            case MealSelectActivity.TYPE_NEW_LIST_DINNER:
                final List<String> allFoods = SharedLiveDataRepository.getFoodList();
                foodsDataSet.addAll(toDefaultSelectableFoods(allFoods));
                break;
            case MealSelectActivity.TYPE_EDIT_LIST_LUNCH:
                final List<String> lunchFoods = SharedLiveDataRepository.Cache.getValue().getLunchFoods();
                foodsDataSet.addAll(toCustomSelectableFood(lunchFoods));
                break;
            case MealSelectActivity.TYPE_EDIT_LIST_DINNER:
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