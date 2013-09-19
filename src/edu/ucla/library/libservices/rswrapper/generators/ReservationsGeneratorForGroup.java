package edu.ucla.library.libservices.rswrapper.generators;

//import java.io.IOException;
//import java.io.StringWriter;

import java.util.Date;
import java.util.List;
import java.util.Map;

//import javax.ws.rs.WebApplicationException;

//import org.apache.commons.io.IOUtils;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

//import javax.ws.rs.core.Response.Status;

//import edu.ucla.library.libservices.rswrapper.utility.messages.ErrorResponse;
//import edu.ucla.library.libservices.rswrapper.beans.StudyroomLocation;
import edu.ucla.library.libservices.rswrapper.beans.Reservation;
import edu.ucla.library.libservices.rswrapper.db.mappers.MapOfRoomNameToReservationExtractor;
import edu.ucla.library.libservices.rswrapper.db.mappers.UdfsExtractor;
import edu.ucla.library.libservices.rswrapper.utility.constants.Constants;
import edu.ucla.library.libservices.rswrapper.utility.converters.DateTimeConverter;
//import edu.ucla.library.libservices.rswrapper.utility.converters.StringConverter;

public class ReservationsGeneratorForGroup
  extends Generator
{
  private static final String ALL_ROOMS_QUERY =
    "SELECT * FROM RedESoft.dbo.vw_rc_rooms_test ORDER BY display_order";
    //"SELECT * FROM RedESoft.dbo.vw_rc_rooms ORDER BY display_order";
  private static final String ROOMS_BY_GROUP_QUERY =
    "SELECT * FROM RedESoft.dbo.vw_rc_rooms_test WHERE grp_id = ? ORDER BY display_order";
    //"SELECT * FROM RedESoft.dbo.vw_rc_rooms WHERE grp_id = ? ORDER BY display_order";
  private static final String RESERVATIONS_QUERY =
    //"SELECT * FROM RedESoft.dbo.vw_rc_reservations WHERE ( (startDate >= ? AND" +
    "SELECT * FROM RedESoft.dbo.vw_rc_reservations_test WHERE ( (startDate >= ? AND" +
    " startDate <= ?) OR (endDate >= ? AND endDate <= ?) OR (startDate <= ? AND " +
    "endDate >= ?) ) ORDER BY display_order, startDate";
  private static final String UDFS_QUERY =
    //"SELECT * FROM RedESoft.dbo.vw_rc_udfs WHERE ( (startDate >= ? AND startDate" +
    "SELECT * FROM RedESoft.dbo.vw_rc_udfs_test WHERE ( (startDate >= ? AND startDate" +
    " <= ?) OR (endDate >= ? AND endDate <= ?) OR (startDate <= ? AND endDate >= ?) )";

  @SuppressWarnings( "unchecked" )
  protected Map<String, List<Reservation>> getCurrentReservations( //StudyroomLocation loc,
    Date startTime, Date endTime )
  {
    makeDBConnection();

    Map<String, List<Reservation>> mapOfReservations =
      ( Map<String, List<Reservation>> ) new JdbcTemplate( ds ).query( RESERVATIONS_QUERY,
                                                                       generateReservationsParams( startTime,
                                                                                                   endTime ),
                                                                       generateReservationsTypes(),
                                                                       new MapOfRoomNameToReservationExtractor() );

    // Get and set user defined fields
    Map<Integer, Map<Integer, String>> udfs =
      getUdfs( startTime, endTime );
    for ( String k: mapOfReservations.keySet() )
    {
      List<Reservation> lr = mapOfReservations.get( k );
      for ( Reservation r: lr )
      {
        int reservationId = r.getId();
        if ( udfs.containsKey( reservationId ) )
        {
          r.setUdfs( udfs.get( reservationId ) );
        }

      }
    }

    return mapOfReservations;
  }

  @SuppressWarnings( "rawtypes" )
  protected List getAllRooms( RowMapper mapper )
  {
    makeDBConnection();

    return new JdbcTemplate( this.ds ).query( ALL_ROOMS_QUERY, mapper );
  }

  @SuppressWarnings( "rawtypes" )
  protected List getRoomsByGroup( int groupID, RowMapper mapper )
  {
    makeDBConnection();

    return new JdbcTemplate( this.ds ).query( ROOMS_BY_GROUP_QUERY,
                                              new Object[]
        { groupID }, mapper );
  }

  @SuppressWarnings( "unchecked" )
  private Map<Integer, Map<Integer, String>> getUdfs( Date startTime,
                                                      Date endTime )
  {
    makeDBConnection();

    return ( Map<Integer, Map<Integer, String>> ) new JdbcTemplate( ds ).query( UDFS_QUERY,
                                                                                generateReservationsParams( startTime,
                                                                                                            endTime ),
                                                                                generateUdfsTypes(),
                                                                                new UdfsExtractor() );
  }

  /*private String generateUdfsQuery( int numberOfGroups, Date startTime,
                                    Date endTime )
  {
    String udfsQuery;
    try
    {
      StringWriter writer = new StringWriter();
      IOUtils.copy( getClass().getResourceAsStream( "udfsByGroup.sql" ),
                    writer, "UTF-8" );
      udfsQuery = writer.toString();
    }
    catch ( IOException e )
    {
      throw new WebApplicationException( ErrorResponse.createErrorResponse( e.getMessage(),
                                                                            Status.INTERNAL_SERVER_ERROR ) );
    }

    StringBuilder sb = new StringBuilder( udfsQuery );
    int toReplace = sb.indexOf( "??" );
    sb.replace( toReplace, toReplace + 2,
                StringConverter.convertQuestionMarks( numberOfGroups ) );

    return sb.toString();
  }*/

  private Object[] generateReservationsParams( Date startTime,
                                               Date endTime )
  {
    Object[] params;
    String startString;
    String endString;

    params = new Object[ Constants.DATE_PARAM_COUNT ];

    startString =
        DateTimeConverter.createDateTimeFormat().format( startTime );
    endString = DateTimeConverter.createDateTimeFormat().format( endTime );
    //System.out.println( "start = " + startString + ", end = " + endString );

    params[ 0 ] = startString;
    params[ 1 ] = endString;
    params[ 2 ] = startString;
    params[ 3 ] = endString;
    params[ 4 ] = startString;
    params[ 5 ] = endString;

    return params;
  }

  /*private Object[] generateUdfsParams( Date startTime, Date endTime )
  {
    return generateReservationsParams( startTime, endTime );
  }*/

  private int[] generateReservationsTypes()
  {
    int[] params = new int[ Constants.DATE_PARAM_COUNT ];

    for ( int i = 0; i < Constants.DATE_PARAM_COUNT; i++ )
    {
      params[ i ] = java.sql.Types.VARCHAR;
    }

    return params;
  }

  private int[] generateUdfsTypes()
  {
    return this.generateReservationsTypes();
  }
}
    //int[] groupIds = loc.getGroupIds();

    //String infoQuery = this.generateResourcesQuery( groupIds.length );
    //Object[] infoParams = this.generateResourcesParams( loc.getGroupIds() );
    //int[] infoTypes = this.generateResourcesTypes( loc.getGroupIds().length );
    //import edu.ucla.library.libservices.rswrapper.utility.Utility;
