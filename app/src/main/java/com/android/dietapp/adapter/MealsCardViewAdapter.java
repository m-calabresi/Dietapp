package com.android.dietapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.android.dietapp.R;
import com.android.dietapp.Utils;
import com.android.dietapp.model.meal.Meal;
import com.android.dietapp.ui.RecyclerViewClickListener;
import com.android.dietapp.viewmodel.livedata.MutableListLiveData;
import com.google.android.material.chip.ChipGroup;

public class MealsCardViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_FOOTER = 1;
    private static final int VIEW_TYPE_CARD = 2;

    private final RecyclerViewClickListener itemListener;
    private final MutableListLiveData<Meal> meals;

    public class MealsViewHolder extends RecyclerView.ViewHolder {
        private final TextView dateTextView;
        private final ChipGroup lunchChipGroup;
        private final ChipGroup dinnerChipGroup;

        public MealsViewHolder(@NonNull final View v) {
            super(v);
            final View.OnClickListener cardOnClickListener = view -> itemListener.recyclerViewItemClicked(getAdapterPosition());

            final CardView cardView = v.findViewById(R.id.daily_meal_card_view);
            cardView.setOnClickListener(cardOnClickListener);

            this.dateTextView = v.findViewById(R.id.meal_date_text_view);
            this.lunchChipGroup = v.findViewById(R.id.lunch_chip_group);
            this.dinnerChipGroup = v.findViewById(R.id.dinner_chip_group);
        }
    }

    public static class FooterViewHolder extends RecyclerView.ViewHolder {
        public FooterViewHolder(@NonNull View v) {
            super(v);
        }
    }

    public MealsCardViewAdapter(final MutableListLiveData<Meal> meals, final RecyclerViewClickListener itemListener) {
        super();
        this.itemListener = itemListener;
        this.meals = meals;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        final LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        final RecyclerView.ViewHolder viewHolder;

        if (viewType == VIEW_TYPE_FOOTER) {
            final View footerView = layoutInflater.inflate(R.layout.layout_item_footer, parent, false);
            viewHolder = new FooterViewHolder(footerView);
        } else {
            final View cardView = layoutInflater.inflate(R.layout.layout_item_cardview, parent, false);
            viewHolder = new MealsViewHolder(cardView);
        }
        return viewHolder;
    }

    @Override
    public int getItemViewType(final int position) {
        if (position == meals.size())
            return VIEW_TYPE_FOOTER;
        return VIEW_TYPE_CARD;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        if (position != this.meals.size()) {
            final MealsViewHolder viewHolder = (MealsViewHolder) holder;
            final Meal meal = this.meals.get(position);
            final String dateString = meal.getDate().toString();

            viewHolder.dateTextView.setText(dateString);

            Utils.setFoodChips(viewHolder.lunchChipGroup, meal.getLunchFoods(), true);
            Utils.setFoodChips(viewHolder.dinnerChipGroup, meal.getDinnerFoods(), true);
        }
    }

    @Override
    public int getItemCount() {
        return this.meals.size() + 1;
    }
}
