package com.pingchat.authenticationservice.service.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pingchat.authenticationservice.data.mysql.entity.DSNodeEntity;
import com.pingchat.authenticationservice.data.mysql.entity.UserEntity;
import com.pingchat.authenticationservice.data.mysql.repository.DataSpaceNodeRepository;
import com.pingchat.authenticationservice.data.mysql.repository.UserRepository;
import com.pingchat.authenticationservice.enums.DSNodeType;
import com.pingchat.authenticationservice.model.dto.DSNodeDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class DataSpaceDataService {
    @Value("${service.static-base-path}")
    protected String staticBasePath;

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

    // Create nodes
    public DSNodeDto create(DSNodeDto dsNodeDto) {
        dsNodeDto.setCreatedTimestamp(Instant.now().toEpochMilli());
        dsNodeDto.setLastModifiedTimestamp(Instant.now().toEpochMilli());

        DSNodeEntity dsNodeEntity = objectMapper.convertValue(dsNodeDto, DSNodeEntity.class);
        dsNodeEntity = dsNodeRepository.save(dsNodeEntity);

        return objectMapper.convertValue(dsNodeEntity, DSNodeDto.class);
    }

    public void createRootNodes(UserEntity userEntity) {
        DSNodeDto sentDirectory = create(DSNodeDto.builder()
                .nodeName("Sent")
                .nodeType(DSNodeType.DIRECTORY)
                .ownerId(userEntity.getId())
                .build());
        DSNodeDto receivedDirectory = create(DSNodeDto.builder()
                .nodeName("Received")
                .nodeType(DSNodeType.DIRECTORY)
                .ownerId(userEntity.getId())
                .build());
        userEntity.setSentNodeId(sentDirectory.getId());
        userEntity.setReceivedNodeId(receivedDirectory.getId());
        userRepository.save(userEntity);
    }

    // Get nodes
    public List<DSNodeDto> getDataSpace(Long userId) {
        return objectMapper.convertValue(dsNodeRepository.findAllByOwnerIdAndParentDirectoryNodeIdIsNull(userId),
                List.class);
    }

    public List<DSNodeDto> getDirectory(Long userId, Long directoryId) {
        return objectMapper.convertValue(dsNodeRepository.findAllByDirectory(directoryId, userId), List.class);
    }

    public List<DSNodeDto> getAllSent(Long userId, Long directoryId) {
        return objectMapper.convertValue(dsNodeRepository.findAllByParentDirectoryNodeId(directoryId, userId), List.class);
    }

    public List<DSNodeDto> getAllReceived(Long userId) {
        return objectMapper.convertValue(dsNodeRepository.findAllByReceiverId(userId), List.class);
    }

    public List<DSNodeDto> getSharedData(Long userId, Long anotherUserId) {
        return objectMapper.convertValue(dsNodeRepository.findSharedDataByUsers(userId, anotherUserId), List.class);
    }

    public List<DSNodeDto> getSharedData(Long userId, Long anotherUserId, Long nodesCount) {
        return objectMapper.convertValue(dsNodeRepository.findSharedDataByUsersLimit(userId, anotherUserId, nodesCount),
                List.class);
    }

    // Delete nodes
    @Transactional
    public void deleteByUploadId(String uploadId) {
        dsNodeRepository.deleteByUploadId(uploadId);
    }

    @Transactional
    public void deleteForUser(Long nodeId, Long userId) {
        dsNodeRepository.deleteForUser(nodeId, userId);
    }

    @Transactional
    public void deleteByNodeId(Long nodeId) {
        Optional<DSNodeEntity> optionalDSNodeEntity = dsNodeRepository.findById(nodeId);
        if (optionalDSNodeEntity.isPresent()) {
            DSNodeEntity dsNodeEntity = optionalDSNodeEntity.get();
            try {
                Files.deleteIfExists(Paths.get(staticBasePath + "/uploads").resolve(dsNodeEntity.getNodeName()));
            } catch (IOException e) {
                log.error("Error deleting file from disk", e);
            }
        }

        dsNodeRepository.deleteById(nodeId);
    }

    @Transactional
    public void deleteDirectoryByNodeId(Long nodeId) {
        Optional<DSNodeEntity> dsNodeEntityOptional = dsNodeRepository.findById(nodeId);

        if (dsNodeEntityOptional.isPresent()) {
            DSNodeEntity dsNodeEntity = dsNodeEntityOptional.get();
            recursiveDeleteNode(dsNodeEntity);
        }
    }

    private void recursiveDeleteNode(DSNodeEntity dsNodeEntity) {
        dsNodeRepository.deleteById(dsNodeEntity.getId());

        if (dsNodeEntity.getNodeType() == DSNodeType.DIRECTORY) {
            List<DSNodeEntity> childNodes = dsNodeRepository.findAllByParentDirectoryNodeId(dsNodeEntity.getId());
            for (DSNodeEntity child : childNodes) {
                recursiveDeleteNode(child);
            }
        }
    }
}
