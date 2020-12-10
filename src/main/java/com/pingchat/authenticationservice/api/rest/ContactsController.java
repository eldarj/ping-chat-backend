package com.pingchat.authenticationservice.api.rest;

import com.pingchat.authenticationservice.auth.util.SecurityContextUserProvider;
import com.pingchat.authenticationservice.model.ContactDto;
import com.pingchat.authenticationservice.service.data.ContactDataService;
import com.pingchat.authenticationservice.util.pagination.PagedSearchResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/contacts")
public class ContactsController {
    private final ContactDataService contactDataService;

    public ContactsController(ContactDataService contactDataService) {
        this.contactDataService = contactDataService;
    }

    @GetMapping
    public PagedSearchResult<ContactDto> findAll(Integer pageSize, Integer pageNumber, Long userId) {
        pageNumber = pageNumber - 1;
        PagedSearchResult<ContactDto> pagedContactDtos = contactDataService.findAllByFilter(pageSize,
                pageNumber, userId);

        log.info("Get paged contacts (pageSize={}, pageNumber={}, userId={}, returnedElements={}, totalElements={})",
                pageSize, pageNumber, userId,
                pagedContactDtos.getPage().size(),
                pagedContactDtos.getTotalElements());

        return pagedContactDtos;
    }

    @PostMapping
    public Map<String, Object> addContact(@RequestBody ContactDto contactDto) {
        Map<String, Object> response = new HashMap<>();
        String currentUserPhoneNumber = SecurityContextUserProvider.currentUserPrincipal();

        try {
            response.put("contact", contactDataService.addContact(currentUserPhoneNumber, contactDto));
        } catch (DataIntegrityViolationException e) {
            response.put("error", "Kontakt već postoji.");
        } catch (Exception e) {
            String pox = "pox";
        }

        return response;
    }

//    @GetMapping
//    public List<ContactDto> findAll() {
//        return contactDataService.findAll(SecurityContextUserProvider.currentUser());
//    }

//    @PostMapping
//    public Map<String, Object> addContacts(@RequestBody List<ContactDto> contacts) {
//        Map<String, Object> response = new HashMap<>();
//
//        try {
//            response.put("content", contactDataService.addContacts(SecurityContextUserProvider.currentUser(), contacts));
//        } catch (Exception e) {
//            response.put("error", e.getMessage());
//        }
//
//        return response;
//    }
}

