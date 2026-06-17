package com.gruapim;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAsync
@EnableScheduling
public class GruapimApplication {
    public static void main(String[] args) {
        SpringApplication.run(GruapimApplication.class, args);
    }
}
