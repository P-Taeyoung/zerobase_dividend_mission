package com.zerobase.stock_dividend.persist.entity;

import com.zerobase.stock_dividend.model.Company;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Entity(name = "COMPANY")
@Getter
@ToString
@NoArgsConstructor
public class CompanyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true)
    private String ticker;

    public CompanyEntity(Company company) {
        this.name = company.getName();
        this.ticker = company.getTicker();
    }
}

