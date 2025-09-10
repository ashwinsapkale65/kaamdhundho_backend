package com.backend.KaamDhundho.dto.AuthDto;


import jakarta.validation.constraints.*;


import java.time.LocalDate;

public record RegisterRequest(
        @NotBlank String name,
        @NotBlank @Email String email,
        @NotBlank String mobile_number,
        @NotNull LocalDate dob,
        @NotBlank String gender,
        @NotBlank String role,
        @NotBlank @Size(min=6) String password// optional; default to "user" if null/blank
) {}
