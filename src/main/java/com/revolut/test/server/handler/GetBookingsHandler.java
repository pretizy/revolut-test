package com.revolut.test.server.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revolut.test.Util;
import com.revolut.test.dto.BookingDto;
import com.revolut.test.model.Booking;
import com.revolut.test.service.BookingService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.revolut.test.Util.BAD_REQUEST_ERROR_MESSAGE;
import static com.revolut.test.Util.NOTFOUND_ERROR_MESSAGE;

/**
 * Http Get Handler for /bookings context that handles polling of bookings by various clients
 */
public class GetBookingsHandler implements HttpHandler {
    private BookingService bookingService;
    private ObjectMapper objectMapper;

    public GetBookingsHandler() {
        bookingService = new BookingService();
        objectMapper = new ObjectMapper();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        OutputStream outputStream = exchange.getResponseBody();
        if (method.equals("GET")) {
            Map<String, String> queryParam = Util.queryToMap(exchange.getRequestURI().getQuery());
            if (queryParam.get("accountId") != null) {
                final String accountId = queryParam.get("accountId");
                List<Booking> bookings = bookingService.getBookingsForAccount(Long.parseLong(accountId));
                List<BookingDto> bookingDtoList = bookings.stream().map(booking -> {
                    BookingDto bookingDto = new BookingDto();
                    bookingDto.setAmount(booking.getAmount());
                    bookingDto.setBookingType(booking.getBookingType());
                    bookingDto.setCreatedAt(booking.getCreatedAt());
                    bookingDto.setDescription(booking.getDescription());
                    bookingDto.setFailureReason(booking.getFailureReason());
                    bookingDto.setId(booking.getId());
                    bookingDto.setReceiver_iban(booking.getReceiverIban());
                    bookingDto.setTransactionId(booking.getTransactionId());

                    return bookingDto;
                }).collect(Collectors.toList());
                String response = objectMapper.writeValueAsString(bookingDtoList);
                exchange.sendResponseHeaders(200, response.length());
                outputStream.write(response.getBytes());
            } else {
                exchange.sendResponseHeaders(400, BAD_REQUEST_ERROR_MESSAGE.length());
                outputStream.write(BAD_REQUEST_ERROR_MESSAGE.getBytes());
                outputStream.close();
            }
        } else {
            exchange.sendResponseHeaders(404, NOTFOUND_ERROR_MESSAGE.length());
            outputStream.write(NOTFOUND_ERROR_MESSAGE.getBytes());
            outputStream.close();
        }
    }


}
