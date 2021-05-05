package com.pingchat.authenticationservice.api.rest;

import com.pingchat.authenticationservice.auth.util.SecurityContextUserProvider;
import com.pingchat.authenticationservice.model.dto.ContactDto;
import com.pingchat.authenticationservice.service.SmsService;
import com.pingchat.authenticationservice.service.data.ContactDataService;
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

    private final SmsService smsService;

    public ContactsController(ContactDataService contactDataService,
                              SmsService smsService) {
        this.contactDataService = contactDataService;
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

    @GetMapping("search")
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

    @PostMapping("qr")
    public ContactDto addContact(@RequestBody String contactPhoneNumber) {
        contactPhoneNumber = contactPhoneNumber.replaceAll("\"", "");
        String currentUserPhoneNumber = SecurityContextUserProvider.currentUserPrincipal();

        ContactDto contactDto = contactDataService.findByUserAndPeer(currentUserPhoneNumber, contactPhoneNumber);

        if (contactDto == null) {
            contactDto = contactDataService.addContact(currentUserPhoneNumber, contactPhoneNumber);
        }

        return contactDto;
    }


    @GetMapping("{userId}/search/{peerId}")
    public ContactDto findByUserAndPeer(@PathVariable Long userId, @PathVariable Long peerId) {
        return contactDataService.findByUserAndPeer(userId, peerId);
    }

    @PostMapping("sync")
    public List<ContactDto> addContacts(@RequestBody List<ContactDto> contacts) {
        return contactDataService.addContacts(SecurityContextUserProvider.currentUserPrincipal(), contacts);
    }

//    @DeleteMapping("{contactId}/delete")
//    public void deleteContact(@PathVariable Long contactId) {
//        contactDataService.delete(contactId);
//    }

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
        contactDataService.setFavouriteStatus(contactId, isFavourite);
    }

    @PostMapping("{contactId}/name")
    public void updateContactName(@PathVariable Long contactId, @RequestBody String contactName) {
        contactName = contactName.replaceAll("\"", "");
        contactDataService.updateContactName(contactId, contactName);
    }

    @PostMapping("{contactId}/background")
    public void updateBackground(@PathVariable Long contactId, @RequestBody String background) {
        background = background.replaceAll("\"", "");
        contactDataService.updateBackground(contactId, background);
    }
}

