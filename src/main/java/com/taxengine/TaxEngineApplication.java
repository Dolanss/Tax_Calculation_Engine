package com.taxengine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class TaxEngineApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaxEngineApplication.class, args);
    }
}
