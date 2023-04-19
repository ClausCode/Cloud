package com.imclaus.cloud.dto.response;

import com.imclaus.cloud.dto.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAuthResponseDTO {
    private String token;
    private UserDTO user;
}


