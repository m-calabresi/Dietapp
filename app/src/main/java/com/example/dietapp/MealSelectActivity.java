package com.example.dietapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.dietapp.data.SharedLiveDataRepository;
import com.example.dietapp.meal.Meal;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

public class MealSelectActivity extends AppCompatActivity {
    public static final int TYPE_NEW_LIST_LUNCH = 1;
    public static final int TYPE_NEW_LIST_DINNER = 2;
    public static final int TYPE_EDIT_LIST_LUNCH = 3;
    public static final int TYPE_EDIT_LIST_DINNER = 4;
    public static final int TYPE_ERROR = -1;

    public static final String EXTRA_REQUEST_TYPE = "com.example.dietapp.request_type";

    private int requestType;
    private int itemPosition;

    private TextView lunchText;
    private TextView dinnerText;

    private TextView lunchListTextView;
    private TextView dinnerListTextView;

    private Button lunchButton;
    private Button dinnerButton;

    private Button editLunchButton;
    private Button editDinnerButton;

    private ExtendedFloatingActionButton addExtendedFab;

    private Toolbar toolbar;

    private final ActivityResultLauncher<Intent> foodSelectActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    assert result.getData() != null;
                    final int requestType = result.getData().getIntExtra(EXTRA_REQUEST_TYPE, TYPE_ERROR);

                    switch (requestType) {
                        case TYPE_NEW_LIST_LUNCH:
                        case TYPE_EDIT_LIST_LUNCH:
                            this.lunchButton.setVisibility(View.INVISIBLE);
                            this.editLunchButton.setVisibility(View.VISIBLE);
                            this.lunchText.setVisibility(View.VISIBLE);

                            final String lunchFoodsStr = SharedLiveDataRepository.Cache.getValue().getLunchFoods().toString();
                            this.lunchListTextView.setText(lunchFoodsStr);

                            break;
                        case TYPE_NEW_LIST_DINNER:
                        case TYPE_EDIT_LIST_DINNER:
                            this.dinnerButton.setVisibility(View.INVISIBLE);
                            this.editDinnerButton.setVisibility(View.VISIBLE);
                            this.dinnerText.setVisibility(View.VISIBLE);

                            final String dinnerFoodsStr = SharedLiveDataRepository.Cache.getValue().getDinnerFoods().toString();
                            this.dinnerListTextView.setText(dinnerFoodsStr);

                            break;
                        default:
                            throw new NullPointerException("Invalid request code, activity returned in an unintended way");
                    }

                    boolean actionDone = this.lunchListTextView.getText().length() > 0 &&
                            this.dinnerListTextView.getText().length() > 0;

                    if (actionDone) {
                        addExtendedFab.setVisibility(View.VISIBLE);
                        addExtendedFab.extend();
                        addExtendedFab.setEnabled(true);
                    }

                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_select);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SharedLiveDataRepository.loadFoodList(this);
        requestType = getIntent().getIntExtra(MainActivity.EXTRA_REQUEST_TYPE, MainActivity.TYPE_ERROR);

        lunchText = findViewById(R.id.meal_lunch_text);
        dinnerText = findViewById(R.id.meal_dinner_text);

        editLunchButton = findViewById(R.id.edit_lunch_button);
        editLunchButton.setOnClickListener(view -> startActivityForResultWithExtra(view.getContext(), TYPE_EDIT_LIST_LUNCH));

        editDinnerButton = findViewById(R.id.edit_dinner_button);
        editDinnerButton.setOnClickListener(view -> startActivityForResultWithExtra(view.getContext(), TYPE_EDIT_LIST_DINNER));

        lunchListTextView = findViewById(R.id.meal_lunch_list);
        dinnerListTextView = findViewById(R.id.meal_dinner_list);

        lunchButton = findViewById(R.id.lunch_button);
        lunchButton.setOnClickListener(view -> startActivityForResultWithExtra(view.getContext(), TYPE_NEW_LIST_LUNCH));

        dinnerButton = findViewById(R.id.dinner_button);
        dinnerButton.setOnClickListener(view -> startActivityForResultWithExtra(view.getContext(), TYPE_NEW_LIST_DINNER));

        initCard();

        addExtendedFab = findViewById(R.id.add_extended_fab);
        addExtendedFab.setVisibility(View.GONE);
        addExtendedFab.setOnClickListener(view -> {
            // get the temporary meal and finalize it
            final Meal tempMeal = SharedLiveDataRepository.Cache.commitAndGet();

            switch (requestType) {
                case MainActivity.TYPE_TODAY_NEW:
                    SharedLiveDataRepository.saveNewMeal(view.getContext(), tempMeal);
                    break;
                case MainActivity.TYPE_EDIT:
                    SharedLiveDataRepository.updateMeal(view.getContext(), tempMeal);
                    break;
                default:
                    throw new NullPointerException("No request code found, activity started in an unexpected way");
            }

            SharedLiveDataRepository.Cache.rollBack();
            Intent intent = new Intent();
            intent.putExtra(MainActivity.EXTRA_REQUEST_TYPE, requestType);
            setResult(RESULT_OK, intent);
            finish();
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        this.onBackActionPerformed();
        this.finish();
        return true;
    }

    @Override
    public void onBackPressed() {
        this.onBackActionPerformed();
        super.onBackPressed();
    }

    private void startActivityForResultWithExtra(Context context, int requestType) {
        Intent intent = new Intent(context, FoodSelectActivity.class);
        intent.putExtra(EXTRA_REQUEST_TYPE, requestType);
        foodSelectActivityResultLauncher.launch(intent);
    }

    private void initCard() {
        String dateText;
        Meal editedMeal;
        switch (requestType) {
            case MainActivity.TYPE_TODAY_NEW:
                dateText = getString(R.string.action_add_today_meal);
                editedMeal = Meal.todayEmptyMeal();
                break;
            case MainActivity.TYPE_EDIT:
                itemPosition = getIntent().getIntExtra(MainActivity.EXTRA_CARD_POSITION, TYPE_ERROR);

                lunchText.setVisibility(View.VISIBLE);
                lunchButton.setVisibility(View.INVISIBLE);
                dinnerButton.setVisibility(View.INVISIBLE);

                dinnerText.setVisibility(View.VISIBLE);
                editLunchButton.setVisibility(View.VISIBLE);
                editDinnerButton.setVisibility(View.VISIBLE);

                // take the meal corresponding to the given position
                editedMeal = SharedLiveDataRepository.getMealsList().get(itemPosition);
                dateText = editedMeal.getDate().toString();

                // load temporary meal data into proper fields
                this.lunchListTextView.setText(editedMeal.getLunchFoods().toString());
                this.dinnerListTextView.setText(editedMeal.getDinnerFoods().toString());
                break;
            default:
                throw new NullPointerException("No request code found, activity started in an unexpected way");
        }

        toolbar.setTitle(dateText);

        // cache the new temporary meal
        SharedLiveDataRepository.Cache.init(editedMeal);
    }

    private void onBackActionPerformed() {
        if (requestType == MainActivity.TYPE_EDIT) {
            Meal oldMeal = SharedLiveDataRepository.Cache.rollBackAndGet();
            SharedLiveDataRepository.getMealsList().set(itemPosition, oldMeal);
        } else {
            SharedLiveDataRepository.Cache.rollBack();
        }
        Intent intent = new Intent();
        this.setResult(RESULT_CANCELED, intent);
    }
}