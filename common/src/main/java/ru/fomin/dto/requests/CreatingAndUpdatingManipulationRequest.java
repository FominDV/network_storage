package ru.fomin.dto.requests;

import lombok.Data;
import lombok.Value;
import ru.fomin.dto.DataPackage;

/**
 * DTO with command for creating and renaming directory, and also renaming file.
 */
@Value
public class CreatingAndUpdatingManipulationRequest extends DataPackage {

    String newName;
    Long id;
    Type type;

    public enum Type {
        CREATE, RENAME_DIR, RENAME_FILE
    }

}
