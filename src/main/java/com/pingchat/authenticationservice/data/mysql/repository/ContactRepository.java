package com.pingchat.authenticationservice.data.mysql.repository;

import com.pingchat.authenticationservice.data.mysql.entity.ContactEntity;
import com.pingchat.authenticationservice.model.dto.ContactDto;
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
    Page<ContactEntity> findAllByUserIdAndIsDeletedIsFalseOrderByContactNameAsc(Long userId, Pageable pageable);

    Page<ContactEntity> findAllByUserIdAndIsFavoriteAndIsDeletedIsFalseOrderByContactNameAsc(Long userId,
                                                                          boolean favourites,
                                                                          Pageable pageable);

    @Query(value = "SELECT c.* FROM contacts c " +
            "WHERE c.user_id = ?1 " +
            "AND CONCAT(c.contact_name, c.contact_phone_number) LIKE %?2% " +
            "AND c.is_deleted = false " +
            "ORDER BY c.contact_name ASC",
            nativeQuery = true)
    List<ContactEntity> findAllByNameOrPhonenumber(Long userId, String query);

    ContactEntity findByUserIdAndContactUserId(Long userId, Long contactUserId);

    ContactEntity findByUserIdAndContactBindingId(Long userId, long contactUserId);

    ContactEntity findByUserPhoneNumberAndContactPhoneNumber(String userPhoneNumber, String contactPhoneNumber);

    List<ContactEntity> findAllByContactPhoneNumber(String contactPhoneNumber);

    @Modifying
    @Transactional
    @Query(value = "UPDATE contacts SET contact_id = ?2 WHERE contact_phone_number = ?1",
            nativeQuery = true)
    void updateContactsOnRegister(String fullPhoneNumber, Long userId);

    @Modifying
    @Query("UPDATE ContactEntity c set c.isFavorite = :isFavourite where c.id = :contactId")
    void updateFavouriteStatus(Long contactId, Boolean isFavourite);

    @Modifying
    @Query("UPDATE ContactEntity c set c.contactName = :contactName where c.id = :contactId")
    void updateContactName(Long contactId, String contactName);

    @Modifying
    @Query("UPDATE ContactEntity c set c.backgroundImagePath = :backgroundImagePath where c.id = :contactId")
    void updateBackground(Long contactId, String backgroundImagePath);

    @Modifying
    @Query("UPDATE ContactEntity  c set c.isDeleted = :isDeleted where c.id = :contactId")
    void updateDeletedStatus(Long contactId, Boolean isDeleted);
}
