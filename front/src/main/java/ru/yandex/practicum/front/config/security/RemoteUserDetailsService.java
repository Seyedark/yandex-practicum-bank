package ru.yandex.practicum.front.config.security;


import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.front.dto.AccountDto;
import ru.yandex.practicum.front.service.AccountService;
import ru.yandex.practicum.front.service.MetricService;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class RemoteUserDetailsService implements UserDetailsService {

    private final AccountService accountService;
    private final MetricService metricService;

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        try {
            AccountDto accountDto = accountService.getAccountByUsername(login);
            return User.withUsername(accountDto.getLogin())
                    .password(accountDto.getPassword())
                    .authorities(Collections.emptyList())
                    .build();
        } catch (Exception e) {
            metricService.failedLogin(login);
            throw e;
        }
    }
}