package edu.ucla.library.libservices.rswrapper.beans;

//import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
//import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

//import edu.ucla.library.libservices.rswrapper.utility.adapters.DateTimeAdapter;

@XmlType(name = "reservation")
@XmlAccessorType(XmlAccessType.FIELD)
public class SimpleReservation
{
  @XmlElement( name = "schedID" )
  private int schedID;
  @XmlElement( name = "roomName" )
  private String roomName;
  @XmlElement( name = "startDate" )
  //@XmlJavaTypeAdapter( DateTimeAdapter.class )
  private String startDate;
  @XmlElement( name = "endDate" )
  //@XmlJavaTypeAdapter( DateTimeAdapter.class )
  private String endDate;
  @XmlElement( name = "title" )
  private String title;
  
  public SimpleReservation()
  {
    super();
  }

  public void setSchedID( int schedID )
  {
    this.schedID = schedID;
  }

  public int getSchedID()
  {
    return schedID;
  }

  public void setRoomName( String roomName )
  {
    this.roomName = roomName;
  }

  public String getRoomName()
  {
    return roomName;
  }

  public void setStartDate( String startDate )
  {
    this.startDate = startDate;
  }

  public String getStartDate()
  {
    return startDate;
  }

  public void setEndDate( String endDate )
  {
    this.endDate = endDate;
  }

  public String getEndDate()
  {
    return endDate;
  }

  public void setTitle( String title )
  {
    this.title = title;
  }

  public String getTitle()
  {
    return title;
  }
}
