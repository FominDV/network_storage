package ru.fomin.need.commands;

public class DirectoryManipulationCommand extends DataPackage{

    private final String newDirectoryName;
    private final Long currentDirectoryId;
    private final Type type;

    public DirectoryManipulationCommand(String newDirectoryName, Long currentDirectoryId, Type type) {
        this.newDirectoryName = newDirectoryName;
        this.currentDirectoryId = currentDirectoryId;
        this.type = type;
    }

    public enum Type{
        CREATE, RENAME
    }

    public String getNewDirectoryName() {
        return newDirectoryName;
    }

    public Long getCurrentDirectoryId() {
        return currentDirectoryId;
    }

    public Type getType() {
        return type;
    }
}
