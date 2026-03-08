package com.googlekeep;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class GoogleKeepApplication {
    public static void main(String[] args) {
        SpringApplication.run(GoogleKeepApplication.class, args);
        String url = "http://localhost:8080/swagger-ui/index.html";
        System.out.println("----------------------------------------------------------");
        System.out.println("Application is running!");
        System.out.println("Access Swagger UI: {} "+ url);
        System.out.println("----------------------------------------------------------");
    }
}
