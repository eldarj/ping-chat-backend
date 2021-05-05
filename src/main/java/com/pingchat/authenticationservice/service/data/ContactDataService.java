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
                contactRepository.findAllByUserIdAndIsFavoriteAndIsDeletedIsFalseOrderByContactNameAsc(userId, favourites, pageable)
                : contactRepository.findAllByUserIdAndIsDeletedIsFalseOrderByContactNameAsc(userId, pageable);

        List<ContactDto> contactDtos = objectMapper.convertValue(pageOfContactEntities.getContent(), List.class);
        return new PagedSearchResult<>(contactDtos, pageOfContactEntities.getTotalElements());
    }


    public List<ContactDto> findAllByNameOrPhonenumber(Long userId, String searchQuery) {
        return contactRepository.findAllByNameOrPhonenumber(userId, searchQuery).stream()
                .map(contactEntity -> objectMapper.convertValue(contactEntity, ContactDto.class))
                .collect(Collectors.toList());
    }

    public ContactDto findByUserAndPeer(Long userId, Long peerId) {
        return objectMapper.convertValue(contactRepository.findByUserIdAndContactUserId(userId, peerId), ContactDto.class);
    }

    public ContactDto findByUserAndPeer(String userPhoneNumber, String contactPhoneNumber) {
        return objectMapper.convertValue(
                contactRepository.findByUserPhoneNumberAndContactPhoneNumber(userPhoneNumber, contactPhoneNumber),
                ContactDto.class);
    }

    @Transactional
    public ContactDto addContact(String currentPhoneNumber, ContactDto contactDto) {
        ContactEntity contactEntity = objectMapper.convertValue(contactDto, ContactEntity.class);

        contactEntity.setContactBindingId(UniqueUtil.nextUniqueLong());

        UserEntity currentUser = userRepository.findByDialCodeAndPhoneNumber(currentPhoneNumber);
        contactEntity.setUser(currentUser);

        UserEntity contactUser = userRepository.findByDialCodeAndPhoneNumber(contactDto.getContactPhoneNumber());
        contactEntity.setContactUser(contactUser);

        if (contactUser != null) {
            contactEntity.setContactUserExists(true);
            ContactEntity existingContactEntity = contactRepository.findByUserIdAndContactUserId(
                    currentUser.getId(), contactUser.getId());

            if (existingContactEntity != null) {
                contactEntity = existingContactEntity;
                contactEntity.setContactName(contactDto.getContactName());
                contactEntity.setDeleted(false);
            } else {
                ContactEntity inverseContactEntity = contactRepository.findByUserIdAndContactUserId(contactUser.getId(), currentUser.getId());
                if (inverseContactEntity != null) {
                    contactEntity.setContactBindingId(inverseContactEntity.getContactBindingId());
                }
            }
        }

        contactEntity = contactRepository.save(contactEntity);

        return objectMapper.convertValue(contactEntity, ContactDto.class);
    }

    public ContactDto addContact(String currentPhoneNumber, String contactPhoneNumber) {
        ContactEntity contactEntity = new ContactEntity();

        UserEntity currentUser = userRepository.findByDialCodeAndPhoneNumber(currentPhoneNumber);
        contactEntity.setUser(currentUser);

        UserEntity contactUser = userRepository.findByDialCodeAndPhoneNumber(contactPhoneNumber);

        if (contactUser == null) {
            throw new RuntimeException("User added via QR code doesn't exist");
        }

        ContactEntity existingContactEntity = contactRepository.findByUserIdAndContactUserId(
                currentUser.getId(), contactUser.getId());

        if (existingContactEntity != null) {
            contactEntity = existingContactEntity;
            contactEntity.setDeleted(false);

        } else {
            contactEntity.setContactUser(contactUser);
            contactEntity.setContactName(contactUser.getFirstName());
            contactEntity.setContactPhoneNumber(contactUser.getFullPhoneNumber());
            contactEntity.setContactUserExists(true);

            long contactBindingId = UniqueUtil.nextUniqueLong();

            ContactEntity inverseContactEntity = contactRepository.findByUserIdAndContactUserId(
                    contactUser.getId(), currentUser.getId());

            if (inverseContactEntity != null) {
                contactBindingId = inverseContactEntity.getContactBindingId();
            }

            contactEntity.setContactBindingId(contactBindingId);
        }

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
    public void updateFavouriteStatus(Long contactId, Boolean isFavourite) {
        log.info("Updating {} favourites status to {}", contactId, isFavourite);
        contactRepository.updateFavouriteStatus(contactId, isFavourite);
    }

    @Transactional
    public void updateContactName(Long contactId, String contactName) {
        log.info("Updating {} contact name to {}", contactId, contactName);
        contactRepository.updateContactName(contactId, contactName);
    }

    @Transactional
    public void updateBackground(Long contactId, String background) {
        contactRepository.updateBackground(contactId, background);
    }

    @Transactional
    public void delete(Long contactId) {
        contactRepository.updateDeletedStatus(contactId, true);
    }
}

