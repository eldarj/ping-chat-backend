package com.pingchat.authenticationservice.api.rest;

import com.pingchat.authenticationservice.model.dto.MessageDto;
import com.pingchat.authenticationservice.service.data.ContactDataService;
import com.pingchat.authenticationservice.service.data.MessageDataService;
import com.pingchat.authenticationservice.util.pagination.PagedSearchResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/messages")
public class MessagesController {
    private final MessageDataService messageDataService;

    public MessagesController(MessageDataService messageDataService) {
        this.messageDataService = messageDataService;
    }

    @GetMapping
    public PagedSearchResult<MessageDto> findMessagesByUsers(@RequestParam Long userId,
                                                             @RequestParam Long anotherUserId,
                                                             @RequestParam Integer pageSize,
                                                             @RequestParam Integer pageNumber) {
        long startTime = System.nanoTime();

        PagedSearchResult<MessageDto> userMessagesPage = messageDataService.findMessagesByUsers(userId, anotherUserId,
                PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "sentTimestamp")));

        long endTime = System.nanoTime();
        long ms = (endTime - startTime) / 1_000_000;

        log.info("Fetched user messages in s={} (ms={})", ms / 1000, ms);

        return userMessagesPage;
    }

//    @GetMapping("/{receiverPhoneNumber}/{pageNumber}")
//    public Page<MessageDto> findMessagesByContactId(@PathVariable String receiverPhoneNumber,
//                                                    @PathVariable int pageNumber) {
//        String senderPhoneNumber = SecurityContextUserProvider.currentUserPrincipal();
//        return messageDataService.findBySenderOrReceiver(
//                senderPhoneNumber,
//                receiverPhoneNumber,
//                PageRequest.of(pageNumber, 20, Sort.by(Sort.Direction.DESC, "sentTimestamp"))
//        );
//    }
}

