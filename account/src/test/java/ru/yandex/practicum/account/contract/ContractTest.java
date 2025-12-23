package ru.yandex.practicum.account.contract;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.yandex.practicum.account.dto.*;
import ru.yandex.practicum.account.enums.CurrencyEnum;
import ru.yandex.practicum.account.service.AccountService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.when;

@WebMvcTest
public abstract class ContractTest {

    @Autowired
    private WebApplicationContext context;

    @MockBean
    AccountService accountService;


    @BeforeEach
    public void setup() {

        AccountDto mockAccount = new AccountDto();
        mockAccount.setLogin("login");
        mockAccount.setPassword("12345");
        mockAccount.setFirstName("Тест");
        mockAccount.setLastName("Тестов");
        mockAccount.setEmail("login@mail.ru");
        mockAccount.setBirthDate(LocalDate.of(1990, 1, 1));

        AccountWithUsersDto mockAccountWithUsers = new AccountWithUsersDto();
        mockAccountWithUsers.setLogin("login");
        mockAccountWithUsers.setPassword("12345");
        mockAccountWithUsers.setFirstName("Тест");
        mockAccountWithUsers.setLastName("Тестов");
        mockAccountWithUsers.setEmail("login@mail.ru");
        mockAccountWithUsers.setBirthDate(LocalDate.of(1990, 1, 1));

        ShortAccountDto shortAccount = new ShortAccountDto();
        shortAccount.setLogin("login");
        shortAccount.setFirstName("Тест1");
        shortAccount.setLastName("Тестов1");
        mockAccountWithUsers.setShortAccountDtoList(List.of(shortAccount));

        AccountBalanceDto accountBalanceDto = new AccountBalanceDto();
        accountBalanceDto.setCurrency(CurrencyEnum.RUB.name());
        mockAccountWithUsers.setAccountBalanceDtoList(List.of(accountBalanceDto));

        TransferAccountsDto transferAccounts = new TransferAccountsDto();

        transferAccounts.setLoginFrom("login1");
        transferAccounts.setEmailFrom("login1@mail.ru");
        transferAccounts.setBalanceFrom(BigDecimal.ONE);

        transferAccounts.setLoginTo("login2");
        transferAccounts.setEmailTo("login2@mail.ru");
        transferAccounts.setBalanceTo(BigDecimal.ONE);

        BalanceDto balanceDto = new BalanceDto();
        balanceDto.setEmail("login@mail.ru");
        balanceDto.setBalance(BigDecimal.ONE);

        when(accountService.getTransferAccountsDto("login1", CurrencyEnum.RUB.name(), "login2", CurrencyEnum.RUB.name())).thenReturn(transferAccounts);
        when(accountService.findAccountByLoginWithUsers("login")).thenReturn(mockAccountWithUsers);
        when(accountService.findAccountByLogin("login")).thenReturn(mockAccount);
        when(accountService.findAccountByLoginAndCurrency("login", "RUB")).thenReturn(balanceDto);


        io.restassured.module.mockmvc.RestAssuredMockMvc.mockMvc(
                MockMvcBuilders.webAppContextSetup(context).build()
        );
    }
}