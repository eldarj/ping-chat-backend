package com.pingchat.authenticationservice.data.mysql.entity;

import com.pingchat.authenticationservice.enums.MessageType;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Instant;

@Data
@NoArgsConstructor
@ToString(exclude = {"sender", "receiver"})
@EqualsAndHashCode(exclude = {"sender", "receiver"})
@Entity
@Table(name = "messages")
public class MessageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @NotBlank
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

    // denormalized for performance
    private String senderContactName;

    private String receiverContactName;

    private MessageType messageType = MessageType.TEXT_MESSAGE;
}
