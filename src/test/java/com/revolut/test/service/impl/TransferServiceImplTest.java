package com.revolut.test.service.impl;


import com.revolut.test.dao.AccountDao;
import com.revolut.test.dto.TransferRequest;
import com.revolut.test.model.Account;
import com.revolut.test.testsupport.factories.AccountFactory;
import com.revolut.test.util.TestUtil;
import factoryduke.FactoryDuke;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.mockito.Matchers.matches;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class TransferServiceImplTest {
    @InjectMocks
    private TransferServiceImpl transferService;

    @Mock
    private EntityManager entityManager;

    @Mock
    private AccountDao accountDao;

    @Mock
    private EntityTransaction entityTransaction;

    private List<Account> accounts;


    @Before
    public void setup() {
        FactoryDuke.load("com.revolut.test.testsupport.factories");
        initMocks(this);
        //given
        accounts = FactoryDuke.build(Account.class).times(2).toList();
        doNothing().when(entityTransaction).rollback();
        doNothing().when(entityTransaction).begin();
        doNothing().when(entityTransaction).commit();
        when(entityManager.getTransaction()).thenReturn(entityTransaction);
        when(accountDao.getAccountByIban(matches("TESTIBAN1"))).thenReturn(accounts.get(0));
        when(accountDao.getAccountByIban(matches("TESTIBAN2"))).thenReturn(accounts.get(1));

    }


    @After
    public void destroy() {
        AccountFactory.reset();
    }

    @Test
    public void should_showCorrectValue_whenTransferRequestIsValid(){
        TransferRequest transferRequest = TestUtil.createSampleTransferequest(100L);
        //when
        transferService.transferCredit(transferRequest);

        //then
        assertThat(accounts.get(0).getBalance(), comparesEqualTo(BigDecimal.valueOf(100)));
    }

    @Test
    public void should_throwError_whenTransferRequestIsInValid(){
        TransferRequest transferRequest = TestUtil.createSampleTransferequest(200L);

        //when
        assertThatThrownBy(() -> transferService.transferCredit(transferRequest)).isInstanceOf(UnsupportedOperationException.class);

    }
}
