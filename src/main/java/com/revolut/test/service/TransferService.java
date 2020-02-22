package com.revolut.test.service;

import com.revolut.test.dto.TransferRequest;

public interface TransferService {
    String transferCredit(TransferRequest transferRequest);
}
