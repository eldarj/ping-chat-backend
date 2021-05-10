package com.pingchat.authenticationservice.model.dto;

import com.pingchat.authenticationservice.enums.MessageType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReplyDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private long id;

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
