package com.example.dietapp.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;

import com.example.dietapp.R;
import com.example.dietapp.viewmodel.CustomFoodViewModel;
import com.example.dietapp.viewmodel.FoodSelectViewModel;
import com.google.android.material.textfield.TextInputLayout;

public class CustomFoodActivity extends AppCompatActivity {
    private EditText foodEditText;
    private TextInputLayout foodTextInputLayout;
    private Button doneButton;

    private CustomFoodViewModel viewModel;

    private final TextWatcher foodTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.length() > 0) {
                viewModel.setFoodText(s.toString());
                if (doneButton.getVisibility() != View.VISIBLE)
                    doneButton.setVisibility(View.VISIBLE);
            } else if (doneButton.getVisibility() == View.VISIBLE)
                doneButton.setVisibility(View.GONE);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private final View.OnClickListener doneButtonOnClickListener = view -> {
        String customFood = this.foodEditText.getText().toString();
        Intent intent = new Intent();
        intent.putExtra(FoodSelectViewModel.EXTRA_CUSTOM_FOOD, customFood);
        setResult(RESULT_OK, intent);
        finish();
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_food);

        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.viewModel = new ViewModelProvider(this).get(CustomFoodViewModel.class);

        this.foodTextInputLayout = findViewById(R.id.food_text_input);

        this.foodEditText = this.foodTextInputLayout.getEditText();
        assert this.foodEditText != null;
        this.foodEditText.setText(this.viewModel.getFoodText());
        this.foodEditText.addTextChangedListener(this.foodTextWatcher);

        showSoftInput(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_menu, menu);

        final ConstraintLayout constraintLayout = (ConstraintLayout) menu.findItem(R.id.item_action_button).getActionView();
        this.doneButton = constraintLayout.findViewById(R.id.action_button);
        this.doneButton.setText(R.string.action_done);
        this.doneButton.setOnClickListener(this.doneButtonOnClickListener);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        showSoftInput(false);
    }

    @Override
    protected void onPause() {
        super.onPause();
        hideSoftInput();
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

    // show soft input
    private void showSoftInput(final boolean force) {
        this.foodTextInputLayout.requestFocus();
        this.foodTextInputLayout.post(() -> {
            final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

            if (force) {
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
            } else
                imm.showSoftInput(this.foodEditText, InputMethodManager.SHOW_FORCED);
        });
    }

    // hide soft input (if displayed)
    private void hideSoftInput() {
        this.foodTextInputLayout.post(() -> {
            final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(this.foodEditText.getWindowToken(), 0);
        });
    }

    private void onBackActionPerformed() {
        final Intent intent = new Intent();
        this.setResult(RESULT_CANCELED, intent);
    }
}