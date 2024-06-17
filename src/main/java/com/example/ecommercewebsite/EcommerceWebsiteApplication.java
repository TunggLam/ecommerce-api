package com.example.ecommercewebsite;

import org.keycloak.adapters.springboot.KeycloakSpringBootProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.ResponseEntity;
@EnableConfigurationProperties(KeycloakSpringBootProperties.class)
@SpringBootApplication
public class EcommerceWebsiteApplication {

    public static void main(String[] args) {
        SpringApplication.run(EcommerceWebsiteApplication.class, args);
    }

}
