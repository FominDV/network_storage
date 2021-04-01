package ru.fomin.dto.responses;


import lombok.Value;
import ru.fomin.dto.DataPackage;

import java.util.Map;

/**
 * DTO for sending from server to client list of files and nested directories of current directory that is been using client now.
 */
@Value
public class CurrentDirectoryEntityList extends DataPackage {

    Map<String, Long> fileMap;
    Map<String, Long> directoryMap;
    String currentDirectory;
    Long currentDirectoryId;

}