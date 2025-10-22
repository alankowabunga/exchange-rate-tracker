package com.example.exchangerateproject.config;

import com.example.exchangerateproject.service.ExchangeRateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class SchedulerConfig {

    private final ExchangeRateService exchangeRateService;

    /**
     * 每天早上 9:00 執行匯率爬取
     */
    @Scheduled(cron = "0 0 9 * * ?")
    public void scheduledFetchRates() {
        log.info("開始執行定時匯率爬取任務");
        try {
            exchangeRateService.fetchAndSaveRates();
            log.info("定時匯率爬取任務完成");
        } catch (Exception e) {
            log.error("定時匯率爬取任務失敗", e);
        }
    }
}