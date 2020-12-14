package com.pingchat.authenticationservice.service.data;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.pingchat.authenticationservice.data.mysql.entity.ContactEntity;
import com.pingchat.authenticationservice.data.mysql.entity.UserEntity;
import com.pingchat.authenticationservice.data.mysql.repository.ContactRepository;
import com.pingchat.authenticationservice.data.mysql.repository.UserRepository;
import com.pingchat.authenticationservice.model.dto.ContactDto;
import com.pingchat.authenticationservice.util.UniqueUtil;
import com.pingchat.authenticationservice.util.pagination.PagedSearchResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
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

    public PagedSearchResult<ContactDto> findAllByFilter(Integer pageSize, Integer pageNumber, Long userId,
                                                         boolean favourites) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        Page<ContactEntity> pageOfContactEntities = favourites ?
                contactRepository.findAllByUserIdAndIsFavoriteOrderByContactNameAsc(userId, favourites, pageable) :
                contactRepository.findAllByUserIdOrderByContactNameAsc(userId, pageable);

        List<ContactDto> contactDtos = objectMapper.convertValue(pageOfContactEntities.getContent(), List.class);
        return new PagedSearchResult<>(contactDtos, pageOfContactEntities.getTotalElements());
    }

    public ContactDto addContact(String userPhoneNumber, ContactDto contactDto) {
        ContactEntity contactEntity = objectMapper.convertValue(contactDto, ContactEntity.class);
        contactEntity.setUser(userRepository.findByDialCodeAndPhoneNumber(userPhoneNumber));

        UserEntity contactUser = userRepository.findByDialCodeAndPhoneNumber(contactDto.getContactPhoneNumber());
        contactEntity.setContactUser(contactUser);
        contactEntity.setContactUserExists(contactUser != null);

        // TODO: Fix vice versa adding contacts
        contactEntity.setContactBindingId(UniqueUtil.nextUniqueLong());

        contactEntity = contactRepository.save(contactEntity);

        return objectMapper.convertValue(contactEntity, ContactDto.class);
    }

    @Transactional
    public void setFavouriteStatus(Long contactId, Boolean isFavourite) {
        log.info("Updating {} favourites status to {}", contactId, isFavourite);
        contactRepository.updateFavouriteStatus(contactId, isFavourite);
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

