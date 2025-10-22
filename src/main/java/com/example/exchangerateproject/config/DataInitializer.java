package com.example.exchangerateproject.config;

import com.example.exchangerateproject.service.ExchangeRateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements ApplicationRunner {

    private final ExchangeRateService exchangeRateService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("初始化匯率資料");
        try {
            exchangeRateService.fetchAndSaveRates();
            log.info("匯率資料初始化完成");
        } catch (Exception e) {
            log.error("匯率資料初始化失敗: {}", e.getMessage());
        }
    }
}
