package com.imclaus.cloud.dto;

import com.imclaus.cloud.enums.Status;
import lombok.Data;

import java.time.Instant;

@Data
public class UserDTO {
    private String email;
    private String name;
    private Boolean tfa;
    private Instant created;
    private Instant updated;
    private Status status;
}
