package com.pingchat.authenticationservice.model.dto;

import com.pingchat.authenticationservice.enums.DSNodeType;
import com.pingchat.authenticationservice.enums.MessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DSNodeDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private long id;

    private long ownerId;

    private long receiverId;

    private Long parentDirectoryNodeId;

    private String nodeName;

    private String nodePath;

    private String fileUrl;

    private String pathOnSourceDevice;

    private Long fileSizeBytes;

    private String uploadId;

    private DSNodeType nodeType = DSNodeType.IMAGE;

    private String recordingDuration;

    private Long createdTimestamp = Instant.now().toEpochMilli();

    private Long lastModifiedTimestamp = Instant.now().toEpochMilli();
}
