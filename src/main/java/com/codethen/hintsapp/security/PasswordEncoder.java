package com.codethen.hintsapp.security;

import at.favre.lib.crypto.bcrypt.BCrypt;

import javax.inject.Singleton;

@Singleton
public class PasswordEncoder {

    public PasswordEncoder() {
    }

    public String encode(String password) {
        return BCrypt.withDefaults().hashToString(12, password.toCharArray());
    }

    public boolean verify(String clearPassword, String hashedPassword) {
        return BCrypt.verifyer().verify(clearPassword.toCharArray(), hashedPassword).verified;
    }
}
