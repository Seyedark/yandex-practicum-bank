package ru.yandex.practicum.cash.contact;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.yandex.practicum.cash.service.CashService;

@WebMvcTest
public abstract class ContractTest {

    @Autowired
    private WebApplicationContext context;

    @MockBean
    CashService cashService;

    @BeforeEach
    public void setup() {
        io.restassured.module.mockmvc.RestAssuredMockMvc.mockMvc(
                MockMvcBuilders.webAppContextSetup(context).build());
    }
}