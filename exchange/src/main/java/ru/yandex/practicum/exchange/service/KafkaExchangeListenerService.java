package ru.yandex.practicum.exchange.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.exchange.dto.ExchangeDtoList;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaExchangeListenerService {

    private final ExchangeService exchangeService;

    @KafkaListener(topics = "${spring.kafka.topic.exchange}",
            groupId = "${spring.kafka.consumer.group-id}")
    public void listen(ExchangeDtoList exchangeDtoList) {
        log.info("Exchange message received: {}", exchangeDtoList);
        exchangeService.saveExchange(exchangeDtoList.getExchangeDtoList());
    }
}