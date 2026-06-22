package com.example.userapi.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI / Swagger configuration.
 *
 * <p>Defines the public-facing API documentation metadata and the servers
 * that should appear in the Swagger UI.</p>
 */
@Configuration
public class OpenApiConfig {

    /**
     * Builds the {@link OpenAPI} bean describing the API.
     *
     * @return a configured {@link OpenAPI} instance
     */
    @Bean
    public OpenAPI userApiOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("User API")
                        .description("Enterprise-grade Spring Boot 3 REST API for user management")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("API Support")
                                .email("support@example.com")
                                .url("https://example.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Local Development"),
                        new Server().url("https://api.example.com").description("Production")
                ));
    }
}