package ru.yandex.practicum.front.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import ru.yandex.practicum.front.dto.*;
import ru.yandex.practicum.front.enums.KeycloakEnum;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {

    @Value("${urls.account.get.account}")
    private String getAccountUrl;

    @Value("${urls.account.create}")
    private String createAccountUrl;

    @Value("${urls.account.patch.password}")
    private String patchPasswordAccountUrl;

    @Value("${urls.account.patch.info}")
    private String patchInfoAccountUrl;

    @Value("${urls.account.get.full}")
    private String getAccountWithUsersUrl;

    private final RestTemplate restTemplate;
    private final OAuth2Service oAuth2Service;
    private final PasswordEncoder passwordEncoder;
    private final FallbackProcessService fallbackProcessService;

    @Retry(name = "accountService")
    @CircuitBreaker(name = "accountService", fallbackMethod = "fallbackGetAccountByUsername")
    public AccountDto getAccountByUsername(String login) {
        HttpEntity<Void> request = new HttpEntity<>(oAuth2Service.formHeadersWithToken(KeycloakEnum.ACCOUNT));
        ResponseEntity<AccountDto> response = restTemplate.exchange(getAccountUrl, HttpMethod.GET, request,
                AccountDto.class, login);
        return response.getBody();
    }

    @Retry(name = "accountService")
    @CircuitBreaker(name = "accountService", fallbackMethod = "fallbackGetAccountWithAllUsers")
    public AccountWithUsersDto getAccountWithAllUsers(String login) {
        HttpEntity<Void> request = new HttpEntity<>(oAuth2Service.formHeadersWithToken(KeycloakEnum.ACCOUNT));
        ResponseEntity<AccountWithUsersDto> response = restTemplate.exchange(getAccountWithUsersUrl, HttpMethod.GET,
                request, AccountWithUsersDto.class, login);
        return response.getBody();
    }

    @Retry(name = "accountService")
    @CircuitBreaker(name = "accountService", fallbackMethod = "fallbackCreateAccount")
    public List<String> createAccount(String login, String password, String firstName, String lastName, String birthdate,
                                      String email) {
        AccountCreateRequestDto accountCreateRequestDto = new AccountCreateRequestDto();
        accountCreateRequestDto.setLogin(login);
        accountCreateRequestDto.setPassword(formPassword(password));
        accountCreateRequestDto.setFirstName(firstName);
        accountCreateRequestDto.setLastName(lastName);
        accountCreateRequestDto.setEmail(email);
        accountCreateRequestDto.setBirthDate(LocalDate.parse(birthdate));
        HttpEntity<AccountCreateRequestDto> request =
                new HttpEntity<>(accountCreateRequestDto, oAuth2Service.formHeadersWithToken(KeycloakEnum.ACCOUNT));
        restTemplate.postForObject(createAccountUrl, request, Void.class);
        UserDetails userDetails = User.withUsername(login)
                .password(password)
                .authorities(Collections.emptyList())
                .build();
        setAuth(userDetails);
        return new ArrayList<>();
    }

    @Retry(name = "accountService")
    @CircuitBreaker(name = "accountService", fallbackMethod = "fallbackChangePassword")
    public List<String> changePassword(String userName, String password) {
        AccountPasswordChangeDto accountPasswordChangeDto = new AccountPasswordChangeDto();
        accountPasswordChangeDto.setLogin(userName);
        accountPasswordChangeDto.setPassword(formPassword(password));
        HttpEntity<AccountPasswordChangeDto> request =
                new HttpEntity<>(accountPasswordChangeDto, oAuth2Service.formHeadersWithToken(KeycloakEnum.ACCOUNT));
        restTemplate.patchForObject(patchPasswordAccountUrl, request, Void.class);
        UserDetails userDetails = User.withUsername(userName)
                .password(password)
                .authorities(Collections.emptyList())
                .build();
        setAuth(userDetails);
        return new ArrayList<>();
    }

    @Retry(name = "accountService")
    @CircuitBreaker(name = "accountService", fallbackMethod = "fallbackChangeInfo")
    public List<String> changeInfo(String username, String firstName, String lastName, String birthdate) {
        AccountInfoChangeDto accountInfoChangeDto = new AccountInfoChangeDto();
        accountInfoChangeDto.setLogin(username);
        accountInfoChangeDto.setFirstName(firstName);
        accountInfoChangeDto.setLastName(lastName);
        accountInfoChangeDto.setBirthDate(LocalDate.parse(birthdate));
        HttpEntity<AccountInfoChangeDto> request =
                new HttpEntity<>(accountInfoChangeDto, oAuth2Service.formHeadersWithToken(KeycloakEnum.ACCOUNT));
        restTemplate.patchForObject(patchInfoAccountUrl, request, Void.class);
        return new ArrayList<>();

    }

    private String formPassword(String password) {
        return password.trim().length() < 4 ? password : passwordEncoder.encode(password);
    }

    private void setAuth(UserDetails userDetails) {
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = attr.getRequest();
        HttpSession session = request.getSession(true);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());
    }

    public List<String> fallbackCreateAccount(String login, String password, String firstName, String lastName, String birthdate,
                                              String email, Exception exception) {
        return fallbackProcessService.basicUnprocessableEntityFallback(exception);
    }

    public List<String> fallbackChangePassword(String userName, String password, Exception exception) {
        return fallbackProcessService.basicUnprocessableEntityFallback(exception);
    }

    public List<String> fallbackChangeInfo(String username, String firstName, String lastName, String birthdate, Exception exception) {
        return fallbackProcessService.basicUnprocessableEntityFallback(exception);
    }

    public AccountDto fallbackGetAccountByUsername(String login, Exception exception) {
        return fallbackProcessService.getAccountFallback(login, exception);
    }

    public AccountWithUsersDto fallbackGetAccountWithAllUsers(String login, Exception exception) throws Exception {
        return fallbackProcessService.getAccountWithAllUsersFallback(login, exception);
    }
}