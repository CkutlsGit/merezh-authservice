package ru.merezh.authservice.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRegisterFormDto(
        @NotBlank(message = "Почта обязательна к заполнению")
        @Email(message = "Неверный формат почты")
        String email,

        @NotBlank(message = "Логин обязателен к заполнению")
        @Size(min = 3, max = 30, message = "Минимальный и максимальный размер 3 и 30 символов")
        String login,

        @NotBlank(message = "Пароль обязателен к заполнению")
        @Size(min = 6, message = "Минимальная длина пароля 6 символов")
        String password
) {
}
