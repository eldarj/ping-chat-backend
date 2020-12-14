package com.pingchat.authenticationservice.api.ws;


import com.pingchat.authenticationservice.model.dto.MessageDto;
import com.pingchat.authenticationservice.model.dto.MessageStatusChangeDto;
import com.pingchat.authenticationservice.service.data.MessageDataService;
import com.pingchat.authenticationservice.service.memory.UnreadMessagesInMemoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Slf4j
@Controller
public class MessagesWsController {
    private final SimpMessagingTemplate simpMessagingTemplate;

    private final UnreadMessagesInMemoryService unreadMessagesInMemoryService;

    private final MessageDataService messageDataService;

    public MessagesWsController(SimpMessagingTemplate simpMessagingTemplate,
                                UnreadMessagesInMemoryService unreadMessagesInMemoryService,
                                MessageDataService messageDataService) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.unreadMessagesInMemoryService = unreadMessagesInMemoryService;
        this.messageDataService = messageDataService;
    }

    @MessageMapping("/messages/send")
    public void sendMessage(@Payload MessageDto messageDto, Principal senderPrincipal) {
        messageDto.setSent(true);

        messageDto = messageDataService.save(messageDto);

        String senderPhoneNumber = senderPrincipal.getName();
        String receiverPhoneNumber = messageDto.getReceiver().getFullPhoneNumber();
        unreadMessagesInMemoryService.add(messageDto, receiverPhoneNumber, senderPhoneNumber);

        simpMessagingTemplate.convertAndSendToUser(senderPhoneNumber, "/messages/sent",
                messageDto);
        simpMessagingTemplate.convertAndSendToUser(receiverPhoneNumber, "/messages/receive",
                messageDto);
    }

    @MessageMapping("/messages/received")
    public void messageReceived(@Payload MessageStatusChangeDto messageStatusChangeDto, Principal receiverPrincipal) {
        long messageId = messageStatusChangeDto.getId();
        String senderPhoneNumber = messageStatusChangeDto.getSenderPhoneNumber();

        messageDataService.updateToReceived(messageId);

        unreadMessagesInMemoryService.remove(messageId, senderPhoneNumber, receiverPrincipal.getName());

        simpMessagingTemplate.convertAndSendToUser(senderPhoneNumber, "/messages/received",
                messageId);
    }

    @MessageMapping("/messages/seen")
    public void messageSeen(@Payload MessageStatusChangeDto messageStatusChangeDto, Principal receiverPrincipal) {
        long messageId = messageStatusChangeDto.getId();
        String senderPhoneNumber = messageStatusChangeDto.getSenderPhoneNumber();

        messageDataService.updateToSeen(messageId);

        unreadMessagesInMemoryService.remove(messageId, senderPhoneNumber, receiverPrincipal.getName());

        simpMessagingTemplate.convertAndSendToUser(senderPhoneNumber, "/messages/seen",
                messageId);
    }
}
