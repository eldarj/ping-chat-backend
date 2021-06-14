package com.pingchat.authenticationservice.data.mysql.entity;

import com.pingchat.authenticationservice.enums.DSNodeType;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.Instant;

@Data
@NoArgsConstructor
@Entity
@Table(name = "dataspace_nodes")
public class DSNodeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private long ownerId;
    private long receiverId;

    private Long parentDirectoryNodeId;
    private String nodeName;
    private String nodePath;

    private String description;

    private String fileUrl;
    private String pathOnSourceDevice;
    private Long fileSizeBytes;
    private String uploadId;
    private String recordingDuration;

    private DSNodeType nodeType = DSNodeType.IMAGE;

    @NotNull
    private Long createdTimestamp = Instant.now().toEpochMilli();

    private Long lastModifiedTimestamp = Instant.now().toEpochMilli();

    private String deletedForUserIds = "";
}
