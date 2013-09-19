package edu.ucla.library.libservices.rswrapper.beans;

import java.text.ParseException;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonView;

import webservice.resourcescheduler.peoplecube.ResourceSchedulerServiceStub.ArrayOfReservationResourceData;
import webservice.resourcescheduler.peoplecube.ResourceSchedulerServiceStub.ArrayOfReservationResourceUdfData;
import webservice.resourcescheduler.peoplecube.ResourceSchedulerServiceStub.ReservationBaseData;
import webservice.resourcescheduler.peoplecube.ResourceSchedulerServiceStub.ReservationData;
import webservice.resourcescheduler.peoplecube.ResourceSchedulerServiceStub.ReservationResourceData;
import webservice.resourcescheduler.peoplecube.ResourceSchedulerServiceStub.ReservationResourceUdfData;
import webservice.resourcescheduler.peoplecube.ResourceSchedulerServiceStub.ScheduleData;
import webservice.resourcescheduler.peoplecube.ResourceSchedulerServiceStub.ScheduleDisplay;

import edu.ucla.library.libservices.rswrapper.utility.adapters.DateTimeAdapter;
//import edu.ucla.library.libservices.rswrapper.utility.Utility;
import edu.ucla.library.libservices.rswrapper.utility.converters.DateTimeConverter;

//import java.util.concurrent.TimeUnit;

@XmlType
@XmlAccessorType( XmlAccessType.FIELD )
public class Reservation
{

  @JsonIgnore
  private final String RS_TIME_ZONE_ID = "Pacific Standard Time";

  public Reservation()
  {
    super();
    this.id = 0;
    this.resources = new LinkedList<Resource>();
  }

  public Reservation( ReservationData reservationData )
  {
    //// If data is not valid, return null
    //if (!reservationData.getIsValid()) {
    //	return null;
    //}
    this();

    this.setNumberOfAttendees( reservationData.getNumberOfAttendees() );

    ReservationBaseData baseData =
      reservationData.getReservationBaseData();
    this.setId( baseData.getId() );
    this.setDescription( baseData.getDescription() );
    this.setPending( baseData.getIsPending() );

    // Fill in the resources
    ArrayOfReservationResourceData resources = baseData.getResources();
    if ( resources.isReservationResourceDataSpecified() == true )
    {
      for ( ReservationResourceData rd:
            resources.getReservationResourceData() )
      {
        Resource resource = new Resource();
        // This only has description and id
        resource.setDescription( description );
        resource.setId( rd.getId() );
        this.resources.add( resource );
      }
    }

    // Fill in the times
    Date start;
    try
    {
      start =
          DateTimeConverter.createRSDateTimeFormat().parse( reservationData.getScheduleData().getStart() );
    }
    catch ( ParseException e )
    {
      return;
    }
    this.setStart( start );
    long endTime =
      start.getTime() + ( reservationData.getScheduleData().getDuration() /
                          10000L );
    this.setEnd( new Date( endTime ) );

    // Extract the udfs
    Map<Integer, String> mapOfUdfs = new HashMap<Integer, String>();
    ReservationResourceUdfData[] udfs =
      reservationData.getUserDefinedFields().getReservationResourceUdfData();
    for ( ReservationResourceUdfData d: udfs )
    {
      mapOfUdfs.put( d.getId(), d.getValue() );
    }
    this.setUdfs( mapOfUdfs );
  }

