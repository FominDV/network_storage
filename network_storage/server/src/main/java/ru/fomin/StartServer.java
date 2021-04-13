package ru.fomin;

import lombok.SneakyThrows;
import org.flywaydb.core.Flyway;
import ru.fomin.core.Server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class StartServer {

    /**
     * Start the server.
     */
    @SneakyThrows
    public static void main(String[] args) {
        Properties properties = new Properties();
        properties.load(new FileReader("server/src/main/resources/hibernate.properties"));

        String url = properties.getProperty("hibernate.connection.url");
        String user = properties.getProperty("hibernate.connection.username");
        String password = properties.getProperty("hibernate.connection.password");

        // Create the Flyway instance and point it to the database
        Flyway flyway = Flyway.configure().dataSource(url, user, password).load();

        // Start the migration
        flyway.migrate();
        new Server().start();
    }
}
