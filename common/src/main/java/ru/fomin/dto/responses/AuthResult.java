package ru.fomin.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.fomin.dto.DataPackage;
import ru.fomin.enumeration.AuthAndRegResult;

/**
 * DTO with authAndRegResult of authentication or registration request.
 */
@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class AuthResult extends DataPackage {

    private final AuthAndRegResult authAndRegResult;
    private String login;

}