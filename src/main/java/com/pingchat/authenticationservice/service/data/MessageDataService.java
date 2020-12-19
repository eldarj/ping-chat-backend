package com.pingchat.authenticationservice.service.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pingchat.authenticationservice.data.mysql.entity.MessageEntity;
import com.pingchat.authenticationservice.data.mysql.repository.MessageRepository;
import com.pingchat.authenticationservice.data.mysql.repository.UserRepository;
import com.pingchat.authenticationservice.model.dto.MessageDto;
import com.pingchat.authenticationservice.model.event.PresenceEvent;
import com.pingchat.authenticationservice.service.memory.PresenceInMemoryService;
import com.pingchat.authenticationservice.util.pagination.PagedSearchResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Slf4j
@Service
public class MessageDataService {
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final ObjectMapper objectMapper;

    private final PresenceInMemoryService presenceInMemoryService;

    public MessageDataService(UserRepository userRepository,
                              MessageRepository messageRepository,
                              ObjectMapper objectMapper,
                              PresenceInMemoryService presenceInMemoryService) {
        this.userRepository = userRepository;
        this.messageRepository = messageRepository;
        this.objectMapper = objectMapper;
        this.presenceInMemoryService = presenceInMemoryService;
    }

    public PagedSearchResult<MessageDto> findRecentSentOrReceived(Long userId, int pageSize, int pageNumber) {
        List<MessageEntity> messageEntitiesPage = messageRepository.findDistinctByUser(userId,
                pageSize, pageNumber * pageSize);

        List<MessageDto> messageDtos = messageEntitiesPage.stream().map(messageEntity -> {
            MessageDto messageDto = objectMapper.convertValue(messageEntity, MessageDto.class);

            PresenceEvent receiverPresence = presenceInMemoryService.getPresence(
                    messageDto.getReceiver().getCountryCode().getDialCode() + messageDto.getReceiver().getPhoneNumber());

            PresenceEvent senderPresence = presenceInMemoryService.getPresence(
                    messageDto.getSender().getCountryCode().getDialCode() + messageDto.getSender().getPhoneNumber());

            if (receiverPresence != null) {
                messageDto.setReceiverOnline(receiverPresence.isStatus());
                messageDto.setReceiverLastOnlineTimestamp(receiverPresence.getEventTimestamp());
            }

            if (senderPresence != null) {
                messageDto.setSenderOnline(senderPresence.isStatus());
                messageDto.setSenderLastOnlineTimestamp(senderPresence.getEventTimestamp());
            }

            return messageDto;
        }).collect(toList());

        return new PagedSearchResult<>(messageDtos, Integer.toUnsignedLong(messageDtos.size()));
    }

    public PagedSearchResult<MessageDto> findMessagesByUsers(Long userId, Long anotherUserId, PageRequest pageRequest) {
        Page<MessageEntity> messageEntitiesPage = messageRepository.findByUsers(userId, anotherUserId, pageRequest);

        List<MessageDto> messageDtos = objectMapper.convertValue(messageEntitiesPage.getContent(), List.class);

        return new PagedSearchResult<>(messageDtos, messageEntitiesPage.getTotalElements());
    }

    public MessageDto save(MessageDto messageDto) {
        MessageEntity messageEntity = messageRepository.save(objectMapper.convertValue(messageDto,
                MessageEntity.class));

        return objectMapper.convertValue(messageEntity, MessageDto.class);
    }

    @Transactional
    public void updateToSeen(long messageId) {
        messageRepository.setToSeen(messageId);
    }


    @Transactional
    public void updateToReceived(long messageId) {
        messageRepository.setToReceived(messageId);
    }

    @Transactional
    public void deleteById(Long messageId) {
        messageRepository.deleteMessage(messageId);
    }

//    public Page<MessageDto> findBySenderOrReceiver(String senderPhoneNumber,
//                                                   String receiverPhoneNumber,
//                                                   PageRequest pageRequest) {
//        List<Long> senderOrReceiver = List.of(userRepository.findByDialCodeAndPhoneNumber(senderPhoneNumber).getId(),
//                userRepository.findByDialCodeAndPhoneNumber(receiverPhoneNumber).getId());
//
//        log.info("Get messages by {}", senderOrReceiver);
//
//        return messageRepository.findAllBySenderIdInAndReceiverIdIn(senderOrReceiver, senderOrReceiver, pageRequest)
//                .map(messageEntity -> objectMapper.convertValue(messageEntity, MessageDto.class));
//    }
//
//    public void saveMessage(MessageDto messageDto) {
//        MessageEntity messageEntity = objectMapper.convertValue(messageDto, MessageEntity.class);
//        messageEntity.setReceiver(userRepository.findByDialCodeAndPhoneNumber(messageDto.getSendTo())); // TODO avoid
//        // getting from db
//        messageEntity.setSender(userRepository.findByDialCodeAndPhoneNumber(messageDto.getSentFrom()));
//
//        messageRepository.save(messageEntity);
//    }
}
