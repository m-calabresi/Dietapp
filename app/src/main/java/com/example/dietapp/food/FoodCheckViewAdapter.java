package com.example.dietapp.food;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dietapp.R;

import java.util.List;

public class FoodCheckViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final RecyclerViewClickListener itemListener;

    private final List<SelectableFood> foodDataSet;

    public class FoodViewHolder extends RecyclerView.ViewHolder {
        private final CheckBox checkBox;
        private final TextView foodTextView;

        public FoodViewHolder(View v) {
            super(v);
            this.checkBox = v.findViewById(R.id.check_box);
            this.checkBox.setOnClickListener(view -> {
                final int position = getAdapterPosition();
                final SelectableFood food = foodDataSet.get(position);
                food.setSelected(!food.isSelected());
                itemListener.recyclerViewItemClicked(selectedItemsCount());
            });
            this.foodTextView = v.findViewById(R.id.food_text_view);
        }
    }

    public FoodCheckViewAdapter(List<SelectableFood> foodDataSet, RecyclerViewClickListener itemListener) {
        this.foodDataSet = foodDataSet;
        this.itemListener = itemListener;
    }

    public int selectedItemsCount() {
        return (int) this.foodDataSet.stream().filter(SelectableFood::isSelected).count();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.layout_item_checkview, parent, false);
        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        final FoodViewHolder viewHolder = (FoodViewHolder) holder;
        final SelectableFood food = this.foodDataSet.get(position);

        viewHolder.foodTextView.setText(food.getFood());
        viewHolder.checkBox.setChecked(food.isSelected());
        viewHolder.itemView.setOnClickListener(view -> viewHolder.checkBox.performClick());
    }

    @Override
    public int getItemCount() {
        return this.foodDataSet.size();
    }
}
