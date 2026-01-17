package ru.yandex.practicum.exchange.kafka;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import ru.yandex.practicum.exchange.SpringBootPostgreSQLBase;
import ru.yandex.practicum.exchange.TestKafkaConfig;
import ru.yandex.practicum.exchange.TestSecurityConfig;
import ru.yandex.practicum.exchange.dto.ExchangeDto;
import ru.yandex.practicum.exchange.dto.ExchangeDtoList;
import ru.yandex.practicum.exchange.enums.CurrencyEnum;
import ru.yandex.practicum.exchange.service.ExchangeService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@SpringBootTest(properties = "spring.kafka.consumer.group-id=exchange-${random.uuid}")
@ActiveProfiles("test")
@EmbeddedKafka(partitions = 1,
        topics = "exchange-dto-list-topic",
        brokerProperties = {"listeners=PLAINTEXT://localhost:9093",
                "port=9093"})
@Import({TestKafkaConfig.class, TestSecurityConfig.class})
public class KafkaTest extends SpringBootPostgreSQLBase {

    @SpyBean
    private ExchangeService exchangeService;

    @Autowired
    private KafkaTemplate<String, ExchangeDtoList> kafkaTemplate;

    private final Long KAFKA_INITIALIZATION_TIMEOUT_MS = 3000L;

    @BeforeEach
    void waitForKafka() throws InterruptedException {
        Thread.sleep(KAFKA_INITIALIZATION_TIMEOUT_MS);
    }

    @Test
    @DisplayName("Проверка получения курсов валют и обработка для обновления")
    void shouldUpdateCurrencyRatesSuccessfully() {
        ExchangeDtoList exchangeDtoList = createTestExchangeDtoList();

        await().atMost(10, TimeUnit.SECONDS)
                .until(() -> kafkaTemplate != null);

        kafkaTemplate.send("exchange-dto-list-topic", exchangeDtoList);

        verify(exchangeService, timeout(30000).atLeastOnce())
                .saveExchange(any());
    }

    private ExchangeDtoList createTestExchangeDtoList() {
        ExchangeDtoList exchangeDtoList = new ExchangeDtoList();
        ExchangeDto exchangeDto = new ExchangeDto();
        exchangeDto.setCurrency(CurrencyEnum.USD.name());
        exchangeDto.setPurchaseRate(BigDecimal.TWO);
        exchangeDto.setSellingRate(BigDecimal.TEN);
        List<ExchangeDto> list = new ArrayList<>();
        list.add(exchangeDto);
        exchangeDtoList.setExchangeDtoList(list);
        return exchangeDtoList;
    }
}