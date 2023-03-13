package com.iman.coindeskapi_task.repository;

import com.iman.coindeskapi_task.entity.Currency;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CurrencyRepository extends JpaRepository<Currency, Long> {
    Currency findCoinByCurrencyName(String currencyName);
}
