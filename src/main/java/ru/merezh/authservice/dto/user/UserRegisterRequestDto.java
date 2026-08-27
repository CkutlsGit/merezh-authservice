package ru.merezh.authservice.dto.user;

public record UserRegisterRequestDto(
        String email,
        String login,
        String hashPassword
) {
}
