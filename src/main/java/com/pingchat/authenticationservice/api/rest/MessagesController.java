package com.pingchat.authenticationservice.api.rest;

import com.pingchat.authenticationservice.api.ws.MessagesWsController;
import com.pingchat.authenticationservice.auth.util.SecurityContextUserProvider;
import com.pingchat.authenticationservice.model.dto.MessageDto;
import com.pingchat.authenticationservice.service.data.DataSpaceDataService;
import com.pingchat.authenticationservice.service.data.MessageDataService;
import com.pingchat.authenticationservice.service.files.StaticFileStorageService;
import com.pingchat.authenticationservice.util.pagination.PagedSearchResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/api/messages")
public class MessagesController {
    private final MessageDataService messageDataService;
    private final StaticFileStorageService staticFileStorageService;
    private final DataSpaceDataService dataSpaceDataService;
    private final MessagesWsController messagesWsController;

    public MessagesController(MessageDataService messageDataService,
                              StaticFileStorageService staticFileStorageService,
                              DataSpaceDataService dataSpaceDataService,
                              MessagesWsController messagesWsController) {
        this.messageDataService = messageDataService;
        this.staticFileStorageService = staticFileStorageService;
        this.dataSpaceDataService = dataSpaceDataService;
        this.messagesWsController = messagesWsController;
    }

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

    @GetMapping("pinned")
    public List<MessageDto> findPinnedMessagesByUsers(@RequestParam Long userId,
                                                      @RequestParam Long contactUserId) {
        return messageDataService.findPinnedMessagesByUsers(userId, contactUserId);
    }

    @PostMapping("{messageId}/pin")
    public void updatePinStatus(@PathVariable Long messageId, @RequestBody Boolean isPinned) {
        messageDataService.updatePinnedStatus(messageId, isPinned);
    }

    @PostMapping("{messageId}")
    public void updateMessage(@PathVariable Long messageId, @RequestBody String text) {
        text = text.replaceAll("\"", "");
        messageDataService.update(messageId, text);
    }

    @DeleteMapping("{messageId}")
    public void deleteById(@PathVariable Long messageId) {
        MessageDto messageDto = messageDataService.findById(messageId);
        messageDataService.setDeleted(messageId);
        messagesWsController.messageDeleted(messageDto);

        Long nodeId = messageDto.getNodeId();
        if (nodeId != null) {
            if (Objects.equals(messageDto.getSender().getFullPhoneNumber(),
                    SecurityContextUserProvider.currentUserPrincipal())) {
                dataSpaceDataService.setOwnerDeletedById(nodeId);
                try {
                    staticFileStorageService.delete(messageDto.getFileName());
                } catch (IOException e) {
                    log.warn("Error deleting file: {}", messageDto.getFilePath());
                }
            } else {
                dataSpaceDataService.setReceiverDeletedById(nodeId);
            }
        }
    }

    @DeleteMapping
    public void deleteByContact(@RequestParam Long contactBindingId,
                                @RequestParam Long userId) {
        messageDataService.delete(contactBindingId, userId);
    }
}

