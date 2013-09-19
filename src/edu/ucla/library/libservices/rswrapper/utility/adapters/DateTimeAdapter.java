package edu.ucla.library.libservices.rswrapper.utility.adapters;

import edu.ucla.library.libservices.rswrapper.utility.converters.DateTimeConverter;

import java.util.Date;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class DateTimeAdapter
  extends XmlAdapter<String, Date>
{

  @Override
  public String marshal( Date v )
    throws Exception
  {
    return DateTimeConverter.createDateTimeFormat().format( v );
  }

  @Override
  public Date unmarshal( String v )
    throws Exception
  {
    return DateTimeConverter.createDateTimeFormat().parse( v );
  }

}
