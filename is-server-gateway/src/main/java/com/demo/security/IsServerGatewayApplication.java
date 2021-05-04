package com.demo.security;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

@EnableZuulProxy
@SpringBootApplication
public class IsServerGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(IsServerGatewayApplication.class, args);
    }

}
