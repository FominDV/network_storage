package ru.fomin.dto.requests;

import lombok.Value;
import ru.fomin.dto.DataPackage;

/**
 * DTO for changing password.
 */
@Value
public class ChangePasswordRequest extends DataPackage {

    String password;

}
