package edu.ucla.library.libservices.rswrapper.utility.testing;

import edu.ucla.library.libservices.rswrapper.beans.Reservation;
import edu.ucla.library.libservices.rswrapper.utility.adapters.DateTimeAdapter;
import edu.ucla.library.libservices.rswrapper.utility.constants.Constants;
import edu.ucla.library.libservices.rswrapper.utility.db.Magi;

import java.util.Date;

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
          magi.returnHoursOfOperation( Constants.SETOURS_RC_ID,
                                       // Constants.MAGI_RC_ID,
        new DateTimeAdapter().unmarshal( "2013-10-01 01:00 PM" ),
        new DateTimeAdapter().unmarshal( "2013-10-01 03:00 PM" ) ) )
    {
      System.out.println( "opens = " + s.getStart() + "\tcloses = " +
                          s.getEnd() );
    }
  }
}
