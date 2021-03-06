package ru.fomin.dto.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.fomin.dto.DataPackage;
import ru.fomin.enumeration.FileManipulateRequest;

/**
 * DTO for removing, downloading and getting names of resources.
 * DTO with command for removing file or directory,
 * downloading file from server
 * and getting all files and nested directories from the particular directory.
 */
@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class FileManipulationRequest extends DataPackage {

    private final FileManipulateRequest request;
    private Long id;

}
