package com.example.dietapp.ui;

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
import androidx.lifecycle.ViewModelProvider;

import com.example.dietapp.R;
import com.example.dietapp.model.meal.Meal;
import com.example.dietapp.viewmodel.MealSelectViewModel;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

public class MealSelectActivity extends AppCompatActivity {
    private TextView lunchText;
    private TextView dinnerText;

    private TextView lunchListTextView;
    private TextView dinnerListTextView;

    private Button lunchButton;
    private Button dinnerButton;

    private Button editLunchButton;
    private Button editDinnerButton;

    private ExtendedFloatingActionButton addExtendedFab;

    private MealSelectViewModel viewModel;

    private final ActivityResultLauncher<Intent> foodSelectActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    updateView();
                }
            });

    private final View.OnClickListener addExtendedFabOnClickListener = view -> {
        this.viewModel.finalizeSelectedMeal();
        final Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_select);

        this.viewModel = new ViewModelProvider(this).get(MealSelectViewModel.class);

        final String title = getTitleString(this.viewModel.getSelectedMeal());
        final Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);

        this.lunchText = findViewById(R.id.meal_lunch_text);
        this.dinnerText = findViewById(R.id.meal_dinner_text);

        this.editLunchButton = findViewById(R.id.edit_lunch_button);
        this.editLunchButton.setOnClickListener(view -> startActivityForResultWithExtra(view.getContext(), MealSelectViewModel.TYPE_EDIT_LIST_LUNCH));

        this.editDinnerButton = findViewById(R.id.edit_dinner_button);
        this.editDinnerButton.setOnClickListener(view -> startActivityForResultWithExtra(view.getContext(), MealSelectViewModel.TYPE_EDIT_LIST_DINNER));

        this.lunchListTextView = findViewById(R.id.meal_lunch_list);
        this.dinnerListTextView = findViewById(R.id.meal_dinner_list);

        this.lunchButton = findViewById(R.id.lunch_button);
        this.lunchButton.setOnClickListener(view -> startActivityForResultWithExtra(view.getContext(), MealSelectViewModel.TYPE_NEW_LIST_LUNCH));

        this.dinnerButton = findViewById(R.id.dinner_button);
        this.dinnerButton.setOnClickListener(view -> startActivityForResultWithExtra(view.getContext(), MealSelectViewModel.TYPE_NEW_LIST_DINNER));

        this.addExtendedFab = findViewById(R.id.add_extended_fab);
        this.addExtendedFab.setOnClickListener(addExtendedFabOnClickListener);
        this.addExtendedFab.setExtended(true);
        final String action = getActionString(this.viewModel.getSelectedMeal());
        this.addExtendedFab.setText(action);

        updateView();
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
        final Intent intent = new Intent(context, FoodSelectActivity.class);
        intent.putExtra(MealSelectViewModel.EXTRA_REQUEST_TYPE, requestType);
        this.foodSelectActivityResultLauncher.launch(intent);
    }

    private void updateView() {
        int addExtendedFabVisibility;

        int emptyLunchGroupVisibility; // visibility of views appearing when no lunch food is present
        int filledLunchGroupVisibility; // visibility of views appearing when lunch foods are present

        int emptyDinnerGroupVisibility; // visibility of views appearing when no dinner food is present
        int filledDinnerGroupVisibility; // visibility of views appearing when dinner foods are present

        if (this.viewModel.isMealReady()) {
            addExtendedFabVisibility = View.VISIBLE;
        } else {
            addExtendedFabVisibility = View.GONE;
        }

        if (this.viewModel.getSelectedMeal().getLunchFoods().isEmpty()) {
            emptyLunchGroupVisibility = View.VISIBLE;
            filledLunchGroupVisibility = View.GONE;
        } else {
            emptyLunchGroupVisibility = View.INVISIBLE;
            filledLunchGroupVisibility = View.VISIBLE;
        }

        if (this.viewModel.getSelectedMeal().getDinnerFoods().isEmpty()) {
            emptyDinnerGroupVisibility = View.VISIBLE;
            filledDinnerGroupVisibility = View.GONE;
        } else {
            emptyDinnerGroupVisibility = View.INVISIBLE;
            filledDinnerGroupVisibility = View.VISIBLE;
        }

        this.addExtendedFab.setVisibility(addExtendedFabVisibility);

        this.lunchText.setVisibility(filledLunchGroupVisibility);
        this.lunchListTextView.setVisibility(filledLunchGroupVisibility);
        this.editLunchButton.setVisibility(filledLunchGroupVisibility);
        this.lunchButton.setVisibility(emptyLunchGroupVisibility);

        this.dinnerText.setVisibility(filledDinnerGroupVisibility);
        this.dinnerListTextView.setVisibility(filledDinnerGroupVisibility);
        this.editDinnerButton.setVisibility(filledDinnerGroupVisibility);
        this.dinnerButton.setVisibility(emptyDinnerGroupVisibility);

        this.lunchListTextView.setText(this.viewModel.getSelectedMeal().getLunchFoods().toString());
        this.dinnerListTextView.setText(this.viewModel.getSelectedMeal().getDinnerFoods().toString());
    }

    private String getActionString(Meal editedMeal) {
        if (editedMeal.getDate().isToday())
            return getString(R.string.action_add);
        return getString(R.string.action_update);
    }

    private String getTitleString(Meal editedMeal) {
        if (editedMeal.getDate().isToday())
            return getString(R.string.action_add_today_meal);
        return editedMeal.getDate().toString();
    }

    private void onBackActionPerformed() {
        this.viewModel.clearSelectedMeal();

        final Intent intent = new Intent();
        this.setResult(RESULT_CANCELED, intent);
    }
}