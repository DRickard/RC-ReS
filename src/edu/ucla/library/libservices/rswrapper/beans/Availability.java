package edu.ucla.library.libservices.rswrapper.beans;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import edu.ucla.library.libservices.rswrapper.utility.adapters.DateTimeAdapter;

@XmlType
@XmlAccessorType( XmlAccessType.FIELD )
public class Availability
{

  public Availability()
  {
    super();
  }

  @XmlElement( name = "name" )
  String name;

  @XmlElement( name = "available" )
  boolean available;

  @XmlElement( name = "availableLater" )
  boolean availableLater;

  @XmlElement( name = "availableWhen" )
  @XmlJavaTypeAdapter( DateTimeAdapter.class )
  Date availableWhen;

  @XmlElement( name = "currentReservation" )
  Reservation reservation;

  public String getName()
  {
    return name;
  }

  public void setName( String name )
  {
    this.name = name;
  }

  public boolean isAvailable()
  {
    return available;
  }

  public void setAvailable( boolean available )
  {
    this.available = available;
  }

  public boolean isAvailableLater()
  {
    return availableLater;
  }

  public void setAvailableLater( boolean availableLater )
  {
    this.availableLater = availableLater;
  }

  public Date getAvailableWhen()
  {
    return availableWhen;
  }

  public void setAvailableWhen( Date availableWhen )
  {
    this.availableWhen = availableWhen;
  }

  public Reservation getReservation()
  {
    return reservation;
  }

  public void setReservation( Reservation reservation )
  {
    this.reservation = reservation;
  }
}
