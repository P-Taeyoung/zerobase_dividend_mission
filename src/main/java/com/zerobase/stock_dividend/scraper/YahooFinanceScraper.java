package com.zerobase.stock_dividend.scraper;


import com.zerobase.stock_dividend.model.Company;
import com.zerobase.stock_dividend.model.Dividend;
import com.zerobase.stock_dividend.model.ScrapedResult;
import com.zerobase.stock_dividend.model.contents.Month;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class YahooFinanceScraper implements Scraper {
    private static final String STATISTICS_URL = "https://finance.yahoo.com/quote/%s/history/?frequency=1mo&period1=%d&period2=%d";
    private static final String SUMMARY_URL = "https://finance.yahoo.com/quote/%s?p=%s";
    private static final long START_TIME = 86400; //60 * 60 * 24

    @Override
    public ScrapedResult scrape(Company company) {
        var scrapResult = new ScrapedResult();
        scrapResult.setCompany(company);
        try {
            long end = System.currentTimeMillis() / 1000; // 현재 시간을 초단위로 바꿔줌

            String url = String.format(STATISTICS_URL, company.getTicker(), START_TIME, end);
            Connection connection = Jsoup.connect(url);
            Document document = connection
                    .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36")
                    .timeout(5000) // 5초 타임아웃 설정
                    .get();

            Elements parsingDivs = document.getElementsByAttributeValue("data-testid", "history-table");
            Element tableEle = parsingDivs.get(0); // 테이블 전체를 가져옴

            Element tBody = tableEle.children().get(2).children().get(0).children().get(1); // 테이블 바로 밑 단계에서 두번째로 작성된 코드내용(thead(0) - tbody(1) - tfoot(2))을 가져옴

            List<Dividend> dividends = new ArrayList<>();
            for (Element e : tBody.children()) {
                String text = e.text();

                if (!text.endsWith("Dividend")) {
                    continue;
                }

                String[] splits = text.split(" ");
                int month = Month.strToNUmber(splits[0]);
                int day = Integer.valueOf(splits[1].replace(",", ""));
                int year = Integer.valueOf(splits[2]);
                String dividend = splits[3];

                if (month < 0) {
                    throw new RuntimeException("Invalid month: " + splits[0]);
                }


                dividends.add(new Dividend(LocalDateTime.of(year, month, day,
                        0, 0, 0), dividend));
            }
            scrapResult.setDividends(dividends);

        } catch (Exception e) {
            //TODO
            e.printStackTrace();
        }
        return scrapResult;
    }

    @Override
    public Company scrapeCompanyByTicker(String ticker) {
        String url = String.format(SUMMARY_URL, ticker, ticker);
        try {
            Document document = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36")
                    .timeout(3000) // 3초 타임아웃 설정
                    .get();
            Element titleEle = document.getElementsByTag("h1").get(1);
            String title = titleEle.text().split("\\(")[0].trim();

            return new Company(ticker, title);


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
