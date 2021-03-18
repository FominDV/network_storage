package ru.fomin;


import static ru.fomin.AuthResult.Result.FAIL;
import static ru.fomin.AuthResult.Result.OK;


public class AuthResult
		extends DataPackage
{

  private final Result result;


  private AuthResult(Result result)
  {
	this.result = result;
  }


  public Result getResult()
  {
	return result;
  }


  public static AuthResult ok()
  {
	return new AuthResult(OK);
  }


  public static AuthResult fail()
  {
	return new AuthResult(FAIL);
  }


  public enum Result
  {
	FAIL,
	OK
  }

}