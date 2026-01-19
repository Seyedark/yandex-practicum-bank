package ru.yandex.practicum.exchange.dao.repository;

import io.micrometer.observation.annotation.Observed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.exchange.dao.entity.ExchangeEntity;

@Repository
@Observed(name = "database", contextualName = "exchange-repository")
public interface ExchangeRepository extends JpaRepository<ExchangeEntity, Long> {
}
