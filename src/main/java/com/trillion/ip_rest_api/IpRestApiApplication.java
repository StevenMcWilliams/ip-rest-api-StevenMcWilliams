package com.trillion.ip_rest_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Launches our application.
 */
@SpringBootApplication
public class IpRestApiApplication {
    /**
     * Name of entry in application.properties for controlling JDBC insert batching on repository saveAll calls.
     */
    public static final String JDBC_BATCH_SIZE = "spring.jpa.properties.hibernate.jdbc.batch_size=256";

    /**
     * Launches SpringBoot to run our application.
     * 
     * @param args Optional CLI arguments to pass to SpringBoot.
     */
    public static void main(String[] args) {
        SpringApplication.run(IpRestApiApplication.class, args);
    }
}
