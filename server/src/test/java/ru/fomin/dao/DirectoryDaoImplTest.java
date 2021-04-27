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
import ru.fomin.dao.impl.DirectoryDaoImpl;
import ru.fomin.util.PreparationsMaker;
import ru.fomin.entity.Directory;

import java.util.stream.Stream;


@Testcontainers
class DirectoryDaoImplTest {

    @Container
    private static final PostgreSQLContainer<?> postgresqlContainer = PostgreSQLTestContainer.getTestContainer();

    private DirectoryDaoImpl directoryDaoImpl = new DirectoryDaoImpl();

    @BeforeAll
    public static void init() {
        PostgreSQLTestContainer.changeProperties();
        new PreparationsMaker().migrateDB();
    }

    @ParameterizedTest
    @MethodSource("getStreamForCreate")
    public void save(Directory directory, Long expectedId) {
        Long actualId = directoryDaoImpl.save(directory);
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
