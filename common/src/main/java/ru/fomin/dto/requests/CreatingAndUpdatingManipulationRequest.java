package ru.fomin.dto.requests;

import lombok.Value;
import ru.fomin.dto.DataPackage;
import ru.fomin.dto.enumeration.CreatingAndUpdatingRequest;

/**
 * DTO with command for creating and renaming directory, and also renaming file.
 */
@Value
public class CreatingAndUpdatingManipulationRequest extends DataPackage {

    String newName;
    Long id;
    CreatingAndUpdatingRequest type;

}
