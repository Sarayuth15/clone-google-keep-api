package com.googlekeep;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class GoogleKeepApplication {
    public static void main(String[] args) {
        SpringApplication.run(GoogleKeepApplication.class, args);
    }
}
