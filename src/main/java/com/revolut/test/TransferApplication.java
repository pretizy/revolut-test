package com.revolut.test;

import com.revolut.test.server.HttpServerConnection;
import com.revolut.test.service.BankEngine;
import com.revolut.test.service.impl.SimpleRevolutTestBankEngineImpl;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.time.LocalDate;

@Slf4j
public class TransferApplication {
    private static HttpServerConnection httpServerConnection;
    private static BankEngine bankEngine;

    public static void main(String[] args) {
        try {
            startProcess();
        } catch (IOException | InterruptedException e) {
            log.error("Error :{}", e);
        }
    }

    public static void startProcess() throws IOException, InterruptedException {
        log.info("starting application at {}", LocalDate.now());

        bankEngine = bankEngine == null ? new SimpleRevolutTestBankEngineImpl() : bankEngine;
        httpServerConnection = httpServerConnection == null ? new HttpServerConnection() : httpServerConnection;
        httpServerConnection.startBlockingHttpConnection(8080, 20);
        bankEngine.start();
    }

    public static HttpServerConnection getHttpserverConnection() {
        return httpServerConnection;
    }
}
