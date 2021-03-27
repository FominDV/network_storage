package ru.fomin.dto.requests;

import ru.fomin.dto.DataPackage;

/**
 * DTO with command for creating and renaming directory, and also renaming file.
 */
public class CreatingAndUpdatingManipulationRequest extends DataPackage {

    private final String newName;
    private final Long id;
    private final Type type;

    public CreatingAndUpdatingManipulationRequest(String newName, Long id, Type type) {
        this.newName = newName;
        this.id = id;
        this.type = type;
    }

    public enum Type {
        CREATE, RENAME_DIR, RENAME_FILE
    }

    public String getNewName() {
        return newName;
    }

    public Long getId() {
        return id;
    }

    public Type getType() {
        return type;
    }
}
