package com.pingchat.authenticationservice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private long id;
    private String text;

    private UserDto sender;
    private UserDto receiver;

    private boolean received;
    private boolean seen;

    private Long sentTimestamp = Instant.now().toEpochMilli();

    private String senderContactName;

    private String receiverContactName;

    private boolean senderOnline;

    private boolean receiverOnline;

    private Long senderLastOnlineTimestamp;

    private Long receiverLastOnlineTimestamp;
}
