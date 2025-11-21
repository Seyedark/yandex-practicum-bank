package ru.yandex.practicum.front.config.security;


import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.front.dto.AccountDto;
import ru.yandex.practicum.front.service.AccountService;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class RemoteUserDetailsService implements UserDetailsService {

    private final AccountService accountService;

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        AccountDto accountDto = accountService.getAccountByUsername(login);
        return User.withUsername(accountDto.getLogin())
                .password(accountDto.getPassword())
                .authorities(Collections.emptyList())
                .build();
    }
}