package com.pingchat.authenticationservice.api.rest;

import com.pingchat.authenticationservice.model.dto.MessageDto;
import com.pingchat.authenticationservice.service.data.MessageDataService;
import com.pingchat.authenticationservice.util.pagination.PagedSearchResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/messages")
public class MessagesController {
    private final MessageDataService messageDataService;

    public MessagesController(MessageDataService messageDataService) {
        this.messageDataService = messageDataService;
    }

    // Get chat messages
    @GetMapping
    public PagedSearchResult<MessageDto> findMessagesByUsers(@RequestParam Long userId,
                                                             @RequestParam Long contactUserId,
                                                             @RequestParam Integer pageSize,
                                                             @RequestParam Integer pageNumber) {
        long startTime = System.nanoTime();

        PagedSearchResult<MessageDto> userMessagesPage = messageDataService.findMessagesByUsers(userId, contactUserId,
                PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "sentTimestamp")));

        long endTime = System.nanoTime();
        long ms = (endTime - startTime) / 1_000_000;

        log.info("Fetched user messages in s={} (ms={})", ms / 1000, ms);

        return userMessagesPage;
    }

    // Get pinned messages
    @GetMapping("pinned")
    public List<MessageDto> findPinnedMessagesByUsers(@RequestParam Long userId,
                                                      @RequestParam Long contactUserId) {
        return messageDataService.findPinnedMessagesByUsers(userId, contactUserId);
    }

    // Pin message
    @PostMapping("{messageId}/pin")
    public void updatePinStatus(@PathVariable Long messageId,
                                @RequestBody Boolean isPinned) {
        messageDataService.updatePinnedStatus(messageId, isPinned);
    }

    // Delete single message
    @DeleteMapping("{messageId}")
    public void deleteById(@PathVariable Long messageId,
                           @RequestParam Long userId,
                           @RequestParam(required = false) Boolean deleteForEveryone) {
        if (deleteForEveryone != null && deleteForEveryone) {
            messageDataService.deleteForEveryone(messageId);
        } else {
            messageDataService.deleteForUser(messageId, userId);
        }
    }

    // Delete all messages by contact
    @DeleteMapping
    public void deleteByContact(@RequestParam Long userId,
                                @RequestParam Long contactBindingId) {
        messageDataService.deleteAllForUser(contactBindingId, userId);
    }
}

