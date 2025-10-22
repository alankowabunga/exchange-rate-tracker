package com.example.exchangerateproject.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class UpdateRateRequest {
    private String currency;
    private BigDecimal rate;
    private LocalDate date;
}
