package com.example.caloriesapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.caloriesapp.R;
import com.example.caloriesapp.dto.response.WorkoutResponse;

import java.util.List;

public class WorkoutAdapter extends RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder> {
    private List<WorkoutResponse> workoutList;
    private OnWorkoutClickListener listener;

    public interface OnWorkoutClickListener {
        void onWorkoutClick(WorkoutResponse workout);
    }

    public WorkoutAdapter(List<WorkoutResponse> workoutList, OnWorkoutClickListener listener) {
        this.workoutList = workoutList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public WorkoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_workout, parent, false);
        return new WorkoutViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkoutViewHolder holder, int position) {
        WorkoutResponse workout = workoutList.get(position);
        holder.bind(workout);
    }

    @Override
    public int getItemCount() {
        return workoutList != null ? workoutList.size() : 0;
    }

    public void updateWorkouts(List<WorkoutResponse> newWorkouts) {
        this.workoutList = newWorkouts;
        notifyDataSetChanged();
    }

    class WorkoutViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivWorkoutImage;
        private TextView tvWorkoutName;
        private TextView tvDuration;
        private TextView tvDescription;

        WorkoutViewHolder(@NonNull View itemView) {
            super(itemView);
            ivWorkoutImage = itemView.findViewById(R.id.iv_workout_image);
            tvWorkoutName = itemView.findViewById(R.id.tv_workout_name);
            tvDuration = itemView.findViewById(R.id.tv_duration);
            tvDescription = itemView.findViewById(R.id.tv_description);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onWorkoutClick(workoutList.get(position));
                }
            });
        }

        void bind(WorkoutResponse workout) {
            tvWorkoutName.setText(workout.getName());
            tvDuration.setText(workout.getDuration() + " ngày");
            tvDescription.setText(workout.getDes() != null ? workout.getDes() : "");

            // Set default image or load from URL if needed
            // For now, using a placeholder
            ivWorkoutImage.setImageResource(R.drawable.ic_running);
        }
    }
}











