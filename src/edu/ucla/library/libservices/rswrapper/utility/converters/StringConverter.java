package edu.ucla.library.libservices.rswrapper.utility.converters;

public class StringConverter
{
  public StringConverter()
  {
    super();
  }

  public static String convertQuestionMarks(int length)
  {
    // Should look like (?,?,?)
    StringBuilder groupIdString = new StringBuilder("(");
    String delim = "";
    for (int i = 0; i < length; i++) {
      groupIdString.append(delim);
      groupIdString.append('?');
      delim = ",";
    }
    groupIdString.append(")");
    
    return groupIdString.toString();
  }
}
