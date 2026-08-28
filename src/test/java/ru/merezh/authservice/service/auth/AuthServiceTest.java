package ru.merezh.authservice.service.auth;

import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.client.RestTemplate;
import ru.merezh.authservice.dto.RefreshTokenDto;
import ru.merezh.authservice.dto.TokensDto;
import ru.merezh.authservice.dto.user.*;
import ru.merezh.authservice.entity.UserAuth;
import ru.merezh.authservice.exception.AuthException;
import ru.merezh.authservice.repository.UserAuthRepository;
import ru.merezh.authservice.service.JwtService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserAuthRepository userAuthRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private AuthService authService;

    @Value("${service.users.url}")
    private String baseUrlUserService;

    @Test
    void registerUser_NullResponse_ThrowsAuthException() {
        UserRegisterFormDto mockUserRegisterFormDto = new UserRegisterFormDto(
                "test@gmail.test",
                "test",
                "123456test"
        );

        when(restTemplate.postForObject(
                eq(baseUrlUserService + "/create"),
                any(UserRegisterRequestDto.class),
                eq(UserResponseDto.class)
        )).thenReturn(null);

        AuthException result = assertThrows(AuthException.class, () -> {
            authService.registerUser(mockUserRegisterFormDto);
        });

        String expected = "Проблема с получением данных";

        assertEquals(expected, result.getMessage());
    }

    @Test
    void registerUser_ValidData_SaveUser() {
        UserRegisterFormDto mockUserRegisterFormDto = new UserRegisterFormDto(
                "test@gmail.test",
                "test",
                "123456test"
        );
        UserResponseDto mockUserResponseDto = new UserResponseDto(
                1L,
                "USER"
        );
        TokensDto mockTokens = new TokensDto("accessToken", "refreshToken");

        when(restTemplate.postForObject(
                eq(baseUrlUserService + "/create"),
                any(UserRegisterRequestDto.class),
                eq(UserResponseDto.class)
        )).thenReturn(mockUserResponseDto);
        when(jwtService.getTokens(mockUserResponseDto.id(), mockUserResponseDto.role())).thenReturn(mockTokens);

        UserRegisterDto result = authService.registerUser(mockUserRegisterFormDto);

        assertNotNull(result);
        assertEquals(mockUserResponseDto, result.userResponseDto());
        assertEquals(mockTokens, result.tokensDto());
    }

    @Test
    void loginUser_NullResponse_ThrowsAuthException() {
        UserLoginFormDto mockUserLoginFormDto = new UserLoginFormDto(
                "test@gmail.test",
                "123456test"
        );

        when(restTemplate.postForObject(
                eq(baseUrlUserService + "/validate"),
                any(UserLoginFormDto.class),
                eq(UserResponseDto.class)
        )).thenReturn(null);

        AuthException result = assertThrows(AuthException.class, () -> {
            authService.loginUser(mockUserLoginFormDto);
        });

        String expected = "Проблема с получением данных";

        assertEquals(expected, result.getMessage());
    }

    @Test
    void loginUser_ValidData_SaveUser() {
        UserLoginFormDto mockUserLoginFormDto = new UserLoginFormDto(
                "test@gmail.test",
                "123456test"
        );
        UserResponseDto mockResponse = new UserResponseDto(
                1L,
                "USER"
        );
        UserAuth mockUserAuth = new UserAuth(
                1L,
                "hashedRefreshToken"
        );
        TokensDto mockTokens = new TokensDto("accessToken", "refreshToken");

        when(restTemplate.postForObject(
                eq(baseUrlUserService + "/validate"),
                any(UserLoginFormDto.class),
                eq(UserResponseDto.class)
        )).thenReturn(mockResponse);
        when(jwtService.getTokens(mockResponse.id(), mockResponse.role()))
                .thenReturn(mockTokens);
        when(userAuthRepository.getUserAuthsById(mockResponse.id()))
                .thenReturn(Optional.of(mockUserAuth));

        TokensDto result = authService.loginUser(mockUserLoginFormDto);

        assertNotNull(result);
        assertEquals(mockTokens, result);

        verify(jwtService).getTokens(eq(mockResponse.id()), eq(mockResponse.role()));
    }

    @Test
    void refreshTokens_NotFoundUser_ThrowsAuthException() {
        RefreshTokenDto mockRefreshTokenDto = new RefreshTokenDto("refreshToken");

        when(jwtService.getUserIdFromToken(mockRefreshTokenDto.refreshToken())).thenReturn(1L);
        when(userAuthRepository.getUserAuthsById(jwtService.getUserIdFromToken(mockRefreshTokenDto.refreshToken())))
                .thenReturn(Optional.empty());

        AuthException result = assertThrows(AuthException.class, () -> {
            authService.refreshTokens(mockRefreshTokenDto);
        });

        String expected = "Отсутствует сессия пользователя";

        assertEquals(expected, result.getMessage());
    }

    @Test
    void refreshTokens_NotValidToken_ThrowsAuthException() {
        RefreshTokenDto mockRefreshTokenDto = new RefreshTokenDto("refreshToken");
        UserAuth mockUserAuth = new UserAuth(
                1L,
                "tokenHash"
        );

        when(jwtService.getUserIdFromToken(mockRefreshTokenDto.refreshToken())).thenReturn(1L);
        when(userAuthRepository.getUserAuthsById(jwtService.getUserIdFromToken(mockRefreshTokenDto.refreshToken())))
                .thenReturn(Optional.of(mockUserAuth));
        when(jwtService.checkValidateRefreshToken(mockRefreshTokenDto.refreshToken(), mockUserAuth.getId()))
                .thenReturn(false);

        AuthException result = assertThrows(AuthException.class, () -> {
            authService.refreshTokens(mockRefreshTokenDto);
        });

        String expected = "Токен не валидный";

        assertEquals(expected, result.getMessage());
    }
}