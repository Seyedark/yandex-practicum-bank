package ru.yandex.practicum.generator.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.generator.dto.ExchangeDto;
import ru.yandex.practicum.generator.enums.CurrencyEnum;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GeneratorService {
    private final RestCallerService restCallerService;

    @Scheduled(initialDelay = 60000, fixedRate = 60000)
    public void generateNewExchangeDtoList() {
        log.info("Start generate new exchange");
        List<ExchangeDto> exchangeDtoList = new ArrayList<>();
        for (CurrencyEnum currency : CurrencyEnum.values()) {
            if (currency.equals(CurrencyEnum.RUB)) {
                continue;
            }
            ExchangeDto exchangeDto = new ExchangeDto();
            int number = (int) (Math.random() * 9) + 2;
            exchangeDto.setCurrency(currency.name());
            exchangeDto.setPurchaseRate(BigDecimal.valueOf(number));
            exchangeDto.setSellingRate(BigDecimal.valueOf(number - 1));
            exchangeDtoList.add(exchangeDto);
        }
        restCallerService.sendNewExchangeDtoList(exchangeDtoList);
    }
}