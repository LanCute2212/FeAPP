package com.example.caloriesapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.caloriesapp.R;
import com.example.caloriesapp.model.ActivityItem;

import java.util.List;

public class ActivityAdapter extends RecyclerView.Adapter<ActivityAdapter.ActivityViewHolder> {
    private List<ActivityItem> activityList;
    private OnActivityDeleteListener deleteListener;
    private OnActivityClickListener clickListener;

    public interface OnActivityDeleteListener {
        void onActivityDelete(int position);
    }

    public interface OnActivityClickListener {
        void onActivityClick(ActivityItem activity, int position);
    }

    public ActivityAdapter(List<ActivityItem> activityList) {
        this.activityList = activityList;
    }

    public void setOnActivityDeleteListener(OnActivityDeleteListener listener) {
        this.deleteListener = listener;
    }

    public void setOnActivityClickListener(OnActivityClickListener listener) {
        this.clickListener = listener;
    }

    public void removeActivity(int position) {
        if (position >= 0 && position < activityList.size()) {
            activityList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void updateActivity(int position, ActivityItem updatedActivity) {
        if (position >= 0 && position < activityList.size()) {
            activityList.set(position, updatedActivity);
            notifyItemChanged(position);
        }
    }

    @NonNull
    @Override
    public ActivityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_activity, parent, false);
        return new ActivityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActivityViewHolder holder, int position) {
        ActivityItem activity = activityList.get(position);
        holder.bind(activity, position, clickListener);
    }

    @Override
    public int getItemCount() {
        return activityList.size();
    }

    public static class ActivityViewHolder extends RecyclerView.ViewHolder {
        private ImageView activityIcon;
        private TextView activityName;
        private TextView activitySummary;
        private TextView toggleButton;
        private LinearLayout activityDetails;
        private TextView durationText;
        private TextView caloriesText;
        private TextView intensityText;
        private TextView distanceText;
        private TextView dateText;

        public ActivityViewHolder(@NonNull View itemView) {
            super(itemView);
            activityIcon = itemView.findViewById(R.id.activity_icon);
            activityName = itemView.findViewById(R.id.activity_name);
            activitySummary = itemView.findViewById(R.id.activity_summary);
            toggleButton = itemView.findViewById(R.id.toggle_button);
            activityDetails = itemView.findViewById(R.id.activity_details);
            durationText = itemView.findViewById(R.id.duration_text);
            caloriesText = itemView.findViewById(R.id.calories_text);
            intensityText = itemView.findViewById(R.id.intensity_text);
            distanceText = itemView.findViewById(R.id.distance_text);
            dateText = itemView.findViewById(R.id.date_text);
        }

        public void bind(ActivityItem activity, int position, OnActivityClickListener clickListener) {
            activityIcon.setImageResource(activity.getIconResource());
            activityName.setText(activity.getName());
            activitySummary.setText(activity.getSummary());
            
            durationText.setText("• Duration: " + activity.getDuration());
            caloriesText.setText("• Calories burned: " + activity.getCalories() + " kcal");
            intensityText.setText("• Intensity: " + activity.getIntensity());
            dateText.setText("• Date: " + activity.getDate());
            
            if (activity.getDistance() != null && !activity.getDistance().isEmpty()) {
                distanceText.setText("• Distance: " + activity.getDistance());
                distanceText.setVisibility(View.VISIBLE);
            } else {
                distanceText.setVisibility(View.GONE);
            }

            toggleButton.setOnClickListener(v -> toggleActivityDetails());
            
            itemView.setOnClickListener(v -> {
                if (clickListener != null) {
                    clickListener.onActivityClick(activity, position);
                }
            });
        }

        private void toggleActivityDetails() {
            if (activityDetails.getVisibility() == View.VISIBLE) {
                activityDetails.setVisibility(View.GONE);
                toggleButton.setText("▼");
            } else {
                activityDetails.setVisibility(View.VISIBLE);
                toggleButton.setText("▲");
            }
        }
    }
}
