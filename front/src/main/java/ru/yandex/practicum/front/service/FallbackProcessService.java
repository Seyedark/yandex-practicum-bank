package ru.yandex.practicum.front.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import ru.yandex.practicum.front.dto.AccountDto;
import ru.yandex.practicum.front.dto.AccountWithUsersDto;
import ru.yandex.practicum.front.dto.ExchangeDto;
import ru.yandex.practicum.front.enums.ErrorMessageEnum;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class FallbackProcessService {

    public List<String> basicUnprocessableEntityFallback(Exception exception) {
        List<String> checkErrorList = new ArrayList<>();
        if (exception instanceof HttpClientErrorException.UnprocessableEntity) {
            HttpClientErrorException.UnprocessableEntity unprocessableEntity =
                    (HttpClientErrorException.UnprocessableEntity) exception;
            log.error(exception.getMessage());
            List<String> responseErrorList = unprocessableEntity.getResponseBodyAs(new ParameterizedTypeReference<>() {
            });
            checkErrorList.addAll(responseErrorList);
            return checkErrorList;
        } else {
            log.error(exception.getMessage());
            checkErrorList.add(ErrorMessageEnum.SERVICE_ERROR.getMessage());
            return checkErrorList;
        }
    }

    public AccountDto getAccountFallback(String login, Exception exception) {
        if (exception instanceof HttpClientErrorException.BadRequest) {
            log.error(exception.getMessage());
            throw new UsernameNotFoundException(ErrorMessageEnum.USER_NOT_FOUND.getMessage().formatted(login, "http://localhost:8081/sign-up"));
        } else {
            log.error(exception.getMessage());
            throw new UsernameNotFoundException(ErrorMessageEnum.SERVICE_ERROR.getMessage());
        }
    }

    public AccountWithUsersDto getAccountWithAllUsersFallback(String login, Exception exception) throws Exception {
        log.error(exception.getMessage());
        throw exception;
    }

    public List<ExchangeDto> fallbackGetExchangeDtoList(Exception exception) {
        log.error((exception.getMessage()));
        return null;
    }
}