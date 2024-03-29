package com.pingchat.authenticationservice.data.mysql.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Instant;

@Data
@NoArgsConstructor
@ToString(exclude = {"user", "contactUser"})
@EqualsAndHashCode(exclude = {"user", "contactUser"})
@Entity
@Table(name = "contacts", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "contact_phone_number"}))
public class ContactEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @NotNull
    private long contactBindingId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "contact_id")
    private UserEntity contactUser;

    @NotBlank
    private String contactName;

    @Column(name = "contact_phone_number")
    private String contactPhoneNumber;

    private boolean isFavorite;

    private boolean contactUserExists; //TODO: Handle this

    @NotNull
    private Long addedTimestamp = Instant.now().toEpochMilli();

    private String backgroundImagePath;

    private boolean isDeleted = false;
}
