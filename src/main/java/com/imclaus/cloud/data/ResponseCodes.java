package com.imclaus.cloud.data;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseCodes {
    private ResponseCodes() {}

    public static final ResponseEntity<?> BAD_REQUEST =
            ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

    public static final ResponseEntity<?> UNAUTHORIZED =
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

    public static final ResponseEntity<?> LOCKED =
            ResponseEntity.status(HttpStatus.LOCKED).build();
}
