package com.android.dietapp;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.List;

public class Utils {
    private static int toDp(@NonNull final Context context, final int px) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, px, context.getResources().getDisplayMetrics());
    }

    @SuppressWarnings("SameParameterValue")
    private static void setMarginStart(@NonNull final View view, final int dp) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            final ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            final int realDp = toDp(view.getContext(), dp);

            p.setMarginStart(realDp);
        }
    }

    @SuppressWarnings("SameParameterValue")
    private static void setMarginEnd(@NonNull final View view, final int dp) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            final ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            final int realDp = toDp(view.getContext(), dp);

            p.setMarginEnd(realDp);
        }
    }

    public static void setFoodChips(@NonNull final ChipGroup parent, @NonNull final List<String> foods, final boolean adjustMargins) {
        parent.post(() -> {
            if (parent.getChildCount() > 0)
                parent.removeAllViews();

            final LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

            for (int i = 0; i < foods.size(); i++) {
                final String foodName = foods.get(i);
                final Chip chip = (Chip) layoutInflater.inflate(R.layout.layout_item_chip, parent, false);
                chip.setText(foodName);

                if (adjustMargins) {
                    if (i == 0) {
                        Utils.setMarginStart(chip, 12);
                    } else if (i == foods.size() - 1) {
                        Utils.setMarginEnd(chip, 12);
                    }
                }
                parent.addView(chip);
            }
        });
    }
}
