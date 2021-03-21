package ru.fomin.need.commands;


import static ru.fomin.need.commands.AuthResult.Result.FAIL_AUTH;
import static ru.fomin.need.commands.AuthResult.Result.OK_AUTH;


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