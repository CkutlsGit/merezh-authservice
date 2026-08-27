package ru.merezh.authservice.dto;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenDto(
        @NotBlank(message = "Токен обязателен к заполнению")
        String refreshToken
) {
}
