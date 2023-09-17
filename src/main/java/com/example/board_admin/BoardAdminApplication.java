package com.example.board_admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan
@SpringBootApplication
public class BoardAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(BoardAdminApplication.class, args);
    }

}
