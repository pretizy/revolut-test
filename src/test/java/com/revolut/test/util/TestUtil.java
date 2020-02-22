package com.revolut.test.util;

import com.revolut.test.dto.TransferRequest;

import java.math.BigDecimal;

public class TestUtil {
    public static TransferRequest createSampleTransferequest(Long amount) {
        TransferRequest transferRequest = new TransferRequest();
        transferRequest.setAmount(BigDecimal.valueOf(amount));
        transferRequest.setDescription("February rent");
        transferRequest.setSenderIban("TESTIBAN1");
        transferRequest.setReceiverIban("TESTIBAN2");
        return transferRequest;
    }
}
