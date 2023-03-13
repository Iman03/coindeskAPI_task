package com.iman.coindeskapi_task.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class CoinDeskAPI {
    private Time time;
    private String disclaimer;
    private String chartName;
    private Map<String, APICurrency> bpi;

    @Getter
    @Setter
    public static class Time{
        private String updated;
        private String updatedISO;
        private String updateduk;
    }

    @Getter
    @Setter
    public static class APICurrency{
        private String code;
        private String symbol;
        private String rate;
        private String description;
        private double rate_float;
    }
}
