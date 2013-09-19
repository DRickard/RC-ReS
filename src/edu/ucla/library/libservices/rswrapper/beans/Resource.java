package edu.ucla.library.libservices.rswrapper.beans;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.codehaus.jackson.map.annotate.JsonView;

@XmlType
@XmlAccessorType( XmlAccessType.FIELD )
public class Resource
{
  @JsonView( ReservationViews.AvailableSlot.class )
  @XmlElement( name = "id" )
  private int id;

  @JsonView( ReservationViews.FullReservation.class )
  @XmlElement( name = "groupId" )
  private int groupId;

  @JsonView( ReservationViews.FullReservation.class )
  @XmlElement( name = "resourceTypeId" )
  private int resourceTypeId;

  @JsonView( ReservationViews.AvailableSlot.class )
  @XmlElement( name = "description" )
  private String description;

  @JsonView( ReservationViews.AvailableSlot.class )
  @XmlElement( name = "title" )
  private String headerTitle;

  @JsonView( ReservationViews.AvailableSlot.class )
  @XmlElement( name = "capacity" )
  private int capacity;

  @JsonView( ReservationViews.AvailableSlot.class )
  @XmlElement( name = "smallImage" )
  private String smallImage;

  @JsonView( ReservationViews.AvailableSlot.class )
  @XmlElement( name = "bigImage" )
  private String bigImage;

  public Resource()
  {
    super();
  }

  public Resource( int id )
  {
    this();
    this.id = id;
  }

  public Resource( int id, String description )
  {
    this();
    this.id = id;
    this.description = description;
  }

  public Resource( int id, int groupId, int resourceTypeId, int capacity,
                   String description, String headerTitle )
  {
    this();
    this.id = id;
    this.description = description;
    this.capacity = capacity;
    this.headerTitle = headerTitle;
    this.resourceTypeId = resourceTypeId;
    this.groupId = groupId;
  }

  public int getId()
  {
    return id;
  }

  public void setId( int id )
  {
    this.id = id;
  }

  public int getGroupId()
  {
    return groupId;
  }

  public void setGroupId( int groupId )
  {
    this.groupId = groupId;
  }

  public int getResourceTypeId()
  {
    return resourceTypeId;
  }

  public void setResourceTypeId( int resourceTypeId )
  {
    this.resourceTypeId = resourceTypeId;
  }

  public String getDescription()
  {
    return description;
  }

  public void setDescription( String description )
  {
    this.description = description;
  }

  public String getHeaderTitle()
  {
    return headerTitle;
  }

  public void setHeaderTitle( String title )
  {
    this.headerTitle = title;
  }

  public int getCapacity()
  {
    return capacity;
  }

  public void setCapacity( int capacity )
  {
    this.capacity = capacity;
  }

  public void setSmallImage( String smallImage )
  {
    this.smallImage = smallImage;
  }

  public String getSmallImage()
  {
    return smallImage;
  }

  public void setBigImage( String bigImage )
  {
    this.bigImage = bigImage;
  }

  public String getBigImage()
  {
    return bigImage;
  }
}
