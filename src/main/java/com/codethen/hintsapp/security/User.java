package com.codethen.hintsapp.security;

@lombok.Data
@lombok.Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor

public class User {

    String id;
    String email;
    String password;
}
