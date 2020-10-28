package com.example.dietapp.meal;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dietapp.MainActivity;
import com.example.dietapp.MealSelectActivity;
import com.example.dietapp.R;
import com.example.dietapp.data.SharedLiveDataRepository;

public class MealsCardViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private int lastEditedItemIndex;

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

    public MealsCardViewAdapter() {
        super();
        this.lastEditedItemIndex = -1;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        final View cardView = layoutInflater.inflate(R.layout.layout_item_cardview, parent, false);
        return new MealsViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final MealsViewHolder viewHolder = (MealsViewHolder) holder;
        final Meal meal = SharedLiveDataRepository.getMealsList().get(position);
        final String dateString = meal.getDate().toString();

        viewHolder.dateTextView.setText(dateString);
        viewHolder.lunchListTextView.setText(meal.getLunchFoods().toString());
        viewHolder.dinnerListTextView.setText(meal.getDinnerFoods().toString());
        viewHolder.editButton.setOnClickListener(view -> {
            this.lastEditedItemIndex = position;

            Intent intent = new Intent(view.getContext(), MealSelectActivity.class);
            intent.putExtra(MainActivity.EXTRA_REQUEST_TYPE, MainActivity.TYPE_EDIT);
            intent.putExtra(MainActivity.EXTRA_CARD_POSITION, position);
            ((MainActivity) view.getContext()).mealSelectActivityResultLauncher.launch(intent);
        });
    }

    @Override
    public int getItemCount() {
        return SharedLiveDataRepository.getMealsList().size();
    }

    public int getLastEditedItemPosition() {
        return this.lastEditedItemIndex;
    }
}
