package com.codethen.hintsapp.examples.jwt;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashSet;

import org.eclipse.microprofile.jwt.Claims;

import io.smallrye.jwt.build.Jwt;

/**
 * Example of how to generate JWT tokens.
 * Needs the Java property "smallrye.jwt.sign.key-location" with the path for the private key.
 * https://quarkus.io/guides/security-jwt
 */
public class GenerateToken {
    /**
     * Generate JWT token
     */
    public static void main(String[] args) {
        String token =
           Jwt.issuer("https://example.com/issuer")
             .upn("jdoe@quarkus.io")
             .groups(new HashSet<>(Arrays.asList("User", "Admin"))) 
             .claim(Claims.birthdate.name(), "2001-07-13")
             .expiresIn(Duration.ofDays(30))
           .sign();
        System.out.println(token);
    }
}