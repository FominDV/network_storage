package ru.fomin.dao;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.fomin.PostgreSQLTestContainer;
import ru.fomin.util.PreparationsMaker;
import ru.fomin.entity.Directory;

import java.util.stream.Stream;


@Testcontainers
class DirectoryDaoTest {

    @Container
    private static final PostgreSQLContainer<?> postgresqlContainer = PostgreSQLTestContainer.getTestContainer();

    private DirectoryDao directoryDao = new DirectoryDao();

    @BeforeAll
    public static void init() {
        PostgreSQLTestContainer.changeProperties();
        new PreparationsMaker().migrateDB();
    }

    @ParameterizedTest
    @MethodSource("getStreamForCreate")
    public void create(Directory directory, Long expectedId) {
        Long actualId = directoryDao.create(directory);
        Assertions.assertEquals(expectedId, actualId);
    }

    private static Stream<Arguments> getStreamForCreate() {
        Directory parentDirectory;
        return Stream.of(
                Arguments.arguments(new Directory(), 1L),
                Arguments.arguments(parentDirectory = new Directory(null, null, "path1"), 2L),
                Arguments.arguments(new Directory(null, parentDirectory, "path1"), 3L)
        );
    }

}
