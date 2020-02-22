package com.revolut.test.service.impl;

import com.revolut.test.Util;
import com.revolut.test.dao.AccountDao;
import com.revolut.test.dao.BookingDao;
import com.revolut.test.dao.TransactionDao;
import com.revolut.test.model.Account;
import com.revolut.test.model.Booking;
import com.revolut.test.model.Transaction;
import com.revolut.test.service.BankEngine;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.LockModeType;
import javax.persistence.Persistence;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class SimpleRevolutTestBankEngineImpl implements BankEngine {
    private static final EntityManagerFactory emf =
            Persistence.createEntityManagerFactory(
                    "revolut");
    private EntityManager entityManager = emf.createEntityManager();
    private AccountDao accountDao;
    private BookingDao bookingDao;
    private TransactionDao transactionDao;
    private boolean shutdown = true;

    public void start() {
        if(shutdown) {
            shutdown = false;
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.execute(() -> {
                try {
                    execute();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }

    }

    private void execute() throws InterruptedException {
        log.info("Starting Bank Engine");
        int count = 0;
        while (!shutdown) {
            transactionDao = new TransactionDao(entityManager);
            List<Transaction> transactions = transactionDao.getTransactionsForProcesing();
            Queue<Transaction> transactionQueue = new LinkedList<>(transactions);

            while (!transactionQueue.isEmpty()) {
                Transaction transaction = transactionQueue.remove();

                EntityTransaction tx = entityManager.getTransaction();
                try {
                    tx.begin();
                    accountDao = new AccountDao(entityManager);
                    bookingDao = new BookingDao(entityManager);
                    Account receiverAccount = accountDao.getAccountByIban(transaction.getReceiverIban());
                    Booking senderBooking = bookingDao.getBookingByTransactionId(transaction.getTransactionId());
                    Account senderAccount = senderBooking.getAccount();
                    entityManager.lock(senderAccount, LockModeType.PESSIMISTIC_WRITE);
                    entityManager.lock(receiverAccount, LockModeType.PESSIMISTIC_WRITE);
                    if (senderAccount.getBalance().compareTo(transaction.getAmount()) >= 0) {
                        Booking receiverBooking = Util.createBookingsForRequest(transaction);
                        senderAccount.setBalance(senderAccount.getBalance().subtract(transaction.getAmount()));
                        receiverAccount.setBalance(receiverAccount.getBalance().add(transaction.getAmount()));
                        receiverBooking.setAmount(transaction.getAmount());
                        receiverBooking.setBookingType("CREDIT");
                        receiverBooking.setAccount(receiverAccount);
                        senderBooking.setStatus("CONFIRMED");
                        receiverBooking.setStatus("CONFIRMED");
                        receiverAccount.getBookings().add(receiverBooking);
                        entityManager.merge(receiverAccount);
                        entityManager.merge(senderAccount);
                    } else {
                        senderBooking.setStatus("FAILED");
                        senderBooking.setFailureReason("INSUFFICIENT FUNDS");
                    }
                    transaction.setStatus("FINISHED");
                    entityManager.merge(transaction);
                    entityManager.merge(senderBooking);
                    entityManager.flush();
                    tx.commit();
                } catch (Exception e) {
                    tx.rollback();
                }
                count++;
            }

            //shutdown after 2nd processing also sleep for a few seconds, as this is not a real world scenario no need to for real time processing
            if (count > 2) {
                log.info("closed on count "+ count);
                shutdown = true;
            }
            Thread.sleep(3000);
        }
    }

}
