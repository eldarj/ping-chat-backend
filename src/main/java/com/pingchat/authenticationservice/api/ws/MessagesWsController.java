package com.pingchat.authenticationservice.api.ws;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.pingchat.authenticationservice.model.dto.MessageDto;
import com.pingchat.authenticationservice.model.dto.MessageStatusChangeDto;
import com.pingchat.authenticationservice.service.FirebaseService;
import com.pingchat.authenticationservice.service.data.DataSpaceDataService;
import com.pingchat.authenticationservice.service.data.MessageDataService;
import com.pingchat.authenticationservice.service.memory.UnreadMessagesInMemoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
public class MessagesWsController {
    private final SimpMessagingTemplate simpMessagingTemplate;

    private final UnreadMessagesInMemoryService unreadMessagesInMemoryService;

    private final MessageDataService messageDataService;
    private final DataSpaceDataService dataSpaceDataService;

    private final FirebaseService firebaseService;

    public MessagesWsController(SimpMessagingTemplate simpMessagingTemplate,
                                UnreadMessagesInMemoryService unreadMessagesInMemoryService,
                                MessageDataService messageDataService,
                                DataSpaceDataService dataSpaceDataService,
                                FirebaseService firebaseService) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.unreadMessagesInMemoryService = unreadMessagesInMemoryService;
        this.messageDataService = messageDataService;
        this.dataSpaceDataService = dataSpaceDataService;
        this.firebaseService = firebaseService;
    }

    @MessageMapping("/messages/send")
    public void sendMessage(@Payload MessageDto messageDto, Principal senderPrincipal)
            throws FirebaseMessagingException {
        messageDto.setSent(true);

        messageDto = messageDataService.save(messageDto);

        String senderPhoneNumber = senderPrincipal.getName();
        String receiverPhoneNumber = messageDto.getReceiver().getFullPhoneNumber();

        int totalUnreadMessages = unreadMessagesInMemoryService.add(
                messageDto, senderPhoneNumber, receiverPhoneNumber);

        messageDto.setTotalUnreadMessages(totalUnreadMessages);

        simpMessagingTemplate.convertAndSendToUser(senderPhoneNumber, "/messages/sent",
                messageDto);
        simpMessagingTemplate.convertAndSendToUser(receiverPhoneNumber, "/messages/receive",
                messageDto);

        firebaseService.sendMessageNotification(messageDto);
    }

    @MessageMapping("/messages/edit")
    public void editMessage(@Payload MessageDto messageDto, Principal senderPrincipal) {
        messageDataService.update(messageDto.getId(), messageDto.getText());

        String receiverPhoneNumber = messageDto.getReceiver().getFullPhoneNumber();

        simpMessagingTemplate.convertAndSendToUser(receiverPhoneNumber, "/messages/edited",
                messageDto);
    }

    @MessageMapping("/messages/received")
    public void messageReceived(@Payload MessageStatusChangeDto messageStatusChangeDto, Principal receiverPrincipal) {
        long messageId = messageStatusChangeDto.getId();
        String senderPhoneNumber = messageStatusChangeDto.getSenderPhoneNumber();

        messageDataService.updateToReceived(messageId);

//        unreadMessagesInMemoryService.remove(messageId, senderPhoneNumber, receiverPrincipal.getName());

        simpMessagingTemplate.convertAndSendToUser(senderPhoneNumber, "/messages/received",
                messageId);
    }

    @MessageMapping("/messages/seen")
    public void messageSeen(@Payload List<MessageStatusChangeDto> messageStatusChangeDto, Principal receiverPrincipal) {
        String senderPhoneNumber = messageStatusChangeDto.get(0).getSenderPhoneNumber();
        List<Long> messageIds = new ArrayList<>();

        messageStatusChangeDto.forEach(messageStatusChangeDto1 -> {
            long messageId = messageStatusChangeDto1.getId();

            messageDataService.updateToSeen(messageId);

            unreadMessagesInMemoryService.remove(messageId, senderPhoneNumber, receiverPrincipal.getName());

            messageIds.add(messageId);
        });

        simpMessagingTemplate.convertAndSendToUser(senderPhoneNumber, "/messages/seen",
                messageIds);
    }

    @MessageMapping("/messages/deleted")
    public void messageDeleted(@Payload MessageDto messageDto) {
        String receiverPhoneNumber = messageDto.getReceiver().getFullPhoneNumber();
        simpMessagingTemplate.convertAndSendToUser(receiverPhoneNumber, "/messages/deleted",
                messageDto);
    }
}
