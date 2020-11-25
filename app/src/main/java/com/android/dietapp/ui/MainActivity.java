package com.android.dietapp.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.android.dietapp.R;
import com.android.dietapp.adapter.MealsCardViewAdapter;
import com.android.dietapp.model.meal.Meal;
import com.android.dietapp.viewmodel.MainViewModel;
import com.android.dietapp.viewmodel.livedata.MutableListHolder;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements LifecycleOwner, RecyclerViewClickListener {
    private MainViewModel viewModel;

    private RecyclerView recyclerView;
    private MealsCardViewAdapter adapter;
    private ConstraintLayout emptyViewLayout;
    private ExtendedFloatingActionButton todayButton;

    @NonNull
    private final Observer<MutableListHolder<Meal>> mealsUpdateObserver = new Observer<MutableListHolder<Meal>>() {
        @Override
        public void onChanged(@NonNull final MutableListHolder<Meal> listMutableListHolder) {
            listMutableListHolder.notifyChange(adapter);
        }
    };

    @NonNull
    private final ActivityResultLauncher<Intent> mealSelectActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    final boolean isToday = this.viewModel.isSharedMealToday();
                    final String confirmationMessage = getConfirmationMessage(isToday);
                    displaySnackBar(confirmationMessage);
                    selectVisibleView();
                }
            });

    @NonNull
    private final View.OnClickListener todayButtonOnCLickListener = view -> {
        if (this.viewModel.canAddTodayMeal()) {
            this.viewModel.shareNewMeal();
            startActivityForResult();
        } else {
            displayErrorAlertDialog();
        }
    };

    @NonNull
    private final RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            if (dy < 0 && !todayButton.isExtended()) {
                todayButton.extend();
                viewModel.setButtonState(true);
            } else if (dy > 0 && todayButton.isExtended()) {
                todayButton.shrink();
                viewModel.setButtonState(false);
            }
        }
    };

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        this.viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        this.viewModel.getMeals().observe(this, this.mealsUpdateObserver);

        // remove splash screen
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // sets default theme
        final int mode = this.viewModel.getThemePreference();
        AppCompatDelegate.setDefaultNightMode(mode);

        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.todayButton = findViewById(R.id.add_today_extended_fab);
        this.todayButton.setOnClickListener(this.todayButtonOnCLickListener);

        this.emptyViewLayout = findViewById(R.id.empty_view_layout);

        this.recyclerView = findViewById(R.id.days_recycler_view);
        ((SimpleItemAnimator) Objects.requireNonNull(this.recyclerView.getItemAnimator())).setSupportsChangeAnimations(false);
        this.recyclerView.setHasFixedSize(true);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        this.recyclerView.addOnScrollListener(this.onScrollListener);

        this.adapter = new MealsCardViewAdapter(this.viewModel.getMeals(), this);
        this.recyclerView.setAdapter(this.adapter);

        selectVisibleView();
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull final MenuItem item) {
        if (item.getItemId() == R.id.action_settings_theme) {
            final int itemPosition = this.toPositionIndex(this.viewModel.getThemePreference());
            displayThemesAlertDialog(itemPosition);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void recyclerViewItemClicked(final int selectedItemPosition) {
        this.viewModel.shareSelectedMeal(selectedItemPosition);
        startActivityForResult();
    }

    @NonNull
    private String getConfirmationMessage(final boolean todayNew) {
        if (todayNew)
            return getString(R.string.text_new_item_added);
        return getString(R.string.text_item_edited);

    }

    private void selectVisibleView() {
        this.todayButton.setExtended(this.viewModel.getButtonState());

        if (this.viewModel.getMeals().size() == 0) {
            if (this.emptyViewLayout.getVisibility() != View.VISIBLE) {
                this.emptyViewLayout.setVisibility(View.VISIBLE);
                this.recyclerView.setVisibility(View.GONE);
            }
        } else {
            if (this.recyclerView.getVisibility() != View.VISIBLE) {
                this.recyclerView.setVisibility(View.VISIBLE);
                this.emptyViewLayout.setVisibility(View.GONE);
            }
            this.recyclerView.scrollToPosition(0);
        }
    }

    private int toPositionIndex(final int themeValue) {
        /*  -1 -> 2
         *   1 -> 0
         *   2 -> 1 */
        if (themeValue < 0)
            return 2;
        return themeValue - 1;
    }

    private int toThemeIndex(final int position) {
        switch (position) {
            case 0:
                return AppCompatDelegate.MODE_NIGHT_NO;
            case 1:
                return AppCompatDelegate.MODE_NIGHT_YES;
            default:
                return AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
        }
    }

    private void displayThemesAlertDialog(final int selectedPosition) {
        new MaterialAlertDialogBuilder(this, R.style.AppTheme_RoundedAlertDialog)
                .setTitle(R.string.title_theme_alert)
                .setSingleChoiceItems(R.array.themes_array, selectedPosition, (dialog, which) -> {
                    final int mode = this.toThemeIndex(which);
                    this.viewModel.setThemePreference(mode);
                    dialog.dismiss();
                })
                .create()
                .show();
    }

    private void displayErrorAlertDialog() {
        new MaterialAlertDialogBuilder(this, R.style.AppTheme_RoundedAlertDialog)
                .setTitle(R.string.title_error_alert_message)
                .setMessage(R.string.text_error_alert_message)
                .setPositiveButton(R.string.action_ok, null)
                .create()
                .show();
    }

    private void displaySnackBar(@NonNull final String message) {
        Snackbar.make(findViewById(android.R.id.content), message, BaseTransientBottomBar.LENGTH_SHORT)
                .setAnchorView(findViewById(R.id.add_today_extended_fab))
                .show();
    }

    private void startActivityForResult() {
        final Intent intent = new Intent(this, MealSelectActivity.class);
        this.mealSelectActivityResultLauncher.launch(intent);
    }
}