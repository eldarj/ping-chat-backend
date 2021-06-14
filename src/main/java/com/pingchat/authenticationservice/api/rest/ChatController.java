package com.pingchat.authenticationservice.api.rest;

import com.pingchat.authenticationservice.auth.util.SecurityContextUserProvider;
import com.pingchat.authenticationservice.model.dto.MessageDto;
import com.pingchat.authenticationservice.model.event.PresenceEvent;
import com.pingchat.authenticationservice.service.FirebaseService;
import com.pingchat.authenticationservice.service.data.MessageDataService;
import com.pingchat.authenticationservice.service.memory.PresenceInMemoryService;
import com.pingchat.authenticationservice.util.pagination.PagedSearchResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/chat")
public class ChatController {
    private final MessageDataService messageDataService;
    private final PresenceInMemoryService presenceInMemoryService;
    private final FirebaseService firebaseService;

    public ChatController(MessageDataService messageDataService,
                          PresenceInMemoryService presenceInMemoryService,
                          FirebaseService firebaseService) {
        this.messageDataService = messageDataService;
        this.presenceInMemoryService = presenceInMemoryService;
        this.firebaseService = firebaseService;
    }

    // Get recent chat messages (ChatListActivity)
    @GetMapping("{userId}")
    public PagedSearchResult<MessageDto> findRecentChats(@PathVariable Long userId,
                                                         @RequestParam Integer pageSize,
                                                         @RequestParam Integer pageNumber) {
        return messageDataService.findRecentSentOrReceived(userId, pageSize, pageNumber);
    }

    // Set presence status
    @GetMapping("presence")
    public List<PresenceEvent> findPresenceStatuses(@RequestParam List<String> phoneNumbers) {
        return presenceInMemoryService.getPresences(phoneNumbers);
    }

    // Send call notification
    @GetMapping("call")
    public void sendCallNotification(@RequestParam String senderContactName,
                                     @RequestParam String receiverPhoneNumber) throws UnsupportedEncodingException {
        receiverPhoneNumber = URLDecoder.decode(receiverPhoneNumber, StandardCharsets.UTF_8.toString());
        String senderPhoneNumber = SecurityContextUserProvider.currentPhoneNumber();
        firebaseService.sendCallNotification(senderContactName, senderPhoneNumber, receiverPhoneNumber);
    }

    // Backgrounds
    @GetMapping("backgrounds")
    public List<String> findBackgrounds() {
        return List.of(
                "bg-5.png",
                "bg-6.png",
                "bg-7.png",
                "bg-8.png",
                "bg-9.png",
                "bg-11.png",
                "chat-1.jpg",
                "chat-2.png",
                "chat-3.png",
                "chat-4.jpg",
                "chat-5.png",
                "chat-6.png",
                "chat-7.png",
                "chat-8.png",
                "chat-11.png",
                "chat-12.png"
        );
    }
}
