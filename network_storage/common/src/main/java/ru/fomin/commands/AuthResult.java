package ru.fomin.commands;

/**
 * DTO with result of authentication or registration request.
 */
public class AuthResult
        extends DataPackage {

    private final Result result;
    private String login;

    public AuthResult(Result result) {
        this.result = result;
    }

    public AuthResult(Result result, String login) {
        this(result);
        this.login = login;
    }

    public enum Result {
        FAIL_AUTH, OK_AUTH, FAIL_REG, OK_REG
    }

    public Result getResult() {
        return result;
    }

    public String getLogin() {
        return login;
    }
}