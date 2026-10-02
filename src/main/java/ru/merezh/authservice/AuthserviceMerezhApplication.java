package ru.merezh.authservice;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(
        info = @Info(
                title ="Authservice Merezh",
                version = "v1.0",
                description ="Эндпоинты для аутентификации/авторизации и для работы с JWT-токенами"
        )
)
@SpringBootApplication
public class AuthserviceMerezhApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthserviceMerezhApplication.class, args);
	}

}