  public ReservationData getReservationData()
  {
    ReservationData r = new ReservationData();

    Date startDate = this.getStart();
    Date endDate = this.getEnd();
    long duration = 0;

    String start;
    String end;
    if ( startDate == null || endDate == null )
    {
      start = "";
      end = "";
    }
    else
    {
      start =
          DateTimeConverter.createRSDateTimeFormat().format( startDate );
      end = DateTimeConverter.createRSDateTimeFormat().format( endDate );
      duration =
          ( this.getEnd().getTime() - this.getStart().getTime() ) * 10000L;
    }

    ScheduleDisplay scheduleDisplayData = new ScheduleDisplay();
    scheduleDisplayData.setStart( start );
    scheduleDisplayData.setDuration( duration );
    scheduleDisplayData.setTimeZoneId( this.RS_TIME_ZONE_ID );

    ArrayOfReservationResourceData arrOfResResData =
      new ArrayOfReservationResourceData();
    if ( this.resources != null )
    {
      for ( Resource x: resources )
      {
        ReservationResourceData resresData = new ReservationResourceData();
        resresData.setId( x.getId() );
        resresData.setIsDeleted( false );
        resresData.setScheduleDataDisplay( scheduleDisplayData );
        resresData.setSetupId( 0 );
        resresData.setComboId( 0 );
        resresData.setIsValid( true );
        arrOfResResData.addReservationResourceData( resresData );
      }
    }

    ReservationBaseData baseData = new ReservationBaseData();
    baseData.setId( this.getId() );
    baseData.setDescription( this.getDescription() );
    baseData.setIsPrivate( false );
    baseData.setIsPending( this.isPending() );
    baseData.setIsDeleted( false );
    baseData.setLocationId( 0 );
    baseData.setResources( arrOfResResData );
    baseData.setCreatedByUserId( 71 ); // TODO: find out what 73 is
    baseData.setCreatedForUserId( 0 ); // TODO: find out what 0  is
    baseData.setIsValid( true );

    ScheduleData scheduleData = new ScheduleData();
    scheduleData.setStart( start );
    scheduleData.setEnd( end );
    scheduleData.setStartAdjusted( start );
    scheduleData.setEndAdjusted( end );
    scheduleData.setDuration( duration );
    scheduleData.setTimeZoneId( this.RS_TIME_ZONE_ID );
    scheduleData.setIsValid( true );

    r.setReservationBaseData( baseData );
    r.setScheduleData( scheduleData );
    r.setUserDefinedFields( this.retrieveUdfs() );
    r.setNumberOfAttendees( this.getNumberOfAttendees() );
    //r.setCheckedInBy(73);
    //r.setCheckedOutBy(73);
    r.setIsValid( true );

    return r;
  }

  private ArrayOfReservationResourceUdfData retrieveUdfs()
  {

    ArrayOfReservationResourceUdfData ad =
      new ArrayOfReservationResourceUdfData();

    Map<Integer, String> udfs = new HashMap<Integer, String>();

    if ( this.getAcademicDept() != null )
      udfs.put( 122, this.getAcademicDept() );
    if ( this.getAcademicDeptOther() != null )
      udfs.put( 123, this.getAcademicDeptOther() );
    if ( this.getCourseLevel() != null )
      udfs.put( 124, this.getCourseLevel() );
    if ( this.getFirstName() != null )
      udfs.put( 125, this.getFirstName() );
    if ( this.getEmail() != null )
      udfs.put( 126, this.getEmail() );
    if ( this.getPhone() != null )
      udfs.put( 127, this.getPhone() );
    if ( this.getLastName() != null )
      udfs.put( 128, this.getLastName() );
    if ( this.getInternalNotes() != null )
      udfs.put( 131, this.getInternalNotes() );
    if ( this.getUid() != null )
      udfs.put( 153, this.getUid() );
    if ( this.getSrs() != null )
      udfs.put( 154, this.getSrs() );
    if ( this.getDivision() != null )
      udfs.put( 164, this.getDivision() );
    if ( this.getClaimed() != null )
      udfs.put( 166, this.getClaimed() );
    if ( this.getComputerUse() != null )
      udfs.put( 169, this.getComputerUse() );

    for ( Integer i: udfs.keySet() )
    {
      ReservationResourceUdfData d = new ReservationResourceUdfData();
      d.setId( i );
      d.setValue( udfs.get( i ) );
      ad.addReservationResourceUdfData( d );
    }

    return ad;
  }

  // Sets the non-null fields of r to the object (cannot edit reservation id)

  public void updateFields( Reservation r )
  {

    if ( r.getDescription() != null )
      this.setDescription( r.getDescription() );
    if ( r.isPendingValid() )
      this.setPending( r.isPending() );
    if ( r.getNumberOfAttendees() != 0 )
      this.setNumberOfAttendees( r.getNumberOfAttendees() );

    // udfs
    if ( r.getAcademicDept() != null )
      this.setAcademicDept( r.getAcademicDept() );
    if ( r.getAcademicDeptOther() != null )
      this.setAcademicDeptOther( r.getAcademicDeptOther() );
    if ( r.getCourseLevel() != null )
      this.setCourseLevel( r.getCourseLevel() );
    if ( r.getFirstName() != null )
      this.setFirstName( r.getFirstName() );
    if ( r.getEmail() != null )
      this.setEmail( r.getEmail() );
    if ( r.getPhone() != null )
      this.setPhone( r.getPhone() );
    if ( r.getLastName() != null )
      this.setLastName( r.getLastName() );
    if ( r.getInternalNotes() != null )
      this.setInternalNotes( r.getInternalNotes() );
    if ( r.getUid() != null )
      this.setUid( r.getUid() );
    if ( r.getSrs() != null )
      this.setSrs( r.getSrs() );
    if ( r.getDivision() != null )
      this.setDivision( r.getDivision() );
    if ( r.getClaimed() != null )
      this.setClaimed( r.getClaimed() );
    if ( r.getComputerUse() != null )
      this.setComputerUse( r.getComputerUse() );
  }

