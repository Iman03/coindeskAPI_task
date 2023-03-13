package com.iman.coindeskapi_task.service;

import com.iman.coindeskapi_task.entity.Currency;

import java.util.List;

public interface CurrencyService {
    List<Currency> getCurrencys();
    Currency getCurrency(Long id);
    Currency getCurrencyByCurrencyName(String currency);
    Currency createCurrency(Currency currency);
    Currency updateCurrency(Long id,Currency currency);
    void deleteCurrency(Long id);
}
