package it.unical.demacs.informatica.immobiliare_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ImmobiliareBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(ImmobiliareBackendApplication.class, args);
    }

}