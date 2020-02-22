package com.revolut.test;

import com.revolut.test.dto.TransferRequest;
import com.revolut.test.model.Booking;
import com.revolut.test.model.Transaction;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class Util {

    public static final String NOTFOUND_ERROR_MESSAGE = "Not Found";
    public static final String BAD_REQUEST_ERROR_MESSAGE = "Bad Request";


    public static Booking createBookingsForRequest(TransferRequest transferRequest) {
        Booking booking = new Booking();
        booking.setDescription(transferRequest.getDescription());
        booking.setCreatedAt(LocalDate.now());
        booking.setSenderIban(transferRequest.getSenderIban());
        booking.setReceiverIban(transferRequest.getReceiverIban());
        return booking;
    }

    public static Booking createBookingsForRequest(Transaction transaction) {
        Booking booking = new Booking();
        booking.setDescription(transaction.getDescription());
        booking.setCreatedAt(LocalDate.now());
        booking.setSenderIban(transaction.getSenderIban());
        booking.setReceiverIban(transaction.getReceiverIban());
        return booking;
    }

    public static Map<String, String> queryToMap(String query) {
        Map<String, String> result = new HashMap<>();
        for (String param : query.split("&")) {
            String[] entry = param.split("=");
            if (entry.length > 1) {
                result.put(entry[0], entry[1]);
            } else {
                result.put(entry[0], "");
            }
        }
        return result;
    }


    public static String retrieveHttpBody(Integer contentLength, InputStream inputStream) throws IOException {
        String encoding = "ISO-8859-1";
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte buf[] = new byte[contentLength];
        for (int n = inputStream.read(buf); n > 0; n = inputStream.read(buf)) {
            out.write(buf, 0, n);
        }
        return new String(out.toByteArray(), encoding);
    }
}
