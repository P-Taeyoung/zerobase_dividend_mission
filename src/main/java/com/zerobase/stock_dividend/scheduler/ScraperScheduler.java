package com.zerobase.stock_dividend.scheduler;

import com.zerobase.stock_dividend.model.Company;
import com.zerobase.stock_dividend.model.ScrapedResult;
import com.zerobase.stock_dividend.model.contents.CacheKey;
import com.zerobase.stock_dividend.persist.entity.CompanyEntity;
import com.zerobase.stock_dividend.persist.entity.DividendEntity;
import com.zerobase.stock_dividend.persist.repository.CompanyRepository;
import com.zerobase.stock_dividend.persist.repository.DividendRepository;
import com.zerobase.stock_dividend.scraper.Scraper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
public class ScraperScheduler {

    private final CompanyRepository companyRepository;
    private final Scraper yahooFinanceScraper;
    private final DividendRepository dividendRepository;


/*    @Scheduled(fixedDelay = 1000)
    public void scrape() throws InterruptedException {
        Thread.sleep(10000);
        System.out.println(Thread.currentThread().getName() + ": scrape started :" + LocalDateTime.now());
    }

    @Scheduled(fixedDelay = 1000)
    public void scrape2() throws InterruptedException {
        System.out.println(Thread.currentThread().getName() + ": scrape started :" + LocalDateTime.now());
    }*/
    @CacheEvict(value = CacheKey.KEY_FINANCE, allEntries = true)
    @Scheduled(cron = "${scheduler.scrap.yahoo}")
    public void yahooFinanceScheduler() {
        //저장된 회사 목록을 조회
        List<CompanyEntity> companyEntities = companyRepository.findAll();

        //회사마다 배당금 정보를 새로 스크래핑
        for (var company : companyEntities) {
            log.info("Scraping company -> " + company.getName());
            ScrapedResult scrapedResult = this.yahooFinanceScraper.scrape(new Company(company.getTicker(), company.getName()));
            //스크래핑한 배당금 정보 중 데이터베이스에 없는 값은 저장
            scrapedResult.getDividends().stream()
                    //디비든 모델을 디비든 엔티티로 매핑
                    .map(e -> new DividendEntity(company.getId(), e))
                    //엘리먼트를 하나씩 디비든 레파지토리에 삽입(해당 엘리먼트가 없을 경우에만)
                    .forEach(e ->
                    {
                        boolean exists = this.dividendRepository.existsByCompanyIdAndDate(e.getCompanyId(), e.getDate());
                        if (!exists) {
                            this.dividendRepository.save(e);
                        }
                    });

            //연속적으로 스크래핑 대상 사이트 서버에 요청을 날리지 않도록 일시정지
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }

    }
}
