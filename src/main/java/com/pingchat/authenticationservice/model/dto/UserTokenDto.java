package com.pingchat.authenticationservice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserTokenDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private long id;

    private UserDto user;
    private String token;

    private Instant generatedTimestamp;
}
