package ru.fomin.commands;


public class AuthRequest
		extends DataPackage
{

    private final String login;
  private final String password;
  private final RequestType requestType;


  public AuthRequest(String login, String password, RequestType requestType) {
	this.login = login;
	this.password = password;
	this.requestType=requestType;
  }

 public enum RequestType{
      AUTH, REGISTRATION
  }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public RequestType getRequestType() {
        return requestType;
    }
}