/*
  private String generateResourcesQuery( int numberOfGroups )
  {

    String infoQuery;
    try
    {
      StringWriter writer = new StringWriter();
      IOUtils.copy( getClass().getResourceAsStream( "resourcesByGroup.sql" ),
                    writer, "UTF-8" );
      infoQuery = writer.toString();
    }
    catch ( IOException e )
    {
      throw new WebApplicationException( ErrorResponse.createServerError( e ) );
    }

    StringBuilder sb = new StringBuilder( infoQuery );
    int toReplace = sb.indexOf( "??" );
    sb.replace( toReplace, toReplace + 2,
                StringConverter.convertQuestionMarks( numberOfGroups ) );

    return sb.toString();
  }
 */
    //int[] groupIds = loc.getGroupIds();

    // Query
    //String reservationsQuery = this.generateReservationsQuery( groupIds.length, startTime, endTime );
    //Object[] reservationParams = this.generateReservationsParams( startTime, endTime ); //groupIds, 
    //int[] reservationTypes = this.generateReservationsTypes( groupIds.length );

    //int[] groupIds,
    ////int numberOfDates = 6;
        //int arraylength = numberOfDates + groupIds.length;
    /*for ( int i = 0; i < groupIds.length; i++ )
    {
      params[ numberOfDates + i ] = groupIds[ i ];
    }*/

//int[] groupIds, 
///int numberOfGroups
/*
    int numberOfDates = 6;
    int arraylength = numberOfDates + numberOfGroups;
 *//*
 for ( int i = 0; i < numberOfGroups; i++ )
 {
   params[ numberOfDates + i ] = java.sql.Types.INTEGER;
 }

    */
/*

  private int[] generateResourcesTypes( int numberOfGroups )
  {
    int[] params = new int[ numberOfGroups ];
    for ( int i = 0; i < numberOfGroups; i++ )
    {
      params[ i ] = java.sql.Types.INTEGER;
    }
    return params;
  }
 */
                                              //generateResourcesParams( loc.getGroupIds() ),
                                              //generateResourcesTypes( loc.getGroupIds().length ), 
/*

  private String generateReservationsQuery( int numberOfGroups,
                                            Date startTime, Date endTime )
  {
    String reservationsQuery;
    try
    {
      StringWriter writer = new StringWriter();
      IOUtils.copy( getClass().getResourceAsStream( "reservationsByGroupAndTime.sql" ),
                    writer, "UTF-8" );
      reservationsQuery = writer.toString();
    }
    catch ( IOException e )
    {
      throw new WebApplicationException( ErrorResponse.createErrorResponse( e.getMessage(),
                                                                            Status.INTERNAL_SERVER_ERROR ) );
    }

    StringBuilder sb = new StringBuilder( reservationsQuery );
    int toReplace = sb.indexOf( "??" );
    sb.replace( toReplace, toReplace + 2,
                StringConverter.convertQuestionMarks( numberOfGroups ) );

    return sb.toString();
  }
 */
/*

  private Object[] generateResourcesParams( int[] groupIds )
  {
    Object[] params = new Object[ groupIds.length ];
    for ( int i = 0; i < groupIds.length; i++ )
    {
      params[ i ] = groupIds[ i ];
    }
    return params;
  }
 */
//int[] groupIds,
////String query = this.generateUdfsQuery( groupIds.length, startTime, endTime );
    //Object[] params = this.generateUdfsParams( startTime, endTime );
    //int[] types = this.generateUdfsTypes();

//loc.getGroupIds(), 