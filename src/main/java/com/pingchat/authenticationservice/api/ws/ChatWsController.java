package com.pingchat.authenticationservice.api.ws;

import com.pingchat.authenticationservice.model.event.PresenceEvent;
import com.pingchat.authenticationservice.model.event.TypingEvent;
import com.pingchat.authenticationservice.service.memory.PresenceInMemoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Slf4j
@Controller
public class ChatWsController {
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final PresenceInMemoryService presenceInMemoryService;

    public ChatWsController(SimpMessagingTemplate simpMessagingTemplate,
                            PresenceInMemoryService presenceInMemoryService) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.presenceInMemoryService = presenceInMemoryService;
    }

    @Async
    @EventListener
    @MessageMapping("/users/status")
    public void onPresenceStatusChange(@Payload PresenceEvent presenceEvent) {
        log.info("User's status changed(user={}, status={})", presenceEvent.getUserPhoneNumber(), presenceEvent.isStatus());

        presenceInMemoryService.setPresence(presenceEvent);

        simpMessagingTemplate.convertAndSend(
                "/users/" + presenceEvent.getUserPhoneNumber() + "/status",
                presenceEvent);
    }

    @MessageMapping("/users/typing")
    public void sendMessage(@Payload TypingEvent typingEvent, Principal senderPrincipal) {
        String receiverPhoneNumber = typingEvent.getReceiverPhoneNumber();

        String senderPhoneNumber = senderPrincipal.getName();
        typingEvent.setSenderPhoneNumber(senderPhoneNumber);

        simpMessagingTemplate.convertAndSendToUser(receiverPhoneNumber, "/users/typing", typingEvent);
    }
}
