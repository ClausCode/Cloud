package com.imclaus.cloud.dto.request;

import lombok.Data;

@Data
public class UserSignInRequestDTO {
    private String email;
    private String password;
    private String code;
}
