package ru.yandex.practicum.account.dao.repository;

import io.micrometer.observation.annotation.Observed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.account.dao.entity.NotificationEntity;

import java.util.List;

@Repository
@Observed(name = "database", contextualName = "notification-repository")
public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {
    List<NotificationEntity> findFirst100ByNotificationSentFalseOrderByCreatedAtAsc();
}