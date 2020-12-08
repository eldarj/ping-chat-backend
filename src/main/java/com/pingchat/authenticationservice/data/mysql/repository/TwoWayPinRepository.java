package com.pingchat.authenticationservice.data.mysql.repository;


import com.pingchat.authenticationservice.data.mysql.entity.TwoWayPinEntity;
import com.pingchat.authenticationservice.data.mysql.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TwoWayPinRepository extends JpaRepository<TwoWayPinEntity, Long> {
    TwoWayPinEntity findByUserAndPinAndUsed(UserEntity user, int pin, boolean used);
}
