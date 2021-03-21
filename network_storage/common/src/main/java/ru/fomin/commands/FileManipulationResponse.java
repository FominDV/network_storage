package ru.fomin.commands;

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
        FILE_ALREADY_EXIST, DIR_ALREADY_EXIST, FILE_UPLOADED, DIR_CREATED, FILE_REMOVED, DIRECTORY_REMOVED, RENAME_DIR, RENAME_FILE
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
