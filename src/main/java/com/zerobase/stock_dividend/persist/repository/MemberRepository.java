package com.zerobase.stock_dividend.persist.repository;

import com.zerobase.stock_dividend.model.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<MemberEntity, Long> {
    Optional<MemberEntity> findByUsername(String username);

    boolean existsByUsername(String username);
    //회원가입을 할 때 이미 존재하는 아이디인지 확인
}
