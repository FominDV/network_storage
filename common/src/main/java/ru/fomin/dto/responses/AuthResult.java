package ru.fomin.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.fomin.dto.DataPackage;

/**
 * DTO with result of authentication or registration request.
 */
@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class AuthResult extends DataPackage {

    private final Result result;
    private String login;

    public enum Result {
        FAIL_AUTH, OK_AUTH, FAIL_REG, OK_REG
    }

}