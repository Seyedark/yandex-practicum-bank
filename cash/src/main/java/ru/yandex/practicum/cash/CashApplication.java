package ru.yandex.practicum.cash;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableAspectJAutoProxy
@EnableDiscoveryClient
@SpringBootApplication
public class CashApplication {
	public static void main(String[] args) {
		SpringApplication.run(CashApplication.class, args);
	}
}