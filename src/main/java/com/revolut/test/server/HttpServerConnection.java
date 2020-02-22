package com.revolut.test.server;

import com.revolut.test.server.handler.TransferPostHandler;
import com.revolut.test.server.handler.GetBookingsHandler;
import com.sun.net.httpserver.HttpServer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;

@Slf4j
public class HttpServerConnection {
    HttpServer httpServer;
    private boolean open = true;

    public void startBlockingHttpConnection(Integer port, Integer numberOfPools) throws IOException {

        httpServer = HttpServer.create(new InetSocketAddress(port), numberOfPools);
        GetBookingsHandler getBookingsHandler = new GetBookingsHandler();
        TransferPostHandler transferPostHandler = new TransferPostHandler();
        httpServer.createContext("/bookings", getBookingsHandler);
        httpServer.createContext("/transfer", transferPostHandler);

        httpServer.start();

    }


    public void stop() {
        httpServer.stop(0);
    }

}
