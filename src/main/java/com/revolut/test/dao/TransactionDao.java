package com.revolut.test.dao;

import com.revolut.test.model.Transaction;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;

public class TransactionDao {
    private EntityManager entityManager;

    public TransactionDao(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public List<Transaction> getTransactionsForProcesing(){
        TypedQuery<Transaction> query = entityManager.createQuery(
                "SELECT t FROM Transaction t WHERE t.status = :status ORDER BY t.createdAt ASC", Transaction.class);
        return query.setParameter("status", "PENDING").getResultList();
    }

    public List<Transaction> findSuccesfulTransaction() {
        entityManager.getTransaction().begin();
        entityManager.flush();
        entityManager.getTransaction().commit();
        Query query = entityManager.createQuery("SELECT t FROM Transaction t WHERE t.status = :status");
        List<Transaction> transactions =  query.setParameter("status",  "FINISHED").getResultList();
        return transactions;
    }
}
