package com.inventory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Application Class for Inventory Management System
 * 
 * This is the entry point of our Spring Boot application.
 * @SpringBootApplication annotation enables:
 * - @Configuration: Tags the class as a source of bean definitions
 * - @EnableAutoConfiguration: Tells Spring Boot to start adding beans based on classpath settings
 * - @ComponentScan: Tells Spring to look for other components, configurations, and services
 * 
 * @author Inventory Management Team
 * @version 1.0
 */
@SpringBootApplication
public class InventoryManagementApplication {

    /**
     * Main method to run the Spring Boot application
     * 
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(InventoryManagementApplication.class, args);
        System.out.println("🚀 Inventory Management System Started Successfully!");
        System.out.println("📊 Access the application at: http://localhost:8080");
        System.out.println("📚 API Documentation available at: http://localhost:8080/swagger-ui.html");
    }
}
