package com.roamandframe.customer.security;

public interface PasswordHasher {
    boolean matches(String rawPassword, String hashedPassword);
}
