package ru.merezh.authservice.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserLoginFormDto(

        @NotBlank(message = "Почта обязательна к заполнению")
        @Email(message = "Неверный формат почты")
        String email,

        @NotBlank(message = "Пароль обязателен к заполнению")
        String password
) {
}
