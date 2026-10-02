package ru.merezh.authservice.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Данные для аутентификации пользователя")
public record UserLoginFormDto(

        @NotBlank(message = "Почта обязательна к заполнению")
        @Email(message = "Неверный формат почты")
        @Schema(description = "Почта пользователя")
        String email,

        @NotBlank(message = "Пароль обязателен к заполнению")
        @Schema(description = "Пароль пользователя")
        String password
) {
}
