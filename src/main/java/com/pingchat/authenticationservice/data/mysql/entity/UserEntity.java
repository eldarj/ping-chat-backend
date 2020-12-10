package com.pingchat.authenticationservice.data.mysql.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"countryCode", "tokens", "twoWayPins"})
@EqualsAndHashCode(exclude = {"countryCode", "tokens", "twoWayPins"})
@Entity
@Table(name = "users", uniqueConstraints = @UniqueConstraint(columnNames = {"country_code_id", "phone_number"}))
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    @Column(name = "phone_number")
    private String phoneNumber;

    private String firstName;
    private String lastName;

    private boolean displayMyFullName = true;

    @ManyToOne(optional = false)
    @JoinColumn(name = "country_code_id")
    private CountryCodeEntity countryCode;

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private Set<TwoWayPinEntity> twoWayPins;

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private Set<UserTokenEntity> tokens;

    @NotNull
    private Long joinedTimestamp = Instant.now().toEpochMilli();

    private String profileImagePath;
}
