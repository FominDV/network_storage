package ru.fomin.dto.requests;

import lombok.Value;
import lombok.experimental.NonFinal;
import ru.fomin.dto.DataPackage;

/**
 * DTO for changing password.
 */
@Value
@NonFinal
public class ChangePasswordRequest extends DataPackage {

    String password;

}
