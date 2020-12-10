package com.pingchat.authenticationservice.data.mysql.repository;

import com.pingchat.authenticationservice.data.mysql.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    UserEntity findById(long id);

    @Query("SELECT u FROM UserEntity u JOIN CountryCodeEntity cc on cc.id = u.countryCode.id " +
            "WHERE CONCAT(cc.dialCode, u.phoneNumber) = :fullPhoneNumber")
    UserEntity findByDialCodeAndPhoneNumber(String fullPhoneNumber);

    @Query("SELECT u FROM UserEntity u JOIN CountryCodeEntity cc on cc.id = u.countryCode.id " +
            "WHERE cc.dialCode = :dialCode AND u.phoneNumber = :phoneNumber")
    UserEntity findByDialCodeAndPhoneNumber(String dialCode, String phoneNumber);

    @Modifying
    @Query("UPDATE UserEntity u SET u.firstName = :firstName, u.lastName = :lastName WHERE u.id = :id")
    int updateFirstNameAndLastName(@Param("id") long id,
                                   @Param("firstName") String firstName,
                                   @Param("lastName") String lastName);

    @Modifying
    @Query("UPDATE UserEntity  u SET u.profileImagePath = :savedProfileImagePath WHERE u.id = :id")
    int updateProfileImage(@Param("id") long id,
                           @Param("savedProfileImagePath") String savedProfileImagePath);
}
