package ru.merezh.authservice.exception.controller;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import ru.merezh.authservice.exception.dto.ExceptionDto;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@RestControllerAdvice
@Order(1)
public class ServiceExceptionHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @ExceptionHandler(HttpClientErrorException.BadRequest.class)
    public ResponseEntity<ExceptionDto> httpClientErrorExceptionBadRequestHandler(HttpClientErrorException.BadRequest e) {
        return responseException(e.getStatusCode(), e.getResponseBodyAsString());
    }

    @ExceptionHandler(HttpClientErrorException.NotFound.class)
    public ResponseEntity<ExceptionDto> httpClientErrorExceptionNotFoundHandler(HttpClientErrorException.NotFound e) {
        return responseException(e.getStatusCode(), e.getResponseBodyAsString());
    }

    @ExceptionHandler(HttpClientErrorException.Conflict.class)
    public ResponseEntity<ExceptionDto> httpClientErrorExceptionConflictHandler(HttpClientErrorException.Conflict e) {
        return responseException(e.getStatusCode(), e.getResponseBodyAsString());
    }

    private String readBodyJson(String body) {
        JsonNode jsonNode = objectMapper.readTree(body);

        return jsonNode.get("message").asString();
    }

    private ResponseEntity<ExceptionDto> responseException(HttpStatusCode code, String body) {
        return ResponseEntity.status(code).body(new ExceptionDto(
                readBodyJson(body)
        ));
    }
}
