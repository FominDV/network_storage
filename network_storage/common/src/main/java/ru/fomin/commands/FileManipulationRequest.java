package ru.fomin.commands;

public class FileManipulationRequest extends DataPackage{

    private Request request;
    private Long id;

    public FileManipulationRequest(Request request) {
        this.request = request;
    }

    public FileManipulationRequest(Request request, Long id) {
        this.request = request;
        this.id = id;
    }

    public enum Request{
       DELETE_DIR ,DELETE_FILE, DOWNLOAD, GET_FILES_LIST
    }

    public Request getRequest() {
        return request;
    }

    public Long getId() {
        return id;
    }
}