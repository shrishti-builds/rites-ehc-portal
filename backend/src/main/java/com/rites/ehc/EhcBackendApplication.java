package com.rites.ehc;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class EhcBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(EhcBackendApplication.class, args);
    }

    @Bean
    public CommandLineRunner initDatabase() {
        return args -> {
            try {
                SqlServerBootstrap.ensureSchema();
                SeedData.seedIfNeeded();
            } catch (Exception e) {
                System.err.println("Database initialization failed: " + e.getMessage());
                e.printStackTrace();
            }
        };
    }
}

