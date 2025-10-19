package com.example.caloriesapp.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class UserLoginForm {
    private String email;
    private String password;

    public UserLoginForm() {}

    public UserLoginForm(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
