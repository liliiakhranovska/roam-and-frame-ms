package com.roamandframe.gateway.api;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

record AuthenticationRequest(
        @NotBlank @Email @Size(max = 255) String email,
        @NotBlank @Size(max = 72) String password   // BCrypt truncates at 72 bytes
) {
    @Override public String toString() { return "AuthenticationRequest[email=" + email + "]"; }
}