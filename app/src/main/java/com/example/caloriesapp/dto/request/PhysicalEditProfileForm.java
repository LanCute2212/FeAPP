package com.example.caloriesapp.dto.request;

public class PhysicalEditProfileForm {

    private int age;
    private Boolean gender;
    private double weight;
    private double height;
    private Double activityLevel;
    private Double goal;

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Boolean getGender() {
        return gender;
    }

    public void setGender(Boolean gender) {
        this.gender = gender;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public Double getActivityLevel() {
        return activityLevel;
    }

    public void setActivityLevel(Double activityLevel) {
        this.activityLevel = activityLevel;
    }

    public Double getGoal() {
        return goal;
    }

    public void setGoal(Double goal) {
        this.goal = goal;
    }

    public PhysicalEditProfileForm() {
    }

    public PhysicalEditProfileForm(int id, int age, Boolean gender, double weight, double height, Double activityLevel, Double goal) {
        this.age = age;
        this.gender = gender;
        this.weight = weight;
        this.height = height;
        this.activityLevel = activityLevel;
        this.goal = goal;
    }
}

