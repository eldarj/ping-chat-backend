package com.pingchat.authenticationservice.api.rest;

import com.pingchat.authenticationservice.auth.util.SecurityContextUserProvider;
import com.pingchat.authenticationservice.model.dto.ContactDto;
import com.pingchat.authenticationservice.model.dto.MessageDto;
import com.pingchat.authenticationservice.service.SmsService;
import com.pingchat.authenticationservice.service.data.ContactDataService;
import com.pingchat.authenticationservice.service.data.MessageDataService;
import com.pingchat.authenticationservice.util.pagination.PagedSearchResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/contacts")
public class ContactsController {
    private final ContactDataService contactDataService;
    private final MessageDataService messageDataService;

    private final SmsService smsService;

    public ContactsController(ContactDataService contactDataService,
                              MessageDataService messageDataService,
                              SmsService smsService) {
        this.contactDataService = contactDataService;
        this.messageDataService = messageDataService;
        this.smsService = smsService;
    }

    @GetMapping
    public PagedSearchResult<ContactDto> findAll(Integer pageSize, Integer pageNumber,
                                                 Long userId, Boolean favourites) {
        pageNumber = pageNumber - 1;
        PagedSearchResult<ContactDto> pagedContactDtos = contactDataService.findAllByFilter(pageSize,
                pageNumber, userId, favourites);

        log.info("Get paged contacts (pageSize={}, pageNumber={}, userId={}, favourites={}, returnedElements={}, " +
                        "totalElements={})",
                pageSize, pageNumber, userId, favourites,
                pagedContactDtos.getPage().size(),
                pagedContactDtos.getTotalElements());

        return pagedContactDtos;
    }

    @GetMapping("/search")
    public List<ContactDto> findAllByNameOrPhonenumber(Long userId, String searchQuery) {
        return contactDataService.findAllByNameOrPhonenumber(userId, searchQuery);
    }

    @PostMapping
    public Map<String, Object> addContact(@RequestBody ContactDto contactDto) {
        Map<String, Object> response = new HashMap<>();
        String currentUserPhoneNumber = SecurityContextUserProvider.currentUserPrincipal();

        try {
            response.put("contact", contactDataService.addContact(currentUserPhoneNumber, contactDto));
        } catch (DataIntegrityViolationException e) {
            response.put("error", "Contact already exists");
        }

        return response;
    }

    @PostMapping("sync")
    public List<ContactDto> addContacts(@RequestBody List<ContactDto> contacts) {
        return contactDataService.addContacts(SecurityContextUserProvider.currentUserPrincipal(), contacts);
    }

    @DeleteMapping("{contactId}/delete")
    public void deleteContact(@PathVariable Long contactId) {
        contactDataService.delete(contactId);
    }

    // TODO Include app download link
    @PostMapping("invite")
    public void inviteContact(@RequestBody String phoneNumber) throws IOException, InterruptedException {
        String currentUserPhoneNumber = SecurityContextUserProvider.currentUserPrincipal();
        phoneNumber = phoneNumber.replaceAll("\"", "");
        smsService.sendSms(
                String.format("Upravo ste dobili pozivnicu za Ping Chat od %s", currentUserPhoneNumber),
                phoneNumber,
                "PingChat"
        );
    }

    @PostMapping("{contactId}/favourite")
    public void updateFavouriteStatus(@PathVariable Long contactId, @RequestBody Boolean isFavourite) {
        try {
            contactDataService.setFavouriteStatus(contactId, isFavourite);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}

