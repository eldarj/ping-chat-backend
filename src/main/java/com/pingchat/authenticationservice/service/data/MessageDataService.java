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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

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

    public PagedSearchResult<MessageDto> findRecentSentOrReceived(Long userId, PageRequest pageRequest) {
        Page<MessageEntity> messageEntitiesPage = messageRepository.findAllBySenderOrReceiver(
                userId, pageRequest);

        List<MessageDto> messageDtos = messageEntitiesPage.getContent().stream().map(messageEntity -> {
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

        return new PagedSearchResult<>(messageDtos, messageEntitiesPage.getTotalElements());
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
