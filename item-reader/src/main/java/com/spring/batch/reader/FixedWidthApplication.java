package com.spring.batch.reader;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableBatchProcessing
@SpringBootApplication
public class FixedWidthApplication {

    public static void main(String[] args) {
        SpringApplication.run(FixedWidthApplication.class, args);
    }
}
