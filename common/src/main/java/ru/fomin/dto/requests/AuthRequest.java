package ru.fomin.dto.requests;

import lombok.Value;
import ru.fomin.enumeration.AuthAndRegRequest;

/**
 * DTO with command for authentication and registration.
 */
@Value
public class AuthRequest extends BasePasswordRequest {

    String login;
    AuthAndRegRequest authAndRegRequest;

    public AuthRequest(String password, String login, AuthAndRegRequest authAndRegRequest) {
        super(password);
        this.login = login;
        this.authAndRegRequest = authAndRegRequest;
    }

}