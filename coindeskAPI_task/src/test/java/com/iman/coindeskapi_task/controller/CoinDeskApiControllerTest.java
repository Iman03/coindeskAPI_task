package com.iman.coindeskapi_task.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iman.coindeskapi_task.entity.Currency;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CoinDeskApiControllerTest {
    @Autowired
    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @Order(1)
    // 測試取得CoinDeskAPI
    void getCoinDeskAPI() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/coinDeskAPI");

        String result = mockMvc.perform(requestBuilder)
                .andDo(print())
                .andReturn().getResponse().getContentAsString();

        System.out.println("getCoinDeskAPI回傳之內容: " + result);
    }

    @Test
    @Order(2)
    // 測試transformCoinDeskAPI
    void transformCoinDeskAPI() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/transformCoinDeskAPI");

        String result = mockMvc.perform(requestBuilder)
                .andDo(print())
                .andReturn().getResponse().getContentAsString();

        System.out.println("transformCoinDeskAPI回傳之內容: " + result);
    }

    @Transactional
    @Test
    @Order(3)
    // 測試取得全部Currency
    void getCurrencys() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/currencys");

        String result = mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andReturn().getResponse().getContentAsString();
        System.out.println("getCurrencys: " + result);
    }

    @Transactional
    @Test
    @Order(4)
    // 測試取得單一 Currency 成功
    void getCurrency_Success() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/currencys/{id}", 1);

        String result =mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currencyName", equalTo("USD")))
                .andReturn().getResponse().getContentAsString();
        System.out.println("getCurrency_Success: " + result);
    }

    @Transactional
    @Test
    @Order(5)
    // 測試取得單一 Currency 失敗
    void getCurrency_NotFound() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/currencys/{id}", 200);

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(404));
    }

    @Transactional
    @Test
    @Order(6)
    // 測試創建 Currency 成功
    void createCurrency_Success() throws Exception {
        Currency currency = new Currency();
        currency.setCurrencyName("USD");
        currency.setCurrencyChinese("美元");
        currency.setRate(20442.2501);

        String json = objectMapper.writeValueAsString(currency);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/currencys")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().is(201))
                .andExpect(jsonPath("$.currencyName", equalTo("USD")))
                .andExpect(jsonPath("$.currencyChinese", equalTo("美元")))
                .andExpect(jsonPath("$.rate", equalTo(20442.2501)));
    }

    @Transactional
    @Test
    @Order(7)
    // 測試創建 Currency 失敗
    void createCurrency_Fail() throws Exception {
        Currency currency = new Currency();

        String json = objectMapper.writeValueAsString(currency);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/currencys")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().is(400));
    }

    @Transactional
    @Test
    @Order(8)
    // 測試更新 Currency 成功
    void updateCurrency_Success() throws Exception {
        Currency updateCurrency = new Currency();
        updateCurrency.setCurrencyName("USD_test");
        updateCurrency.setCurrencyChinese("美元_test");
        updateCurrency.setRate(12345.6789);

        String json = objectMapper.writeValueAsString(updateCurrency);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .put("/currencys/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        String result = mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currencyName", equalTo("USD_test")))
                .andExpect(jsonPath("$.currencyChinese", equalTo("美元_test")))
                .andExpect(jsonPath("$.rate", equalTo(12345.6789)))
                .andReturn().getResponse().getContentAsString();

        System.out.println("updateCurrency_Success回傳之內容: " + result);
    }

    @Transactional
    @Test
    @Order(9)
    // 測試更新 Currency 失敗
    void updateCurrency_Fail() throws Exception {
        Currency updateCurrency = new Currency();
        updateCurrency.setCurrencyName("");

        String json = objectMapper.writeValueAsString(updateCurrency);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .put("/currencys/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().is(400));
    }

    @Transactional
    @Test
    @Order(10)
    // 測試刪除 Currency
    void deleteCurrency() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/currencys/{id}", 1);

        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().is(204));
    }
}