package com.pingchat.authenticationservice.data.mysql.repository;

import com.pingchat.authenticationservice.data.mysql.entity.ContactEntity;
import com.pingchat.authenticationservice.data.mysql.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactRepository extends JpaRepository<ContactEntity, Long> {
    List<ContactEntity> findByUser(UserEntity user);

    boolean existsByUserAndContactUser(UserEntity user, UserEntity contactUser);

    @Query("SELECT c FROM ContactEntity c WHERE c.user.id=:userId ORDER BY c.contactName ASC")
    Page<ContactEntity> findAllByUserId(Long userId, Pageable pageable);
}
