package com.pingchat.authenticationservice.data.mysql.entity;

import com.pingchat.authenticationservice.enums.MessageType;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.Set;

@Data
@NoArgsConstructor
@Entity
@Table(name = "replies")
public class ReplyEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(columnDefinition = "text")
    private String text;

    private String fileName;

    private String filePath;

    private String fileUrl;

    private Long fileSizeBytes;

    private MessageType messageType = MessageType.TEXT_MESSAGE;

    private String recordingDuration;

    private Long nodeId;

    private Long messageId;

    private Long sentTimestamp = Instant.now().toEpochMilli();
}
