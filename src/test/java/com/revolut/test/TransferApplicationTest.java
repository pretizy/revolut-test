package com.revolut.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.revolut.test.dao.AccountDao;
import com.revolut.test.dao.BookingDao;
import com.revolut.test.dao.TransactionDao;
import com.revolut.test.dto.BookingDto;
import com.revolut.test.dto.TransferRequest;
import com.revolut.test.model.Account;
import com.revolut.test.testsupport.factories.AccountFactory;
import factoryduke.FactoryDuke;
import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;

import static com.revolut.test.util.TestUtil.createSampleTransferequest;
import static io.restassured.RestAssured.given;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


@Slf4j
public class TransferApplicationTest {
    private ObjectMapper objectMapper = new ObjectMapper();


    @Before
    public void setup() {
        FactoryDuke.load("com.revolut.test.testsupport.factories");
        final EntityManagerFactory emf = Persistence.createEntityManagerFactory("revolut");
        final EntityManager entityManager = emf.createEntityManager();
        EntityTransaction tx = entityManager.getTransaction();
        try {
            tx.begin();
            FactoryDuke.build(Account.class).times(2).toList().forEach(account -> entityManager.persist(account));
            tx.commit();
            TransferApplication.startProcess();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @After
    public void destroy() {
        TransferApplication.getHttpserverConnection().stop();
        EntityManager manager = Persistence.createEntityManagerFactory("revolut").createEntityManager();
        AccountDao accountDao = new AccountDao(manager);
        BookingDao bookingDao = new BookingDao(manager);
        manager.getTransaction().begin();
        bookingDao.findAllBookings().forEach(booking -> manager.remove(booking));
        accountDao.findAllAccounts().forEach(account -> manager.remove(account));
        manager.getTransaction().commit();
        manager.close();
        AccountFactory.reset();
    }

    @Test
    public void should_transferMoney_when_transferRequestIsMade() throws JsonProcessingException {
        //given
        RestAssured.baseURI = "http://localhost:8080";
        RequestSpecification request = given();
        TransferRequest transferRequest = createSampleTransferequest(100L);
        String json = objectMapper.writeValueAsString(transferRequest);

        //when
        final EntityManagerFactory emf = Persistence.createEntityManagerFactory("revolut");
        final EntityManager entityManager = emf.createEntityManager();
        TransactionDao transactionDao = new TransactionDao(entityManager);
        try {
            AccountDao accountDao = new AccountDao(entityManager);
            request.body(json);
            request.post("/transfer")
                    .then()
                    .assertThat()
                    .statusCode(200)
                    .body(containsString("trx"))
                    .body(containsString("\"status\":\"PROCESSING\""));

            await().atMost(Duration.ofSeconds(10)).until(() -> transactionDao.findSuccesfulTransaction().size() > 0);

            //then
            assertThat(accountDao.getAccountById(1L).getBalance(), comparesEqualTo(BigDecimal.ZERO));
            assertThat(accountDao.getAccountById(2L).getBalance(), comparesEqualTo(BigDecimal.valueOf(300)));
        } finally {
            entityManager.close();
        }
    }

    @Test
    public void should_ReturnError_whenInssuficientBalanceInSenderAccount() throws JsonProcessingException {
        //given
        RestAssured.baseURI = "http://localhost:8080";
        RequestSpecification request = given();
        TransferRequest transferRequest = createSampleTransferequest(200L);
        String json = objectMapper.writeValueAsString(transferRequest);


        //when
        final EntityManagerFactory emf = Persistence.createEntityManagerFactory("revolut");
        final EntityManager entityManager = emf.createEntityManager();
        try {
            AccountDao accountDao = new AccountDao(entityManager);
            request.body(json);
            request.post("/transfer")
                    .then()
                    .assertThat()
                    .statusCode(500)
                    .body(containsString("\"code\":\"500\""))
                    .body(containsString("\"message\":\"Balance not enough for transaction\""));

            //then
            assertThat(accountDao.getAccountById(1L).getBalance(), comparesEqualTo(BigDecimal.valueOf(100)));
            assertThat(accountDao.getAccountById(2L).getBalance(), comparesEqualTo(BigDecimal.valueOf(200)));
        } finally {
            entityManager.close();
        }

    }

    @Test
    public void should_ReturnBookingsWithUpdatedStatus_whenTransferRequestHasBeenMade() throws JsonProcessingException {
        TransferRequest transferRequest = createSampleTransferequest(100L);
        //given
        RestAssured.baseURI = "http://localhost:8080";
        given().body(transferRequest)
                .post("/transfer")
                .then()
                .assertThat()
                .statusCode(200);

        await().atLeast(Duration.ofSeconds(10));  //wait 10 seconds

        String jsonResponse = given()
                .queryParam("accountId", "1")
                .when()
                .get("/bookings")
                .then()
                .statusCode(200)
                .extract()
                .response()
                .getBody()
                .asString();

        List<BookingDto> bookings = objectMapper.readValue(jsonResponse, new TypeReference<List<BookingDto>>() {
        });

        //then
        bookings.forEach(bookingDto -> assertThat(bookingDto.getStatus(), is(equalTo("CONFIRMED"))));

    }


}
