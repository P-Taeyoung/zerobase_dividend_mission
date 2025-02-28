package com.zerobase.stock_dividend.persist.repository;

import com.zerobase.stock_dividend.persist.entity.DividendEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DividendRepository extends JpaRepository<DividendEntity, Long> {
    List<DividendEntity> findAllByCompanyId(Long companyId);
    boolean existsByCompanyIdAndDate(Long companyId, LocalDateTime date);
    @Transactional
    void deleteAllByCompanyId(Long companyId);
}
