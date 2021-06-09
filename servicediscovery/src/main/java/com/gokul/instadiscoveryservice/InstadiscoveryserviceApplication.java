package com.gokul.instadiscoveryservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class InstadiscoveryserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(InstadiscoveryserviceApplication.class, args);
    }

}
