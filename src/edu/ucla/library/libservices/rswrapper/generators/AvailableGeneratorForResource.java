package edu.ucla.library.libservices.rswrapper.generators;

import java.util.Date;
import java.util.List;

import edu.ucla.library.libservices.rswrapper.beans.Availability;
import edu.ucla.library.libservices.rswrapper.beans.ClassroomLocation;
import edu.ucla.library.libservices.rswrapper.beans.Reservation;
import edu.ucla.library.libservices.rswrapper.roomsched.RoomSchedule;

public class AvailableGeneratorForResource
  extends ReservationsGeneratorForResource
{

  public Availability getAvailability( ClassroomLocation loc, String locale,
                                       Date startTime, Date endTime )
  {

    List<Reservation> reservations =
      this.getReservations( loc, startTime, endTime );
    RoomSchedule sched = new RoomSchedule( startTime, endTime, 1 );
    sched.populateHours( getMagidb(), locale );

    Reservation firstReservation = null;

    Date currentTime = sched.getBasetime();
    for ( Reservation r: reservations )
    {
      sched.addToSchedule( 0, r.getStart(), r.getEnd() );
      if ( currentTime.getTime() >= r.getStart().getTime() &&
           currentTime.getTime() <= r.getEnd().getTime() )
      {
        firstReservation = r;
      }
    }

    Date firstTime = sched.firstTimeRoomIsAvailable();

    Availability a = new Availability();
    if ( firstTime == null )
    {
      a.setAvailable( false );
      a.setAvailableLater( false );
    }
    else if ( firstTime.equals( currentTime ) )
    {
      a.setAvailable( true );
      a.setAvailableLater( true );
      a.setAvailableWhen( firstTime );
    }
    else
    {
      a.setAvailable( false );
      a.setAvailableLater( true );
      a.setAvailableWhen( firstTime );
      a.setReservation( firstReservation );
    }

    return a;
  }
}
