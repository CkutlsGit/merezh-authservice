package ru.merezh.authservice.dto;

public record TokensDto(
        String accessToken,
        String refreshToken
) {
}
