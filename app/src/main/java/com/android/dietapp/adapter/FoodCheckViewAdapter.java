package com.android.dietapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.dietapp.R;
import com.android.dietapp.model.SelectableFood;
import com.android.dietapp.ui.RecyclerViewClickListener;

import java.util.List;

public class FoodCheckViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final RecyclerViewClickListener itemListener;
    private final List<SelectableFood> selectableFoods;

    public class FoodViewHolder extends RecyclerView.ViewHolder {
        private final CheckBox checkBox;
        private final TextView foodTextView;

        public FoodViewHolder(@NonNull final View v) {
            super(v);
            this.checkBox = v.findViewById(R.id.check_box);
            this.checkBox.setOnClickListener(view -> {
                final int position = getAdapterPosition();
                boolean selected = !selectableFoods.get(position).isSelected();

                selectableFoods.get(position).setSelected(selected);
                itemListener.recyclerViewItemClicked();
            });
            this.foodTextView = v.findViewById(R.id.food_text_view);
        }
    }

    public FoodCheckViewAdapter(final List<SelectableFood> selectableFoods, final RecyclerViewClickListener itemListener) {
        this.selectableFoods = selectableFoods;
        this.itemListener = itemListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        final LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        final View view = layoutInflater.inflate(R.layout.layout_item_checkview, parent, false);
        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        final FoodViewHolder viewHolder = (FoodViewHolder) holder;
        final SelectableFood food = this.selectableFoods.get(position);

        viewHolder.foodTextView.setText(food.getName());
        viewHolder.checkBox.setChecked(food.isSelected());
        viewHolder.itemView.setOnClickListener(view -> viewHolder.checkBox.performClick());
    }

    @Override
    public int getItemCount() {
        return this.selectableFoods.size();
    }
}
