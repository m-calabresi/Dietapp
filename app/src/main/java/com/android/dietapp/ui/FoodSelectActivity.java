package com.android.dietapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.dietapp.R;
import com.android.dietapp.adapter.FoodCheckViewAdapter;
import com.android.dietapp.model.SelectableFood;
import com.android.dietapp.viewmodel.FoodSelectViewModel;
import com.android.dietapp.viewmodel.MealSelectViewModel;
import com.android.dietapp.viewmodel.livedata.MutableListHolder;

import java.util.Objects;

public class FoodSelectActivity extends AppCompatActivity implements RecyclerViewClickListener {
    private FoodSelectViewModel viewModel;
    private RecyclerView recyclerView;
    private FoodCheckViewAdapter adapter;

    private Button doneButton;

    @NonNull
    private final Observer<MutableListHolder<SelectableFood>> foodsUpdateObserver = new Observer<MutableListHolder<SelectableFood>>() {
        @Override
        public void onChanged(@NonNull final MutableListHolder<SelectableFood> listMutableListHolder) {
            listMutableListHolder.notifyChange(adapter);
        }
    };

    @NonNull
    private final View.OnClickListener customAddButtonClickListener = view -> {
        final Intent intent = new Intent(view.getContext(), CustomFoodActivity.class);
        this.addActivityResultLauncher.launch(intent);
    };

    @NonNull
    private final View.OnClickListener doneButtonOnClickListener = view -> {
        this.viewModel.saveSelectedFoods();

        final Intent intent = new Intent();
        intent.putExtra(MealSelectViewModel.EXTRA_REQUEST_TYPE, this.viewModel.getRequestType());
        setResult(RESULT_OK, intent);
        finish();
    };

    @NonNull
    private final ActivityResultLauncher<Intent> addActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    final String resultData = Objects.requireNonNull(result.getData()).getStringExtra(FoodSelectViewModel.EXTRA_CUSTOM_FOOD);
                    viewModel.insertCustomFood(Objects.requireNonNull(resultData));

                    //scroll to top, in order to see the newly added item
                    this.recyclerView.scrollToPosition(0);
                    handleDoneButtonVisibility();
                }
            });

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_select);

        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.viewModel = new ViewModelProvider(this).get(FoodSelectViewModel.class);
        this.viewModel.setRequestType(getIntent().getIntExtra(MealSelectViewModel.EXTRA_REQUEST_TYPE, MealSelectViewModel.TYPE_ERROR));
        this.viewModel.initFoods();
        this.viewModel.getFoods().observe(this, this.foodsUpdateObserver);

        this.recyclerView = findViewById(R.id.foods_recycler_view);
        this.recyclerView.setHasFixedSize(true);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        this.adapter = new FoodCheckViewAdapter(this.viewModel.getFoods().asList(), this);
        this.recyclerView.setAdapter(this.adapter);

        final Button customAddButton = findViewById(R.id.custom_add_button);
        customAddButton.setOnClickListener(this.customAddButtonClickListener);
    }

    @Override
    public void recyclerViewItemClicked() {
        handleDoneButtonVisibility();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.onBackActionPerformed();
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull final Menu menu) {
        getMenuInflater().inflate(R.menu.action_menu, menu);

        final ConstraintLayout constraintLayout = (ConstraintLayout) menu.findItem(R.id.item_action_button).getActionView();
        this.doneButton = constraintLayout.findViewById(R.id.action_button);
        this.doneButton.setText(R.string.action_done);
        this.doneButton.setOnClickListener(this.doneButtonOnClickListener);
        handleDoneButtonVisibility();
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        this.onBackActionPerformed();
        this.finish();
        return true;
    }

    private void onBackActionPerformed() {
        final Intent intent = new Intent();
        setResult(RESULT_CANCELED, intent);
    }

    private void handleDoneButtonVisibility() {
        int visibility;
        if (this.viewModel.isAnyItemSelected())
            visibility = View.VISIBLE;
        else
            visibility = View.GONE;

        if (this.doneButton.getVisibility() != visibility)
            this.doneButton.setVisibility(visibility);
    }
}