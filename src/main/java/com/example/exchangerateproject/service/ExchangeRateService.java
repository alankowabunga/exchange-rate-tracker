package com.example.exchangerateproject.service;

import com.example.exchangerateproject.entity.ExchangeRate;
import com.example.exchangerateproject.repository.ExchangeRateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExchangeRateService {

    private final ExchangeRateRepository repository;
    private static final String BOT_URL = "https://rate.bot.com.tw/xrt/flcsv/0/day";

    /**
     * 從台灣銀行爬取匯率資料
     */
    @Transactional
    public void fetchAndSaveRates() {
        try {
            URL url = new URL(BOT_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8)
            );

            String line;
            boolean isFirstLine = true;
            int count = 0;

            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue; // 跳過標題行
                }

                if (line.trim().isEmpty()) {
                    break;
                }

                String[] fields = line.split(",");
                if (fields.length > 12) {
                    String currencyName = fields[0].trim();
                    String rateStr = fields[12].trim();

                    if (!rateStr.isEmpty() && !rateStr.equals("-")) {
                        try {
                            BigDecimal rate = new BigDecimal(rateStr);
                            String currency = extractCurrencyCode(currencyName);
                            LocalDate today = LocalDate.now();

                            // 檢查是否已存在相同幣別和日期的資料
                            if (!repository.existsByCurrencyAndDate(currency, today)) {
                                ExchangeRate exchangeRate = new ExchangeRate();
                                exchangeRate.setCurrency(currency);
                                exchangeRate.setRate(rate);
                                exchangeRate.setDate(today);
                                repository.save(exchangeRate);
                                count++;
                            }
                        } catch (NumberFormatException e) {
                            log.warn("無法解析匯率: {}", rateStr);
                        }
                    }
                }
            }

            reader.close();
            log.info("成功爬取並儲存 {} 筆匯率資料", count);

        } catch (Exception e) {
            log.error("爬取匯率資料失敗", e);
            throw new RuntimeException("爬取匯率資料失敗: " + e.getMessage());
        }
    }

    /**
     * 從幣別名稱提取幣別代碼
     */
    private String extractCurrencyCode(String currencyName) {
        if (currencyName.contains("USD")) return "USD";
        if (currencyName.contains("JPY")) return "JPY";
        if (currencyName.contains("EUR")) return "EUR";
        if (currencyName.contains("GBP")) return "GBP";
        if (currencyName.contains("AUD")) return "AUD";
        if (currencyName.contains("CAD")) return "CAD";
        if (currencyName.contains("CNY")) return "CNY";
        if (currencyName.contains("HKD")) return "HKD";
        if (currencyName.contains("SGD")) return "SGD";
        if (currencyName.contains("CHF")) return "CHF";
        if (currencyName.contains("ZAR")) return "ZAR";
        if (currencyName.contains("SEK")) return "SEK";
        if (currencyName.contains("NZD")) return "NZD";
        if (currencyName.contains("THB")) return "THB";
        if (currencyName.contains("PHP")) return "PHP";
        if (currencyName.contains("IDR")) return "IDR";
        if (currencyName.contains("KRW")) return "KRW";
        if (currencyName.contains("MYR")) return "MYR";
        if (currencyName.contains("VND")) return "VND";

        // 預設返回前三個字元
        return currencyName.length() >= 3 ? currencyName.substring(0, 3) : currencyName;
    }

    /**
     * 查詢指定幣別最近 7 筆資料
     */
    public List<ExchangeRate> getLatest7Rates(String currency) {
        return repository.findTop7ByCurrencyOrderByDateDesc(currency.toUpperCase());
    }

    /**
     * 查詢指定幣別最近 N 天的資料
     */
    public List<ExchangeRate> getRatesByDays(String currency, int days) {
        LocalDate startDate = LocalDate.now().minusDays(days);
        return repository.findByCurrencyAndDateAfter(currency.toUpperCase(), startDate);
    }

    /**
     * 手動更新匯率
     */
    @Transactional
    public ExchangeRate updateRate(String currency, BigDecimal rate, LocalDate date) {
        ExchangeRate exchangeRate = new ExchangeRate();
        exchangeRate.setCurrency(currency.toUpperCase());
        exchangeRate.setRate(rate);
        exchangeRate.setDate(date);
        return repository.save(exchangeRate);
    }

    /**
     * 取得所有幣別
     */
    public List<String> getAllCurrencies() {
        return repository.findDistinctCurrencies();
    }
}