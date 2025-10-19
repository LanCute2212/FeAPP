package com.example.caloriesapp.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class PhysicalEditProfileForm {

    private int id;
    private int age;
    private String gender;
    private double weight;
    private double height;
    private Double levelActivity;
    private Double goal;

    private String email;

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
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
        return levelActivity;
    }

    public void setActivityLevel(Double activityLevel) {
        this.levelActivity = levelActivity;
    }

    public Double getGoal() {
        return goal;
    }

    public void setGoal(Double goal) {
        this.goal = goal;
    }

    public PhysicalEditProfileForm() {}
    public PhysicalEditProfileForm(int id, int age, String gender, double weight, double height, Double levelActivity, Double goal) {
        this.id = id;
        this.age = age;
        this.gender = gender;
        this.weight = weight;
        this.height = height;
        this.levelActivity = levelActivity;
        this.goal = goal;
    }
}

