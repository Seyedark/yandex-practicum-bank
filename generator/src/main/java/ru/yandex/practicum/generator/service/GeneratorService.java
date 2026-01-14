package ru.yandex.practicum.generator.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.generator.dto.ExchangeDto;
import ru.yandex.practicum.generator.dto.ExchangeDtoList;
import ru.yandex.practicum.generator.enums.CurrencyEnum;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class GeneratorService {
    private final KafkaTemplate<String, ExchangeDtoList> kafkaTemplate;

    @Value("${spring.kafka.topic.exchange}")
    private String exchangeTopic;

    @Scheduled(initialDelay = 60000, fixedRate = 60000)
    public void generateNewExchangeDtoList() {
        log.info("Start generate new exchange");
        List<ExchangeDto> list = new ArrayList<>();
        UUID uuid = UUID.randomUUID();
        for (CurrencyEnum currency : CurrencyEnum.values()) {
            if (currency.equals(CurrencyEnum.RUB)) {
                continue;
            }
            ExchangeDto exchangeDto = new ExchangeDto();
            int number = (int) (Math.random() * 9) + 2;
            exchangeDto.setCurrency(currency.name());
            exchangeDto.setPurchaseRate(BigDecimal.valueOf(number));
            exchangeDto.setSellingRate(BigDecimal.valueOf(number - 1));
            list.add(exchangeDto);
        }
        ExchangeDtoList exchangeDtoList = new ExchangeDtoList();
        exchangeDtoList.setExchangeDtoList(list);
        try {
            kafkaTemplate.send(exchangeTopic, uuid.toString(), exchangeDtoList);
            log.info("New exchange successfully generated");
        } catch (Exception e) {
            log.error("Error while generate new exchange {}", e.getMessage());
        }
    }
}