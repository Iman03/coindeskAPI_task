package com.iman.coindeskapi_task.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iman.coindeskapi_task.entity.Currency;
import com.iman.coindeskapi_task.model.CoinDeskAPI;
import com.iman.coindeskapi_task.response.CurrencyResponse;
import com.iman.coindeskapi_task.service.CurrencyService;
import com.iman.coindeskapi_task.service.CurrencyServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.*;

@RestController
public class CoinDeskApiController {
    @Autowired
    private CurrencyServiceImpl currencyService;
    private static final String URL = "https://api.coindesk.com/v1/bpi/currentprice.json";

    //呼叫 coindesk API，轉換成object
    @GetMapping("/coinDeskAPI")
    public CoinDeskAPI getCoinDeskAPI() throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity(URL, String.class);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(response.getBody());
        JsonNode timeNode = rootNode.path("time");
        JsonNode disclaimerNode = rootNode.path("disclaimer");
        JsonNode chartNameNode = rootNode.path("chartName");
        JsonNode bpiNode = rootNode.path("bpi");

        CoinDeskAPI coinDeskAPI = new CoinDeskAPI();
        CoinDeskAPI.Time time = new CoinDeskAPI.Time();
        Map<String, CoinDeskAPI.APICurrency> currencyMap = new HashMap<>();
        time.setUpdated(timeNode.path("updated").asText());
        time.setUpdatedISO(timeNode.path("updatedISO").asText());
        time.setUpdateduk(timeNode.path("updateduk").asText());
        coinDeskAPI.setTime(time);
        coinDeskAPI.setDisclaimer(disclaimerNode.asText());
        coinDeskAPI.setChartName(chartNameNode.asText());
        bpiNode.fields().forEachRemaining(e -> {
            CoinDeskAPI.APICurrency currency = new CoinDeskAPI.APICurrency();
            currency.setCode(e.getValue().path("code").asText());
            currency.setSymbol(e.getValue().path("symbol").asText());
            currency.setRate(e.getValue().path("rate").asText());
            currency.setDescription(e.getValue().path("description").asText());
            currency.setRate_float(e.getValue().path("rate_float").asDouble());
            currencyMap.put(e.getValue().path("code").asText(), currency);
        });
        coinDeskAPI.setBpi(currencyMap);
        return coinDeskAPI;
    }

    //呼叫 coindesk API，進行資料轉換，組成新 API
    @GetMapping("/transformCoinDeskAPI")
    public CurrencyResponse transformCoinDeskAPI() throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity(URL, String.class);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(response.getBody());
        JsonNode timeNode = rootNode.path("time");
        JsonNode bpiNode = rootNode.path("bpi");

        CurrencyResponse currencyResponse = new CurrencyResponse();
        String dateS = timeNode.path("updatedISO").asText();
        long l = ZonedDateTime.parse(dateS).toInstant().toEpochMilli();
        Date date = new Date(l);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        currencyResponse.setUpdateTime(simpleDateFormat.format(date));

        List<CurrencyResponse.Bpi> bpiList = new ArrayList<>();
        bpiNode.fields().forEachRemaining(e -> {
            CurrencyResponse.Bpi bpi = new CurrencyResponse.Bpi();
            bpi.setCurrency(e.getKey());
            bpi.setRate(e.getValue().path("rate_float").asDouble());
            switch (e.getKey()){
                case "USD":
                    bpi.setCurrencyChinese("美元");
                    break;
                case "GBP":
                    bpi.setCurrencyChinese("英鎊");
                    break;
                case "EUR":
                    bpi.setCurrencyChinese("歐元");
                    break;
            }
            bpiList.add(bpi);
        });
        currencyResponse.setBpiList(bpiList);
        for (CurrencyResponse.Bpi bpi:currencyResponse.getBpiList()) {
            Currency getCurrency = currencyService.getCurrencyByCurrencyName(bpi.getCurrency());
            Currency currency = new Currency(bpi.getCurrency(), bpi.getCurrencyChinese(), bpi.getRate());
            if (getCurrency == null){
                currencyService.createCurrency(currency);
            } else {
                currencyService.updateCurrency(getCurrency.getId(), currency);
            }
        }

        return currencyResponse;
    }

    //查詢幣別對應表資料(全部)
    @GetMapping("/currencys")
    public ResponseEntity<List<Currency>> getCurrencys(){
        return ResponseEntity.status(HttpStatus.OK).body(currencyService.getCurrencys());
    }

    //查詢幣別對應表資料(單一)
    @GetMapping("/currencys/{id}")
    public ResponseEntity<Currency> getCurrency(@PathVariable Long id){
        Currency currency = currencyService.getCurrency(id);
        if (currency != null){
            return ResponseEntity.status(HttpStatus.OK).body(currencyService.getCurrency(id));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    //新增幣別對應表資料
    @PostMapping("/currencys")
    public ResponseEntity<?> createCurrency(@RequestBody @Valid Currency currency, BindingResult result){
        if (result.hasErrors()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result.getFieldError().getDefaultMessage());
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(currencyService.createCurrency(currency));
    }

    //更新幣別對應表資料
    @PutMapping("/currencys/{id}")
    public ResponseEntity<?> updateCurrency(@RequestBody @Valid Currency currency,BindingResult result,
                                        @PathVariable Long id){
        if (result.hasErrors()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result.getFieldError().getDefaultMessage());
        } else {
            Currency updateCurrency = currencyService.getCurrency(id);
            if (updateCurrency != null){
                return ResponseEntity.status(HttpStatus.OK).body(currencyService.updateCurrency(id, currency));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        }
    }

    //刪除幣別對應表資料
    @DeleteMapping("/currencys/{id}")
    public ResponseEntity<Currency> deleteCurrency(@PathVariable Long id){
        if (currencyService.getCurrency(id) != null){
            currencyService.deleteCurrency(id);
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
