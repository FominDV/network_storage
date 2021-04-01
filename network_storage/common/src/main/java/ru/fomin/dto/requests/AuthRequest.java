package ru.fomin.dto.requests;

import lombok.Getter;
import lombok.Setter;
import lombok.Value;
import ru.fomin.dto.DataPackage;

/**
 * DTO with command for authentication and registration.
 */
@Value
public class AuthRequest extends DataPackage {

    String login;
    String password;
    RequestType requestType;

    public enum RequestType {
        AUTH, REGISTRATION
    }
}