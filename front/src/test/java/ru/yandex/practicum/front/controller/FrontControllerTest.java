package ru.yandex.practicum.front.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.front.dto.AccountWithUsersDto;
import ru.yandex.practicum.front.enums.ActionEnum;
import ru.yandex.practicum.front.enums.ErrorMessageEnum;
import ru.yandex.practicum.front.service.AccountService;
import ru.yandex.practicum.front.service.CashService;
import ru.yandex.practicum.front.service.TransferService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FrontController.class)
@DisplayName("Класс для основного контроллера и представлений")
public class FrontControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private AccountService accountService;
    @MockBean
    private CashService cashService;
    @MockBean
    private TransferService transferService;

    private final String TEST_LOGIN = "login";
    private final String TEST_PASSWORD = "12345";
    private final String TEST_FIRST_NAME = "Тест";
    private final String TEST_LAST_NAME = "Тестов";
    private final String TEST_EMAIL = "test@mail.ru";
    private final String TEST_BIRTHDATE = "1990-01-01";
    private final String TEST_CURRENCY = "RUB";


    @Test
    @WithMockUser(username = TEST_LOGIN)
    @DisplayName("Главная страница")
    void mainPageTest() throws Exception {
        // Given
        AccountWithUsersDto accountDto = new AccountWithUsersDto();
        accountDto.setEmail(TEST_EMAIL);
        accountDto.setFirstName(TEST_FIRST_NAME);
        accountDto.setLastName(TEST_LAST_NAME);
        accountDto.setBirthDate(LocalDate.parse(TEST_BIRTHDATE));
        accountDto.setShortAccountDtoList(new ArrayList<>());
        accountDto.setAccountBalanceDtoList(new ArrayList<>());

        when(accountService.getAccountWithAllUsers(TEST_LOGIN)).thenReturn(accountDto);


        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("main"))
                .andExpect(model().attribute("login", TEST_LOGIN))
                .andExpect(model().attribute("firstName", TEST_FIRST_NAME))
                .andExpect(model().attribute("lastName", TEST_LAST_NAME))
                .andExpect(model().attribute("birthdate", LocalDate.parse(TEST_BIRTHDATE)));
    }

    @Test
    @WithMockUser(username = TEST_LOGIN)
    @DisplayName("Страница регистрации")
    void signUpTest() throws Exception {
        mockMvc.perform(get("/sign-up"))
                .andExpect(status().isOk())
                .andExpect(view().name("signup"));
    }


    @Test
    @WithMockUser(username = TEST_LOGIN)
    @DisplayName("Успешное создание пользователя")
    void createAccountTestWithRedirectTest() throws Exception {
        when(accountService.createAccount(anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString()))
                .thenReturn(Collections.emptyList());
        mockMvc.perform(post("/account")
                        .with(csrf())
                        .param("login", TEST_LOGIN)
                        .param("password", TEST_PASSWORD)
                        .param("firstName", TEST_FIRST_NAME)
                        .param("lastName", TEST_LAST_NAME)
                        .param("email", TEST_EMAIL)
                        .param("birthdate", TEST_BIRTHDATE))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    @WithMockUser(username = TEST_LOGIN)
    @DisplayName("Создание пользователя с ошибкой")
    void createAccountTestWithErrorsTest() throws Exception {
        List<String> errorList = Arrays.asList(ErrorMessageEnum.SERVICE_ERROR.getMessage());
        when(accountService.createAccount(anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString()))
                .thenReturn(errorList);

        mockMvc.perform(post("/account")
                        .with(csrf())
                        .param("login", TEST_LOGIN)
                        .param("password", TEST_PASSWORD)
                        .param("firstName", TEST_FIRST_NAME)
                        .param("lastName", TEST_LAST_NAME)
                        .param("email", TEST_EMAIL)
                        .param("birthdate", TEST_BIRTHDATE))
                .andExpect(status().isOk())
                .andExpect(view().name("signup"))
                .andExpect(model().attribute("login", TEST_LOGIN))
                .andExpect(model().attribute("password", TEST_PASSWORD))
                .andExpect(model().attribute("firstName", TEST_FIRST_NAME))
                .andExpect(model().attribute("lastName", TEST_LAST_NAME))
                .andExpect(model().attribute("email", TEST_EMAIL))
                .andExpect(model().attribute("birthdate", TEST_BIRTHDATE))
                .andExpect(model().attribute("errorList", errorList));
    }


    @Test
    @WithMockUser(username = TEST_LOGIN)
    @DisplayName("Успешная смена пароля")
    void changePasswordSuccessTest() throws Exception {
        when(accountService.changePassword(TEST_LOGIN, TEST_PASSWORD + 1))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(post("/account/password")
                        .with(csrf())
                        .param("password", TEST_PASSWORD + 1))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attributeExists("errorPasswordList"));
    }

    @Test
    @WithMockUser(username = TEST_LOGIN)
    @DisplayName("Смена пароля с ошибкой")
    void changePasswordWithErrorsTest() throws Exception {
        List<String> errorList = Arrays.asList("Длина пароля должна быть больше 3 символов");
        when(accountService.changePassword(TEST_LOGIN, "123"))
                .thenReturn(errorList);

        mockMvc.perform(post("/account/password")
                        .with(csrf())
                        .param("password", "123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attribute("errorPasswordList", errorList));
    }

    @Test
    @WithMockUser(username = TEST_LOGIN)
    @DisplayName("Успешная смена данных пользователя")
    void changeInfoSuccessTest() throws Exception {
        when(accountService.changeInfo(TEST_LOGIN, TEST_FIRST_NAME + 1, TEST_LAST_NAME + 1, TEST_BIRTHDATE))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(post("/account/info")
                        .with(csrf())
                        .param("firstName", TEST_FIRST_NAME + 1)
                        .param("lastName", TEST_LAST_NAME + 1)
                        .param("birthdate", TEST_BIRTHDATE))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attributeExists("errorInfoList"));
    }

    @Test
    @WithMockUser(username = TEST_LOGIN)
    @DisplayName("Успешное изменение баланса")
    void changeAccountBalanceSuccessTest() throws Exception {
        when(cashService.changeAccountBalance(TEST_LOGIN, ActionEnum.ACCRUAL, BigDecimal.ONE, TEST_CURRENCY))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(post("/account/balance")
                        .with(csrf())
                        .param("action", ActionEnum.ACCRUAL.name())
                        .param("balance", BigDecimal.ONE.toString())
                        .param("currency", TEST_CURRENCY))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attributeExists("errorBalanceList"));
    }

    @Test
    @WithMockUser(username = TEST_LOGIN)
    @DisplayName("Успешный перевод")
    void transferSuccessTest() throws Exception {
        when(transferService.transfer(TEST_LOGIN, TEST_LOGIN + 1, BigDecimal.ONE, TEST_CURRENCY, TEST_CURRENCY))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(post("/account/transfer")
                        .with(csrf())
                        .param("loginTo", TEST_LOGIN + 1)
                        .param("transferAmount", BigDecimal.ONE.toString())
                        .param("currencyFrom", TEST_CURRENCY)
                        .param("currencyTo", TEST_CURRENCY))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    @WithMockUser(username = TEST_LOGIN)
    @DisplayName("Успешный перевод")
    void createNewBalanceSuccessTest() throws Exception {
        when(accountService.createNewBalance(TEST_LOGIN, TEST_CURRENCY))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(post("/account/create-balance")
                        .with(csrf())
                        .param("newCurrency", TEST_CURRENCY))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attributeExists("errorCurrencyList"));
    }
}
