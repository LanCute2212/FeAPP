package com.example.caloriesapp.dto.request;

import lombok.Data;

// dto nhận yêu cầu tạo log mới từ client
@Data
public class LogActivityRequest {
    private Integer userId;
    private Integer activityId;
    private double durationInMinutes;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getActivityId() {
        return activityId;
    }

    public void setActivityId(Integer activityId) {
        this.activityId = activityId;
    }

    public double getDurationInMinutes() {
        return durationInMinutes;
    }

    public void setDurationInMinutes(double durationInMinutes) {
        this.durationInMinutes = durationInMinutes;
    }
}