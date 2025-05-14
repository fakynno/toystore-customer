package com.toystore.customer.infrastructure.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Toystore Customer API",
                version = "1.0.0",
                description = "API para Gestão Clientes para a Toystore"
        )
)
public class OpenApiConfig {
}
