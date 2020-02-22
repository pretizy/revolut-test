package com.revolut.test.service;

import com.revolut.test.dao.AccountDao;
import com.revolut.test.model.Account;
import com.revolut.test.model.Booking;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;

public class BookingService {
    private static final EntityManagerFactory emf =
            Persistence.createEntityManagerFactory(
                    "revolut");
    private EntityManager entityManager = emf.createEntityManager();
    private AccountDao accountDao;

    public BookingService () {
        accountDao = new AccountDao(entityManager);
    }


    public List<Booking> getBookingsForAccount(Long accountId){
        Account account = accountDao.getAccountById(accountId);
        return account.getBookings();
    }
}
