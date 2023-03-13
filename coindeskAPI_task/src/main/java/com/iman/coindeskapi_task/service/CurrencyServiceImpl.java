package com.iman.coindeskapi_task.service;

import com.iman.coindeskapi_task.entity.Currency;
import com.iman.coindeskapi_task.repository.CurrencyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CurrencyServiceImpl implements CurrencyService{
    @Autowired
    private CurrencyRepository currencyRepository;

    @Override
    public List<Currency> getCurrencys() {
        return currencyRepository.findAll();
    }

    @Override
    public Currency getCurrency(Long id) {
        if (currencyRepository.findById(id).isPresent()){
            return currencyRepository.findById(id).get();
        }
        return null;
    }

    @Override
    public Currency getCurrencyByCurrencyName(String currencyName) {
        return currencyRepository.findCoinByCurrencyName(currencyName);
    }

    @Override
    public Currency createCurrency(Currency currency) {
        return currencyRepository.save(currency);
    }

    @Override
    public Currency updateCurrency(Long id, Currency currency) {
        Currency updateCurrency = currencyRepository.findById(id).get();
        updateCurrency.setCurrencyName(currency.getCurrencyName());
        updateCurrency.setCurrencyChinese(currency.getCurrencyChinese());
        updateCurrency.setRate(currency.getRate());
        return currencyRepository.save(updateCurrency);
    }

    @Override
    public void deleteCurrency(Long id) {
        currencyRepository.deleteById(id);
    }
}
