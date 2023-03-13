package com.iman.coindeskapi_task.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
@Data
@Table(name = "Currency")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Currency {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "currencyName不可為空")
    private String currencyName;
    @NotBlank(message = "currencyChinese不可為空")
    private String currencyChinese;
    @NotNull(message = "rate不可為空")
    private double rate;

    public Currency(String currencyName, String currencyChinese, double rate){
        this.currencyName = currencyName;
        this.currencyChinese = currencyChinese;
        this.rate = rate;
    }
}
