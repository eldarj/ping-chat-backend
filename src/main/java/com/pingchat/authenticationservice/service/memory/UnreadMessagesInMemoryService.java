package com.pingchat.authenticationservice.service.memory;

import com.pingchat.authenticationservice.model.dto.MessageDto;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UnreadMessagesInMemoryService {
    private final Map<String, Map<String, Map<Long, MessageDto>>> unreadMessages = new HashMap<>();

    // TODO: After impl of saving messages on phones, use this to fetch unread (without pinging the db)
    public int getTotalUnreadMessages(String senderPhoneNumber, String receiverPhoneNumber) {
        int totalUnreadMessages = 0;

        Map<String, Map<Long, MessageDto>> messagesByReceiver = unreadMessages.get(receiverPhoneNumber);

        if (messagesByReceiver != null) {
            Map<Long, MessageDto> messagesBySender = messagesByReceiver.get(senderPhoneNumber);
            if (messagesBySender != null) {
                totalUnreadMessages = messagesBySender.size();
            }
        }

        return totalUnreadMessages;
    }

    public int add(MessageDto messageDto, String senderPhoneNumber, String receiverPhoneNumber) {
        Map<String, Map<Long, MessageDto>> messagesByReceiver = unreadMessages.get(receiverPhoneNumber);

        Map<Long, MessageDto> messagesBySender;


        if (messagesByReceiver == null) {
            messagesByReceiver = new HashMap<>();
            unreadMessages.put(receiverPhoneNumber, messagesByReceiver);

            messagesBySender = new HashMap<>();
            messagesByReceiver.put(senderPhoneNumber, messagesBySender);
        } else {
            messagesBySender = messagesByReceiver.get(senderPhoneNumber);

            if (messagesBySender == null) {
                messagesBySender = new HashMap<>();
                messagesByReceiver.put(senderPhoneNumber, messagesBySender);
            }
        }

        messagesBySender.put(messageDto.getId(), messageDto);

        int totalUnreadMessages = messagesBySender.size();

        return totalUnreadMessages;
    }

    public void remove(long messageId, String senderPhoneNumber, String receiverPhoneNumber) {
        Map<String, Map<Long, MessageDto>> receiverMessages = unreadMessages.get(receiverPhoneNumber);

        if (receiverMessages != null) {
            receiverMessages.remove(senderPhoneNumber);
//            Map<Long, MessageDto> receiverMessagesBySender = receiverMessages.get(senderPhoneNumber);
//
//            if (receiverMessagesBySender != null) {
//                receiverMessages.remove(receiverMessagesBySender);
//            }
        }
    }
}
