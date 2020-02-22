package com.revolut.test.service.impl;

import com.revolut.test.Util;
import com.revolut.test.dao.AccountDao;
import com.revolut.test.dto.TransferRequest;
import com.revolut.test.model.Account;
import com.revolut.test.model.Booking;
import com.revolut.test.model.Transaction;
import com.revolut.test.service.TransferService;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class TransferServiceImpl implements TransferService {

    private final EntityManagerFactory emf =
            Persistence.createEntityManagerFactory(
                    "revolut");
    private EntityManager entityManager;
    private AccountDao accountDao;

    public TransferServiceImpl() {
        entityManager = emf.createEntityManager();
        accountDao = new AccountDao(entityManager);
    }

    public String transferCredit(TransferRequest transferRequest) throws UnsupportedOperationException {

        try {
            entityManager.getTransaction().begin();
            Account senderAccount = accountDao.getAccountByIban(transferRequest.getSenderIban());
            Account receiverAccount = accountDao.getAccountByIban(transferRequest.getReceiverIban());
            if (senderAccount != null && receiverAccount != null) {
                if (senderAccount.getBalance().subtract(transferRequest.getAmount()).compareTo(BigDecimal.ZERO) >= 0) {
                    UUID uuid = UUID.randomUUID();
                    final String transactionId = uuid.toString() + "trx";
                    Transaction transaction = new Transaction();
                    transaction.setAmount(transferRequest.getAmount());
                    transaction.setSenderIban(transferRequest.getSenderIban());
                    transaction.setReceiverIban(transferRequest.getReceiverIban());
                    transaction.setDescription(transferRequest.getDescription());
                    transaction.setBookingType("CREDIT_TRANSFER");
                    transaction.setCreatedAt(LocalDate.now());
                    transaction.setTransactionId(transactionId);
                    transaction.setStatus("PENDING");
                    Booking senderBooking = Util.createBookingsForRequest(transferRequest);
                    senderBooking.setAmount(transferRequest.getAmount().negate());
                    senderBooking.setBookingType("DEBIT");
                    senderBooking.setAccount(senderAccount);
                    senderBooking.setStatus("PENDING");
                    senderBooking.setTransactionId(transactionId);
                    entityManager.persist(transaction);
                    entityManager.persist(senderBooking);
                    entityManager.getTransaction().commit();
                    return transactionId;
                } else {
                    throw new UnsupportedOperationException("Balance not enough for transaction");
                }
            }
        } finally {
            entityManager.close();
        }

        return null;
    }


}
