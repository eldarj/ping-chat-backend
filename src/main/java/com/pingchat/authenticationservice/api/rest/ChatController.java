package com.pingchat.authenticationservice.api.rest;


import com.pingchat.authenticationservice.model.dto.MessageDto;
import com.pingchat.authenticationservice.model.event.PresenceEvent;
import com.pingchat.authenticationservice.service.data.MessageDataService;
import com.pingchat.authenticationservice.service.memory.PresenceInMemoryService;
import com.pingchat.authenticationservice.util.pagination.PagedSearchResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/chat")
public class ChatController {
    private final MessageDataService messageDataService;
    private final PresenceInMemoryService presenceInMemoryService;

    public ChatController(MessageDataService messageDataService,
                          PresenceInMemoryService presenceInMemoryService) {
        this.messageDataService = messageDataService;
        this.presenceInMemoryService = presenceInMemoryService;
    }

    @GetMapping("{userId}")
    public PagedSearchResult<MessageDto> findRecentChats(@PathVariable Long userId,
                                                         @RequestParam Integer pageSize,
                                                         @RequestParam Integer pageNumber) {
        return messageDataService.findRecentSentOrReceived(userId, pageSize, pageNumber);
    }

    @GetMapping("presence")
    public List<PresenceEvent> findPresenceStatuses(@RequestParam List<String> phoneNumbers) {
        return presenceInMemoryService.getPresences(phoneNumbers);
    }

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
