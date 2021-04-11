package ru.fomin.dto.requests;

import lombok.Value;
import lombok.experimental.NonFinal;
import ru.fomin.dto.DataPackage;

/**
 * Base DTO for requests with password field.
 */
@Value
@NonFinal
public class BasePasswordRequest extends DataPackage {

    String password;

}
