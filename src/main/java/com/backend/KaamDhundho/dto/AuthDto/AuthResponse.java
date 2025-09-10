package com.backend.KaamDhundho.dto.AuthDto;


public record AuthResponse(
        long id,
        String access_token,
        String refresh_token,
        String name,
        String email,
        String role,
        boolean is_email_verified
) {}
