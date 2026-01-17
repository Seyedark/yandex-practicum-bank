package ru.yandex.practicum.generator.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.generator.dto.ExchangeDtoList;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaSenderService {
    private final KafkaTemplate<String, ExchangeDtoList> kafkaTemplate;

    @Value("${spring.kafka.topic.exchange}")
    private String exchangeTopic;

    public void sendExchangeDtoList(ExchangeDtoList exchangeDtoList) {
        UUID uuid = UUID.randomUUID();
        try {
            kafkaTemplate.send(exchangeTopic, uuid.toString(), exchangeDtoList);
            log.info("New exchange successfully generated");
        } catch (Exception e) {
            log.error("Error while generate new exchange {}", e.getMessage());
        }
    }
}