package com.example.caloriesapp.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRegisterForm {
    private String username;

    private String password;

    private String email;

    public UserRegisterForm() {
    }

    public UserRegisterForm(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }
}
