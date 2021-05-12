package com.pingchat.authenticationservice.data.mysql.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Data
@NoArgsConstructor
@ToString(exclude = {"user"})
@EqualsAndHashCode(exclude = {"user"})
@Entity
@Table(name = "user_settings")
public class UserSettingsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String chatBubbleColorHex;

    private boolean darkMode = false;

    private boolean vibrate = true;

    private boolean receiveNotifications = true;

    @JsonIgnore
    @OneToOne(mappedBy = "userSettings")
    private UserEntity user;
}
