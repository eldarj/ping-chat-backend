package com.pingchat.authenticationservice.service.data;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pingchat.authenticationservice.data.mysql.entity.UserEntity;
import com.pingchat.authenticationservice.data.mysql.repository.UserRepository;
import com.pingchat.authenticationservice.model.dto.UserDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.List;

@Slf4j
@Service
public class UserDataService {
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    public UserDataService(UserRepository userRepository, ObjectMapper objectMapper) {
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
    }

    public List<UserDto> findAll() throws IOException {
        List<UserEntity> all = userRepository.findAll();
        return objectMapper.readValue(objectMapper.writeValueAsBytes(all),
                new TypeReference<>() {});
    }

    public UserDto findById(long id) {
        return objectMapper.convertValue(userRepository.findById(id), UserDto.class);
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
}
