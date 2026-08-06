package com.rabittel.notificationservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * SpringDoc / Swagger UI configuration.
 */
@Configuration
public class OpenApiConfiguration {

    @Bean
    public OpenAPI notificationServiceOpenAPI() {

        return new OpenAPI()
                .info(new Info()
                        .title("RabitTel — Notification Service API")
                        .description("REST API for managing and dispatching notifications across multiple channels (Email, In-App, SMS, WhatsApp, Microsoft Teams).")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("RabitTel Engineering")
                                .email("engineering@rabittel.com"))
                        .license(new License()
                                .name("Proprietary")
                                .url("https://rabittel.com")));
    }
}
