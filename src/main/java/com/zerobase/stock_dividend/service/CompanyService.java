package com.zerobase.stock_dividend.service;

import com.zerobase.stock_dividend.exception.impl.AlreadyExistCompany;
import com.zerobase.stock_dividend.exception.impl.NoCompanyException;
import com.zerobase.stock_dividend.model.Company;
import com.zerobase.stock_dividend.model.ScrapedResult;
import com.zerobase.stock_dividend.persist.entity.CompanyEntity;
import com.zerobase.stock_dividend.persist.entity.DividendEntity;
import com.zerobase.stock_dividend.persist.repository.CompanyRepository;
import com.zerobase.stock_dividend.persist.repository.DividendRepository;
import com.zerobase.stock_dividend.scraper.Scraper;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.Trie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CompanyService {

    private final Trie trie;
    private final Scraper yahooFinanceScraper;

    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;


    public Company save(String ticker) {
        boolean exists = this.companyRepository.existsByTicker(ticker);
        if (exists) {
            throw new AlreadyExistCompany();
        }
        return this.storeCompanyAndDividend(ticker);
    }

    public Page <CompanyEntity> getAllCompanies(Pageable pageable) {
        return this.companyRepository.findAll(pageable);
    }

    private Company storeCompanyAndDividend(String ticker) {
        //ticker를 기준으로 회사를 스크래핑
        Company company = this.yahooFinanceScraper.scrapeCompanyByTicker(ticker);
        if (ObjectUtils.isEmpty(company)) {
            throw new NoCompanyException();
        }

        // 해당 회사가 존재할 경우, 회사의 배당금 정보를 스크래핑
        ScrapedResult scrapedResult = this.yahooFinanceScraper.scrape(company);

        // 스크래핑 결과
        CompanyEntity companyEntity =  this.companyRepository
                .save(new CompanyEntity(company));

        List<DividendEntity> dividendEntities = scrapedResult.getDividends().stream()
                .map(e ->  new DividendEntity(companyEntity.getId(), e))
                .collect(Collectors.toList());

        this.dividendRepository.saveAll(dividendEntities);
        return company;
    }

    public void addAutocompleteKeyword(String keyword) {
        this.trie.put(keyword, null);
    }

    public List<String> autocomplete(String keyword) {
        return (List<String>) this.trie.prefixMap(keyword).keySet()
                .stream().collect(Collectors.toList());
    }

    public void deleteAutocompleteKeyword(String keyword) {
        this.trie.remove(keyword);
    }

    public List<String> getAutocompleteCompanyNamesByLike(String keyword) {
        Pageable pageable = PageRequest.of(0, 10);
        return this.companyRepository.findByNameStartingWithIgnoreCase(keyword, pageable)
                .stream().map(CompanyEntity::getName).collect(Collectors.toList());
    }

    public String deleteCompany(String ticker) {
        var company = this.companyRepository.findByTicker(ticker)
                .orElseThrow(NoCompanyException::new);

        this.dividendRepository.deleteAllByCompanyId(company.getId());
        this.companyRepository.delete(company);

        this.deleteAutocompleteKeyword(company.getName());

        return company.getName();
    }
}
