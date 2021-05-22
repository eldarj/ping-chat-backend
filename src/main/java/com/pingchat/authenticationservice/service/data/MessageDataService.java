package com.pingchat.authenticationservice.service.data;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pingchat.authenticationservice.data.mysql.entity.ContactEntity;
import com.pingchat.authenticationservice.data.mysql.entity.MessageEntity;
import com.pingchat.authenticationservice.data.mysql.repository.ContactRepository;
import com.pingchat.authenticationservice.data.mysql.repository.MessageRepository;
import com.pingchat.authenticationservice.model.dto.MessageDto;
import com.pingchat.authenticationservice.model.event.PresenceEvent;
import com.pingchat.authenticationservice.service.memory.PresenceInMemoryService;
import com.pingchat.authenticationservice.service.memory.UnreadMessagesInMemoryService;
import com.pingchat.authenticationservice.util.pagination.PagedSearchResult;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.bridge.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

        Map<Object, Object> messagesPerContact = new HashMap<>();

        List<MessageDto> messages = messageEntitiesPage.stream().map(messageEntity -> {
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

            // Load messages for contact
            PagedSearchResult<MessageDto> userMessagesPage = findMessagesByContactBindingId(userId, messageDto.getContactBindingId(),
                    PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "sentTimestamp")));

            messagesPerContact.put(messageDto.getContactBindingId(), userMessagesPage);

            return messageDto;
        }).collect(toList());

        return new PagedSearchResult<>(
                messages,
                Integer.toUnsignedLong(messages.size()),
                messagesPerContact
        );
    }

    public PagedSearchResult<MessageDto> findMessagesByContactBindingId(Long userId,
                                                                        Long contactBindingId,
                                                                        PageRequest pageRequest) {
        Page<MessageEntity> messageEntitiesPage = messageRepository.findByContactBindingId(userId, contactBindingId, pageRequest);

        List<MessageDto> messageDtos = objectMapper.convertValue(messageEntitiesPage.getContent(), List.class);

        PagedSearchResult<MessageDto> pagedSearchResult = new PagedSearchResult<>(messageDtos,
                messageEntitiesPage.getTotalElements());

        ContactEntity contactEntity = contactRepository.findByUserIdAndContactBindingId(userId, contactBindingId);
        boolean isContactAdded = contactEntity != null && !contactEntity.isDeleted();

        pagedSearchResult.setAdditionalData(Map.of("isContactAdded", isContactAdded));

        return pagedSearchResult;
    }

    // TODO: Deprecated, remove
    public PagedSearchResult<MessageDto> findMessagesByUsers(Long userId, Long contactUserId, PageRequest pageRequest) {
        Page<MessageEntity> messageEntitiesPage = messageRepository.findByUsers(userId, contactUserId, pageRequest);

        List<MessageDto> messageDtos = objectMapper.convertValue(messageEntitiesPage.getContent(), List.class);

        PagedSearchResult<MessageDto> pagedSearchResult = new PagedSearchResult<>(messageDtos,
                messageEntitiesPage.getTotalElements());

        ContactEntity contactEntity = contactRepository.findByUserIdAndContactUserId(userId, contactUserId);
        boolean isContactAdded = contactEntity != null && !contactEntity.isDeleted();

        pagedSearchResult.setAdditionalData(Map.of("isContactAdded", isContactAdded));

        return pagedSearchResult;
    }

    public List<MessageDto> findPinnedMessagesByUsers(Long userId, Long contactUserId) {
        List<MessageEntity> messageEntities = messageRepository.findPinnedMessagesByUsers(userId, contactUserId);

        return objectMapper.convertValue(messageEntities, new TypeReference<>() {});
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
    public void deleteForUser(Long messageId, Long userId) {
        messageRepository.deleteForUser(messageId, userId);
    }

    @Transactional
    public void deleteAllForUser(Long contactBindingId, Long userId) {
        messageRepository.deleteByContactBindingId(contactBindingId, userId);
    }

    // TODO: Remove unused?
    @Transactional
    public void updatePinnedStatus(Long messageId, Boolean isPinned) {
        Optional<MessageEntity> optionalMessageEntity = messageRepository.findById(messageId);

        if (optionalMessageEntity.isPresent()) {
            MessageEntity messageEntity = optionalMessageEntity.get();
            messageEntity.setPinned(isPinned);
            messageEntity.setPinnedTimestamp(Instant.now().toEpochMilli());
        }
    }

    // TODO: Push edit-change to receiver
    @Transactional
    public void update(Long messageId, String text) {
        Optional<MessageEntity> optionalMessageEntity = messageRepository.findById(messageId);

        if (optionalMessageEntity.isPresent()) {
            MessageEntity messageEntity = optionalMessageEntity.get();
            messageEntity.setText(text);
            messageEntity.setEdited(true);

            messageRepository.save(messageEntity);
        }
    }
}
