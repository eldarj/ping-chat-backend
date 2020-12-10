package com.pingchat.authenticationservice.service.data;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.pingchat.authenticationservice.data.mysql.entity.ContactEntity;
import com.pingchat.authenticationservice.data.mysql.entity.UserEntity;
import com.pingchat.authenticationservice.data.mysql.repository.ContactRepository;
import com.pingchat.authenticationservice.data.mysql.repository.UserRepository;
import com.pingchat.authenticationservice.model.ContactDto;
import com.pingchat.authenticationservice.util.pagination.PagedSearchResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ContactDataService {
    private final ContactRepository contactRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    public ContactDataService(ContactRepository contactRepository,
                              UserRepository userRepository,
                              ObjectMapper objectMapper) {
        this.contactRepository = contactRepository;
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
    }

    public List<ContactDto> findAll(UserEntity userEntity) {
        return contactRepository.findByUser(userEntity).stream()
                .map(contactEntity -> objectMapper.convertValue(contactEntity, ContactDto.class))
                .collect(Collectors.toList());
    }

    public PagedSearchResult<ContactDto> findAllByFilter(Integer pageSize, Integer pageNumber, Long userId) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        Page<ContactEntity> pageOfContactEntities = contactRepository.findAllByUserId(userId, pageable);

        List<ContactDto> contactDtos = objectMapper.convertValue(pageOfContactEntities.getContent(), List.class);
        return new PagedSearchResult<>(contactDtos, pageOfContactEntities.getTotalElements());
    }

    public ContactDto addContact(String userPhoneNumber, ContactDto contactDto) {
        ContactEntity contactEntity = objectMapper.convertValue(contactDto, ContactEntity.class);
        contactEntity.setContactUser(userRepository.findByDialCodeAndPhoneNumber(contactDto.getContactPhoneNumber()));
        contactEntity.setUser(userRepository.findByDialCodeAndPhoneNumber(userPhoneNumber));

        contactEntity = contactRepository.save(contactEntity);

        return objectMapper.convertValue(contactEntity, ContactDto.class);
    }


    // TODO: clean up this stuff
//    public List<ContactDto> addContacts(UserEntity userEntity,
//                                        List<ContactDto> contactDtos) {
//        List<ContactEntity> contactEntitiesToAdd = contactDtos.stream()
//                .map(contactDto -> {
//                    ContactEntity contactEntity = objectMapper.convertValue(contactDto, ContactEntity.class);
//                    contactEntity.setContactUser(userDataService.findByDialCodeAndPhoneNumber(
//                            contactDto.getDialCodeAndPhoneNumber()));
//                    contactEntity.setUser(userEntity);
//
//                    return contactEntity;
//                })
//                .filter(contactEntity -> contactEntity.getContactUser() != null &&
//                        !contactRepository.existsByUserAndContactUser(userEntity, contactEntity.getContactUser()))
//                .collect(Collectors.toList());
//
//        return contactRepository.saveAll(contactEntitiesToAdd).stream()
//                .map(contactEntity -> {
//                    ContactDto contactDto = objectMapper.convertValue(contactEntity, ContactDto.class);
//                    contactDto.setDialCodeAndPhoneNumber(contactEntity.getContactUser().getCountryCode().getDialCode() +
//                            contactEntity.getContactUser().getPhoneNumber());
//                    return contactDto;
//                })
//                .collect(Collectors.toList());
//    }
}

