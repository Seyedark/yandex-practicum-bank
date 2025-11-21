package ru.yandex.practicum.account.contract;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.yandex.practicum.account.dto.AccountDto;
import ru.yandex.practicum.account.dto.AccountWithUsersDto;
import ru.yandex.practicum.account.dto.ShortAccountDto;
import ru.yandex.practicum.account.dto.TransferAccountsDto;
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
        mockAccount.setBalance(BigDecimal.ONE);

        AccountWithUsersDto mockAccountWithUsers = new AccountWithUsersDto();
        mockAccountWithUsers.setLogin("login");
        mockAccountWithUsers.setPassword("12345");
        mockAccountWithUsers.setFirstName("Тест");
        mockAccountWithUsers.setLastName("Тестов");
        mockAccountWithUsers.setEmail("login@mail.ru");
        mockAccountWithUsers.setBirthDate(LocalDate.of(1990, 1, 1));
        mockAccountWithUsers.setBalance(BigDecimal.ONE);

        ShortAccountDto shortAccount = new ShortAccountDto();
        shortAccount.setLogin("login");
        shortAccount.setFirstName("Тест1");
        shortAccount.setLastName("Тестов1");
        mockAccountWithUsers.setShortAccountDtoList(List.of(shortAccount));

        TransferAccountsDto transferAccounts = new TransferAccountsDto();

        transferAccounts.setLoginFrom("login1");
        transferAccounts.setEmailFrom("login1@mail.ru");
        transferAccounts.setBalanceFrom(BigDecimal.ONE);

        transferAccounts.setLoginTo("login2");
        transferAccounts.setEmailTo("login2@mail.ru");
        transferAccounts.setBalanceTo(BigDecimal.ONE);

        when(accountService.getTransferAccountsDto("login1", "login2")).thenReturn(transferAccounts);
        when(accountService.findAccountByLoginWithUsers("login")).thenReturn(mockAccountWithUsers);
        when(accountService.findAccountByLogin("login")).thenReturn(mockAccount);

        io.restassured.module.mockmvc.RestAssuredMockMvc.mockMvc(
                MockMvcBuilders.webAppContextSetup(context).build()
        );
    }
}