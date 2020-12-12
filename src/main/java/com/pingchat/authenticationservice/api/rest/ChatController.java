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
@RequestMapping("/api/chat")
public class ChatController {
    private final ContactDataService contactDataService;
    private final MessageDataService messageDataService;

    public ChatController(ContactDataService contactDataService,
                          MessageDataService messageDataService) {
        this.contactDataService = contactDataService;
        this.messageDataService = messageDataService;
    }

    @GetMapping("{userId}")
    public PagedSearchResult<MessageDto> findRecentChats(@PathVariable Long userId,
                                                         @RequestParam Integer pageSize,
                                                         @RequestParam Integer pageNumber) {
        return messageDataService.findRecentSentOrReceived(userId,
                PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "sentTimestamp")));
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
