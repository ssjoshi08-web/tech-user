package com.example.userapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the User API Spring Boot application.
 *
 * <p>This application provides a REST API to manage and retrieve user
 * information. It follows enterprise-grade standards with layered
 * architecture, comprehensive testing, and production-ready configuration.</p>
 */
@SpringBootApplication
public class UserApiApplication {

    /**
     * Bootstraps the Spring Boot application.
     *
     * @param args command-line arguments passed at startup
     */
    public static void main(String[] args) {
        SpringApplication.run(UserApiApplication.class, args);
    }
}