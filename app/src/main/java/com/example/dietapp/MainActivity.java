package com.example.dietapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dietapp.data.SharedLiveDataRepository;
import com.example.dietapp.meal.Date;
import com.example.dietapp.meal.Meal;
import com.example.dietapp.meal.MealsCardViewAdapter;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    public static final int TYPE_ERROR = -1;
    public static final int TYPE_TODAY_NEW = 1;
    public static final int TYPE_EDIT = 2;

    public static final String EXTRA_REQUEST_TYPE = "com.example.dietapp.request_type";
    public static final String EXTRA_CARD_POSITION = "com.example.diatapp.card_position";

    private RecyclerView recyclerView;
    private MealsCardViewAdapter adapter;
    private ConstraintLayout emptyViewLayout;

    public final ActivityResultLauncher<Intent> mealSelectActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    final int itemPosition;
                    final int requestType = Objects.requireNonNull(result.getData()).getIntExtra(EXTRA_REQUEST_TYPE, TYPE_ERROR);

                    switch (requestType) {
                        case TYPE_TODAY_NEW:
                            itemPosition = adapter.getItemCount() - 1;
                            adapter.notifyItemInserted(itemPosition);
                            break;
                        case TYPE_EDIT:
                            itemPosition = adapter.getLastEditedItemPosition();
                            adapter.notifyItemChanged(itemPosition);
                            break;
                        default:
                            throw new NullPointerException("No valid result code, activity returned in an unexpected way");
                    }
                    adapter.notifyItemRangeChanged(itemPosition, 1);
                    recyclerView.scrollToPosition(itemPosition);
                    setupEmptyView();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // sets default theme
        final int mode = SharedLiveDataRepository.Preferences.getThemePreference(this);
        AppCompatDelegate.setDefaultNightMode(mode);

        // preload data
        SharedLiveDataRepository.initEnvironment(this);
        SharedLiveDataRepository.loadMealList(this);

        // remove splash screen
        setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ExtendedFloatingActionButton todayButton = findViewById(R.id.add_today_extended_fab);
        todayButton.setOnClickListener(view -> {
            List<Meal> meals = SharedLiveDataRepository.getMealsList();

            if (meals.size() != 0 && meals.get(meals.size() - 1).getDate().equals(Date.today())) {
                final MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this, R.style.AppTheme_RoundedAlertDialog);
                builder.setTitle(R.string.title_error_alert_message)
                        .setMessage(R.string.text_error_alert_message)
                        .setPositiveButton(R.string.action_ok, null)
                        .create()
                        .show();
            } else {
                Intent intent = new Intent(view.getContext(), MealSelectActivity.class);
                intent.putExtra(EXTRA_REQUEST_TYPE, TYPE_TODAY_NEW);
                mealSelectActivityResultLauncher.launch(intent);
            }
        });

        recyclerView = findViewById(R.id.days_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 && !todayButton.isExtended())
                    todayButton.extend();
                else if (dy < 0 && todayButton.isExtended())
                    todayButton.shrink();
            }
        });

        adapter = new MealsCardViewAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.scrollToPosition(adapter.getItemCount() - 1);

        emptyViewLayout = findViewById(R.id.empty_view_layout);
        setupEmptyView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings_theme) {
            int itemPosition = this.toPositionIndex(SharedLiveDataRepository.Preferences.getThemePreference(this));

            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this, R.style.AppTheme_RoundedAlertDialog);
            builder.setTitle(R.string.title_theme_alert)
                    .setSingleChoiceItems(R.array.themes_array, itemPosition, (dialog, which) -> {
                        int mode = this.toThemeIndex(which);

                        SharedLiveDataRepository.Preferences.setThemePreference(this, mode);
                        dialog.dismiss();
                    });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupEmptyView() {
        if (adapter.getItemCount() == 0) {
            recyclerView.setVisibility(View.GONE);
            emptyViewLayout.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyViewLayout.setVisibility(View.GONE);
        }
    }

    private int toPositionIndex(int themeValue) {
        /*  -1 -> 2
         *   1 -> 0
         *   2 -> 1 */
        if (themeValue < 0)
            return 2;
        return themeValue - 1;
    }

    private int toThemeIndex(int position) {
        switch (position) {
            case 0:
                return AppCompatDelegate.MODE_NIGHT_NO;
            case 1:
                return AppCompatDelegate.MODE_NIGHT_YES;
            default:
                return AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
        }
    }
}