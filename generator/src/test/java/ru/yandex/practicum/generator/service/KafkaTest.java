package ru.yandex.practicum.generator.service;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.ActiveProfiles;
import ru.yandex.practicum.generator.dto.ExchangeDtoList;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
@EmbeddedKafka(partitions = 1,
        topics = {"exchange-dto-list-topic"},
        brokerProperties = {"listeners=PLAINTEXT://localhost:9093",
                "port=9093"})
@Import(TestKafkaConfig.class)
public class KafkaTest {

    @Autowired
    private GeneratorService generatorService;

    @Autowired
    private ConsumerFactory<String, ExchangeDtoList> consumerFactory;

    @Test
    @DisplayName("Проверка отправки сообщения с курсами валют")
    void shouldSendExchangeRatesSuccessfully() {
        generatorService.generateNewExchangeDtoList();

        try (Consumer<String, ExchangeDtoList> consumer =
                     consumerFactory.createConsumer("test-group", "test-client")) {

            consumer.subscribe(List.of("exchange-dto-list-topic"));

            ConsumerRecords<String, ExchangeDtoList> records =
                    KafkaTestUtils.getRecords(consumer);

            assertThat(records.count()).isGreaterThan(0);

            ConsumerRecord<String, ExchangeDtoList> received =
                    records.iterator().next();
            assertThat(received.value()).isNotNull();
            assertEquals(2, received.value().getExchangeDtoList().size());
        }
    }
}