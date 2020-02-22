package com.revolut.test.dto;

import lombok.Data;

@Data
public class ErrorResponse {
    private String code;
    private String message;
}
