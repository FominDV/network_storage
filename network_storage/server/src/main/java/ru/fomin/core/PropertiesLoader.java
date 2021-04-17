package ru.fomin.core;

import lombok.Getter;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class PropertiesLoader {

    private static final String SERVER_PROPERTIES_PATH = "server/src/main/resources/server.properties";
    private static final String PROPERTIES_PATH = "server/src/main/resources/hibernate.properties";

    @Getter
    private static final String ROOT_DIRECTORY;
    @Getter
    private static final Integer PORT;
    @Getter
    private static final String URL;
    @Getter
    private static final String USER;
    @Getter
    private static final String PASSWORD;


    static {

        Properties properties = new Properties();

        try (FileReader fileReaderServerProperties = new FileReader(SERVER_PROPERTIES_PATH);
             FileReader fileReaderDbProperties = new FileReader(PROPERTIES_PATH)) {

            properties.load(fileReaderServerProperties);
            properties.load(fileReaderDbProperties);

            URL = properties.getProperty("hibernate.connection.url");
            USER = properties.getProperty("hibernate.connection.username");
            PASSWORD = properties.getProperty("hibernate.connection.password");
            ROOT_DIRECTORY = properties.getProperty("rootDirectory");
            PORT = Integer.parseInt(properties.getProperty("port"));

        } catch (IOException | NumberFormatException e) {
            throw new RuntimeException("Loading server properties error: " + e.getCause());
        }
    }

    private static void checkProperties() {
        if (URL == null ||
                USER == null ||
                PASSWORD == null ||
                ROOT_DIRECTORY == null ||
                PORT == null) {
            throw new RuntimeException("Incorrect properties.");
        }
    }

}
