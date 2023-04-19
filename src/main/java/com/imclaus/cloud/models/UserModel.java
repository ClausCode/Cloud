package com.imclaus.cloud.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.imclaus.cloud.enums.Status;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Table("users")
public class UserModel implements UserDetails {
    @Id
    @Column("id")
    private Long id;

    @Column("email")
    private String email;

    @Column("name")
    private String name;

    @JsonIgnore
    @Column("password")
    private String password;

    @Column("secret")
    private String secret;

    @Column("tfa")
    private Boolean tfa;

    @CreatedDate
    @Column("created")
    private Instant created;

    @LastModifiedDate
    @Column("updated")
    private Instant updated;

    @Column("status")
    private Status status;

    @Transient
    private List<RoleModel> roles;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public String getUsername() {
        return getName();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return getStatus() != Status.BANNED;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return getStatus() != Status.DELETED;
    }
}
