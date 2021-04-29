package com.pingchat.authenticationservice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class FsUser {
    private String id;

    private Map<String, String> params;

    private Map<String, String> variables;
}
