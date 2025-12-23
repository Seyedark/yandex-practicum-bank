package ru.yandex.practicum.transfer.contract;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.yandex.practicum.transfer.dto.ExchangeDto;
import ru.yandex.practicum.transfer.enums.CurrencyEnum;
import ru.yandex.practicum.transfer.service.TransferService;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.when;

@WebMvcTest
public abstract class ContractTest {

    @Autowired
    private WebApplicationContext context;

    @MockBean
    TransferService transferService;

    @BeforeEach
    public void setup() {
        ExchangeDto exchangeDto = new ExchangeDto();
        exchangeDto.setCurrency(CurrencyEnum.USD.name());
        exchangeDto.setPurchaseRate(BigDecimal.TWO);
        exchangeDto.setSellingRate(BigDecimal.ONE);
        when(transferService.getExchangeDtoList()).thenReturn(List.of(exchangeDto));
        io.restassured.module.mockmvc.RestAssuredMockMvc.mockMvc(
                MockMvcBuilders.webAppContextSetup(context).build());
    }
}