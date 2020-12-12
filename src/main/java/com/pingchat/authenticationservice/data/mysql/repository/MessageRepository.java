package com.pingchat.authenticationservice.data.mysql.repository;

import com.pingchat.authenticationservice.data.mysql.entity.MessageEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;

public interface MessageRepository extends JpaRepository<MessageEntity, Long> {
    @Query("SELECT m FROM MessageEntity m WHERE m.receiver.id = :userId OR m.sender.id = :userId")
    Page<MessageEntity> findAllBySenderOrReceiver(Long userId, Pageable pageable);

//    Page<MessageEntity> findAllBySenderIdAndReceiverId(long senderId, long receiverId, Pageable pageable);
//
//    Page<MessageEntity> findAllBySenderIdInAndReceiverIdIn(Collection<Long> senderReceiver,
//                                                           Collection<Long> receiverSender,
//                                                           Pageable pageable);
}
