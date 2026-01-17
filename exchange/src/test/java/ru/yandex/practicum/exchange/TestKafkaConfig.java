package ru.yandex.practicum.exchange;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import ru.yandex.practicum.exchange.dto.ExchangeDtoList;

import java.util.HashMap;
import java.util.Map;

@Configuration
@Profile("test")
public class TestKafkaConfig {
    @Bean
    public ProducerFactory<String, ExchangeDtoList> producerFactory(
            @Value("${spring.kafka.bootstrap-servers:localhost:9093}") String bootstrapServers) {

        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);


        return new DefaultKafkaProducerFactory<>(
                props,
                new StringSerializer(),
                new JsonSerializer<>()
        );
    }

    @Bean
    public KafkaTemplate<String, ExchangeDtoList> kafkaTemplate(
            ProducerFactory<String, ExchangeDtoList> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }
}