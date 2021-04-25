package ru.fomin.util;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.io.*;
import java.util.Properties;

/**
 * Class for loading properties from properties files.
 */
@Log4j2
public class PropertiesLoader {

    private static final String SERVER_PROPERTIES = "server.properties";

    private static final String HIBERNATE_PROPERTIES = "hibernate.properties";

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

        Properties properties = getProperties();

        URL = properties.getProperty("hibernate.connection.url");
        USER = properties.getProperty("hibernate.connection.username");
        PASSWORD = properties.getProperty("hibernate.connection.password");
        ROOT_DIRECTORY = properties.getProperty("rootDirectory");
        PORT = Integer.parseInt(properties.getProperty("port"));

        //verify values by null
        checkProperties();

    }

    private static void checkProperties() {
        if (URL == null ||
                USER == null ||
                PASSWORD == null ||
                ROOT_DIRECTORY == null ||
                PORT == null) {
            throw new RuntimeException("Incorrect properties.");
        } else {
            log.info("Properties was loaded successful.");
        }
    }

    /**
     * Returns filled properties.
     */
    private static Properties getProperties() {

        //get list of target files
        ClassLoader classLoader = PropertiesLoader.class.getClassLoader();
        File classpathRoot = new File(classLoader.getResource("").getPath());
        File[] fileList = classpathRoot.listFiles((dir, name) -> (name.endsWith(SERVER_PROPERTIES) || name.equals(HIBERNATE_PROPERTIES)));

        Properties properties = new Properties();
        for (File file : fileList) {
            try {
                Reader reader = getFileReader(file);
                properties.load(reader);
                reader.close();
            } catch (IOException e) {
                throw new RuntimeException("Can not load properties by IO: " + e.getCause());
            }
        }

        return properties;
    }

    private static FileReader getFileReader(File file) {
        try {
            return new FileReader(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Loading server properties error: " + e.getCause());
        }
    }

}
