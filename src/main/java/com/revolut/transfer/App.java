package com.revolut.transfer;

import com.revolut.transfer.environment.Database;
import com.revolut.transfer.environment.Server;
import com.revolut.transfer.rest.RestController;

public class App {

    public static void main(String[] args) throws Exception {
        Database database = new Database();
        database.start();
        Runtime.getRuntime().addShutdownHook(new Thread(database::stop));

        Server server = new Server(RestController.class);
        server.start();
        Runtime.getRuntime().addShutdownHook(new Thread(server::stop));
        Thread.currentThread().join();
    }
}