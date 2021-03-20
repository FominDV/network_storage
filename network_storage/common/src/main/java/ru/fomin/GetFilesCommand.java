package ru.fomin;


import ru.fomin.need.commands.DataPackage;

import java.util.List;


public class GetFilesCommand
		extends DataPackage
{

  private final List<String> filenames;


  public GetFilesCommand(List<String> filenames)
  {
	this.filenames = filenames;
  }


  public List<String> getFileNames()
  {
	return filenames;
  }


}