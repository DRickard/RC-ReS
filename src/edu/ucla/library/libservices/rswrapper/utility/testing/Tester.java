package edu.ucla.library.libservices.rswrapper.utility.testing;

import edu.ucla.library.libservices.rswrapper.beans.Reservation;
import edu.ucla.library.libservices.rswrapper.generators.AvailableRoomsInLocationGenerator;
import edu.ucla.library.libservices.rswrapper.utility.adapters.DateTimeAdapter;
import edu.ucla.library.libservices.rswrapper.utility.constants.Constants;
import edu.ucla.library.libservices.rswrapper.utility.converters.DateTimeConverter;
import edu.ucla.library.libservices.rswrapper.utility.db.Magi;

import edu.ucla.library.libservices.rswrapper.utility.messages.ErrorResponse;

import java.io.UnsupportedEncodingException;

import java.net.URLDecoder;

import java.net.URLEncoder;

import java.text.ParseException;

import java.util.Date;

import javax.ws.rs.WebApplicationException;
import java.util.List;

public class Tester
{

  public Tester()
  {
    super();
  }

  public static void main( String[] args )
    throws Exception
  {
    Magi magi = new Magi();
    magi.setDbName( "" );

    for ( Reservation s:
          magi.returnHoursOfOperation( Constants.SETHOURS_SR_ID,
                                        //Constants.SETOURS_RC_ID,
                                       // Constants.MAGI_RC_ID,
        new DateTimeAdapter().unmarshal( "2013-10-01 01:00 PM" ),
        new DateTimeAdapter().unmarshal( "2013-10-01 03:00 PM" ) ) )
    {
      System.out.println( "opens = " + s.getStart() + "\tcloses = " +
                          s.getEnd() );
    }

      AvailableRoomsInLocationGenerator generator;
      List<Reservation> rooms;
      Date start;
      
      try
      {
        start =
            DateTimeConverter.createDateTimeFormat().parse( URLDecoder.decode( 
            URLEncoder.encode("2013-10-01 01:00 PM", "utf-8"),
                                                                               "utf-8" ) );
      }
      catch ( ParseException e )
      {
        throw new WebApplicationException( ErrorResponse.INVALID_DATETIME_FORMAT );
      }
      catch ( UnsupportedEncodingException e )
      {
        throw new WebApplicationException( ErrorResponse.createServerError( e ) );
      }

      // Invoke the generator
      generator = new AvailableRoomsInLocationGenerator();
      generator.setDbName( "" );
      generator.setMagidb( "" ); // "datasource.magi" ) );
      rooms =
          generator.getAvailableRoomsInLocationAtStartTimeMaxTime( start, 4,
                                                                   "powell" );

      for ( Reservation theRoom: rooms )
      {
        System.out.println( "room " +
                            theRoom.getResources().get( 0 ).getHeaderTitle()
                            + "\tavailable at" + theRoom.getStart()
                            + "\tuntil " + theRoom.getEnd());
      }
  }
}
