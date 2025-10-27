package com.example.caloriesapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.caloriesapp.R;

import java.util.List;

public class AdjustmentOptionAdapter extends RecyclerView.Adapter<AdjustmentOptionAdapter.AdjustmentViewHolder> {
    private List<Integer> options;
    private int selectedOption;
    private boolean isWeightLoss;
    private int selectedPosition = -1;

    public AdjustmentOptionAdapter(List<Integer> options, int selectedOption, boolean isWeightLoss) {
        this.options = options;
        this.selectedOption = selectedOption;
        this.isWeightLoss = isWeightLoss;
        
        // Find the position of the selected option
        for (int i = 0; i < options.size(); i++) {
            if (options.get(i) == selectedOption) {
                selectedPosition = i;
                break;
            }
        }
    }

    @NonNull
    @Override
    public AdjustmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_adjustment_option, parent, false);
        return new AdjustmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdjustmentViewHolder holder, int position) {
        int option = options.get(position);
        String prefix = isWeightLoss ? "Cắt giảm" : "Tăng thêm";
        holder.tvOption.setText(prefix + " " + option + " kcal/ngày");
        
        // Highlight selected option
        boolean isSelected = (position == selectedPosition);
        holder.ivCheck.setVisibility(isSelected ? View.VISIBLE : View.GONE);
        
        if (isSelected) {
            holder.cardView.setCardBackgroundColor(0xFFE8F5E9);
        } else {
            holder.cardView.setCardBackgroundColor(0xFFF5F5F5);
        }
        
        holder.container.setOnClickListener(v -> {
            int oldPosition = selectedPosition;
            selectedPosition = position;
            notifyItemChanged(oldPosition);
            notifyItemChanged(selectedPosition);
        });
    }

    @Override
    public int getItemCount() {
        return options.size();
    }
    
    public Integer getSelectedOption() {
        if (selectedPosition >= 0 && selectedPosition < options.size()) {
            return options.get(selectedPosition);
        }
        return null;
    }

    public static class AdjustmentViewHolder extends RecyclerView.ViewHolder {
        TextView tvOption;
        ImageView ivCheck;
        CardView cardView;
        LinearLayout container;

        public AdjustmentViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = (CardView) itemView;
            container = itemView.findViewById(R.id.container);
            tvOption = container.findViewById(R.id.tv_option);
            ivCheck = container.findViewById(R.id.iv_check);
        }
    }
}


