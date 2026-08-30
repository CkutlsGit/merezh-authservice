package ru.merezh.authservice.exception.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.merezh.authservice.exception.AuthException;
import ru.merezh.authservice.exception.dto.ExceptionDto;
import ru.merezh.authservice.exception.dto.ExceptionValidateDto;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
@Order(Integer.MAX_VALUE)
public class BaseExceptionHandler {

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ExceptionDto> authExceptionHandler(AuthException e) {
        return ResponseEntity.status(e.getCode()).body(new ExceptionDto(
                e.getMessage()
        ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionValidateDto> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
        Map<String, List<String>> errorMessage = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.groupingBy(
                        FieldError::getField,
                        Collectors.mapping(FieldError::getDefaultMessage, Collectors.toList())
                ));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ExceptionValidateDto(errorMessage));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> exceptionHandler(Exception e) {
        log.info("Класс исключения - {}", e.getClass());
        log.error("Ошибка исключения - {}", e.getMessage());

        return ResponseEntity.status(500).body("Ошибка сервиса");
    }
}
