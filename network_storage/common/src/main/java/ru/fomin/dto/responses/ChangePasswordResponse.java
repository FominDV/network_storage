package ru.fomin.dto.responses;

import lombok.Value;
import ru.fomin.dto.DataPackage;

/**
 * DTO with result of changing password.
 */
@Value
public class ChangePasswordResponse extends DataPackage {
    boolean isSuccessful;
}
