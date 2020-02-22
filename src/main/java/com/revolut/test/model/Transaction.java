package com.revolut.test.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
public class Transaction {
    @Id
    @GeneratedValue(generator = "uuid")
    private Long id;

    private String transactionId;

    private String description;

    @Column(name = "sender_iban")
    private String senderIban;

    @Column(name = "receiver_iban")
    private String receiverIban;

    private String bookingType;

    private BigDecimal amount;

    private LocalDate createdAt;

    private String  status;
}
