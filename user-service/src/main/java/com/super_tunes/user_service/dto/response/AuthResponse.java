package com.super_tunes.user_service.dto.response;

public record AuthResponse(
    String token,
    Long userId,
    String username,
    String email

) {}
