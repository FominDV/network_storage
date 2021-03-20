package ru.fomin.need;


import ru.fomin.need.DataPackage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CurrentDirectoryEntityList extends DataPackage {

    private Map<String, Long> fileMap;
    private Map<String, Long> directoryMap;
    private String currentDirectory;

    public CurrentDirectoryEntityList(Map<String, Long> fileMap, Map<String, Long> directoryMap, String currentDirectory) {
        this.fileMap = fileMap;
        this.directoryMap = directoryMap;
        this.currentDirectory = currentDirectory;
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

}