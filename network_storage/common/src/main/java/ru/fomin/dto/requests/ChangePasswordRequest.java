package ru.fomin.dto.requests;

import lombok.Value;

/**
 * DTO for changing password.
 */
@Value
public class ChangePasswordRequest extends BasePasswordRequest{

    String currentPassword;

    public ChangePasswordRequest(String password, String currentPassword) {
        super(password);
        this.currentPassword = currentPassword;
    }
}
