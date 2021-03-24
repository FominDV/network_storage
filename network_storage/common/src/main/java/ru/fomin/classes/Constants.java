package ru.fomin.classes;

/**
 * Constants for client and serves side.
 */
public class Constants {

    //parts of name for view in GUI
    private static final String FILE_NAME_PREFIX = "file: ";
    private static final String DIRECTORY_NAME_PREFIX = "directory: ";

    public static String getFileNamePrefix() {
        return FILE_NAME_PREFIX;
    }

    public static String getDirectoryNamePrefix() {
        return DIRECTORY_NAME_PREFIX;
    }
}
