package ru.merezh.authservice.exception.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;

import java.net.SocketTimeoutException;
import java.rmi.UnknownHostException;

@RestControllerAdvice
@Slf4j
@Order(3)
public class ServerExceptionHandler {

    @ExceptionHandler(SocketTimeoutException.class)
    public ResponseEntity<String> socketTimeoutExceptionHandler() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Не удалось подключиться к сервису и получить данные");
    }

    @ExceptionHandler(ResourceAccessException.class)
    public ResponseEntity<String> resourceAccessExceptionHandler() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Не удалось подключиться к сервису");
    }

    @ExceptionHandler(RestClientException.class)
    public ResponseEntity<String> restClientExceptionHandler(RestClientException e) {
        log.error("Ошибка при отправке запроса - {} : {}", e.getClass(), e.getMessage());

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Не удалось отправить запрос к подключению");
    }

    @ExceptionHandler(HttpServerErrorException.class)
    public ResponseEntity<String> httpServerErrorExceptionHandler(HttpServerErrorException e) {
        log.error("Ошибка сервера - {} : {}", e.getClass(), e.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Сервис временно недоступен");
    }
}
