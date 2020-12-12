package com.pingchat.authenticationservice.auth.ws;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.security.Principal;

@Data
@AllArgsConstructor
public class StompPrincipal implements Principal {
    private String name;
}
