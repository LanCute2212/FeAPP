package com.example.caloriesapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DayAdapterActivity extends RecyclerView.Adapter<DayAdapterActivity.DayViewHolder> {
    private final List<Integer> days;
    private int selectedPosition = RecyclerView.NO_POSITION;

    public DayAdapterActivity(List<Integer> days) {
        this.days = days;
    }

    @NonNull
    @Override
    public DayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_day, parent, false);
        return new DayViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final DayViewHolder holder, int position) {
        Integer day = (days == null || position < 0 || position >= days.size()) ? null : days.get(position);
        holder.tvDay.setText(day == null ? "" : String.valueOf(day));

        holder.tvDay.setSelected(selectedPosition == position);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterPos = holder.getAdapterPosition();
                if (adapterPos == RecyclerView.NO_POSITION) return;

                int oldPos = selectedPosition;
                if (oldPos == adapterPos) {
                    return;
                }

                selectedPosition = adapterPos;

                if (oldPos != RecyclerView.NO_POSITION) notifyItemChanged(oldPos);
                notifyItemChanged(selectedPosition);
            }
        });
    }

    @Override
    public int getItemCount() {
        return days == null ? 0 : days.size();
    }

    public static class DayViewHolder extends RecyclerView.ViewHolder {
        TextView tvDay;
        public DayViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDay = itemView.findViewById(R.id.tvDay);
        }
    }
}
