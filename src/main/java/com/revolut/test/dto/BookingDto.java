package com.revolut.test.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class BookingDto {

    private Long id;

    private String description;

    private String sender_iban;

    private String receiver_iban;

    private String bookingType;

    private BigDecimal amount;

    private LocalDate createdAt;

    private String status;

    private String failureReason;

    private String transactionId;

}
