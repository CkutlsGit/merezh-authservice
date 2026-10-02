package ru.merezh.authservice.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Возвращаемые данные о пользователе")
public record UserResponseDto(

        @Schema(description = "Индефикатор пользователя")
        long id,

        @Schema(description = "Роль пользователя")
        String role
) {
}
