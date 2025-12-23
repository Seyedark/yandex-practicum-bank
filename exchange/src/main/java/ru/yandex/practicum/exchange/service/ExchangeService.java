package ru.yandex.practicum.exchange.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.exchange.dao.entity.ExchangeEntity;
import ru.yandex.practicum.exchange.dao.repository.ExchangeRepository;
import ru.yandex.practicum.exchange.dto.ConvertRequestDto;
import ru.yandex.practicum.exchange.dto.ConvertResponseDto;
import ru.yandex.practicum.exchange.dto.ExchangeDto;
import ru.yandex.practicum.exchange.enums.CurrencyEnum;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExchangeService {

    private final ExchangeRepository exchangeRepository;

    public List<ExchangeDto> getExchangeDtoList() {
        return exchangeRepository.findAll().stream()
                .map(x -> {
                    ExchangeDto exchangeDto = new ExchangeDto();
                    exchangeDto.setCurrency(x.getCurrency());
                    exchangeDto.setPurchaseRate(x.getPurchaseRate());
                    exchangeDto.setSellingRate(x.getSellingRate());
                    return exchangeDto;
                }).toList();
    }

    @Transactional
    public void saveExchange(List<ExchangeDto> exchangeDtoList) {
        List<ExchangeEntity> currentExchangeEntityList = exchangeRepository.findAll();

        Map<String, ExchangeEntity> existingEntities = currentExchangeEntityList.stream()
                .collect(Collectors.toMap(
                        ExchangeEntity::getCurrency,
                        Function.identity()
                ));

        List<ExchangeEntity> entitiesToSave = new ArrayList<>();

        exchangeDtoList.forEach(dto -> {
            ExchangeEntity entity = existingEntities.get(dto.getCurrency());
            if (entity != null) {
                entity.setPurchaseRate(dto.getPurchaseRate());
                entity.setSellingRate(dto.getSellingRate());
            } else {
                entity = new ExchangeEntity();
                entity.setCurrency(dto.getCurrency());
                entity.setPurchaseRate(dto.getPurchaseRate());
                entity.setSellingRate(dto.getSellingRate());
            }
            entitiesToSave.add(entity);
        });

        exchangeRepository.saveAll(entitiesToSave);
    }

    public ConvertResponseDto convert(ConvertRequestDto convertRequestDto) {
        ConvertResponseDto convertResponseDto = new ConvertResponseDto();
        List<ExchangeDto> exchangeDtoList = getExchangeDtoList();
        if (!convertRequestDto.getCurrencyFrom().equals(convertRequestDto.getCurrencyTo())) {
            convertWithDifferenceCurrency(convertResponseDto, convertRequestDto, exchangeDtoList);
        } else {
            convertResponseDto.setConvertedAmount(convertRequestDto.getConvertAmount());
        }
        return convertResponseDto;
    }

    private void convertWithDifferenceCurrency(ConvertResponseDto convertResponseDto,
                                               ConvertRequestDto convertRequestDto,
                                               List<ExchangeDto> exchangeDtoList) {
        if (convertRequestDto.getCurrencyFrom().equals(CurrencyEnum.RUB.name())) {
            ExchangeDto foreignCurrency = findByCurrency(exchangeDtoList, convertRequestDto.getCurrencyTo());
            convertResponseDto.setConvertedAmount(convertRequestDto.getConvertAmount()
                    .divide(foreignCurrency.getPurchaseRate()));
        } else if (convertRequestDto.getCurrencyTo().equals(CurrencyEnum.RUB.name())) {
            ExchangeDto foreignCurrency = findByCurrency(exchangeDtoList, convertRequestDto.getCurrencyFrom());
            convertResponseDto.setConvertedAmount(convertRequestDto.getConvertAmount()
                    .multiply(foreignCurrency.getSellingRate()));
        } else {
            ExchangeDto foreignCurrencyFrom = findByCurrency(exchangeDtoList, convertRequestDto.getCurrencyFrom());
            ExchangeDto foreignCurrencyTo = findByCurrency(exchangeDtoList, convertRequestDto.getCurrencyTo());
            convertResponseDto.setConvertedAmount(convertRequestDto.getConvertAmount()
                    .multiply(foreignCurrencyFrom.getSellingRate()).divide(foreignCurrencyTo.getPurchaseRate()));
        }
    }

    private ExchangeDto findByCurrency(List<ExchangeDto> exchangeDtoList, String currency) {
        return exchangeDtoList.stream()
                .filter(x -> x.getCurrency().equals(currency)).findFirst().get();
    }
}