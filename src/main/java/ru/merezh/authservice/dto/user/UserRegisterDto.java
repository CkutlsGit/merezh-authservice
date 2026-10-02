package ru.merezh.authservice.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.merezh.authservice.dto.TokensDto;

@Schema(description = "Возвращаемые данные о зарегистированном пользователе")
public record UserRegisterDto(
        UserResponseDto userResponseDto,
        TokensDto tokensDto
) {
}
