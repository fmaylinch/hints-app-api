package com.codethen.hintsapp.security;

@lombok.Data
@lombok.Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor

public class Login {

    private String email;
    private String password;
    private String password2;
}
