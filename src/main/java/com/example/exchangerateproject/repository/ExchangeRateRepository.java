package com.example.exchangerateproject.repository;

import com.example.exchangerateproject.entity.ExchangeRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Long> {

    @Query("SELECT e FROM ExchangeRate e WHERE e.currency = :currency ORDER BY e.date DESC LIMIT 7")
    List<ExchangeRate> findTop7ByCurrencyOrderByDateDesc(String currency);

    @Query("SELECT e FROM ExchangeRate e WHERE e.currency = :currency AND e.date >= :startDate ORDER BY e.date ASC")
    List<ExchangeRate> findByCurrencyAndDateAfter(String currency, LocalDate startDate);

    boolean existsByCurrencyAndDate(String currency, LocalDate date);

    @Query("SELECT DISTINCT e.currency FROM ExchangeRate e ORDER BY e.currency")
    List<String> findDistinctCurrencies();
}