package ru.yandex.practicum.account.dao.repository;

import io.micrometer.observation.annotation.Observed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.account.dao.entity.AccountEntity;

@Repository
@Observed(name = "database", contextualName = "account-repository")
public interface AccountRepository extends JpaRepository<AccountEntity, Long> {
    AccountEntity findByLogin(String login);
}