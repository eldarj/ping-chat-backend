package com.pingchat.authenticationservice.auth.ws;

import com.pingchat.authenticationservice.model.event.PresenceEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class UserPresenceInterceptor implements ChannelInterceptor {
    private static final Map<StompCommand, PresenceEvent> PRESENCE_EVENTS_BY_STOMP_COMMAND = Map.of(
            StompCommand.DISCONNECT, new PresenceEvent(false),
            StompCommand.CONNECT, new PresenceEvent(true));

    private final ApplicationEventPublisher applicationEventPublisher;

    public UserPresenceInterceptor(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public void postSend(Message<?> message, MessageChannel channel, boolean sent) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        StompCommand command = accessor.getCommand();
        Principal userPhoneNumber = accessor.getUser();

        log.info("{} executed {}", userPhoneNumber, command);
        if (userPhoneNumber != null) {
            PresenceEvent presenceEvent = PRESENCE_EVENTS_BY_STOMP_COMMAND.get(command);
            if (presenceEvent != null) {
                boolean isActive = true;

                MessageHeaders messageHeaders = message.getHeaders();
                Map<String, List<String>> nativeHeaders = messageHeaders.get("nativeHeaders", Map.class);
                if (nativeHeaders != null) {
                    List<String> isActiveHeader = nativeHeaders.get("isActiveHeader");
                    try {
                        isActive = Boolean.parseBoolean(isActiveHeader.get(0));
                    } catch (Exception ignored) {
                    }
                }

                if (isActive) {
                    presenceEvent.setUserPhoneNumber(userPhoneNumber.getName());
                    applicationEventPublisher.publishEvent(presenceEvent);
                }
            }
        }
    }
}
