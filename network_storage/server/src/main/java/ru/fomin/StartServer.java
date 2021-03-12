package ru.fomin;

import ru.fomin.network.SocketHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StartServer {

    int port = 8189;

    public StartServer() {

        ExecutorService service = Executors.newFixedThreadPool(4);
        try (ServerSocket server = new ServerSocket(port)) {
            Class.forName("ru.fomin.dao.SessionFactory");
            System.out.println("Server started");

            while (true) {
                service.execute(new SocketHandler(server.accept()));
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new StartServer();
    }
}