package com.super_tunes.user_service.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest (
    @NotBlank(message="Username is Required")
    @Size(min=3, max=100, message="Username must be between 3 and 100 characters")
    String username,

    @NotBlank(message="Email is required")
    @Email(message="Email format is invalid")
    @Size(max=255, message="Email must not exceed 255 characters")
    String email,

    @NotBlank(message="Password Cannot Be Blank")
    @Size(min=8,max=72,message="Password must be between 8 and 72 characters")
    String password
) {}
