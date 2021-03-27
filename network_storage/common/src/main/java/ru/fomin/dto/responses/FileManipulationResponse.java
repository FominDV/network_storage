package ru.fomin.dto.responses;

import ru.fomin.dto.DataPackage;

/**
 * DTO with response for client about result of some commands for server.
 * This response can contain responses of successful removing,
 * uploading, creating directory, renaming file or directory
 * and can contain response of fail operations,
 * that was failed because resource with this name already exist.
 */
public class FileManipulationResponse extends DataPackage {

    private Response response;
    private String fileName;
    private Long id;

    public FileManipulationResponse(Response response, String fileName) {
        this.response = response;
        this.fileName = fileName;
    }

    public FileManipulationResponse(Response response, String fileName, Long id) {
        this(response, fileName);
        this.id = id;
    }

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

    public Response getResponse() {
        return response;
    }

    public String getFileName() {
        return fileName;
    }

    public Long getId() {
        return id;
    }
}
