package com.pingchat.authenticationservice.api.ws;

import com.pingchat.authenticationservice.model.event.PresenceEvent;
import com.pingchat.authenticationservice.service.memory.PresenceInMemoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
public class PresenceWsController {
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final PresenceInMemoryService presenceInMemoryService;

    public PresenceWsController(SimpMessagingTemplate simpMessagingTemplate,
                                PresenceInMemoryService presenceInMemoryService) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.presenceInMemoryService = presenceInMemoryService;
    }

    @Async
    @EventListener
    public void handleEventAfterConfigCrud(PresenceEvent presenceEvent) {
        log.info("User's status changed(user={}, status={})", presenceEvent.getUserPhoneNumber(), presenceEvent.isStatus());

        presenceInMemoryService.setPresence(presenceEvent);
        simpMessagingTemplate.convertAndSend("/users/status", presenceEvent);
    }
}
