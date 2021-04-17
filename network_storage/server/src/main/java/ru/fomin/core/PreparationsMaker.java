package ru.fomin.core;

import org.flywaydb.core.Flyway;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Class for preparation for launching server.
 */
public class PreparationsMaker {

    /**
     * Creates root directory and migrates DB.
     */
    public void preparation() {
        migrateDB();
        createRootDirectory();
    }

    private void createRootDirectory() {
        Path path = Paths.get(PropertiesLoader.getROOT_DIRECTORY());
        if (Files.notExists(path)) {
            try {
                Files.createDirectory(path);
            } catch (IOException e) {
                throw new RuntimeException("Wrong path of root directory: " + e.getCause());
            }
        }
    }

    private void migrateDB() {

        String url = PropertiesLoader.getURL();
        String user = PropertiesLoader.getUSER();
        String password = PropertiesLoader.getPASSWORD();

        // Create the Flyway instance and point it to the database
        Flyway flyway = Flyway.configure().dataSource(url, user, password).load();

        // Start the migration
        flyway.migrate();

    }
}
