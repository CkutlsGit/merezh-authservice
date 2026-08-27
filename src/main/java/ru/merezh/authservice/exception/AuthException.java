package ru.merezh.authservice.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AuthException extends RuntimeException {

    private HttpStatus code;

    public AuthException(String message) {
        super(message);
        this.code = HttpStatus.INTERNAL_SERVER_ERROR;
    }

    public AuthException(String message, HttpStatus code) {
        super(message);
        this.code = code;
    }
}
