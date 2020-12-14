package com.pingchat.authenticationservice.data.mysql.repository;

import com.pingchat.authenticationservice.data.mysql.entity.MessageEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MessageRepository extends JpaRepository<MessageEntity, Long> {
    @Query(value = "SELECT m.* FROM (" +
            "SELECT max(sent_timestamp) AS sent_timestamp FROM messages " +
            "WHERE receiver_user_id = ?1 OR sender_user_id = ?1 GROUP BY contact_binding_id" +
            ") t INNER JOIN messages m ON m.sent_timestamp = t.sent_timestamp " +
            "ORDER BY m.sent_timestamp DESC " +
            "LIMIT ?2 OFFSET ?3",
            nativeQuery = true)
    List<MessageEntity> findDistinctByUser(Long userId, int pageSize, int pageNumber);

//    @Query("SELECT m FROM MessageEntity m WHERE m.receiver.id = :userId OR m.sender.id = :userId")
//    Page<MessageEntity> findDistinctByUser(Long userId, Pageable pageable);

    // TODO change this to a single hash value, equal across both contact entities
    @Query("SELECT m FROM MessageEntity  m WHERE " +
            "(m.receiver.id = :userId AND m.sender.id = :anotherUserId) OR " +
            "(m.receiver.id = :anotherUserId AND m.sender.id = :userId)")
    Page<MessageEntity> findByUsers(Long userId, Long anotherUserId, Pageable pageable);

    @Modifying
    @Query("UPDATE MessageEntity m SET m.seen = true WHERE m.id = :messageId")
    void setToSeen(long messageId);


    @Modifying
    @Query("UPDATE MessageEntity m SET m.received = true WHERE m.id = :messageId")
    void setToReceived(long messageId);

//    Page<MessageEntity> findAllBySenderIdAndReceiverId(long senderId, long receiverId, Pageable pageable);
//
//    Page<MessageEntity> findAllBySenderIdInAndReceiverIdIn(Collection<Long> senderReceiver,
//                                                           Collection<Long> receiverSender,
//                                                           Pageable pageable);
}
