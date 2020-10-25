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
    private static final int TYPE_FOOTER = 1;
    private static final int TYPE_LIST_ELEMENT = 0;

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

    public static class FooterLayoutViewHolder extends RecyclerView.ViewHolder {
        public FooterLayoutViewHolder(View v) {
            super(v);
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

        if (viewType == TYPE_FOOTER) {
            View view = layoutInflater.inflate(R.layout.layout_footer, parent, false);
            return new FooterLayoutViewHolder(view);
        }

        View view = layoutInflater.inflate(R.layout.layout_food_checkview, parent, false);
        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == TYPE_LIST_ELEMENT) {
            final FoodViewHolder viewHolder = (FoodViewHolder) holder;
            final SelectableFood food = this.foodDataSet.get(position);

            viewHolder.foodTextView.setText(food.getFood());
            viewHolder.checkBox.setChecked(food.isSelected());
            viewHolder.itemView.setOnClickListener(view -> viewHolder.checkBox.performClick());
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == foodDataSet.size())
            return TYPE_FOOTER;
        return TYPE_LIST_ELEMENT;
    }

    @Override
    public int getItemCount() {
        return this.foodDataSet.size() + 1;
    }
}
