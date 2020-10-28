package com.example.dietapp;

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

import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class AddCustomFoodActivity extends AppCompatActivity {
    private EditText foodEditText; // TODO  fix written content deleted, on theme changed
    private TextInputLayout foodTextInputLayout;
    private Button doneButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_custom_food);

        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        foodTextInputLayout = findViewById(R.id.food_text_input);

        foodEditText = foodTextInputLayout.getEditText();
        Objects.requireNonNull(foodEditText).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                final boolean isTextInputFilled = s.length() > 0;

                if (isTextInputFilled) {
                    if (doneButton.getVisibility() != View.VISIBLE)
                        doneButton.setVisibility(View.VISIBLE);
                } else if (doneButton.getVisibility() == View.VISIBLE)
                    doneButton.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        showSoftInput(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_menu, menu);

        final ConstraintLayout constraintLayout = (ConstraintLayout) menu.findItem(R.id.item_action_button).getActionView();
        doneButton = constraintLayout.findViewById(R.id.action_button);
        doneButton.setText(R.string.action_done);
        doneButton.setOnClickListener(view -> {
            String customFood = foodEditText.getText().toString();
            Intent intent = new Intent();
            intent.putExtra(FoodSelectActivity.EXTRA_CUSTOM_FOOD, customFood);
            setResult(RESULT_OK, intent);
            finish();
        });
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
        this.setResult(RESULT_CANCELED, intent);
    }
}