package com.example.caloriesapp.dto.response;

public class WorkoutResponse {
    private String name;
    private String linkVideo;
    private int duration;
    private String des;

    public WorkoutResponse() {
    }

    public WorkoutResponse(String name, String linkVideo, int duration, String des) {
        this.name = name;
        this.linkVideo = linkVideo;
        this.duration = duration;
        this.des = des;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLinkVideo() {
        return linkVideo;
    }

    public void setLinkVideo(String linkVideo) {
        this.linkVideo = linkVideo;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }
}
