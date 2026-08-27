package ru.merezh.authservice.exception.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.ResourceAccessException;

import java.net.SocketTimeoutException;
import java.rmi.UnknownHostException;

@RestControllerAdvice
public class ServerExceptionHandler {

    @ExceptionHandler(UnknownHostException.class)
    public ResponseEntity<String> unknownHostExceptionHandler() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Неизвестный сервис, подключение не удалось");
    }

    @ExceptionHandler(SocketTimeoutException.class)
    public ResponseEntity<String> socketTimeoutExceptionHandler() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Не удалось подключиться к сервису и получить данные");
    }

    @ExceptionHandler(ResourceAccessException.class)
    public ResponseEntity<String> resourceAccessExceptionHandler() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Не удалось подключиться к сервису");
    }
}
