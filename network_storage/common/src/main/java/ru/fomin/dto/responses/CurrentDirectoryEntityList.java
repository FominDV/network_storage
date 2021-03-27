package ru.fomin.dto.responses;


import ru.fomin.dto.DataPackage;

import java.util.Map;

/**
 * DTO for sending from server to client list of files and nested directories of current directory that is been using client now.
 */
public class CurrentDirectoryEntityList extends DataPackage {

    private Map<String, Long> fileMap;
    private Map<String, Long> directoryMap;
    private String currentDirectory;
    private Long currentDirectoryId;

    public CurrentDirectoryEntityList(Map<String, Long> fileMap, Map<String, Long> directoryMap, String currentDirectory, Long currentDirectoryId) {
        this.fileMap = fileMap;
        this.directoryMap = directoryMap;
        this.currentDirectory = currentDirectory;
        this.currentDirectoryId = currentDirectoryId;
    }

    public String getCurrentDirectory() {
        return currentDirectory;
    }

    public Map<String, Long> getFileMap() {
        return fileMap;
    }

    public Map<String, Long> getDirectoryMap() {
        return directoryMap;
    }

    public Long getCurrentDirectoryId() {
        return currentDirectoryId;
    }
}