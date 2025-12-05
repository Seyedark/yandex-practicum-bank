package ru.yandex.practicum.blocker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class BlockerApplication {
    public static void main(String[] args) {
        SpringApplication.run(BlockerApplication.class, args);
    }
}
