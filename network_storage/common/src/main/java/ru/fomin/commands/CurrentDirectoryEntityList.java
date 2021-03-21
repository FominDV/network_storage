package ru.fomin.need.commands;


import java.util.Map;


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

    public void setCurrentDirectory(String currentDirectory) {
        this.currentDirectory = currentDirectory;
    }

    public Map<String, Long> getFileMap() {
        return fileMap;
    }

    public void setFileMap(Map<String, Long> fileMap) {
        this.fileMap = fileMap;
    }

    public Map<String, Long> getDirectoryMap() {
        return directoryMap;
    }

    public void setDirectoryMap(Map<String, Long> directoryMap) {
        this.directoryMap = directoryMap;
    }

    public Long getCurrentDirectoryId() {
        return currentDirectoryId;
    }
}