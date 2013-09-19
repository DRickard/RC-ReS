package edu.ucla.library.libservices.rswrapper.generators;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

//import edu.ucla.library.libservices.rswrapper.beans.BusinessRules;
//import edu.ucla.library.libservices.rswrapper.beans.StudyroomLocation;
import edu.ucla.library.libservices.rswrapper.beans.Reservation;
import edu.ucla.library.libservices.rswrapper.beans.Resource;
import edu.ucla.library.libservices.rswrapper.db.mappers.ResourceMapper;
import edu.ucla.library.libservices.rswrapper.roomsched.OpenRoom;
import edu.ucla.library.libservices.rswrapper.roomsched.RoomSchedule;
import edu.ucla.library.libservices.rswrapper.utility.constants.Constants;

//import org.springframework.jdbc.core.RowMapper;

public class AvailableRoomsInLocationGenerator
  extends ReservationsGeneratorForGroup
{
  public List<Reservation> getAvailableRoomsInLocationBeforeStart( Date startTime,
                                                                   int numHalfHours,
                                                                   String locale)
  {
    Calendar cal = Calendar.getInstance();
    cal.setTime( startTime );
    cal.add( Calendar.MINUTE, -1 * 10 * Constants.HALF_HOUR );
    List<Reservation> allReservations = new LinkedList<Reservation>();
    for ( int i = 0; i < 10; i++ )
    {
      Date time = cal.getTime();
      for ( Reservation theReservation:
            this.getAvailableRoomsInLocationAtStartTime( //br,
          //loc,
          time, numHalfHours, locale ) )
      {
        if ( !theReservation.isContainedIn( allReservations ) )
        {
          allReservations.add( theReservation );
        }
      }
      cal.add( Calendar.MINUTE, Constants.HALF_HOUR );
    }
    if ( allReservations.size() > 10 )
    {
      while ( allReservations.size() > 10 )
      {
        allReservations.remove( 0 );
      }
    }
    return allReservations;
  }


  public List<Reservation> getAvailableRoomsInLocationAfterStart( Date startTime,
                                                                  int numHalfHours,
                                                                  int span, String locale )
  {
    Calendar cal = Calendar.getInstance();
    cal.setTime( startTime );
    List<Reservation> allReservations = new LinkedList<Reservation>();
    //Go for span half hours. We include
    //i = span because we want the 5th hour
    for ( int i = 0; i <= span; i++ )
    {
      cal.add( Calendar.MINUTE, Constants.HALF_HOUR );
      

      Date time = cal.getTime();
      for ( Reservation res:
            this.getAvailableRoomsInLocationAtStartTime( time, numHalfHours, locale ) )
      {
        if ( !res.isContainedIn( allReservations ) )
          allReservations.add( res );
        if ( allReservations.size() >= 10 )
          return allReservations;
      }

    }
    return allReservations;
  }

  @SuppressWarnings( "unchecked" )
  public List<Reservation> getAvailableRoomsInLocationAtStartTime( //BusinessRules br,
    //StudyroomLocation loc,
    Date startTime, int numHalfHours, String locale )
  {
    Date endTime;
    Map<Integer, List<Resource>> roomsPerGroup;
    Map<String, List<Reservation>> currentReservations;
    List<Reservation> availableReservations;

    // Manually calculate the end time
    endTime = getEndTime( startTime, numHalfHours );

    // Split the list of resources by group into a map, where the group is
    /// represented by a integer key
    roomsPerGroup = new HashMap<Integer, List<Resource>>();
    if ( locale.equalsIgnoreCase( "yrl" ) )
    {
      for ( int theID: Constants.RC_GROUP_IDS )
      {
        roomsPerGroup.put( theID, new LinkedList<Resource>() );
        roomsPerGroup.get( theID ).addAll( getRoomsByGroup( theID,
                                                            new ResourceMapper() ) );
      }
    }
    else
    {
      for ( int theID: Constants.SR_GROUP_IDS )
      {
        roomsPerGroup.put( theID, new LinkedList<Resource>() );
        roomsPerGroup.get( theID ).addAll( getRoomsByGroup( theID,
                                                            new ResourceMapper() ) );
      }
    }

    // Get a list Of Reservations
    currentReservations = getCurrentReservations( startTime, endTime );

    // Create a empty list of available Reservations
    availableReservations = new LinkedList<Reservation>();
    
    if ( locale.equalsIgnoreCase( "yrl" ) )
    {
      for ( int groupId: Constants.RC_GROUP_IDS )
      {
        List<Resource> listOfResources;
        RoomSchedule schedule;

        // Retrieve list of resources from map
        listOfResources = roomsPerGroup.get( groupId );

        // Insert the hours of operation for the location
        schedule =
            new RoomSchedule( startTime, endTime, listOfResources.size() );
        schedule.populateHours( getMagidb(), locale );

        // Add each reservation to the grid
        int index = 0;
        for ( Resource resInList: listOfResources )
        {
          if ( currentReservations.containsKey( resInList.getDescription() ) )
          {
            List<Reservation> listOfResInRes =
              currentReservations.get( resInList.getDescription() );
            for ( Reservation theReservation: listOfResInRes )
            {
              schedule.addToSchedule( index, theReservation.getStart(),
                                      theReservation.getEnd() );
            }
          }
          index++;
        }

        // Maintain a list of reservations
        List<OpenRoom> rooms =
          schedule.generateAvailableRoomsAtStartTime( numHalfHours );
        for ( OpenRoom theRoom: rooms )
        {
          // Retrieve the corresponding resource
          Resource resource = listOfResources.get( theRoom.getRoomIndex() );

          // Form the reservation
          Reservation res = new Reservation();
          res.setStart( theRoom.getStart() );
          res.setEnd( theRoom.getEnd() );
          res.setDuration( ( theRoom.getEnd().getTime() -
                             theRoom.getStart().getTime() ) /
                           ( 60D * 60D * 1000D ) );
          res.addResource( resource );

          //if ( br.isItValid( res ) )
          {
            availableReservations.add( res );
          }
        }
      }
    }
    else
    {
      for ( int groupId: Constants.SR_GROUP_IDS )
      {
        List<Resource> listOfResources;
        RoomSchedule schedule;

        // Retrieve list of resources from map
        listOfResources = roomsPerGroup.get( groupId );

        // Insert the hours of operation for the location
        schedule =
            new RoomSchedule( startTime, endTime, listOfResources.size() );
        schedule.populateHours( getMagidb(), locale );

        // Add each reservation to the grid
        int index = 0;
        for ( Resource resInList: listOfResources )
        {
          if ( currentReservations.containsKey( resInList.getDescription() ) )
          {
            List<Reservation> listOfResInRes =
              currentReservations.get( resInList.getDescription() );
            for ( Reservation theReservation: listOfResInRes )
            {
              schedule.addToSchedule( index, theReservation.getStart(),
                                      theReservation.getEnd() );
            }
          }
          index++;
        }

        // Maintain a list of reservations
        List<OpenRoom> rooms =
          schedule.generateAvailableRoomsAtStartTime( numHalfHours );
        for ( OpenRoom theRoom: rooms )
        {
          // Retrieve the corresponding resource
          Resource resource = listOfResources.get( theRoom.getRoomIndex() );

          // Form the reservation
          Reservation res = new Reservation();
          res.setStart( theRoom.getStart() );
          res.setEnd( theRoom.getEnd() );
          res.setDuration( ( theRoom.getEnd().getTime() -
                             theRoom.getStart().getTime() ) /
                           ( 60D * 60D * 1000D ) );
          res.addResource( resource );

          //if ( br.isItValid( res ) )
          {
            availableReservations.add( res );
          }
        }
      }
    }

    return availableReservations;
  }

  private Date getEndTime( Date startTime, int numHalfHours )
  {
    Calendar cal = Calendar.getInstance();
    cal.setTime( startTime );
    cal.add( Calendar.MINUTE, numHalfHours * Constants.HALF_HOUR );
    return cal.getTime();
  }

  @SuppressWarnings( "unchecked" )
  public List<Reservation> getAvailableRoomsInLocationAtStartTimeMaxTime( 
    Date startTime, int maxMeetLength, String locale )
  {
    Date endTime;
    Map<Integer, List<Resource>> roomsPerGroup;
    Map<String, List<Reservation>> currentReservations;
    List<Reservation> availableReservations;

    // Split the list of resources by group into a map, where the group is
    /// represented by a integer key
    roomsPerGroup = new HashMap<Integer, List<Resource>>();
    if ( locale.equalsIgnoreCase( "yrl" ) )
    {
      for ( int theID: Constants.RC_GROUP_IDS )
      {
        roomsPerGroup.put( theID, new LinkedList<Resource>() );
        roomsPerGroup.get( theID ).addAll( getRoomsByGroup( theID,
                                                            new ResourceMapper() ) );
      }
    }
    else
    {
      for ( int theID: Constants.SR_GROUP_IDS )
      {
        roomsPerGroup.put( theID, new LinkedList<Resource>() );
        roomsPerGroup.get( theID ).addAll( getRoomsByGroup( theID,
                                                            new ResourceMapper() ) );
      }
    }
    
    // Get a list Of Reservations
    endTime = getEndTime( startTime, maxMeetLength );
    currentReservations = getCurrentReservations( startTime, endTime );

    // Create a empty list of available Reservations
    availableReservations = new LinkedList<Reservation>();

    if ( locale.equalsIgnoreCase( "yrl" ) )
    {
      for ( int groupId: Constants.RC_GROUP_IDS )
      {
        List<Resource> listOfResources;
        RoomSchedule schedule;
        int meetLength;
        List<Reservation> temp;

        // Retrieve list of resources from map
        listOfResources = roomsPerGroup.get( groupId );
        temp = new LinkedList<Reservation>();

        for ( meetLength = maxMeetLength;
              meetLength >= Constants.MIN_MEET && temp.size() == 0;
              meetLength-- )
        {
          // Manually calculate the end time
          endTime = getEndTime( startTime, meetLength );

          // Insert the hours of operation for the location
          schedule =
              new RoomSchedule( startTime, endTime, listOfResources.size() );
          schedule.populateHours( getMagidb(), locale );

          // Add each reservation to the grid
          int index = 0;
          for ( Resource resInList: listOfResources )
          {
            if ( currentReservations.containsKey( resInList.getDescription() ) )
            {
              List<Reservation> listOfResInRes =
                currentReservations.get( resInList.getDescription() );
              for ( Reservation theReservation: listOfResInRes )
              {
                schedule.addToSchedule( index, theReservation.getStart(),
                                        theReservation.getEnd() );
              }
            }
            index++;
          }
          // Maintain a list of reservations
          List<OpenRoom> rooms =
            schedule.generateAvailableRoomsAtStartTime( maxMeetLength );
          for ( OpenRoom theRoom: rooms )
          {
            if ( ( ( theRoom.getEnd().getTime() -
                               theRoom.getStart().getTime() ) /
                             ( 60D * 60D * 1000D ) ) >= 1.0D  )
            {
              // Retrieve the corresponding resource
              Resource resource = listOfResources.get( theRoom.getRoomIndex() );

              // Form the reservation
              Reservation res = new Reservation();
              res.setStart( theRoom.getStart() );
              res.setEnd( theRoom.getEnd() );
              res.addResource( resource );
              res.setDuration( ( theRoom.getEnd().getTime() -
                                 theRoom.getStart().getTime() ) /
                               ( 60D * 60D * 1000D ) );
              temp.add( res );
            }
          }
          
          availableReservations.addAll( temp );
        }

      }
    }
    else
    {
      for ( int groupId: Constants.SR_GROUP_IDS )
      {
        List<Resource> listOfResources;
        RoomSchedule schedule;
        int meetLength;
        List<Reservation> temp;

        // Retrieve list of resources from map
        listOfResources = roomsPerGroup.get( groupId );
        temp = new LinkedList<Reservation>();

        for ( meetLength = maxMeetLength;
              meetLength >= Constants.MIN_MEET && temp.size() == 0;
              meetLength-- )
        {
          // Manually calculate the end time
          endTime = getEndTime( startTime, meetLength );

          // Insert the hours of operation for the location
          schedule =
              new RoomSchedule( startTime, endTime, listOfResources.size() );
          schedule.populateHours( getMagidb(), locale );

          // Add each reservation to the grid
          int index = 0;
          for ( Resource resInList: listOfResources )
          {
            if ( currentReservations.containsKey( resInList.getDescription() ) )
            {
              List<Reservation> listOfResInRes =
                currentReservations.get( resInList.getDescription() );
              for ( Reservation theReservation: listOfResInRes )
              {
                schedule.addToSchedule( index, theReservation.getStart(),
                                        theReservation.getEnd() );
              }
            }
            index++;
          }
          // Maintain a list of reservations
          List<OpenRoom> rooms =
            schedule.generateAvailableRoomsAtStartTime( maxMeetLength );
          for ( OpenRoom theRoom: rooms )
          {
            if ( ( ( theRoom.getEnd().getTime() -
                               theRoom.getStart().getTime() ) /
                             ( 60D * 60D * 1000D ) ) >= 1.0D  )
            {
              // Retrieve the corresponding resource
              Resource resource = listOfResources.get( theRoom.getRoomIndex() );

              // Form the reservation
              Reservation res = new Reservation();
              res.setStart( theRoom.getStart() );
              res.setEnd( theRoom.getEnd() );
              res.addResource( resource );
              res.setDuration( ( theRoom.getEnd().getTime() -
                                 theRoom.getStart().getTime() ) /
                               ( 60D * 60D * 1000D ) );
              temp.add( res );
            }
          }
          
          availableReservations.addAll( temp );
        }

      }
    }

    return availableReservations;
  }

}