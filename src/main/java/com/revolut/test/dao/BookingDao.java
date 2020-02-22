package com.revolut.test.dao;

import com.revolut.test.model.Booking;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;

public class BookingDao {
    private EntityManager entityManager;

    public BookingDao(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public Booking getBookingByTransactionId(String transactionId) {
        TypedQuery<Booking> query = entityManager.createQuery(
                "SELECT b FROM Booking b WHERE b.transactionId = :transactionId", Booking.class);
        return query.setParameter("transactionId", transactionId).getSingleResult();
    }

    public List<Booking> findAllBookings() {
        Query query = entityManager.createQuery("SELECT b FROM Booking b");
        List<Booking> bookings = query.getResultList();
        return bookings;
    }
}
