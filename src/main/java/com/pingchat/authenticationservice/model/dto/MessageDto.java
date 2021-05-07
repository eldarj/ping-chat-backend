package com.pingchat.authenticationservice.model.dto;

import com.pingchat.authenticationservice.enums.MessageType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.Instant;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private long id;
    private String text;

    private UserDto sender;
    private UserDto receiver;

    private long contactBindingId;

    private boolean sent;
    private boolean received;
    private boolean seen;

    private Long sentTimestamp = Instant.now().toEpochMilli();

    private String senderContactName;

    private String receiverContactName;

    private boolean senderOnline;

    private boolean receiverOnline;

    private Long senderLastOnlineTimestamp;

    private Long receiverLastOnlineTimestamp;

    private boolean isChained;

    private String fileName;

    private String filePath;

    private String fileUrl;

    private Long fileSizeBytes;

    private boolean isDeleted; //TODO: Deprecate - remove (use deletedFor)

    private MessageType messageType = MessageType.TEXT_MESSAGE;

    private int totalUnreadMessages;

    private String recordingDuration;

    private Long nodeId;

    private String deletedForUserIds = "";

    private boolean isPinned;

    private Long pinnedTimestamp;

    private boolean isEdited;
}
