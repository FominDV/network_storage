package ru.fomin.commands;


import java.io.Serializable;


public abstract class DataPackage
		implements Serializable
{

  @Override
  public String toString()
  {
	String className = getClass().getSimpleName();
	return className;
  }

}