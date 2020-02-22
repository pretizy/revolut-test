package com.revolut.test.model;

import lombok.Data;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
@Table(name = "BOOKING", indexes = {@Index(name = "transaction_index", columnList = "transaction_id", unique = true)})
public class Booking {
    @Id
    @GeneratedValue(generator = "uuid")
    private Long id;

    private String description;

    @Column(name = "sender_iban")
    private String senderIban;


    @Column(name = "receiver_iban")
    private String receiverIban;

    private String bookingType;

    private BigDecimal amount;

    private LocalDate createdAt;

    private String status;

    private String failureReason;

    @Column(name = "transaction_id")
    private String transactionId;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

}
