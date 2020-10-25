package com.example.dietapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dietapp.data.SharedLiveDataRepository;
import com.example.dietapp.meal.Meal;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.Objects;

public class MealSelectActivity extends AppCompatActivity {
    public static final int LUNCH_LIST_NEW_REQUEST_CODE = 1;
    public static final int DINNER_LIST_NEW_REQUEST_CODE = 2;
    public static final int LUNCH_LIST_EDIT_REQUEST_CODE = 3;
    public static final int DINNER_LIST_EDIT_REQUEST_CODE = 4;

    public static final int DATA_ERROR_CODE = -1;
    public static final String MEAL_SELECT_ACTIVITY_REQUEST_CODE = "meal_select_activity_request_code";

    private int requestCode;
    private int itemPosition;

    private TextView lunchText;
    private TextView dinnerText;

    private TextView lunchListTextView;
    private TextView dinnerListTextView;

    private Button lunchButton;
    private Button dinnerButton;

    private ImageButton editLunchButton;
    private ImageButton editDinnerButton;

    private ExtendedFloatingActionButton addExtendedFab;

    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_select);

        actionBar = Objects.requireNonNull(getSupportActionBar());
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setElevation(0);

        SharedLiveDataRepository.loadFoodList(this);
        requestCode = getIntent().getIntExtra(MainActivity.MAIN_ACTIVITY_REQUEST_CODE, MealSelectActivity.DATA_ERROR_CODE);

        lunchText = findViewById(R.id.meal_lunch_text);
        dinnerText = findViewById(R.id.meal_dinner_text);

        editLunchButton = findViewById(R.id.edit_lunch_button);
        editLunchButton.setOnClickListener(view -> startActivityForResultWithExtra(view.getContext(), LUNCH_LIST_EDIT_REQUEST_CODE));

        editDinnerButton = findViewById(R.id.edit_dinner_button);
        editDinnerButton.setOnClickListener(view -> startActivityForResultWithExtra(view.getContext(), DINNER_LIST_EDIT_REQUEST_CODE));

        lunchListTextView = findViewById(R.id.meal_lunch_list);
        dinnerListTextView = findViewById(R.id.meal_dinner_list);

        lunchButton = findViewById(R.id.lunch_button);
        lunchButton.setOnClickListener(view -> startActivityForResultWithExtra(view.getContext(), LUNCH_LIST_NEW_REQUEST_CODE));

        dinnerButton = findViewById(R.id.dinner_button);
        dinnerButton.setOnClickListener(view -> startActivityForResultWithExtra(view.getContext(), DINNER_LIST_NEW_REQUEST_CODE));

        initCard();

        addExtendedFab = findViewById(R.id.add_extended_fab);
        addExtendedFab.setVisibility(View.GONE);
        addExtendedFab.setOnClickListener(view -> {
            // get the temporary meal and finalize it
            final Meal tempMeal = SharedLiveDataRepository.Cache.commitAndGet();

            switch (requestCode) {
                case MainActivity.TODAY_NEW_REQUEST_CODE:
                    SharedLiveDataRepository.saveNewMeal(view.getContext(), tempMeal);
                    break;
                case MainActivity.EDIT_REQUEST_CODE:
                    SharedLiveDataRepository.updateMeal(view.getContext(), tempMeal);
                    break;
                default:
                    throw new NullPointerException("No request code found, activity started in an unexpected way");
            }

            SharedLiveDataRepository.Cache.rollBack();
            setResult(RESULT_OK);
            finish();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case LUNCH_LIST_NEW_REQUEST_CODE:
                case LUNCH_LIST_EDIT_REQUEST_CODE:
                    this.lunchButton.setVisibility(View.INVISIBLE);
                    this.editLunchButton.setVisibility(View.VISIBLE);
                    this.lunchText.setVisibility(View.VISIBLE);

                    final String lunchFoodsStr = SharedLiveDataRepository.Cache.getValue().getLunchFoods().toString();
                    this.lunchListTextView.setText(lunchFoodsStr);

                    break;
                case DINNER_LIST_NEW_REQUEST_CODE:
                case DINNER_LIST_EDIT_REQUEST_CODE:
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
    }

    @Override
    public boolean onSupportNavigateUp() {
        this.onBackActionPerformed();
        this.finish();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.onBackActionPerformed();
    }

    private void startActivityForResultWithExtra(Context context, int extra) {
        Intent intent = new Intent(context, FoodSelectActivity.class);
        intent.putExtra(MEAL_SELECT_ACTIVITY_REQUEST_CODE, extra);
        startActivityForResult(intent, extra); // TODO replace this
    }

    private void initCard() {
        String dateText;
        Meal editedMeal;
        switch (requestCode) {
            case MainActivity.TODAY_NEW_REQUEST_CODE:
                dateText = getString(R.string.action_add_today_meal);
                editedMeal = Meal.todayEmptyMeal();
                break;
            case MainActivity.EDIT_REQUEST_CODE:
                itemPosition = getIntent().getIntExtra(MainActivity.MAIN_ACTIVITY_CARD_POSITION, DATA_ERROR_CODE);

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

        // cache the new temporary meal
        actionBar.setTitle(dateText);
        SharedLiveDataRepository.Cache.init(editedMeal);
    }

    private void onBackActionPerformed() {
        if (requestCode == MainActivity.EDIT_REQUEST_CODE) {
            Meal oldMeal = SharedLiveDataRepository.Cache.rollBackAndGet();
            SharedLiveDataRepository.getMealsList().set(itemPosition, oldMeal);
        } else {
            SharedLiveDataRepository.Cache.rollBack();
        }
        Intent intent = new Intent();
        this.setResult(Activity.RESULT_CANCELED, intent);
    }
}