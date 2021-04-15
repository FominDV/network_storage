package ru.fomin;

import lombok.SneakyThrows;
import org.flywaydb.core.Flyway;
import ru.fomin.core.Server;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class StartServer {

    private final static String PROPERTIES_PATH = "server/src/main/resources/hibernate.properties";

    /**
     * Start the server.
     */
    public static void main(String[] args) {
        Properties properties = new Properties();
        try(FileReader fileReader = new FileReader(PROPERTIES_PATH)){
            properties.load(fileReader);
            String url = properties.getProperty("hibernate.connection.url");
            String user = properties.getProperty("hibernate.connection.username");
            String password = properties.getProperty("hibernate.connection.password");

            // Create the Flyway instance and point it to the database
            Flyway flyway = Flyway.configure().dataSource(url, user, password).load();

            // Start the migration
            flyway.migrate();

            new Server().start();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
