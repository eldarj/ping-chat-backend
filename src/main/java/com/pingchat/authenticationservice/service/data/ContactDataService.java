package com.pingchat.authenticationservice.service.data;

import com.fasterxml.jackson.core.type.TypeReference;
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
import java.util.ArrayList;
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

    public PagedSearchResult<ContactDto> findAllByFilter(Integer pageSize, Integer pageNumber, Long userId,
                                                         boolean favourites) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        Page<ContactEntity> pageOfContactEntities = favourites ?
                contactRepository.findAllByUserIdAndIsFavoriteOrderByContactNameAsc(userId, favourites, pageable) :
                contactRepository.findAllByUserIdOrderByContactNameAsc(userId, pageable);

        List<ContactDto> contactDtos = objectMapper.convertValue(pageOfContactEntities.getContent(), List.class);
        return new PagedSearchResult<>(contactDtos, pageOfContactEntities.getTotalElements());
    }


    public List<ContactDto> findAllByNameOrPhonenumber(Long userId, String searchQuery) {
        return contactRepository.findAllByNameOrPhonenumber(userId, searchQuery).stream()
                .map(contactEntity -> objectMapper.convertValue(contactEntity, ContactDto.class))
                .collect(Collectors.toList());
    }

    public ContactDto addContact(String currentPhoneNumber, ContactDto contactDto) {
        ContactEntity contactEntity = objectMapper.convertValue(contactDto, ContactEntity.class);

        UserEntity currentUser = userRepository.findByDialCodeAndPhoneNumber(currentPhoneNumber);
        contactEntity.setUser(currentUser);

        UserEntity contactUser = userRepository.findByDialCodeAndPhoneNumber(contactDto.getContactPhoneNumber());
        boolean contactUserExists = contactUser != null;

        contactEntity.setContactUser(contactUser);
        contactEntity.setContactUserExists(contactUserExists);

        long contactBindingId = UniqueUtil.nextUniqueLong();
        if (contactUserExists) {
            ContactEntity inverseContactEntity = contactRepository.findByUserIdAndContactUserId(
                    contactUser.getId(), currentUser.getId());

            if (inverseContactEntity != null) {
                contactBindingId = inverseContactEntity.getContactBindingId();
            } else {
                // TODO: Clean this
                inverseContactEntity = new ContactEntity();
                inverseContactEntity.setContactUser(currentUser);
                inverseContactEntity.setContactPhoneNumber(currentUser.getFullPhoneNumber());
                inverseContactEntity.setContactName(currentUser.getFirstName());
                inverseContactEntity.setContactBindingId(contactBindingId);
                inverseContactEntity.setContactUserExists(true);
                inverseContactEntity.setUser(contactUser);
            }
        }

        contactEntity.setContactBindingId(contactBindingId);

        contactEntity = contactRepository.save(contactEntity);
        return objectMapper.convertValue(contactEntity, ContactDto.class);
    }

    public List<ContactDto> addContacts(String currentPhoneNumber, List<ContactDto> contacts) {
        List<ContactEntity> contactEntities = objectMapper.convertValue(contacts, new TypeReference<>() {});

        UserEntity currentUser = userRepository.findByDialCodeAndPhoneNumber(currentPhoneNumber);

        List<ContactEntity> savedContacts = new ArrayList<>();

        contactEntities.forEach(contactEntity -> {
            String contactPhoneNumber = contactEntity.getContactPhoneNumber();
            UserEntity contactUser = userRepository.findByDialCodeAndPhoneNumber(contactPhoneNumber);

            if (contactUser != null) {
                ContactEntity existingContactEntity =
                        contactRepository.findByUserIdAndContactUserId(currentUser.getId(), contactUser.getId());
                if (existingContactEntity == null) {
                    contactEntity.setUser(currentUser);
                    contactEntity.setContactUser(contactUser);
                    contactEntity.setContactUserExists(true);

                    ContactEntity inverseContactEntity = contactRepository.findByUserIdAndContactUserId(
                            contactUser.getId(), currentUser.getId());

                    long contactBindingId;
                    if (inverseContactEntity != null) {
                        contactBindingId = inverseContactEntity.getContactBindingId();
                    } else {
                        contactBindingId = UniqueUtil.nextUniqueLong();
                    }

                    contactEntity.setContactBindingId(contactBindingId);
                    ContactEntity savedContactEntity = contactRepository.save(contactEntity);
                    savedContacts.add(savedContactEntity);
                }
            }
        });

        log.info("Contact book synced (total={})", savedContacts.size());

        return objectMapper.convertValue(savedContacts, new TypeReference<>() {});
    }

    @Transactional
    public void setFavouriteStatus(Long contactId, Boolean isFavourite) {
        log.info("Updating {} favourites status to {}", contactId, isFavourite);
        contactRepository.updateFavouriteStatus(contactId, isFavourite);
    }
}

