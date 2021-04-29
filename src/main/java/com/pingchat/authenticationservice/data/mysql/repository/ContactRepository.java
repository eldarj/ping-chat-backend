package com.pingchat.authenticationservice.data.mysql.repository;

import com.pingchat.authenticationservice.data.mysql.entity.ContactEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface ContactRepository extends JpaRepository<ContactEntity, Long> {
    Page<ContactEntity> findAllByUserIdOrderByContactNameAsc(Long userId, Pageable pageable);

    Page<ContactEntity> findAllByUserIdAndIsFavoriteOrderByContactNameAsc(Long userId,
                                                                          boolean favourites,
                                                                          Pageable pageable);

    @Query(value = "SELECT c.* FROM contacts c " +
            "WHERE c.user_id = ?1 " +
            "AND CONCAT(c.contact_name, c.contact_phone_number) LIKE %?2% " +
            "ORDER BY c.contact_name ASC",
            nativeQuery = true)
    List<ContactEntity> findAllByNameOrPhonenumber(Long userId, String query);

    ContactEntity findByUserIdAndContactUserId(Long userId, Long contactUserId);

    List<ContactEntity> findAllByContactPhoneNumber(String contactPhoneNumber);

    @Modifying
    @Transactional
    @Query(value = "UPDATE contacts SET contact_id = ?2 WHERE contact_phone_number = ?1",
            nativeQuery = true)
    void updateContactsOnRegister(String fullPhoneNumber, Long userId);

    @Modifying
    @Query("UPDATE ContactEntity c set c.isFavorite = :isFavourite where c.id = :contactId")
    void updateFavouriteStatus(Long contactId, Boolean isFavourite);
}
