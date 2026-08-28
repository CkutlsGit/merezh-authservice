package ru.merezh.authservice.service.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import ru.merezh.authservice.dto.RefreshTokenDto;
import ru.merezh.authservice.dto.TokensDto;
import ru.merezh.authservice.dto.user.*;
import ru.merezh.authservice.entity.UserAuth;
import ru.merezh.authservice.exception.AuthException;
import ru.merezh.authservice.repository.UserAuthRepository;
import ru.merezh.authservice.service.JwtService;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserAuthRepository userAuthRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RestTemplate restTemplate;

    @Value("${service.users.url}")
    private String baseUrlUserService;

    @Transactional
    public UserRegisterDto registerUser(UserRegisterFormDto userRegisterFormDto) {
        UserRegisterRequestDto request = new UserRegisterRequestDto(
                userRegisterFormDto.email(),
                userRegisterFormDto.login(),
                passwordEncoder.encode(userRegisterFormDto.password())
        );

        UserResponseDto response = restTemplate.postForObject(
                baseUrlUserService + "/create",
                request,
                UserResponseDto.class
        );

        if (response == null) {
            throw new AuthException("Проблема с получением данных");
        }

        TokensDto tokens = jwtService.getTokens(response.id(), response.role());

        userAuthRepository.save(new UserAuth(
                response.id(),
                hashToken(tokens.refreshToken())
        ));

        return new UserRegisterDto(
                response,
                tokens
        );
    }

    @Transactional
    public TokensDto loginUser(UserLoginFormDto userLoginFormDto) {
        UserResponseDto response = restTemplate.postForObject(
                baseUrlUserService + "/validate",
                userLoginFormDto,
                UserResponseDto.class
        );

        if (response == null) {
            throw new AuthException("Проблема с получением данных");
        }

        TokensDto tokens = jwtService.getTokens(response.id(), response.role());
        String hashToken = hashToken(tokens.refreshToken());

        UserAuth userAuth = userAuthRepository.getUserAuthsById(response.id())
                .orElseGet(() -> new UserAuth(response.id(), hashToken));

        userAuth.setTokenHash(hashToken);
        userAuthRepository.save(userAuth);

        return tokens;
    }

    @Transactional
    public String logoutUser(long userId) {
        if (!userAuthRepository.existsUserAuthsById(userId)) {
            throw new AuthException("Отсутсвует сессия пользователя", HttpStatus.NOT_FOUND);
        }

        userAuthRepository.deleteById(userId);

        return "Успешный выход";
    }

    @Transactional
    public TokensDto refreshTokens(RefreshTokenDto token) {
        UserAuth userAuth = userAuthRepository.getUserAuthsById(jwtService.getUserIdFromToken(token.refreshToken()))
                .orElseThrow(() -> new AuthException("Отсутствует сессия пользователя", HttpStatus.NOT_FOUND));

        if (!jwtService.checkValidateRefreshToken(token.refreshToken(), userAuth.getId())) {
            throw new AuthException("Токен не валидный", HttpStatus.BAD_REQUEST);
        }

        if (!userAuth.getTokenHash().equals(hashToken(token.refreshToken()))) {
            throw new AuthException("Токен сессии не валидный", HttpStatus.BAD_REQUEST);
        }

        TokensDto tokens = jwtService.getTokens(userAuth.getId(), jwtService.getUserRoleFromToken(token.refreshToken()));

        userAuth.setTokenHash(hashToken(tokens.refreshToken()));

        return tokens;
    }

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes =digest.digest(token.getBytes(StandardCharsets.UTF_8));

            return HexFormat.of().formatHex(hashBytes);
        }
        catch (Exception e) {
            throw new RuntimeException("Ошбика при работе с токеном");
        }
    }
}
