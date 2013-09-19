package edu.ucla.library.libservices.rswrapper.roomsched;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Date;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

//import edu.ucla.library.libservices.rswrapper.beans.Location;
import edu.ucla.library.libservices.rswrapper.beans.Reservation;
import edu.ucla.library.libservices.rswrapper.utility.constants.Constants;
import edu.ucla.library.libservices.rswrapper.utility.db.Magi;

/**
 * @author	David Hwang
 * @since	2012-04-23
 */

public class RoomSchedule
{
  private RoomStatus schedule[][];
  private int numrooms;
  private int numentries;
  private Date basetime;
  private Date endtime;
  //private String dbName;

  /**
   * Constructor for StudyRoomSchedule.
   *
   * Constructs a StudyRoomSchedule, a wrapper for a data structure containing study room
   * related logic.
   * O(numrooms*entries)
   *
   * @param starttime			start time for schedule (start time should not be >= endtime)
   * @param endtime			end time for schedule
   * @param numroomsParam		number of rooms (must be greater than 1)
   */
  public RoomSchedule( Date startParam, Date endParam, int numroomsParam )
  {

    // Check if start > end and number of rooms is at least 1
    if ( startParam.getTime() > endParam.getTime() || numroomsParam <= 0 )
    {
      throw new IllegalArgumentException();
    }

    // Round times
    basetime = roundDownToHalfHour( startParam );
    endtime = roundUpToHalfHour( endParam );

    numentries =
        ( int ) ( ( endtime.getTime() - basetime.getTime() ) / ( 1000 *
                                                                 60 *
                                                                 30 ) );

    // Initialize memory for schedule
    numrooms = numroomsParam;
    schedule = new RoomStatus[ numrooms ][ numentries ];

    // Default status is CLOSED
    for ( int i = 0; i < numrooms; i++ )
    {
      for ( int j = 0; j < numentries; j++ )
      {
        schedule[ i ][ j ] = RoomStatus.CLOSED;
      }
    }
  }

  /**
   *
   * @param time	time to check
   * @param room	room to check
   * @return		status of room
   */
  public RoomStatus getStatus( Date time, int room )
  {
    if ( room < 0 || room >= this.numrooms || !this.inSchedule( time ) )
    {
      return RoomStatus.DOESNOTEXIST;
    }
    else
    {
      return this.schedule[ room ][ this.timeToIndex( time ) ];
    }
  }

  /**
   * For the given times, marks closed rooms as open.
   * O(numrooms*entries)
   *
   * @param from		opening time
   * @param until		closing time
   * @return			if parameters given are valid and is reserved successfully
   */
  public void addHoursOfOperation( Date from, Date until )
  {
    int lowerBound = this.timeToIndex( from );
    int upperBound = this.timeToIndex( this.roundUpToHalfHour( until ) );

    // Toggle to open
    for ( int i = 0; i < this.numrooms; i++ )
    {
      for ( int j = lowerBound; j < upperBound; j++ )
      {
        if ( schedule[ i ][ j ] == RoomStatus.CLOSED )
        {
          schedule[ i ][ j ] = RoomStatus.OPEN;
        }
      }
    }
  }

  /**
   * Toggles all rooms that are marked OPEN as CLOSED.  Keeps rooms that are RESERVED, RESERVED.
   * O(rooms*entries)
   */
  public void clearHoursOfOperation()
  {
    // Toggle open to close
    for ( int i = 0; i < this.numrooms; i++ )
    {
      for ( int j = 0; j < this.numentries; j++ )
      {
        if ( schedule[ i ][ j ] == RoomStatus.OPEN )
        {
          schedule[ i ][ j ] = RoomStatus.CLOSED;
        }
      }
    }
  }

  /**
   * Add to schedule.
   *
   * If already reserved, return false.  If end time less than start time return false
   * O(numentries)
   *
   * @param roomindex		room index starts at 0
   * @param from
   * @param until
   * @return				if the spot is open and is reserved successfully
   */
  public boolean addToSchedule( int roomindex, Date from, Date until )
  {
    // If room index does not exist
    if ( roomindex < 0 || roomindex >= this.numrooms )
    {
      return false;
    }

    // If end time is lesser than start time
    if ( from.getTime() >= until.getTime() )
    {
      return false;
    }

    // If times do not exist in schedule, round up/down the times
    if ( from.getTime() < this.basetime.getTime() )
    {
      from = this.basetime;
    }
    if ( until.getTime() > this.endtime.getTime() )
    {
      until = this.endtime;
    }

    int lowerBound = this.timeToIndex( from );
    int upperBound = this.timeToIndex( this.roundUpToHalfHour( until ) );

    // Check if any not open, otherwise return false
    for ( int j = lowerBound; j < upperBound; j++ )
    {
      if ( schedule[ roomindex ][ j ] != RoomStatus.OPEN )
      {
        return false;
      }
    }

    // Reserve the rooms on the specified index
    for ( int j = lowerBound; j < upperBound; j++ )
    {
      if ( schedule[ roomindex ][ j ] == RoomStatus.OPEN )
      {
        schedule[ roomindex ][ j ] = RoomStatus.RESERVED;
      }
    }

    return true;
  }

