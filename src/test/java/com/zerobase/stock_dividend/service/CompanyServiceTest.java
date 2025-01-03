package com.zerobase.stock_dividend.service;

import com.zerobase.stock_dividend.persist.repository.CompanyRepository;
import com.zerobase.stock_dividend.persist.repository.DividendRepository;
import com.zerobase.stock_dividend.scraper.Scraper;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(MockitoExtension.class)
@Transactional
class CompanyServiceTest {
    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private DividendRepository dividendRepository;

    @Mock
    private Scraper yahooFinanceScraper;

    @InjectMocks
    private CompanyService companyService;



}