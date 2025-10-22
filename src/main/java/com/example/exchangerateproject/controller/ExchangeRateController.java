package com.example.exchangerateproject.controller;

import com.example.exchangerateproject.dto.UpdateRateRequest;
import com.example.exchangerateproject.entity.ExchangeRate;
import com.example.exchangerateproject.service.ExchangeRateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ExchangeRateController {

    private final ExchangeRateService exchangeRateService;

    @GetMapping("/rates/all")
    public ResponseEntity<List<String>> getAllCurrencies() {
        List<String> currencies = exchangeRateService.getAllCurrencies();
        return ResponseEntity.ok(currencies);
    }

    @GetMapping("/rates/{currency}")
    public ResponseEntity<List<ExchangeRate>> getLatest7Rates(@PathVariable String currency) {
        List<ExchangeRate> rates = exchangeRateService.getLatest7Rates(currency);
        return ResponseEntity.ok(rates);
    }

    @PostMapping("/update")
    public ResponseEntity<ExchangeRate> updateRate(@RequestBody UpdateRateRequest request) {
        ExchangeRate updatedRate = exchangeRateService.updateRate(
                request.getCurrency(),
                request.getRate(),
                request.getDate()
        );
        return ResponseEntity.ok(updatedRate);
    }
}
