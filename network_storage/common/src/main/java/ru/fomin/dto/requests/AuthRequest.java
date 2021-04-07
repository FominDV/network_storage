package ru.fomin.dto.requests;

import lombok.Value;

/**
 * DTO with command for authentication and registration.
 */
@Value
public class AuthRequest extends ChangePasswordRequest {

    String login;
    RequestType requestType;

    public AuthRequest(String password, String login, RequestType requestType) {
        super(password);
        this.login = login;
        this.requestType = requestType;
    }

    public enum RequestType {
        AUTH, REGISTRATION
    }

}