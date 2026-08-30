package ru.merezh.authservice.exception.controller;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
@Order(2)
public class JwtExceptionHandler {

    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<String> malformedJwtExceptionHandler() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Неверный формат токена");
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<String> expiredJwtExceptionHandler() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Срок токена истек");
    }

   @ExceptionHandler(JwtException.class)
    public ResponseEntity<String> jwtExceptionHandler(JwtException e) {
        log.error("Ошибка в классе при работе с токеном - {}", e.getClass());
        log.error("Ошибка при работе с токеном - {}", e.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Проблема с токеном");
   }
}
