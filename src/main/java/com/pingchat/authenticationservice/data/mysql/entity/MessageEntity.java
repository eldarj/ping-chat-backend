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
@Table(name = "messages")
@ToString(exclude = {"sender", "receiver"})
@EqualsAndHashCode(exclude = {"sender", "receiver"})
public class MessageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(columnDefinition = "text")
    private String text;

    @ManyToOne
    @JoinColumn(name = "sender_user_id")
    private UserEntity sender;

    @ManyToOne
    @JoinColumn(name = "receiver_user_id")
    private UserEntity receiver;

    @NotNull
    private long contactBindingId;

    private boolean sent;
    private boolean received;
    private boolean seen;

    @NotNull
    private Long sentTimestamp = Instant.now().toEpochMilli();

    private String senderContactName;
    private String receiverContactName;

    private MessageType messageType = MessageType.TEXT_MESSAGE;

    private String fileName;
    private String filePath;
    private String fileUrl;
    private Long fileSizeBytes;
    private String recordingDuration;
    private Long nodeId;

    private String deletedForUserIds = "";

    private boolean isPinned;
    private Long pinnedTimestamp;
    private boolean isEdited;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "reply_message_id")
    private ReplyEntity replyMessage;

    // Call info - neutral message
    private String callDuration;
    private String callType;
}
