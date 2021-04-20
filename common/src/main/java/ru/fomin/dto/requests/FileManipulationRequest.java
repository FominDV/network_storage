package ru.fomin.dto.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.fomin.dto.DataPackage;

/**
 * DTO for removing, downloading and getting names of resources.
 * DTO with command for removing file or directory,
 * downloading file from server
 * and getting all files and nested directories from the particular directory.
 */
@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class FileManipulationRequest extends DataPackage {

    private final Request request;
    private Long id;

    public enum Request {
        DELETE_DIR,
        DELETE_FILE,
        DOWNLOAD,
        GET_FILES_LIST,
        INTO_DIR,
        OUT_DIR
    }

}
