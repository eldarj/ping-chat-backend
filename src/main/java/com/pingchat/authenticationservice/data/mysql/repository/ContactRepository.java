package com.pingchat.authenticationservice.data.mysql.repository;

import com.pingchat.authenticationservice.data.mysql.entity.ContactEntity;
import com.pingchat.authenticationservice.data.mysql.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactRepository extends JpaRepository<ContactEntity, Long> {
    List<ContactEntity> findByUser(UserEntity user);

    Page<ContactEntity> findAllByUserIdOrderByContactNameAsc(Long userId, Pageable pageable);

    Page<ContactEntity> findAllByUserIdAndIsFavoriteOrderByContactNameAsc(Long userId,
                                                                          boolean favourites,
                                                                          Pageable pageable);

    @Modifying
    @Query("UPDATE ContactEntity c set c.isFavorite = :isFavourite where c.id = :contactId")
    void updateFavouriteStatus(Long contactId, Boolean isFavourite);
}
