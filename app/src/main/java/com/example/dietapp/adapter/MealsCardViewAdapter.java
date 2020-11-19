package com.example.dietapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dietapp.R;
import com.example.dietapp.model.meal.Meal;
import com.example.dietapp.ui.RecyclerViewClickListener;
import com.example.dietapp.viewmodel.livedata.MutableListLiveData;

public class MealsCardViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final MutableListLiveData<Meal> meals;
    private final RecyclerViewClickListener itemView;

    public static class MealsViewHolder extends RecyclerView.ViewHolder {
        private final TextView dateTextView;
        private final TextView lunchListTextView;
        private final TextView dinnerListTextView;
        private final Button editButton;

        public MealsViewHolder(View v) {
            super(v);
            this.dateTextView = v.findViewById(R.id.meal_date_text_view);
            this.lunchListTextView = v.findViewById(R.id.meal_lunch_list);
            this.dinnerListTextView = v.findViewById(R.id.meal_dinner_list);
            this.editButton = v.findViewById(R.id.meal_edit_button);
        }
    }

    public MealsCardViewAdapter(MutableListLiveData<Meal> meals, RecyclerViewClickListener itemView) {
        super();
        this.meals = meals;
        this.itemView = itemView;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        final View cardView = layoutInflater.inflate(R.layout.layout_item_cardview, parent, false);
        return new MealsViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final MealsViewHolder viewHolder = (MealsViewHolder) holder;
        final Meal meal = this.meals.get(position);
        final String dateString = meal.getDate().toString();

        viewHolder.dateTextView.setText(dateString);
        viewHolder.lunchListTextView.setText(meal.getLunchFoods().toString());
        viewHolder.dinnerListTextView.setText(meal.getDinnerFoods().toString());
        viewHolder.editButton.setOnClickListener(view -> this.itemView.recyclerViewItemClicked(position));
    }

    @Override
    public int getItemCount() {
        return this.meals.size();
    }
}
