package com.revolut.test.server.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revolut.test.dto.ErrorResponse;
import com.revolut.test.dto.TransferRequest;
import com.revolut.test.dto.TransferResponse;
import com.revolut.test.service.TransferService;
import com.revolut.test.service.impl.TransferServiceImpl;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;

import static com.revolut.test.Util.NOTFOUND_ERROR_MESSAGE;
import static com.revolut.test.Util.retrieveHttpBody;


/**
 * Http POST Handler for /transfer context that handles transfer of funds between accounts
 */
public class TransferPostHandler implements HttpHandler {
    private ObjectMapper objectMapper = new ObjectMapper();
    private TransferService transferService;

    public TransferPostHandler() {
        this.transferService = new TransferServiceImpl();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        try {
            if (method.equals("POST")) {
                InputStream inputStream = exchange.getRequestBody();
                OutputStream outputStream = exchange.getResponseBody();
                String length = exchange.getRequestHeaders().getFirst("Content-length");
                InputStream in = exchange.getRequestBody();
                String json = retrieveHttpBody(Integer.parseInt(length), inputStream);
                TransferRequest transferRequest = objectMapper.readValue(json, TransferRequest.class);
                if (isValidRequest(transferRequest)) {
                    String transactionId = transferService.transferCredit(transferRequest);
                    TransferResponse transferResponse = new TransferResponse();
                    transferResponse.setStatus("PROCESSING");
                    transferResponse.setTransactionId(transactionId);
                    final String response = objectMapper.writeValueAsString(transferResponse);
                    exchange.sendResponseHeaders(200, response.length());
                    outputStream.write(response.getBytes());
                    outputStream.close();
                }
            } else {
                exchange.sendResponseHeaders(404, NOTFOUND_ERROR_MESSAGE.length());
                OutputStream outputStream = exchange.getResponseBody();
                outputStream.write(NOTFOUND_ERROR_MESSAGE.getBytes());
                outputStream.close();
            }

        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setCode("500");
            errorResponse.setMessage(e.getMessage());
            String jsonError = objectMapper.writeValueAsString(errorResponse);
            exchange.sendResponseHeaders(500, jsonError.length());
            OutputStream outputStream = exchange.getResponseBody();
            outputStream.write(jsonError.getBytes());
            outputStream.close();
        }
    }


    private boolean isValidRequest(TransferRequest transferRequest) {
        return transferRequest.getAmount().compareTo(BigDecimal.ZERO) > 0
                && !transferRequest.getSenderIban().isBlank() && !transferRequest.getReceiverIban().isBlank();
    }

}
