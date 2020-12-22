package com.pingchat.authenticationservice.data.mysql.entity;

import com.pingchat.authenticationservice.enums.MessageType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

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

    private long parentDirectoryNodeId;

    private String nodeName;

    private String nodePath;

    private String fileUrl;

    private String pathOnSourceDevice;

    private Long fileSizeBytes;

    private boolean isDirectory;

    @NotNull
    private Long createdTimestamp = Instant.now().toEpochMilli();

    private Long lastModifiedTimestamp = Instant.now().toEpochMilli();
}
