package com.example.caloriesapp.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRegisterForm {
    private String name;

    private String password;

    private String email;

    public UserRegisterForm() {
    }

    public UserRegisterForm(String name, String password, String email) {
        this.name = name;
        this.password = password;
        this.email = email;
    }
}
