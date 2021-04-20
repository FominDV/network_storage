package ru.fomin.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.fomin.dto.DataPackage;

/**
 * DTO with response for client about result of some commands for server.
 * This response can contain responses of successful removing,
 * uploading, creating directory, renaming file or directory
 * and can contain response of fail operations,
 * that was failed because resource with this name already exist.
 */
@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class FileManipulationResponse extends DataPackage {

    private final Response response;
    private final String fileName;
    private Long id;

    public enum Response {
        FILE_ALREADY_EXIST,
        DIR_ALREADY_EXIST,
        FILE_UPLOADED,
        DIR_CREATED,
        FILE_REMOVED,
        DIRECTORY_REMOVED,
        RENAME_DIR,
        RENAME_FILE
    }

}