  /**
   * Returns Date object of the first time a room is open on the schedule.
   *
   * Returns Date object of first time available; if none is available, returns null.
   *
   * @return
   */
  public Date firstTimeRoomIsAvailable()
  {
    for ( int j = 0; j < this.numentries; j++ )
    {
      for ( int i = 0; i < this.numrooms; i++ )
      {
        // i is the index we're looking for
        if ( schedule[ i ][ j ] == RoomStatus.OPEN )
        {
          return this.indexToTime( j );
        }
      }
    }

    return null;
  }

  /**
   * Return array of lists of available times.
   *
   * @return
   */
  public List<AvailableTime>[] generateAvailableTimes()
  {
    @SuppressWarnings( "unchecked" )
    List<AvailableTime>[] list = new List[ this.numrooms ];

    for ( int i = 0; i < this.numrooms; i++ )
    {
      list[ i ] =
          new LinkedList<AvailableTime>(); // initialize list of available times
      RoomStatus current =
        RoomStatus.DOESNOTEXIST; // by default, nothing exists
      AvailableTime time = null;
      for ( int j = 0; j < this.numentries; j++ )
      {

        // If there's an opening and it was not open, time starts here
        if ( this.schedule[ i ][ j ] == RoomStatus.OPEN &&
             current != RoomStatus.OPEN )
        {
          time = new AvailableTime();
          time.setStart( this.indexToTime( j ) );
        }
        // If it's now not open, but it was open, time ends here and insert to list
        else if ( this.schedule[ i ][ j ] != RoomStatus.OPEN &&
                  current == RoomStatus.OPEN )
        {
          time.setEnd( this.indexToTime( j ) );
          list[ i ].add( time );
          time = null;
        }
        current = this.schedule[ i ][ j ];
      }

      // Case where last entry is also OPEN
      if ( current == RoomStatus.OPEN )
      {
        time.setEnd( this.endtime );
        list[ i ].add( time );
      }
    }

    return list;
  }

  /**
   * Return list of open study rooms, sorted by room number then time
   *
   * @param numHalfHours
   * @param from
   * @param until
   * @return
   */
  public List<OpenRoom> generateOpenStudyRoomsByDuration( int numHalfHours,
                                                          Date from,
                                                          Date until )
  {
    // Create initial empty set of rooms
    ArrayList<OpenRoom> listOfOpenStudyRooms = new ArrayList<OpenRoom>();

    // Return if numHalfHours is <= 0 or start time is greater than endtime
    if ( numHalfHours <= 0 || from.getTime() > until.getTime() )
    {
      return listOfOpenStudyRooms;
    }

    int continuousHalfHours = 0;
    int lowerBound = this.timeToIndex( from );
    int upperBound = this.timeToIndex( this.roundUpToHalfHour( until ) );

    // For each room
    for ( int i = 0; i < this.numrooms; i++ )
    {
      continuousHalfHours = 0;
      // Loop through schedule
      for ( int j = lowerBound; j < upperBound; j++ )
      {
        if ( this.schedule[ i ][ j ] == RoomStatus.OPEN )
        {
          // Increment
          continuousHalfHours++;
          // If a match, add to the list
          if ( continuousHalfHours >= numHalfHours )
          {
            listOfOpenStudyRooms.add( new OpenRoom( this.indexToTime( j -
                                                                      numHalfHours +
                                                                      1 ),
                                                    numHalfHours, i ) );
          }
        }
        else
        {
          continuousHalfHours = 0;
        }
      }
    }

    return listOfOpenStudyRooms;
  }

