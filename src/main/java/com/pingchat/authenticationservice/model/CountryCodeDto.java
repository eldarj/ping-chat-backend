package com.pingchat.authenticationservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CountryCodeDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private long id;

    private String countryName;
    private String dialCode;

    private boolean isDeleted;
}
