package com.example.caloriesapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.caloriesapp.R;
import com.example.caloriesapp.model.FoodItem;

import java.util.List;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodViewHolder> {
    private List<FoodItem> foodList;
    private OnFoodClickListener clickListener;

    public interface OnFoodClickListener {
        void onFoodClick(FoodItem food, int position);
        void onAddFoodClick(FoodItem food, int position);
    }

    public FoodAdapter(List<FoodItem> foodList) {
        this.foodList = foodList;
    }

    public void setOnFoodClickListener(OnFoodClickListener listener) {
        this.clickListener = listener;
    }

    public void addFood(FoodItem newFood) {
        foodList.add(newFood);
        notifyItemInserted(foodList.size() - 1);
    }

    public void updateFood(int position, FoodItem updatedFood) {
        if (position >= 0 && position < foodList.size()) {
            foodList.set(position, updatedFood);
            notifyItemChanged(position);
        }
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_food, parent, false);
        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        FoodItem food = foodList.get(position);
        holder.bind(food, position, clickListener);
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    public static class FoodViewHolder extends RecyclerView.ViewHolder {
        private ImageView foodImage;
        private TextView foodName;
        private TextView nutritionSummary;
        private ImageView addButton;

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            foodImage = itemView.findViewById(R.id.food_image);
            foodName = itemView.findViewById(R.id.food_name);
            nutritionSummary = itemView.findViewById(R.id.nutrition_summary);
            addButton = itemView.findViewById(R.id.add_button);
        }

        public void bind(FoodItem food, int position, OnFoodClickListener clickListener) {
            String imageUrl = food.getImageUrl();
            if (imageUrl != null && !imageUrl.isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(imageUrl)
                        .placeholder(R.drawable.ic_meal)
                        .error(R.drawable.ic_meal)
                        .centerCrop()
                        .into(foodImage);
            } else {
                foodImage.setImageResource(food.getIconResource());
            }
            foodName.setText(food.getName());
            nutritionSummary.setText(food.getNutritionSummary());

            itemView.setOnClickListener(v -> {
                if (clickListener != null) {
                    clickListener.onFoodClick(food, position);
                }
            });

            addButton.setOnClickListener(v -> {
                if (clickListener != null) {
                    clickListener.onAddFoodClick(food, position);
                }
            });
        }
    }
}

