package com.revolut.test.testsupport.factories;

import com.revolut.test.model.Account;
import factoryduke.FactoryDuke;
import factoryduke.TFactory;
import factoryduke.generators.Generators;
import factoryduke.generators.SequenceGenerator;

import java.math.BigDecimal;
import java.time.LocalDate;

public class AccountFactory implements TFactory {

    private static final SequenceGenerator sequenceGenerator1 = Generators.sequence().startingAt(100);
    private static final SequenceGenerator sequenceGenerator2 = Generators.sequence().startingAt(1);

    @Override
    public void define() {
        FactoryDuke.define(Account.class, account -> {
            long id = sequenceGenerator2.nextValue();
            account.setId(id);
            account.setBalance(BigDecimal.valueOf(sequenceGenerator1.incrementingBy(100).nextValue()));
            account.setCreationDate(LocalDate.now());
            account.setIban("TESTIBAN" + id);
            account.setBic("TEST");
        });
    }



    public static void reset() {
        sequenceGenerator1.startingAt(100);
        sequenceGenerator2.startingAt(1);
    }

}
