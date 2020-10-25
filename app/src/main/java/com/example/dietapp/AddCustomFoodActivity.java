package com.example.dietapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class AddCustomFoodActivity extends AppCompatActivity {
    private EditText foodEditText;
    private TextInputLayout foodTextInputLayout;
    private ExtendedFloatingActionButton extendedFloatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_custom_food);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setElevation(0);

        extendedFloatingActionButton = findViewById(R.id.insert_extended_fab);
        extendedFloatingActionButton.setEnabled(false);
        if (extendedFloatingActionButton.isExtended())
            extendedFloatingActionButton.shrink();

        foodTextInputLayout = findViewById(R.id.food_text_input);

        foodEditText = foodTextInputLayout.getEditText();
        Objects.requireNonNull(foodEditText).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                boolean isTextInputFilled = s.length() > 0;

                if (isTextInputFilled) {
                    if (extendedFloatingActionButton.getVisibility() != View.VISIBLE)
                        extendedFloatingActionButton.show();
                } else if (extendedFloatingActionButton.getVisibility() == View.VISIBLE)
                    extendedFloatingActionButton.hide();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        extendedFloatingActionButton.setOnClickListener(view -> {
            String result = foodEditText.getText().toString();
            Intent intent = new Intent();
            intent.putExtra(FoodSelectActivity.ADD_ACTIVITY_RESULT_NAME, result);
            setResult(Activity.RESULT_OK, intent);
            finish();
        });

        showSoftInput(true);
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
        foodTextInputLayout.requestFocus();
        foodTextInputLayout.postDelayed(() -> {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

            if (force) {
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
            } else
                imm.showSoftInput(foodEditText, InputMethodManager.SHOW_FORCED);
        }, 0);
    }

    // hide soft input (if displayed)
    private void hideSoftInput() {
        foodTextInputLayout.postDelayed(() -> {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(foodEditText.getWindowToken(), 0);
        }, 0);
    }

    private void onBackActionPerformed() {
        Intent intent = new Intent();
        this.setResult(Activity.RESULT_CANCELED, intent);
    }
}