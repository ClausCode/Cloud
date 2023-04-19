package com.imclaus.cloud.dto.request;

import lombok.Data;

@Data
public class UserSignUpRequestDTO {
    private String email;
    private String name;
    private String password;
}
