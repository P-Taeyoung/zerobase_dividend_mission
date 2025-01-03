package com.zerobase.stock_dividend.scraper;

import com.zerobase.stock_dividend.model.Company;
import com.zerobase.stock_dividend.model.ScrapedResult;

public interface Scraper {
    Company scrapeCompanyByTicker(String ticker);
    ScrapedResult scrape(Company company);
}
