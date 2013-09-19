package edu.ucla.library.libservices.rswrapper.generators;

import javax.ws.rs.WebApplicationException;

import edu.ucla.library.libservices.rswrapper.utility.messages.ErrorResponse;
import edu.ucla.library.libservices.rswrapper.beans.Reservation;
import edu.ucla.library.libservices.rswrapper.db.source.ResourceScheduler;

public class GetReservationGenerator
  extends SoapGenerator
{
  public GetReservationGenerator()
  {
    super();
  }

  public Reservation get( int rid )
  {
    ResourceScheduler rs = new ResourceScheduler();
    Reservation r = rs.getReservation( rid );

    if ( r == null )
    {
      throw new WebApplicationException( ErrorResponse.RESERVATION_DNE );
    }

    return r;
  }
}
