package com.imclaus.cloud.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Data
@Table(name = "auth")
public class AuthModel {
    @Id
    @Column("id")
    private Long id;

    @Column("user_id")
    private Long userId;

    @Column("browser")
    private String browser;

    @Column("created")
    private Instant created;

    @Column("updated")
    private Instant updated;
}
