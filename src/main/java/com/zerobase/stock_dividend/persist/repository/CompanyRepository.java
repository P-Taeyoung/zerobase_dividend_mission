package com.zerobase.stock_dividend.persist.repository;

import com.zerobase.stock_dividend.persist.entity.CompanyEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<CompanyEntity, Long> {
    boolean existsByTicker(String ticker);
    Optional<CompanyEntity>findByName(String companyName);
    Page<CompanyEntity> findByNameStartingWithIgnoreCase(String companyName, Pageable pageable);
    Optional<CompanyEntity> findByTicker(String ticker);
}
