package edu.ucla.library.libservices.rswrapper.roomsched;

import edu.ucla.library.libservices.rswrapper.utility.constants.Constants;

import java.util.Calendar;
import java.util.Date;

import java.text.SimpleDateFormat;

public class OpenRoom
{
  private Date time;
  private int numHalfHours;
  private int roomIndex;

  public OpenRoom()
  {
  }

  public OpenRoom( Date time, int numHalfHours, int roomIndex )
  {
    this.time = time;
    this.numHalfHours = numHalfHours;
    this.roomIndex = roomIndex;
  }

  public Date getTime()
  {
    return time;
  }

  public Date getStart()
  {
    return this.getTime();
  }

  public Date getEnd()
  {
    Calendar cal = Calendar.getInstance();
    cal.setTime( time );
    cal.add( Calendar.MINUTE, this.numHalfHours * Constants.HALF_HOUR );
    return cal.getTime();
  }

  public int getNumHalfHours()
  {
    return numHalfHours;
  }

  public int getRoomIndex()
  {
    return roomIndex;
  }

  @Override
  public String toString()
  {
    SimpleDateFormat sdf = new SimpleDateFormat( "MM-dd-yyyy hh:mm a" );
    return roomIndex + ": " + sdf.format( time.getTime() ) + " - " +
      ( numHalfHours * 30 );
  }

  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + numHalfHours;
    result = prime * result + roomIndex;
    result = prime * result + ( ( time == null ) ? 0: time.hashCode() );
    return result;
  }

  @Override
  public boolean equals( Object obj )
  {
    if ( this == obj )
      return true;
    if ( obj == null )
      return false;
    if ( getClass() != obj.getClass() )
      return false;
    OpenRoom other = ( OpenRoom ) obj;
    if ( numHalfHours != other.numHalfHours )
      return false;
    if ( roomIndex != other.roomIndex )
      return false;
    if ( time == null )
    {
      if ( other.time != null )
        return false;
    }
    else if ( !time.equals( other.time ) )
      return false;
    return true;
  }

}
