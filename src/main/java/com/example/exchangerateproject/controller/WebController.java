package com.example.exchangerateproject.controller;

import com.example.exchangerateproject.entity.ExchangeRate;
import com.example.exchangerateproject.service.ExchangeRateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class WebController {

    private final ExchangeRateService service;

    @GetMapping("/")
    public String index(
        @RequestParam(defaultValue = "USD") String currency,
        @RequestParam(defaultValue = "30") int days,
        Model model) {

        List<ExchangeRate> rates = service.getRatesByDays(currency, days);
        List<String> currencies = service.getAllCurrencies();

        model.addAttribute("rates", rates);
        model.addAttribute("currencies", currencies);
        model.addAttribute("selectedCurrency", currency);
        model.addAttribute("selectedDays", days);

        return "index";
    }
}