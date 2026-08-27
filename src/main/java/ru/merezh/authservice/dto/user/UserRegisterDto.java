package ru.merezh.authservice.dto.user;

import ru.merezh.authservice.dto.TokensDto;

public record UserRegisterDto(
        UserResponseDto userResponseDto,
        TokensDto tokensDto
) {
}
