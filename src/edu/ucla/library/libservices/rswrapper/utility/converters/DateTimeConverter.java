package edu.ucla.library.libservices.rswrapper.utility.converters;

import java.text.SimpleDateFormat;

public class DateTimeConverter
{
  public DateTimeConverter()
  {
    super();
  }

  public static SimpleDateFormat createTimeFormat()
  {
    return new SimpleDateFormat( "hh:mm a" );
  }

  public static SimpleDateFormat createDateFormat()
  {
    return new SimpleDateFormat( "yyyy-MM-dd" );
  }

  public static SimpleDateFormat createDateTimeFormat()
  {
    return new SimpleDateFormat( "yyyy-MM-dd hh:mm a" );
  }

  public static SimpleDateFormat createRSDateTimeFormat()
  {
    return new SimpleDateFormat( "yyyy-MM-dd HH:mm" );
  }
}
