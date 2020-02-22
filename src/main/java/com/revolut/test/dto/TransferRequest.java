package com.revolut.test.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferRequest {
    private String description;
    private String senderIban;
    private String receiverIban;
    private String receiverBic;
    private BigDecimal amount;

}
