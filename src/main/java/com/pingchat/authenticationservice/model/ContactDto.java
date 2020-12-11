package com.pingchat.authenticationservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private long id;

    private UserDto contactUser;
    private String contactName;
    private String contactPhoneNumber;
    private boolean isFavorite;

    private boolean exists = contactUser != null;

    private Instant addedTimestamp = Instant.now();
}
