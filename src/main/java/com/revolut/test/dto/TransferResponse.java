package com.revolut.test.dto;

import lombok.Data;

@Data
public class TransferResponse {
    private String transactionId;

    private String status;
}
