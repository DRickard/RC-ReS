package edu.ucla.library.libservices.rswrapper.beans;

import java.text.ParseException;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import javax.ws.rs.WebApplicationException;

import edu.ucla.library.libservices.rswrapper.utility.messages.ErrorResponse;
//import edu.ucla.library.libservices.rswrapper.utility.Utility;
import edu.ucla.library.libservices.rswrapper.db.source.ResourceScheduler;
import edu.ucla.library.libservices.rswrapper.utility.converters.DateTimeConverter;

public class BusinessRules
{
  private String uid;

  private int limitPerTimePeriod;
  private int unitOfTimeInDays;

  private Map<Integer, Integer> timeLimitInMinutesMap;
  private Map<Integer, Integer> maxAdvanceDaysMap;
  private boolean isStudyRoom;

  public BusinessRules()
  {
    super();
    this.timeLimitInMinutesMap = new HashMap<Integer, Integer>();
    this.maxAdvanceDaysMap = new HashMap<Integer, Integer>();
  }

  public boolean isItValid( Reservation r )
  {
    Date now = new Date();
    String datetime = DateTimeConverter.createDateTimeFormat().format( now );
    long difference = r.getEnd().getTime() - r.getStart().getTime();
    int minutes = ( int ) ( difference / ( 1000L * 60L ) );

    // Check through all resources of the reservation for resource/group specific rules
    for ( Resource resource: r.getResources() )
    {
      int idToCheck;
      if ( this.isStudyRoom() )
      {
        idToCheck = resource.getGroupId();
      }
      else
      {
        idToCheck = resource.getId();
      }

      // Check if it is above time limit
      if ( minutes > this.timeLimitInMinutesMap.get( idToCheck ) )
      {
        return false;
      }

      // Check if it is within max advance days
      Calendar cal = Calendar.getInstance();
      cal.setTime( now );
      cal.add( Calendar.DATE, this.maxAdvanceDaysMap.get( idToCheck ) );
      if ( r.getStart().getTime() > cal.getTime().getTime() )
      {
        return false;
      }

      // Check for other rules
      boolean result =
        this.getAvailabilityForResource( this.uid, this.limitPerTimePeriod,
                                         this.maxAdvanceDaysMap.get( idToCheck ),
                                         datetime );
      if ( !result )
      {
        return false;
      }
    }

    // No failures means
    return true;
  }

  public String getUid()
  {
    return uid;
  }

  public void setUid( String uid )
  {
    this.uid = uid;
  }

  public int getLimitPerTimePeriod()
  {
    return limitPerTimePeriod;
  }

  public void setLimitPerTimePeriod( int limitPerTimePeriod )
  {
    this.limitPerTimePeriod = limitPerTimePeriod;
  }

  public int getUnitOfTimeInDays()
  {
    return unitOfTimeInDays;
  }

  public void setUnitOfTimeInDays( int unitOfTimeInDays )
  {
    this.unitOfTimeInDays = unitOfTimeInDays;
  }

  public void addRules( int groupOrResourceId, int maxAdvanceDays,
                        int timeLimitInMinutes )
  {
    this.maxAdvanceDaysMap.put( groupOrResourceId, maxAdvanceDays );
    this.timeLimitInMinutesMap.put( groupOrResourceId,
                                    timeLimitInMinutes );
  }


  private boolean getAvailabilityForResource( String uid,
                                              int maxReservations,
                                              int numDays,
                                              String dateString )
  {
    // Invoke the generator
    ResourceScheduler rs = new ResourceScheduler();

    Date date;
    try
    {
      date = DateTimeConverter.createDateTimeFormat().parse( dateString );
    }
    catch ( ParseException e )
    {
      throw new WebApplicationException( ErrorResponse.INVALID_DATETIME_FORMAT );
    }

    Calendar cal = Calendar.getInstance();
    cal.setTime( date );
    cal.add( Calendar.DATE, numDays * -1 );
    Date start = cal.getTime();
    cal.add( Calendar.DATE, numDays * 2 );
    Date end = cal.getTime();

    List<Reservation> pairs =
      rs.getListOfStartEndPairsWithUidAndTime( uid, start, end );

    // Case 1 : if there are less than max reservations in the whole time duration
    if ( pairs.size() < maxReservations )
    {
      return true;
    }

    // Case 2 : Maintain a queue to see if there are too many reservations
    // in a window
    Queue<Reservation> queue = new LinkedList<Reservation>();
    for ( Reservation p: pairs )
    {
      while ( !queue.isEmpty() &&
              this.minuteDiffBetPairs( queue.peek(), p ) >=
              ( numDays * 24 * 60 ) )
      {
        queue.poll();
      }
      queue.add( p );
      if ( queue.size() == maxReservations )
      {
        return false;
      }
    }

    // If all is good, return true
    return true;
  }

  private int minuteDiffBetPairs( Reservation a, Reservation b )
  {
    long date1 = a.getStart().getTime();
    long date2 = b.getStart().getTime();

    long difference = Math.abs( date1 - date2 );

    // There are 1000 milliseconds in a second
    // There are 60 seconds in a minute
    return ( int ) ( difference / ( 1000L * 60L ) );
  }

  public boolean isStudyRoom()
  {
    return isStudyRoom;
  }

  public void setStudyRoom( boolean isStudyRoom )
  {
    this.isStudyRoom = isStudyRoom;
  }


}
