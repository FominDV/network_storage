package ru.fomin.need.classes;

public class Constants {

    private static final String FILE_NAME_PREFIX = "file: ";
    private static final String DIRECTORY_NAME_PREFIX = "directory: ";

    public static String getFileNamePrefix() {
        return FILE_NAME_PREFIX;
    }

    public static String getDirectoryNamePrefix() {
        return DIRECTORY_NAME_PREFIX;
    }
}
