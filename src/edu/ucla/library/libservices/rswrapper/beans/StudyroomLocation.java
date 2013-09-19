package edu.ucla.library.libservices.rswrapper.beans;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


@XmlType
@XmlAccessorType( XmlAccessType.FIELD )
public class StudyroomLocation
  extends Location
{

  public StudyroomLocation()
  {
    super();
  }

  static public StudyroomLocation getStudyroomLocation( String name )
  {
    StudyroomLocation loc = new StudyroomLocation();

    if ( name.equals( "powell" ) )
    {
      loc.setGroupIds( new int[]
          { 58 } );
      loc.setMagiLocationId( 1 );
    }
    if ( name.equals( "yrl" ) )
    {
      loc.setGroupIds( new int[]
          { 79, 56 } );
      loc.setMagiLocationId( 19 );
    }

    return loc;
  }

  public BusinessRules generate( String uid )
  {
    // TODO Get stuff from the database using uid and group
    BusinessRules rules = new BusinessRules();
    rules.setLimitPerTimePeriod( 1 );
    rules.setUnitOfTimeInDays( 1 );
    rules.setStudyRoom( true );
    for ( int groupId: this.groupIds )
    {
      rules.addRules( groupId, 1, 120 );
    }
    return rules;
  }


  public boolean isValid()
  {
    return ( groupIds != null );
  }

  private int[] groupIds;

  public int[] getGroupIds()
  {
    return groupIds;
  }

  public void setGroupIds( int[] groupIds )
  {
    this.groupIds = groupIds;
  }

}