  /**
   *
   * Return a list of open study rooms, *exactly* at the starting time.
   * The maximum number of half hours of returned study rooms will be numHalfHours
   * The minimum is 1.
   *
   * @param numHalfHours
   * @return
   */
  public List<OpenRoom> generateAvailableRoomsAtStartTime( int numHalfHours )
  {
    LinkedList<OpenRoom> listOfOpenStudyRooms = new LinkedList<OpenRoom>();

    // Has to be at least one entry
    if ( this.numentries < 1 )
    {
      return listOfOpenStudyRooms;
    }

    int lowerBound = 0;
    int upperBound = numHalfHours;

    // For each room
    for ( int i = 0; i < this.numrooms; i++ )
    {
      // If the first time slot is occupied
      if ( this.schedule[ i ][ lowerBound ] == RoomStatus.OPEN )
      {
        int continuousHalfHours = 0;
        // Check how long reservation could be
        for ( int j = lowerBound; j < upperBound; j++ )
        {
          if ( this.schedule[ i ][ j ] == RoomStatus.OPEN )
          {
            continuousHalfHours++;
          }
          else
          {
            break;
          }
        }
        listOfOpenStudyRooms.add( new OpenRoom( this.indexToTime( lowerBound ),
                                                continuousHalfHours, i ) );
      }
    }

    return listOfOpenStudyRooms;
  }

  /**
   * Returns the beginning time of the schedule.
   *
   * @return
   */
  public Date getBasetime()
  {
    return this.basetime;
  }

  /**
   * Returns the end of the schedule
   *
   * @return
   */
  public Date getEndTime()
  {
    return this.endtime;
  }

  /**
   * Getter for number of rooms the schedule keeps track of
   *
   * @return
   */
  public int getNumrooms()
  {
    return numrooms;
  }

  /**
   * Add hours to the schedule based on locationId
   * @throws IOException
   */
  public void populateHours( //Location loc,
    String dbName, String locale )
  {
    Magi magi = new Magi();
    magi.setDbName( dbName );

    for ( Reservation s:
          magi.returnHoursOfOperation( ( locale.equalsIgnoreCase( "yrl" ) ?
                                         Constants.SETOURS_RC_ID:
                                         Constants.SETOURS_SR_ID ),
                                       // Constants.MAGI_RC_ID,
        this.basetime, this.endtime ) )
    {
      this.addHoursOfOperation( s.getStart(), s.getEnd() );
    }
  }

  private Date indexToTime( int i )
    throws ArrayIndexOutOfBoundsException
  {
    if ( i < 0 || i >= numentries )
    {
      throw new ArrayIndexOutOfBoundsException();
    }

    long milliseconds = this.basetime.getTime();
    long newtime = milliseconds + ( 1000 * 60 * 30 ) * i;

    return new Date( newtime );
  }

  private boolean inSchedule( Date time )
  {
    long start = this.basetime.getTime();
    long end = this.endtime.getTime();

    if ( time.getTime() < start || time.getTime() >= end )
    {
      return false;
    }
    else
    {
      return true;
    }
  }


  // Be careful that if time exceeds what the schedule, index can go up to the max which would be out of bounds

  private int timeToIndex( Date time )
  {
    Date roundedtime = roundDownToHalfHour( time );

    long remainder = roundedtime.getTime() - this.basetime.getTime();
    // If inputed time is less than base time, return index 0
    if ( remainder < 0 )
    {
      return 0;
    }

    // If inputed time is larger than base time + hours in schedule
    // 1000 milliseconds * 60 seconds * 30 minutes in a half hour
    long difference = remainder / ( 1000 * 60 * 30 );
    if ( difference >= numentries )
    {
      return this.numentries;
    }

    return ( int ) difference;
  }

  private Date roundDownToHalfHour( Date date )
  {
    Calendar cal = Calendar.getInstance();
    cal.setTime( date );
    cal.set( Calendar.MILLISECOND, 0 );
    cal.set( Calendar.SECOND, 0 );
    if ( cal.get( Calendar.MINUTE ) < 30 )
    {
      cal.set( Calendar.MINUTE, 0 );
    }
    else
    {
      cal.set( Calendar.MINUTE, 30 );
    }

    return cal.getTime();
  }

  private Date roundUpToHalfHour( Date date )
  {
    // Do not do anything if already at 00 or 30
    Calendar cal = Calendar.getInstance();
    cal.setTime( date );
    if ( ( cal.get( Calendar.MINUTE ) == 30 ||
           cal.get( Calendar.MINUTE ) == 0 ) &&
         cal.get( Calendar.SECOND ) == 0 &&
         cal.get( Calendar.MILLISECOND ) == 0 )
    {

      return date;
    }

    // Round down then add 30 minutes
    Date roundedDown = this.roundDownToHalfHour( date );
    cal.setTime( roundedDown );
    cal.add( Calendar.MINUTE, 30 );
    return cal.getTime();
  }

  /*public void setDbName( String dbName )
  {
    this.dbName = dbName;
  }

  private String getDbName()
  {
    return dbName;
  }*/
}
