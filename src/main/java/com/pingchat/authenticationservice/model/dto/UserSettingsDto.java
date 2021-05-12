package com.pingchat.authenticationservice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSettingsDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private long id;

    private String chatBubbleColorHex;

    private boolean darkMode = false;

    private boolean vibrate = true;

    private boolean receiveNotifications = true;
}
