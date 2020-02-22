package com.revolut.test.dao;

import com.revolut.test.model.Account;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;

public class AccountDao {
    private EntityManager entityManager;

    public AccountDao(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public Account getAccountByIban(String iban){
        TypedQuery<Account> query = entityManager.createQuery(
                "SELECT a FROM Account a WHERE a.iban = :iban", Account.class);
        return query.setParameter("iban", iban).getSingleResult();
    }

    public Account getAccountById(Long id){
        TypedQuery<Account> query = entityManager.createQuery(
                "SELECT a FROM Account a WHERE a.id = :id", Account.class);
        return query.setParameter("id", id).getSingleResult();
    }

    public List<Account> findAllAccounts() {
        Query query = entityManager.createQuery("SELECT a FROM Account a");
        return query.getResultList();
    }
}
