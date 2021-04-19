package ru.fomin.dao;

import org.junit.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.fomin.core.PreparationsMaker;
import ru.fomin.core.PropertiesLoader;
import ru.fomin.entities.Directory;

import java.io.*;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
class DirectoryDaoTest {

    @Container
    private static final PostgreSQLContainer<?> postgresqlContainer = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("test_network_storage")
            .withUsername("postgres")
            .withPassword("test_password")
            .withExposedPorts(5436);

    private DirectoryDao directoryDao = new DirectoryDao();

    @BeforeAll
    public static void init() {
        String url = postgresqlContainer.getJdbcUrl();

        Properties properties = getProperties(url);

        new PreparationsMaker().migrateDB();
    }

    @Test
    public void testSimplePutAndGet() {


        postgresqlContainer.isRunning();
        Directory directory = directoryDao.getDirectoryById(46L);
        directoryDao.create(new Directory());
        Assertions.assertEquals(45L, directory.getId());
    }

    private static Properties getProperties(String url) {

        //get list of target files
        ClassLoader classLoader = PropertiesLoader.class.getClassLoader();
        File classpathRoot = new File(classLoader.getResource("").getPath());
        File[] fileList = classpathRoot.listFiles((dir, name) -> (name.equals("hibernate.properties")));

        Properties properties = new Properties();

        try {
            Reader reader = new FileReader(fileList[0]);
            properties.load(reader);
            reader.close();
        } catch (IOException e) {
            throw new RuntimeException("Can not load properties by IO: " + e.getCause());
        }

        properties.setProperty("hibernate.connection.url", url);

        try( Writer writer = new FileWriter(fileList[0])) {

            properties.store(writer, null);

        } catch (IOException e) {
            throw new RuntimeException("Can not load properties by IO: " + e.getCause());
        }

        return properties;
    }
}
