package com.pingchat.authenticationservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    private String phoneNumber;
    private CountryCodeDto countryCode;

    private String firstName;
    private String lastName;

    private boolean displayMyFullName = true;

    private Long joinedTimestamp = Instant.now().toEpochMilli();

    private String profileImagePath;
}
