package ru.fomin.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.fomin.dto.DataPackage;
import ru.fomin.dto.enumeration.FileManipulateResponse;

/**
 * DTO with fileManipulateResponse for client about result of some commands for server.
 * This fileManipulateResponse can contain responses of successful removing,
 * uploading, creating directory, renaming file or directory
 * and can contain fileManipulateResponse of fail operations,
 * that was failed because resource with this name already exist.
 */
@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class FileManipulationResponse extends DataPackage {

    private final FileManipulateResponse fileManipulateResponse;
    private final String fileName;
    private Long id;

}
