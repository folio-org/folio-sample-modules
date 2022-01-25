package org.folio.hello;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Main Spring Boot application class
 */
@SpringBootApplication
@EnableFeignClients
public class HelloApplication {
    /**
     * Run the Spring Boot application
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(HelloApplication.class, args);
    }
}
