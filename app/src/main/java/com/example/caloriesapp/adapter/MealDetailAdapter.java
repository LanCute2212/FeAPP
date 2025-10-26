package com.example.caloriesapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.caloriesapp.R;
import com.example.caloriesapp.model.MealDetail;

import java.util.List;

public class MealDetailAdapter extends RecyclerView.Adapter<MealDetailAdapter.MealDetailViewHolder> {
    private List<MealDetail> mealDetailList;
    private OnMealDetailClickListener clickListener;

    public interface OnMealDetailClickListener {
        void onMealDetailClick(MealDetail mealDetail, int position);
        void onMealDetailDelete(MealDetail mealDetail, int position);
    }

    public MealDetailAdapter(List<MealDetail> mealDetailList) {
        this.mealDetailList = mealDetailList;
    }

    public void setOnMealDetailClickListener(OnMealDetailClickListener listener) {
        this.clickListener = listener;
    }

    public void addMealDetail(MealDetail mealDetail) {
        mealDetailList.add(mealDetail);
        notifyItemInserted(mealDetailList.size() - 1);
    }

    public void removeMealDetail(int position) {
        if (position >= 0 && position < mealDetailList.size()) {
            mealDetailList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void updateMealDetailList(List<MealDetail> newList) {
        mealDetailList.clear();
        mealDetailList.addAll(newList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MealDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_meal_detail, parent, false);
        return new MealDetailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MealDetailViewHolder holder, int position) {
        MealDetail mealDetail = mealDetailList.get(position);
        holder.bind(mealDetail, position, clickListener);
    }

    @Override
    public int getItemCount() {
        return mealDetailList.size();
    }

    public static class MealDetailViewHolder extends RecyclerView.ViewHolder {
        private ImageView foodImage;
        private TextView foodName;
        private TextView nutritionSummary;
        private ImageView deleteButton;

        public MealDetailViewHolder(@NonNull View itemView) {
            super(itemView);
            foodImage = itemView.findViewById(R.id.food_image);
            foodName = itemView.findViewById(R.id.food_name);
            nutritionSummary = itemView.findViewById(R.id.nutrition_summary);
            deleteButton = itemView.findViewById(R.id.delete_button);
        }

        public void bind(MealDetail mealDetail, int position, OnMealDetailClickListener clickListener) {
            foodImage.setImageResource(mealDetail.getIconResource());
            foodName.setText(mealDetail.getFoodName());
            nutritionSummary.setText(mealDetail.getNutritionSummary());

            itemView.setOnClickListener(v -> {
                if (clickListener != null) {
                    clickListener.onMealDetailClick(mealDetail, position);
                }
            });

            deleteButton.setOnClickListener(v -> {
                if (clickListener != null) {
                    clickListener.onMealDetailDelete(mealDetail, position);
                }
            });
        }
    }
}


