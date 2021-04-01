package ru.fomin.dto;


import lombok.ToString;

import java.io.Serializable;

/**
 * Base class for all DTO.
 */
public abstract class DataPackage implements Serializable
{

  @Override
  public String toString()
  {
	String className = getClass().getSimpleName();
	return className;
  }

}