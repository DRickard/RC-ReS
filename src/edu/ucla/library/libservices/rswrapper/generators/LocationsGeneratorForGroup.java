package edu.ucla.library.libservices.rswrapper.generators;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.ucla.library.libservices.rswrapper.beans.Reservation;
import edu.ucla.library.libservices.rswrapper.beans.ResourceAvailableTimes;
import edu.ucla.library.libservices.rswrapper.beans.StudyroomLocation;
import edu.ucla.library.libservices.rswrapper.db.mappers.ResourceAvailableTimeMapper;
import edu.ucla.library.libservices.rswrapper.roomsched.AvailableTime;
import edu.ucla.library.libservices.rswrapper.roomsched.RoomSchedule;

public class LocationsGeneratorForGroup
  extends ReservationsGeneratorForGroup
{
  public List<ResourceAvailableTimes> getRoomAvailableTimes( //StudyroomLocation loc,
                                                             String locale,
                                                             Date startTime,
                                                             Date endTime )
  {

    Map<String, List<Reservation>> mapOfReservations =
      this.getCurrentReservations( //loc,
        startTime, endTime );
    @SuppressWarnings( "unchecked" )
    List<ResourceAvailableTimes> roomAvailableTimes =
      this.getAllRooms( //loc,
        new ResourceAvailableTimeMapper() );

    // For each room :
    for ( ResourceAvailableTimes r: roomAvailableTimes )
    {
      // Generate a room schedule grid
      RoomSchedule sched = new RoomSchedule( startTime, endTime, 1 );
      sched.populateHours( getMagidb(), locale );
      //sched.addHoursOfOperation(startTime, endTime);

      // If key exists in the map of reservations:
      if ( mapOfReservations.containsKey( r.getName() ) )
      {
        // Add each pair of (start, end) to the schedule
        for ( Reservation p: mapOfReservations.get( r.getName() ) )
        {
          sched.addToSchedule( 0, p.getStart(), p.getEnd() );
        }
      }

      // Generate a list of availableTimes
      List<Reservation> availTimes = new LinkedList<Reservation>();
      for ( AvailableTime a: sched.generateAvailableTimes()[ 0 ] )
      {
        Reservation p = new Reservation();
        p.setStart( a.getStart() );
        p.setEnd( a.getEnd() );
        availTimes.add( p );
      }
      r.setAvailableTimes( availTimes );
    }

    return roomAvailableTimes;
  }
}
