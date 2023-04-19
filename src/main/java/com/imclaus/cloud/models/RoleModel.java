package com.imclaus.cloud.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("roles")
public class RoleModel {
    @Id
    @Column("id")
    private Long id;

    @Column("name")
    private String name;
}
