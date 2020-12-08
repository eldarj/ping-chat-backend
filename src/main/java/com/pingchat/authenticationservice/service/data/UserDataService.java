package com.pingchat.authenticationservice.service.data;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pingchat.authenticationservice.data.mysql.entity.UserEntity;
import com.pingchat.authenticationservice.data.mysql.repository.UserRepository;
import com.pingchat.authenticationservice.model.UserDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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

    public UserDto updateUser(UserDto userDto) {
        UserEntity userEntity = objectMapper.convertValue(userDto, UserEntity.class);
        return objectMapper.convertValue(userRepository.save(userEntity), UserDto.class);
    }
}