  public void setUdfs( Map<Integer, String> udfs )
  {
    for ( Integer i: udfs.keySet() )
    {
      String value = udfs.get( i );
      switch ( i )
      {
        case 122: // academic dept
          this.setAcademicDept( value );
          break;
        case 123: // academic dept other
          this.setAcademicDeptOther( value );
          break;
        case 124: // course level
          this.setCourseLevel( value );
          break;
        case 125: // first name
          this.setFirstName( value );
          break;
        case 126: // email
          this.setEmail( value );
          break;
        case 127: // contact phone
          this.setPhone( value );
          break;
        case 128: // last name
          this.setLastName( value );
          break;
        case 131: // internal notes
          this.setInternalNotes( value );
          break;
        case 153: // uid
          this.setUid( value );
          break;
        case 154: // srs
          this.setSrs( value );
          break;
        case 164: // division
          this.setDivision( value );
          break;
        case 166: // claimed
          this.setClaimed( value );
          break;
        case 169: // computer use
          this.setComputerUse( value );
          break;
      }
    }
    return;
  }

  @XmlElement( name = "id" )
  @JsonView( ReservationViews.BasicReservation.class )
  protected int id;

  @XmlElement( name = "description" )
  @JsonView( ReservationViews.BasicReservation.class )
  protected String description;

  @XmlElement( name = "start" )
  @XmlJavaTypeAdapter( DateTimeAdapter.class )
  @JsonView( ReservationViews.StartEndPair.class )
  protected Date start;

  @XmlElement( name = "end" )
  @XmlJavaTypeAdapter( DateTimeAdapter.class )
  @JsonView( ReservationViews.StartEndPair.class )
  protected Date end;

  @XmlElement( name = "pending" )
  @JsonView( ReservationViews.FullReservation.class )
  protected Boolean pending; // made this a class to allow null values

  public boolean isPendingValid()
  {
    return this.pending != null;
  }

  @XmlElement( name = "numberOfAttendees" )
  @JsonView( ReservationViews.FullReservation.class )
  protected int numberOfAttendees;

  // resources
  @XmlElement( name = "resources" )
  @JsonView( ReservationViews.AvailableSlot.class )
  private List<Resource> resources;

  // udfs

  @XmlElement( name = "firstName" )
  @JsonView( ReservationViews.FullReservation.class )
  private String firstName;

  @XmlElement( name = "lastName" )
  @JsonView( ReservationViews.BasicReservation.class )
  private String lastName;

  @XmlElement( name = "email" )
  @JsonView( ReservationViews.FullReservation.class )
  private String email;

  @XmlElement( name = "phone" )
  @JsonView( ReservationViews.FullReservation.class )
  private String phone;

  @XmlElement( name = "academicDept" )
  @JsonView( ReservationViews.FullReservation.class )
  private String academicDept;

  @XmlElement( name = "academicDeptOther" )
  @JsonView( ReservationViews.FullReservation.class )
  private String academicDeptOther;

  @XmlElement( name = "division" )
  @JsonView( ReservationViews.FullReservation.class )
  private String division;

  @XmlElement( name = "courseLevel" )
  @JsonView( ReservationViews.FullReservation.class )
  private String courseLevel;

  @XmlElement( name = "srs" )
  @JsonView( ReservationViews.FullReservation.class )
  private String srs;

  @XmlElement( name = "computerUse" )
  @JsonView( ReservationViews.FullReservation.class )
  private String computerUse;

  @XmlElement( name = "internalNotes" )
  @JsonView( ReservationViews.FullReservation.class )
  private String internalNotes;

  @XmlElement( name = "uid" )
  @JsonView( ReservationViews.FullReservation.class )
  private String uid;

  @XmlElement( name = "claimed" )
  @JsonView( ReservationViews.FullReservation.class )
  private String claimed;

  @XmlElement( name = "duration" )
  @JsonView( ReservationViews.AvailableSlot.class )
  private double duration;

  public int getId()
  {
    return id;
  }

  public void setId( int id )
  {
    this.id = id;
  }

  public String getDescription()
  {
    return description;
  }

  public void setDescription( String description )
  {
    this.description = description;
  }

  public Date getStart()
  {
    return start;
  }

  public void setStart( Date start )
  {
    this.start = start;
  }

  public Date getEnd()
  {
    return end;
  }

  public void setEnd( Date end )
  {
    this.end = end;
  }

  public int getNumberOfAttendees()
  {
    return numberOfAttendees;
  }

  public void setNumberOfAttendees( int numberOfAttendees )
  {
    this.numberOfAttendees = numberOfAttendees;
  }

  public boolean isPending()
  {
    if ( pending == null )
      return false;
    else
      return pending;
  }

