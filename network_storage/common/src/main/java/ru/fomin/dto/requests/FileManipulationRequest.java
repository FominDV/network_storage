package ru.fomin.dto.requests;

import ru.fomin.dto.DataPackage;

/**
 * DTO for removing, downloading and getting names of resources.
 * DTO with command for removing file or directory,
 * downloading file from server
 * and getting all files and nested directories from the particular directory.
 */
public class FileManipulationRequest extends DataPackage {

    private Request request;
    private Long id;

    public FileManipulationRequest(Request request) {
        this.request = request;
    }

    public FileManipulationRequest(Request request, Long id) {
        this.request = request;
        this.id = id;
    }

    public enum Request {
        DELETE_DIR, DELETE_FILE, DOWNLOAD, GET_FILES_LIST
    }

    public Request getRequest() {
        return request;
    }

    public Long getId() {
        return id;
    }
}
