package com.mycompany.demo.warehouse.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class OpenApiConfig {
    private static final String SERVICE_CONFIG_NAME = "Warehouse service";
    private static final String SERVICE_CONFIG_VERSION = "2.0";
    @Bean
    public OpenAPI api() {
        return new OpenAPI()
                .info(new Info().title(SERVICE_CONFIG_NAME).version(SERVICE_CONFIG_VERSION))
                .externalDocs(new ExternalDocumentation().description("Docs"));
    }
}