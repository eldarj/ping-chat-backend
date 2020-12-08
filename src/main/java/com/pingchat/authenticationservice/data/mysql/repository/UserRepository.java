package com.pingchat.authenticationservice.data.mysql.repository;

import com.pingchat.authenticationservice.data.mysql.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    @Query("SELECT u FROM UserEntity u JOIN CountryCodeEntity cc on cc.id = u.countryCode.id " +
            "WHERE cc.dialCode = :dialCode AND u.phoneNumber = :phoneNumber")
    UserEntity findByDialCodeAndPhoneNumber(String dialCode, String phoneNumber);
}
