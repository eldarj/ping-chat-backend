package com.pingchat.authenticationservice.service.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pingchat.authenticationservice.data.mysql.entity.ContactEntity;
import com.pingchat.authenticationservice.data.mysql.entity.MessageEntity;
import com.pingchat.authenticationservice.data.mysql.repository.ContactRepository;
import com.pingchat.authenticationservice.data.mysql.repository.MessageRepository;
import com.pingchat.authenticationservice.data.mysql.repository.UserRepository;
import com.pingchat.authenticationservice.model.dto.MessageDto;
import com.pingchat.authenticationservice.model.event.PresenceEvent;
import com.pingchat.authenticationservice.service.memory.PresenceInMemoryService;
import com.pingchat.authenticationservice.service.memory.UnreadMessagesInMemoryService;
import com.pingchat.authenticationservice.util.pagination.PagedSearchResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

@Slf4j
@Service
public class MessageDataService {
    private final MessageRepository messageRepository;
    private final ContactRepository contactRepository;

    private final ObjectMapper objectMapper;

    private final PresenceInMemoryService presenceInMemoryService;

    private final UnreadMessagesInMemoryService unreadMessagesInMemoryService;

    public MessageDataService(MessageRepository messageRepository,
                              ContactRepository contactRepository,
                              ObjectMapper objectMapper,
                              PresenceInMemoryService presenceInMemoryService,
                              UnreadMessagesInMemoryService unreadMessagesInMemoryService) {
        this.unreadMessagesInMemoryService = unreadMessagesInMemoryService;
        this.messageRepository = messageRepository;
        this.contactRepository = contactRepository;
        this.objectMapper = objectMapper;
        this.presenceInMemoryService = presenceInMemoryService;
    }

    public MessageDto findById(Long messageId) {
        return objectMapper.convertValue(messageRepository.findById(messageId), MessageDto.class);
    }

    public PagedSearchResult<MessageDto> findRecentSentOrReceived(Long userId, int pageSize, int pageNumber) {
        List<MessageEntity> messageEntitiesPage = messageRepository.findDistinctByUser(userId,
                pageSize, pageNumber * pageSize);

        List<MessageDto> messageDtos = messageEntitiesPage.stream().map(messageEntity -> {
            MessageDto messageDto = objectMapper.convertValue(messageEntity, MessageDto.class);

            if (messageEntity.getReceiver().getId().equals(userId)) {
                String receiverPhoneNumber = messageEntity.getReceiver().getCountryCode().getDialCode()
                        + messageEntity.getReceiver().getPhoneNumber();
                String senderPhoneNumber = messageEntity.getSender().getCountryCode().getDialCode()
                        + messageEntity.getSender().getPhoneNumber();

                int totalUnreadMessages = unreadMessagesInMemoryService.getTotalUnreadMessages(
                        senderPhoneNumber, receiverPhoneNumber);
                messageDto.setTotalUnreadMessages(totalUnreadMessages);
            }


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

    public PagedSearchResult<MessageDto> findMessagesByUsers(Long userId, Long contactUserId, PageRequest pageRequest) {
        Page<MessageEntity> messageEntitiesPage = messageRepository.findByUsers(userId, contactUserId, pageRequest);

        List<MessageDto> messageDtos = objectMapper.convertValue(messageEntitiesPage.getContent(), List.class);

        PagedSearchResult<MessageDto> pagedSearchResult = new PagedSearchResult<>(messageDtos,
                messageEntitiesPage.getTotalElements());

        ContactEntity contactEntity = contactRepository.findByUserIdAndContactUserId(userId, contactUserId);
        boolean isContactAdded = contactEntity != null;

        pagedSearchResult.setAdditionalData(Map.of("isContactAdded", isContactAdded));

        return pagedSearchResult;
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
    public void setDeleted(long messageId) {
        messageRepository.setToDeleted(messageId);
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
