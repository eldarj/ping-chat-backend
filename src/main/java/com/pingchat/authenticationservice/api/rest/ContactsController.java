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

    // Get all contacts
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

    // Search by name or phonenumber
    @GetMapping("search")
    public List<ContactDto> findAllByNameOrPhonenumber(Long userId, String searchQuery) {
        return contactDataService.findAllByNameOrPhonenumber(userId, searchQuery);
    }

    // Find single by phonenumber
    @GetMapping("{phoneNumber}")
    public ContactDto findByFullPhoneNumber(@PathVariable String phoneNumber) {
        Long currentUserId = SecurityContextUserProvider.currentUserId();
        return contactDataService.findByUserAndPeer(currentUserId, phoneNumber);
    }

    // Find single by user and peer
    @GetMapping("{userId}/search/{peerId}")
    public ContactDto findByUserAndPeer(@PathVariable Long userId, @PathVariable Long peerId) {
        return contactDataService.findByUserAndPeer(userId, peerId);
    }

    // Find recent contacts
    @GetMapping("recent")
    public List<ContactDto> findRecentContacts() {
        Long userId = SecurityContextUserProvider.currentUserId();
        return contactDataService.findRecent(userId);
    }

    // Add single contact (Form)
    @PostMapping
    public Map<String, Object> addContact(@RequestBody ContactDto contactDto) {
        Map<String, Object> response = new HashMap<>();
        String currentUserPhoneNumber = SecurityContextUserProvider.currentPhoneNumber();

        try {
            response.put("contact", contactDataService.addContact(currentUserPhoneNumber, contactDto));
        } catch (DataIntegrityViolationException e) {
            response.put("error", "Contact already exists");
        }

        return response;
    }

    // Add single contact (QR Code)
    @PostMapping("qr")
    public ContactDto addContact(@RequestBody String contactPhoneNumber) {
        contactPhoneNumber = contactPhoneNumber.replaceAll("\"", "");
        String currentUserPhoneNumber = SecurityContextUserProvider.currentPhoneNumber();

        ContactDto contactDto = contactDataService.findByUserAndPeer(currentUserPhoneNumber, contactPhoneNumber);

        if (contactDto == null) {
            contactDto = contactDataService.addContact(currentUserPhoneNumber, contactPhoneNumber);
        }

        return contactDto;
    }

    // Sync multiple contacts from cotnactbook
    @PostMapping("sync")
    public List<ContactDto> addContacts(@RequestBody List<ContactDto> contacts) {
        return contactDataService.addContacts(SecurityContextUserProvider.currentPhoneNumber(), contacts);
    }

    // TODO: Include app download link
    // Send invite
    @PostMapping("invite")
    public void inviteContact(@RequestBody String phoneNumber) throws IOException, InterruptedException {
        String currentUserPhoneNumber = SecurityContextUserProvider.currentPhoneNumber();
        phoneNumber = phoneNumber.replaceAll("\"", "");
        smsService.sendSms(
                String.format("Upravo ste dobili pozivnicu za Ping Chat od %s", currentUserPhoneNumber),
                phoneNumber,
                "PingChat"
        );
    }

    // Update favourite status
    @PostMapping("{contactId}/favourite")
    public void updateFavouriteStatus(@PathVariable Long contactId, @RequestBody Boolean isFavourite) {
        contactDataService.updateFavouriteStatus(contactId, isFavourite);
    }

    // Update contact name
    @PostMapping("{contactId}/name")
    public void updateContactName(@PathVariable Long contactId,
                                  @RequestBody String contactName,
                                  @RequestParam Long contactBindingId) {
        contactName = contactName.replaceAll("\"", "");
        contactDataService.updateContactName(contactId, contactName, contactBindingId);
    }

    // Update chat background
    @PostMapping("{contactId}/background")
    public void updateBackground(@PathVariable Long contactId, @RequestBody String background) {
        background = background.replaceAll("\"", "");
        contactDataService.updateBackground(contactId, background);
    }

    // Delete contact
    @DeleteMapping("{contactId}/delete")
    public void deleteContact(@PathVariable Long contactId,
                              @RequestParam Long contactBindingId,
                              @RequestParam Long userId) {
        contactDataService.delete(contactId, contactBindingId, userId);
    }
}

