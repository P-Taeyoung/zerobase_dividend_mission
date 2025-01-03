package com.zerobase.stock_dividend;

import com.zerobase.stock_dividend.model.Company;
import com.zerobase.stock_dividend.model.ScrapedResult;
import com.zerobase.stock_dividend.persist.repository.CompanyRepository;
import com.zerobase.stock_dividend.scraper.Scraper;
import com.zerobase.stock_dividend.scraper.YahooFinanceScraper;
import com.zerobase.stock_dividend.service.CompanyService;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableCaching
public class StockDividendApplication {
	public static void main(String[] args) {
		SpringApplication.run(StockDividendApplication.class, args);
	}
}
