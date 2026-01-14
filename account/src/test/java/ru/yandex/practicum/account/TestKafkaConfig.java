package ru.yandex.practicum.account;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import ru.yandex.practicum.account.dto.NotificationEmailRequestDto;

import java.util.HashMap;
import java.util.Map;

@Configuration
@Profile("test")
public class TestKafkaConfig {

    @Bean
    public ConsumerFactory<String, NotificationEmailRequestDto> consumerFactory(
            @Value("${spring.kafka.bootstrap-servers:localhost:9093}") String bootstrapServers) {

        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "test-group");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        JsonDeserializer<NotificationEmailRequestDto> deserializer = new JsonDeserializer<>(NotificationEmailRequestDto.class);
        deserializer.addTrustedPackages("ru.yandex.practicum.account.dto");
        deserializer.setUseTypeHeaders(false);
        deserializer.setRemoveTypeHeaders(true);

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                deserializer
        );
    }
}