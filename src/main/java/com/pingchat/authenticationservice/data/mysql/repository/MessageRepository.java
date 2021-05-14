package com.pingchat.authenticationservice.data.mysql.repository;

import com.pingchat.authenticationservice.data.mysql.entity.MessageEntity;
import com.pingchat.authenticationservice.model.dto.MessageDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MessageRepository extends JpaRepository<MessageEntity, Long> {
    @Query(value = "SELECT m.* FROM (" +
            "SELECT max(sent_timestamp) AS sent_timestamp FROM messages " +
            "WHERE deleted_for_user_ids NOT LIKE CONCAT('%:', ?1, ':%')" +
            "AND (" +
            "receiver_user_id = ?1 OR sender_user_id = ?1" +
            ") " +
            "GROUP BY contact_binding_id" +
            ") t INNER JOIN messages m ON m.sent_timestamp = t.sent_timestamp " +
            "ORDER BY m.sent_timestamp DESC " +
            "LIMIT ?2 OFFSET ?3",
            nativeQuery = true)
    List<MessageEntity> findDistinctByUser(Long userId, int pageSize, int pageNumber);

//    @Query("SELECT m FROM MessageEntity m WHERE m.receiver.id = :userId OR m.sender.id = :userId")
//    Page<MessageEntity> findDistinctByUser(Long userId, Pageable pageable);

    // TODO change this to a single hash value, equal across both contact entities (see: contactBindingId)
    @Query("SELECT m FROM MessageEntity  m WHERE " +
            "m.deletedForUserIds NOT LIKE CONCAT('%:', :userId, ':%') AND (" +
            "(m.receiver.id = :userId AND m.sender.id = :anotherUserId) OR " +
            "(m.receiver.id = :anotherUserId AND m.sender.id = :userId)" +
            ")")
    Page<MessageEntity> findByUsers(Long userId, Long anotherUserId, Pageable pageable);

    @Query("SELECT m FROM MessageEntity  m WHERE " +
            "m.contactBindingId = :contactBindingId AND " +
            "m.deletedForUserIds NOT LIKE CONCAT('%:', :userId, ':%')")
    Page<MessageEntity> findByContactBindingId(long userId, long contactBindingId, Pageable pageable);

    @Query(value = "SELECT m.* FROM messages m " +
            "WHERE m.contact_binding_id = ?1 " +
            "ORDER BY m.sent_timestamp DESC " +
            "LIMIT 1",
            nativeQuery = true)
    MessageEntity findSingleByContactBindingId(long contactBindingId);

    @Query("SELECT m FROM MessageEntity  m WHERE " +
            "m.deletedForUserIds NOT LIKE CONCAT('%:', :userId, ':%') AND " +
            "m.isPinned = true AND " +
            "(" +
            "(m.receiver.id = :userId AND m.sender.id = :contactUserId) OR " +
            "(m.receiver.id = :contactUserId AND m.sender.id = :userId)" +
            ") ORDER BY m.sentTimestamp DESC")
    List<MessageEntity> findPinnedMessagesByUsers(Long userId, Long contactUserId);

    @Modifying
    @Query("UPDATE MessageEntity m SET m.seen = true WHERE m.id = :messageId")
    void setToSeen(long messageId);


    @Modifying
    @Query("UPDATE MessageEntity m SET m.received = true WHERE m.id = :messageId")
    void setToReceived(long messageId);

    @Modifying
    @Query("UPDATE MessageEntity m SET m.isDeleted = true WHERE m.id = :messageId")
    void setToDeleted(long messageId);

    void deleteByNodeId(Long nodeId);

    @Modifying
    @Query("UPDATE MessageEntity m " +
            "SET m.deletedForUserIds = CONCAT(m.deletedForUserIds, ':', :userId, ':') " +
            "WHERE m.contactBindingId = :contactBindingId")
    void deleteByContactBindingId(Long contactBindingId, Long userId);

//    @Modifying
//    @Query("UPDATE MessageEntity m set m.isPinned = :isPinned where m.id = :messageId")
//    void updatePinnedStatus(Long messageId, Boolean isPinned);


//    Page<MessageEntity> findAllBySenderIdAndReceiverId(long senderId, long receiverId, Pageable pageable);
//
//    Page<MessageEntity> findAllBySenderIdInAndReceiverIdIn(Collection<Long> senderReceiver,
//                                                           Collection<Long> receiverSender,
//                                                           Pageable pageable);
}
