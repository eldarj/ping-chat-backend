package com.pingchat.authenticationservice.model.dto;

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

    private long parentDirectoryNodeId;

    private String nodeName;

    private String nodePath;

    private String fileUrl;

    private String pathOnSourceDevice;

    private Long fileSizeBytes;

    private String uploadId;

    private boolean isDirectory;

    private Long createdTimestamp = Instant.now().toEpochMilli();

    private Long lastModifiedTimestamp = Instant.now().toEpochMilli();
}
