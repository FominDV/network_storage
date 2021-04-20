package ru.fomin;

import org.testcontainers.containers.PostgreSQLContainer;
import ru.fomin.core.PropertiesLoader;

import java.io.*;
import java.util.Properties;

public class PostgreSQLTestContainer {

    private static final String DOCKER_IMAGE_NAME ="postgres:latest";
    private static final String DATABASE_NAME = "test_network_storage";
    private static final String USERNAME ="postgres";
    private static final String PASSWORD ="test_password";

    private static PostgreSQLContainer<?> postgreSQLContainer;

    public static PostgreSQLContainer<?> getTestContainer(){
      return   postgreSQLContainer = new PostgreSQLContainer<>(DOCKER_IMAGE_NAME)
                .withDatabaseName(DATABASE_NAME)
                .withUsername(USERNAME)
                .withPassword(PASSWORD);
    }

    public static void changeProperties(){

        //get list of target files
        ClassLoader classLoader = PropertiesLoader.class.getClassLoader();
        File classpathRoot = new File(classLoader.getResource("").getPath());
        File[] fileList = classpathRoot.listFiles((dir, name) -> (name.equals("hibernate.properties")));

        Properties properties = new Properties();

        loadProperties(properties, fileList[0]);

        properties.setProperty("hibernate.connection.url", postgreSQLContainer.getJdbcUrl());
        properties.setProperty("hibernate.connection.username", postgreSQLContainer.getUsername());
        properties.setProperty("hibernate.connection.password", postgreSQLContainer.getPassword());

        overwriteProperties(properties, fileList[0]);

    }

    private static void loadProperties(Properties properties, File file){
        try(Reader reader = new FileReader(file)) {
            properties.load(reader);
        } catch (IOException e) {
            throw new RuntimeException("Can not load properties by IO: " + e.getCause());
        }
    }

    private static void overwriteProperties(Properties properties, File file){
        try( Writer writer = new FileWriter(file)) {
            properties.store(writer, null);
        } catch (IOException e) {
            throw new RuntimeException("Can not load properties by IO: " + e.getCause());
        }
    }

}
