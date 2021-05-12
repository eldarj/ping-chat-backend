package com.pingchat.authenticationservice.service.data;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pingchat.authenticationservice.data.mysql.entity.UserEntity;
import com.pingchat.authenticationservice.data.mysql.entity.UserSettingsEntity;
import com.pingchat.authenticationservice.data.mysql.repository.UserRepository;
import com.pingchat.authenticationservice.data.mysql.repository.UserSettingsRepository;
import com.pingchat.authenticationservice.model.dto.UserDto;
import com.pingchat.authenticationservice.model.dto.UserSettingsDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserDataService {
    private final UserRepository userRepository;
    private final UserSettingsRepository userSettingsRepository;
    private final ObjectMapper objectMapper;

    public UserDataService(UserRepository userRepository,
                           UserSettingsRepository userSettingsRepository,
                           ObjectMapper objectMapper) {
        this.userRepository = userRepository;
        this.userSettingsRepository = userSettingsRepository;
        this.objectMapper = objectMapper;
    }

    public List<String> findAllFsUsers() throws IOException {
        List<UserEntity> all = userRepository.findAll();
        return all.stream().map(user -> user.getCountryCode().getDialCode() + user.getPhoneNumber())
                .collect(Collectors.toList());
    }

    public List<UserDto> findAll() throws IOException {
        List<UserEntity> all = userRepository.findAll();
        return objectMapper.readValue(objectMapper.writeValueAsBytes(all),
                new TypeReference<>() {
                });
    }

    public UserDto findById(long id) {
        UserEntity user = userRepository.findById(id);
        return objectMapper.convertValue(user, UserDto.class);
    }

    public UserDto updateUser(UserDto userDto) {
        UserEntity userEntity = objectMapper.convertValue(userDto, UserEntity.class);
        return objectMapper.convertValue(userRepository.save(userEntity), UserDto.class);
    }

    @Transactional
    public int updateFirstNameAndLastName(long userId, String firstName, String lastName) {
        return userRepository.updateFirstNameAndLastName(userId, firstName, lastName);
    }

    @Transactional
    public int updateProfileImage(long userId, String profileImageName) {
        return userRepository.updateProfileImage(userId, profileImageName);
    }

    @Transactional
    public UserSettingsDto updateUserSettings(UserSettingsDto userSettingsDto) {
        UserSettingsEntity userSettings = objectMapper.convertValue(userSettingsDto, UserSettingsEntity.class);
        return objectMapper.convertValue(userSettingsRepository.save(userSettings), UserSettingsDto.class);
    }
}
