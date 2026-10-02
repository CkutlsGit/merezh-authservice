package ru.merezh.authservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Данные о рефреш токене")
public record RefreshTokenDto(
        @NotBlank(message = "Токен обязателен к заполнению")
        String refreshToken
) {
}