  public void setPending( boolean pending )
  {
    this.pending = pending;
  }

  public String getFirstName()
  {
    return firstName;
  }

  public void setFirstName( String firstName )
  {
    this.firstName = firstName;
  }

  public String getLastName()
  {
    return lastName;
  }

  public void setLastName( String lastName )
  {
    this.lastName = lastName;
  }

  public String getEmail()
  {
    return email;
  }

  public void setEmail( String email )
  {
    this.email = email;
  }

  public String getPhone()
  {
    return phone;
  }

  public void setPhone( String phone )
  {
    this.phone = phone;
  }

  public String getAcademicDept()
  {
    return academicDept;
  }

  public void setAcademicDept( String academicDept )
  {
    this.academicDept = academicDept;
  }

  public String getAcademicDeptOther()
  {
    return academicDeptOther;
  }

  public void setAcademicDeptOther( String academicDeptOther )
  {
    this.academicDeptOther = academicDeptOther;
  }

  public String getDivision()
  {
    return division;
  }

  public void setDivision( String division )
  {
    this.division = division;
  }

  public String getCourseLevel()
  {
    return courseLevel;
  }

  public void setCourseLevel( String courseLevel )
  {
    this.courseLevel = courseLevel;
  }

  public String getSrs()
  {
    return srs;
  }

  public void setSrs( String srs )
  {
    this.srs = srs;
  }

  public String getComputerUse()
  {
    return computerUse;
  }

  public void setComputerUse( String computerUse )
  {
    this.computerUse = computerUse;
  }

  public String getInternalNotes()
  {
    return internalNotes;
  }

  public void setInternalNotes( String internalNotes )
  {
    this.internalNotes = internalNotes;
  }

  public String getUid()
  {
    return uid;
  }

  public void setUid( String uid )
  {
    this.uid = uid;
  }

  public String getClaimed()
  {
    return claimed;
  }

  public void setClaimed( String claimed )
  {
    this.claimed = claimed;
  }

  public List<Resource> getResources()
  {
    return resources;
  }

  public void setResources( List<Resource> resources )
  {
    this.resources = resources;
  }

  public void addResource( Resource r )
  {
    this.resources.add( r );
  }

  public boolean isContainedIn( List<Reservation> allReservations )
  {
    Calendar cal = Calendar.getInstance();
    cal.setTime( this.start );

    for ( Reservation res: allReservations )
    {
      // Add this reservation's resources to a hash set
      Set<Integer> setOfResources = new HashSet<Integer>();
      for ( Resource r: this.getResources() )
      {
        setOfResources.add( r.getId() );
      }
      // If any of res's resources is in the set, return
      boolean match = false;
      for ( Resource r: res.getResources() )
      {
        if ( setOfResources.contains( r.getId() ) )
        {
          match = true;
          break;
        }
      }
      if ( match == false )
      {
        continue;
      }

      // Check times
      if ( this.start.getTime() >= res.start.getTime() &&
           this.start.getTime() < res.end.getTime() )
      {
        return true;
      }
    }
    return false;
  }
  //This checks to see if the reservation that you are trying to add is really
  //just an extension of another reservation that you already have. Since this is
  //only used in the hours before case, we want the latest reservation, not the earliest.
  //Temporarily Deleted, unless conditions changed for endTime cutoff
  /*public boolean isALaterReservation(List<Reservation> allReservations, Date start) {
		if(this.isContainedIn(allReservations))
			for(Reservation res : allReservations) {
				// Add this reservation's resources to a hash set
				Set<Integer> setOfResources = new HashSet<Integer>();
				for (Resource r: this.getResources()) {
					setOfResources.add(r.getId());				
				}
				// If any of res's resources is in the set, return
				boolean match = false;
				
				for (Resource r: res.getResources()) {
					if (setOfResources.contains(r.getId())) {
						match = true;
						break;
					}				
				}
				if (match == false) { continue; }
			}
			for(int i =0;i<allReservations.size();i++)
			{
				Reservation res = allReservations.get(i);
				if(this.start.getTime() >= res.start.getTime() &&
						this.start.getTime() < res.end.getTime() &&
						this.end.getTime() >= res.end.getTime() ){
					allReservations.remove(i);
					return true;
						
					}
			}
			
		return false;
	}*/

  public void setDuration( double duration )
  {
    this.duration = duration;
  }

  public double getDuration()
  {
    /*duration = 0D;
    //if ( start != null && end != null )
    {
      long diff;
      //duration = TimeUnit.HOURS.convert( getEnd().getTime() - getStart().getTime(), TimeUnit.HOURS );
      diff = end.getTime() - start.getTime();
      duration = diff / (60D * 60D * 1000D); 
    }*/
    return duration;
  }
}
