package com.android.dietapp.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.android.dietapp.R;
import com.android.dietapp.Utils;
import com.android.dietapp.viewmodel.MealSelectViewModel;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.List;

public class MealSelectActivity extends AppCompatActivity {
    private ChipGroup lunchChipGroup;
    private ChipGroup dinnerChipGroup;

    private Button lunchButton;
    private Button dinnerButton;

    private Button editLunchButton;
    private Button editDinnerButton;

    private ExtendedFloatingActionButton finalizeExtendedFap;

    private MealSelectViewModel viewModel;

    @NonNull
    private final ActivityResultLauncher<Intent> foodSelectActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    this.viewModel.notifyUserEditedMeal();
                    updateView();
                }
            });

    @NonNull
    private final View.OnClickListener finalizeExtendedFabOnClickListener = view -> {
        this.viewModel.finalizeSelectedMeal();
        final Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    };

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_select);

        this.viewModel = new ViewModelProvider(this).get(MealSelectViewModel.class);

        final String title = getTitleString();
        final Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);

        this.editLunchButton = findViewById(R.id.edit_lunch_button);
        this.editLunchButton.setOnClickListener(view -> startActivityForResultWithExtra(view.getContext(), MealSelectViewModel.TYPE_EDIT_LIST_LUNCH));

        this.editDinnerButton = findViewById(R.id.edit_dinner_button);
        this.editDinnerButton.setOnClickListener(view -> startActivityForResultWithExtra(view.getContext(), MealSelectViewModel.TYPE_EDIT_LIST_DINNER));

        this.lunchChipGroup = findViewById(R.id.lunch_chip_group);
        this.dinnerChipGroup = findViewById(R.id.dinner_chip_group);

        this.lunchButton = findViewById(R.id.lunch_button);
        this.lunchButton.setOnClickListener(view -> startActivityForResultWithExtra(view.getContext(), MealSelectViewModel.TYPE_NEW_LIST_LUNCH));

        this.dinnerButton = findViewById(R.id.dinner_button);
        this.dinnerButton.setOnClickListener(view -> startActivityForResultWithExtra(view.getContext(), MealSelectViewModel.TYPE_NEW_LIST_DINNER));

        this.finalizeExtendedFap = findViewById(R.id.add_extended_fab);
        this.finalizeExtendedFap.setOnClickListener(finalizeExtendedFabOnClickListener);
        this.finalizeExtendedFap.setExtended(true);
        final String action = getActionString();
        this.finalizeExtendedFap.setText(action);

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

    private void startActivityForResultWithExtra(@NonNull final Context context, final int requestType) {
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
            emptyLunchGroupVisibility = View.GONE;
            filledLunchGroupVisibility = View.VISIBLE;
        }

        if (this.viewModel.getSelectedMeal().getDinnerFoods().isEmpty()) {
            emptyDinnerGroupVisibility = View.VISIBLE;
            filledDinnerGroupVisibility = View.GONE;
        } else {
            emptyDinnerGroupVisibility = View.GONE;
            filledDinnerGroupVisibility = View.VISIBLE;
        }

        this.finalizeExtendedFap.setVisibility(addExtendedFabVisibility);

        this.lunchChipGroup.setVisibility(filledLunchGroupVisibility);
        this.editLunchButton.setVisibility(filledLunchGroupVisibility);
        this.lunchButton.setVisibility(emptyLunchGroupVisibility);

        this.dinnerChipGroup.setVisibility(filledDinnerGroupVisibility);
        this.editDinnerButton.setVisibility(filledDinnerGroupVisibility);
        this.dinnerButton.setVisibility(emptyDinnerGroupVisibility);

        final List<String> selectedLunchFoods = this.viewModel.getSelectedMeal().getLunchFoods();
        final List<String> selectedDinnerFoods = this.viewModel.getSelectedMeal().getDinnerFoods();

        Utils.setFoodChips(this.lunchChipGroup, selectedLunchFoods, false);
        Utils.setFoodChips(this.dinnerChipGroup, selectedDinnerFoods, false);
    }

    @NonNull
    private String getActionString() {
        if (this.viewModel.isTodayNewMeal())
            return getString(R.string.action_add);
        return getString(R.string.action_update);
    }

    @NonNull
    private String getTitleString() {
        if (this.viewModel.isTodayMeal())
            return getString(R.string.action_add_today_meal);
        return this.viewModel.getSelectedMeal().getDate().toString();
    }

    private void onBackActionPerformed() {
        this.viewModel.clearSelectedMeal();

        final Intent intent = new Intent();
        this.setResult(RESULT_CANCELED, intent);
    }
}