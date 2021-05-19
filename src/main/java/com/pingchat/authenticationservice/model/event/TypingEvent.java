package com.pingchat.authenticationservice.model.event;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;

@Data
@NoArgsConstructor
public class TypingEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    private String receiverPhoneNumber;

    private String senderPhoneNumber;

    private boolean status;
}
