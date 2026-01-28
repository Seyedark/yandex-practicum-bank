package ru.yandex.practicum.front.config.resttemplate;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .requestFactory(() -> {
                    HttpComponentsClientHttpRequestFactory factory =
                            new HttpComponentsClientHttpRequestFactory();
                    factory.setConnectTimeout(Duration.ofMillis(5000));
                    factory.setConnectionRequestTimeout(Duration.ofMillis(7000));
                    return factory;
                })
                .build();
    }
}