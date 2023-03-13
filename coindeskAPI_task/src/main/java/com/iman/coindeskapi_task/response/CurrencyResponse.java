package com.iman.coindeskapi_task.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CurrencyResponse {
    private String updateTime;
    private List<Bpi> bpiList;

    @Getter
    @Setter
    public static class Bpi{
        private String currency;
        private String currencyChinese;
        private Double rate;
    }
}
