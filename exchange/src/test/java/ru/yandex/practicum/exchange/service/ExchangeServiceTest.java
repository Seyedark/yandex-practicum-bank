package ru.yandex.practicum.exchange.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.exchange.SpringBootPostgreSQLBase;
import ru.yandex.practicum.exchange.TestSecurityConfig;
import ru.yandex.practicum.exchange.dao.entity.ExchangeEntity;
import ru.yandex.practicum.exchange.dao.repository.ExchangeRepository;
import ru.yandex.practicum.exchange.dto.ConvertRequestDto;
import ru.yandex.practicum.exchange.dto.ConvertResponseDto;
import ru.yandex.practicum.exchange.dto.ExchangeDto;
import ru.yandex.practicum.exchange.enums.CurrencyEnum;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Import(TestSecurityConfig.class)
@DisplayName("Класс для проверки взаимодействия с сервисом текущих валют и с базой")
public class ExchangeServiceTest extends SpringBootPostgreSQLBase {

    @Autowired
    ExchangeRepository exchangeRepository;

    @Autowired
    ExchangeService exchangeService;

    @BeforeEach
    void cleanup() {
        exchangeRepository.deleteAll();
    }

    @Test
    @DisplayName("Проверка получения курсов валют")
    void getExchangeDtoListTest() {
        ExchangeEntity exchangeEntity = new ExchangeEntity();
        exchangeEntity.setCurrency(CurrencyEnum.USD.name());
        exchangeEntity.setPurchaseRate(BigDecimal.TWO);
        exchangeEntity.setSellingRate(BigDecimal.ONE);

        exchangeRepository.save(exchangeEntity);

        List<ExchangeDto> exchangeDtoList = exchangeService.getExchangeDtoList();

        assertEquals(1, exchangeDtoList.size());

        assertEquals(exchangeEntity.getCurrency(), exchangeDtoList.get(0).getCurrency());
        assertThat(exchangeEntity.getPurchaseRate()).isEqualByComparingTo(exchangeDtoList.get(0).getPurchaseRate());
        assertThat(exchangeEntity.getSellingRate()).isEqualByComparingTo(exchangeDtoList.get(0).getSellingRate());
    }

    @Test
    @DisplayName("Проверка сохранения")
    void saveExchangeTest() {
        ExchangeDto exchangeDto = new ExchangeDto();
        exchangeDto.setCurrency(CurrencyEnum.USD.name());
        exchangeDto.setPurchaseRate(BigDecimal.TWO);
        exchangeDto.setSellingRate(BigDecimal.ONE);

        exchangeService.saveExchange(List.of(exchangeDto));

        List<ExchangeEntity> exchangeEntityList = exchangeRepository.findAll();

        assertEquals(1, exchangeEntityList.size());
        assertEquals(exchangeDto.getCurrency(), exchangeEntityList.get(0).getCurrency());
        assertThat(exchangeDto.getPurchaseRate()).isEqualByComparingTo(exchangeEntityList.get(0).getPurchaseRate());
        assertThat(exchangeDto.getSellingRate()).isEqualByComparingTo(exchangeEntityList.get(0).getSellingRate());
    }


    @Test
    @DisplayName("Проверка конвертации разных валют")
    void convertTest() {
        ExchangeEntity exchangeEntityFirst = new ExchangeEntity();
        exchangeEntityFirst.setCurrency(CurrencyEnum.USD.name());
        exchangeEntityFirst.setPurchaseRate(BigDecimal.valueOf(6));
        exchangeEntityFirst.setSellingRate(BigDecimal.valueOf(4));

        ExchangeEntity exchangeEntitySecond = new ExchangeEntity();
        exchangeEntitySecond.setCurrency(CurrencyEnum.CNY.name());
        exchangeEntitySecond.setPurchaseRate(BigDecimal.valueOf(24));
        exchangeEntitySecond.setSellingRate(BigDecimal.valueOf(2));

        ConvertRequestDto convertRequestDto = new ConvertRequestDto();
        convertRequestDto.setCurrencyFrom(CurrencyEnum.USD.name());
        convertRequestDto.setCurrencyTo(CurrencyEnum.CNY.name());
        convertRequestDto.setConvertAmount(BigDecimal.valueOf(24));

        exchangeRepository.saveAll(List.of(exchangeEntityFirst, exchangeEntitySecond));
        ConvertResponseDto convertResponseDto = exchangeService.convert(convertRequestDto);

        assertThat(convertResponseDto.getConvertedAmount()).isEqualByComparingTo(BigDecimal.valueOf(4));
    }
}