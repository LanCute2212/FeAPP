package com.example.caloriesapp.dto.response;

import lombok.Getter;
import lombok.Setter;
public class UserDto {

    @Getter
    @Setter
        private  int id;
        private String name;

        private Double age;

        private boolean gender;

        private Double height;

        private Double weight;

        private Double levelActivity;

        private Double goal;

        private String email;

        private Long phoneNumber;


    }

