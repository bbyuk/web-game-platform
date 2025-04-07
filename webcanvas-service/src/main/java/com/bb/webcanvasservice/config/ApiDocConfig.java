package com.bb.webcanvasservice.config;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "webcanvas-service API 문서",
                version = "v1.0.0-BETA",
                description = "web-game-platform project 중 webcanvas-service에서 제공하는 API 문서"
        )
)
public class ApiDocConfig {
}
