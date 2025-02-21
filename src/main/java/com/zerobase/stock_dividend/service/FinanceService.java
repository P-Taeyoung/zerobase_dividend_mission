package com.zerobase.stock_dividend.service;

import com.zerobase.stock_dividend.exception.impl.NoCompanyException;
import com.zerobase.stock_dividend.model.Company;
import com.zerobase.stock_dividend.model.Dividend;
import com.zerobase.stock_dividend.model.ScrapedResult;
import com.zerobase.stock_dividend.model.contents.CacheKey;
import com.zerobase.stock_dividend.persist.entity.CompanyEntity;
import com.zerobase.stock_dividend.persist.entity.DividendEntity;
import com.zerobase.stock_dividend.persist.repository.CompanyRepository;
import com.zerobase.stock_dividend.persist.repository.DividendRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class FinanceService {

    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;

    @Cacheable(key = "#companyName", value = CacheKey.KEY_FINANCE)
    public ScrapedResult findDividendsByCompany(String companyName) {
        log.info("Find dividends by company -> " + companyName);
        //1. 회사명을 기준으로 회사 정보를 조회
        CompanyEntity company = this.companyRepository.findByName(companyName)
                .orElseThrow(NoCompanyException::new);

        //2. 조회된 회사 ID로 배당금 정보 조회
        List<DividendEntity> dividendEntities
                = this.dividendRepository.findAllByCompanyId(company.getId());

        //3.결과 조합 후 반환
        return new ScrapedResult(new Company(company.getTicker(), company.getName()),
                                 dividendEntities.stream().map(
                                e -> new Dividend(e.getDate(), e.getDividend()))
                                .collect(Collectors.toList()));

    }
}
