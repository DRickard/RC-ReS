package edu.ucla.library.libservices.rswrapper.generators;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.ucla.library.libservices.rswrapper.beans.Reservation;
import edu.ucla.library.libservices.rswrapper.beans.ResourceSchedule;
import edu.ucla.library.libservices.rswrapper.beans.StudyroomLocation;
import edu.ucla.library.libservices.rswrapper.db.mappers.ResourceScheduleMapper;

public class ScheduleGeneratorForGroup
  extends ReservationsGeneratorForGroup
{

  public List<ResourceSchedule> getResourceSchedules( StudyroomLocation loc,
                                                      Date startTime,
                                                      Date endTime )
  {

    // Example of an entry in map: "Group Study Room A" => [Reservation 1, Reservation 2]
    Map<String, List<Reservation>> mapOfReservations =
      this.getCurrentReservations( //loc,
        startTime, endTime );
    @SuppressWarnings( "unchecked" )
    List<ResourceSchedule> roomSchedules = this.getAllRooms( //loc,
        new ResourceScheduleMapper() );

    // For each room :
    for ( ResourceSchedule r: roomSchedules )
    {
      String roomname = r.getName();
      // If key exists
      if ( mapOfReservations.containsKey( roomname ) )
      {
        r.setSchedule( mapOfReservations.get( roomname ) );
      }
      else
      {
        r.setSchedule( new LinkedList<Reservation>() );
      }
    }

    return roomSchedules;
  }
}
