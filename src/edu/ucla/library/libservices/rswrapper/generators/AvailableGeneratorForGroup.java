package edu.ucla.library.libservices.rswrapper.generators;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.ucla.library.libservices.rswrapper.beans.Availability;
import edu.ucla.library.libservices.rswrapper.beans.Reservation;
import edu.ucla.library.libservices.rswrapper.beans.ResourceAvailableTimes;
import edu.ucla.library.libservices.rswrapper.beans.StudyroomLocation;
import edu.ucla.library.libservices.rswrapper.db.mappers.ResourceAvailableTimeMapper;
import edu.ucla.library.libservices.rswrapper.roomsched.RoomSchedule;

public class AvailableGeneratorForGroup
  extends ReservationsGeneratorForGroup
{
  public Availability getGroupAvailability( //StudyroomLocation loc,
                                            String locale,
                                            Date startTime, Date endTime )
  {
    Map<String, List<Reservation>> mapOfReservations =
      this.getCurrentReservations( //loc,
        startTime, endTime );
    @SuppressWarnings( "unchecked" )
    List<ResourceAvailableTimes> roomAvailableTimes =
      this.getAllRooms( //loc,
        new ResourceAvailableTimeMapper() );

    // Generate a room schedule grid
    int numberOfRooms = roomAvailableTimes.size();
    RoomSchedule sched =
      new RoomSchedule( startTime, endTime, numberOfRooms );

    sched.populateHours( getMagidb(), locale );
    //sched.addHoursOfOperation(startTime, endTime);

    // For each room :
    int i = 0;
    for ( ResourceAvailableTimes r: roomAvailableTimes )
    {
      // If key exists in the map of reservations:
      if ( mapOfReservations.containsKey( r.getName() ) )
      {
        // Add each pair of (start, end) to the schedule
        for ( Reservation p: mapOfReservations.get( r.getName() ) )
        {
          sched.addToSchedule( i, p.getStart(), p.getEnd() );
        }
      }

      i++;
    }

    // Figure out the available time
    Date firstTime = sched.firstTimeRoomIsAvailable();
    Date currentTime = sched.getBasetime();

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
    }

    return a;
  }

  public List<Availability> getGroupAvailabilitiesPerResource( //StudyroomLocation loc,
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

    List<Availability> availabilities = new LinkedList<Availability>();

    // For each room :
    int i = 0;
    for ( ResourceAvailableTimes r: roomAvailableTimes )
    {
      String roomname = r.getName();

      // Generate a room schedule grid
      RoomSchedule sched = new RoomSchedule( startTime, endTime, 1 );
      sched.populateHours( getMagidb(), locale );
      //sched.addHoursOfOperation(startTime, endTime);

      Reservation firstReservation = null;
      Date currentTime = sched.getBasetime();
      // If key exists in the map of reservations:
      if ( mapOfReservations.containsKey( roomname ) )
      {
        // Add each pair of (start, end) to the schedule
        for ( Reservation p: mapOfReservations.get( roomname ) )
        {
          sched.addToSchedule( i, p.getStart(), p.getEnd() );
          if ( currentTime.getTime() >= p.getStart().getTime() &&
               currentTime.getTime() <= p.getEnd().getTime() )
          {
            firstReservation = p;
          }
        }
      }

      // Figure out the available time
      Date firstTime = sched.firstTimeRoomIsAvailable();

      Availability a = new Availability();
      a.setName( roomname );
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

      // Push to list
      availabilities.add( a );
    }

    return availabilities;
  }
}
