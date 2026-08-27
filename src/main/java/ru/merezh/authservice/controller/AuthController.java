package ru.merezh.authservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.merezh.authservice.dto.RefreshTokenDto;
import ru.merezh.authservice.dto.TokensDto;
import ru.merezh.authservice.dto.user.UserLoginFormDto;
import ru.merezh.authservice.dto.user.UserRegisterDto;
import ru.merezh.authservice.dto.user.UserRegisterFormDto;
import ru.merezh.authservice.service.auth.AuthService;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<UserRegisterDto> registerUser(@RequestBody @Valid UserRegisterFormDto registerData) {
        return ResponseEntity.ok().body(authService.registerUser(registerData));
    }

    @PostMapping("/login")
    public ResponseEntity<TokensDto> loginUser(@RequestBody @Valid UserLoginFormDto loginData) {
        return ResponseEntity.ok().body(authService.loginUser(loginData));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logoutUser(@RequestHeader("X-User-Id") long userId) {
        return ResponseEntity.ok().body(authService.logoutUser(userId));
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokensDto> refreshTokens(@RequestBody @Valid RefreshTokenDto refreshToken) {
        return ResponseEntity.ok().body(authService.refreshTokens(refreshToken));
    }
}
