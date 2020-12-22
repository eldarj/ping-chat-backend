package com.pingchat.authenticationservice.service.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pingchat.authenticationservice.data.mysql.entity.DSNodeEntity;
import com.pingchat.authenticationservice.data.mysql.entity.UserEntity;
import com.pingchat.authenticationservice.data.mysql.repository.DataSpaceNodeRepository;
import com.pingchat.authenticationservice.data.mysql.repository.UserRepository;
import com.pingchat.authenticationservice.model.dto.DSNodeDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
public class DataSpaceDataService {
    private final DataSpaceNodeRepository dsNodeRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    public DataSpaceDataService(DataSpaceNodeRepository dsNodeRepository,
                                UserRepository userRepository,
                                ObjectMapper objectMapper) {
        this.dsNodeRepository = dsNodeRepository;
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
    }

    public long create(DSNodeDto dsNodeDto) {
        dsNodeDto.setCreatedTimestamp(Instant.now().toEpochMilli());
        dsNodeDto.setLastModifiedTimestamp(Instant.now().toEpochMilli());
        return dsNodeRepository.save(objectMapper.convertValue(dsNodeDto, DSNodeEntity.class)).getId();
    }

    public void createRootNodes(UserEntity userEntity) {
        long sentId = create(DSNodeDto.builder()
                .nodeName("Sent")
                .isDirectory(true)
                .ownerId(userEntity.getId())
                .build());
        long receivedId = create(DSNodeDto.builder()
                .nodeName("Received")
                .isDirectory(true)
                .ownerId(userEntity.getId())
                .build());
        userEntity.setSentNodeId(sentId);
        userEntity.setReceivedNodeId(receivedId);
        userRepository.save(userEntity);
    }

    public List<DSNodeDto> getSharedData(Long userId, Long anotherUserId) {
        return objectMapper.convertValue(dsNodeRepository.findSharedDataByUsers(userId, anotherUserId), List.class);
    }
}
