package com.backend.KaamDhundho.dto.AuthDto;


import jakarta.validation.constraints.*;
public record LoginRequest(
        @NotBlank @Email String email,
        @NotBlank String password
) {}
