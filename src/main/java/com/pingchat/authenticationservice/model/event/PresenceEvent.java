package com.pingchat.authenticationservice.model.event;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;

@Data
@NoArgsConstructor
public class PresenceEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    private String userPhoneNumber;

    private boolean status;

    private Long eventTimestamp = Instant.now().toEpochMilli();

    public PresenceEvent(boolean status) {
        this.status = status;
    }
}
