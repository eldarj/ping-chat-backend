package com.pingchat.authenticationservice.data.mysql.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.Set;

@Data
@NoArgsConstructor
@ToString(exclude = {"users"})
@EqualsAndHashCode(exclude = {"users"})
@Entity
@Table(name = "country_codes")
public class CountryCodeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(unique = true)
    private String countryName;

    @Column(unique = true)
    private String dialCode;

    @JsonIgnore
    @OneToMany(mappedBy = "countryCode")
    private Set<UserEntity> users;
}
