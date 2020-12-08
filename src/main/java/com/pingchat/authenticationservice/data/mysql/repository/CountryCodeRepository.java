package com.pingchat.authenticationservice.data.mysql.repository;

import com.pingchat.authenticationservice.data.mysql.entity.CountryCodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CountryCodeRepository extends JpaRepository<CountryCodeEntity, Long> {
    CountryCodeEntity findByDialCode(String dialCode);

    List<CountryCodeEntity> findAll();
}
