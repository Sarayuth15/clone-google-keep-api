package com.googlekeep.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    private static final String BEARER_AUTH = "bearerAuth";

    @Value("${server.port:8080}")
    private String serverPort;

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                // Server list shown in Swagger UI dropdown
                .servers(List.of(
                        new Server().url("http://localhost:" + serverPort).description("Local Development"),
                        new Server().url("https://api.yourdomain.com").description("Production")
                ))
                // API info
                .info(new Info()
                        .title("Google Keep Clone API")
                        .description("""
                    REST API replicating Google Keep features:
                    - 📝 Notes (text, checklist, image)
                    - 🎨 Colors & 📌 Pinning
                    - 🗄️ Archive & 🗑️ Trash
                    - 🏷️ Labels & 🔍 Search
                    - ⏰ Reminders & 👥 Collaboration
                    
                    **Authentication:** Use `/api/auth/login` to get a JWT token,
                    then click **Authorize** and enter: `<your_token>`
                    """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Google Keep Clone")
                                .email("dev@googlekeep.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                // JWT Bearer scheme definition
                .components(new Components()
                        .addSecuritySchemes(BEARER_AUTH, new SecurityScheme()
                                .name(BEARER_AUTH)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Paste your JWT access token here (without 'Bearer ' prefix)")))
                // Apply JWT security globally to ALL endpoints
                .addSecurityItem(new SecurityRequirement().addList(BEARER_AUTH));
    }
}