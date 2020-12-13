package com.pingchat.authenticationservice.service.memory;

import com.pingchat.authenticationservice.model.dto.MessageDto;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UnreadMessagesInMemoryService {
    private final Map<String, Map<String, Map<Long, MessageDto>>> unreadMessages = new HashMap<>();

    // TODO: After impl of saving messages on phones, use this to fetch unread (without pinging the db)
    public Map<String, Map<Long, MessageDto>> getMessagesBySender(String userPhoneNumber) {
        return unreadMessages.get(userPhoneNumber);
    }

    public void add(MessageDto messageDto, String senderPhoneNumber, String receiverPhoneNumber) {
        Map<String, Map<Long, MessageDto>> receiverMessages = unreadMessages.get(receiverPhoneNumber);

        Map<Long, MessageDto> receiverMessagesBySender;


        if (receiverMessages == null) {
            receiverMessages = new HashMap<>();
            unreadMessages.put(receiverPhoneNumber, receiverMessages);

            receiverMessagesBySender = new HashMap<>();
            receiverMessages.put(senderPhoneNumber, receiverMessagesBySender);
        } else {
            receiverMessagesBySender = receiverMessages.get(senderPhoneNumber);

            if (receiverMessagesBySender == null) {
                receiverMessagesBySender = new HashMap<>();
                receiverMessages.put(senderPhoneNumber, receiverMessagesBySender);
            }
        }

        receiverMessagesBySender.put(messageDto.getId(), messageDto);
    }

    public void remove(long messageId, String senderPhoneNumber, String receiverPhoneNumber) {
        Map<String, Map<Long, MessageDto>> receiverMessages = unreadMessages.get(receiverPhoneNumber);

        if (receiverMessages != null) {
            Map<Long, MessageDto> receiverMessagesBySender = receiverMessages.get(senderPhoneNumber);

            if (receiverMessagesBySender != null) {
                receiverMessagesBySender.remove(messageId);
            }
        }
    }
}
