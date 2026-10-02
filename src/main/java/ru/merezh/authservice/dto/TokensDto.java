package ru.merezh.authservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Возвращаемые токены")
public record TokensDto(
        @Schema(description = "Токен аутентификации")
        String accessToken,

        @Schema(description = "Рефреш токен")
        String refreshToken
) {
}
