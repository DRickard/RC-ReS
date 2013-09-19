package edu.ucla.library.libservices.rswrapper.db.source;

import java.io.IOException;
import java.io.StringWriter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import javax.ws.rs.WebApplicationException;

import org.apache.commons.io.IOUtils;

import org.springframework.jdbc.core.JdbcTemplate;

import edu.ucla.library.libservices.rswrapper.utility.messages.ErrorResponse;
//import edu.ucla.library.libservices.rswrapper.utility.Utility;
import edu.ucla.library.libservices.rswrapper.beans.Reservation;
import edu.ucla.library.libservices.rswrapper.beans.SimpleReservation;
import edu.ucla.library.libservices.rswrapper.beans.StudyroomLocation;
import edu.ucla.library.libservices.rswrapper.db.mappers.ReservationMapper;
import edu.ucla.library.libservices.rswrapper.db.mappers.SimpleReservationMapper;
import edu.ucla.library.libservices.rswrapper.db.mappers.UdfsExtractor;
import edu.ucla.library.libservices.rswrapper.utility.converters.DateTimeConverter;
import edu.ucla.library.libservices.rswrapper.utility.converters.StringConverter;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "schedules")
public class ResourceScheduler
{
  private static final String RES_BY_UID =
    //"SELECT * FROM RedESoft.dbo.vw_rc_res_by_udf WHERE udf_val = ? AND startDate >= GETDATE() ORDER BY display_order";
    "SELECT * FROM RedESoft.dbo.vw_rc_res_by_udf_test WHERE udf_val = ? AND startDate >= GETDATE() ORDER BY display_order";

  private DataSource ds;
  private String dbName;
  private List<SimpleReservation> simpleReservation;

  public ResourceScheduler()
  {
    //ds = DataSourceFactory.createDataSource( DBNAME );
  }

  //static final String DBNAME = "java:comp/env/jdbc/irma";

  public Reservation getReservation( int reservationId )
  {
    int[] types;
    List<Reservation> res;
    Object[] params;
    String query;

    query = this.generateReservationByIdQuery();
    params = new Object[] { reservationId };
    types = new int[] { java.sql.Types.INTEGER };

    makeConenction();
    res = new JdbcTemplate( ds ).query( query, params, types,
                                         new ReservationMapper() );

    if ( res.isEmpty() )
    {
      return null;
    }
    else
    {
      Reservation r = res.get( 0 );
      r.setUdfs( this.getUdfsForReservationId( reservationId ) );
      return r;
    }
  }

  public void getReservationsByUID( String uid )
  {
    makeConenction();

    simpleReservation = new JdbcTemplate( ds ).query( RES_BY_UID, new Object[]
        { uid }, new SimpleReservationMapper() );
  }

  public List<Reservation> getListOfStartEndPairsWithUidAndTime( String uid,
                                                                 Date startTime,
                                                                 Date endTime )
  {
    // Form query
    String reservationsQuery = this.generateUidQuery();
    Object[] reservationParams =
      this.generateUidParams( uid, startTime, endTime );
    int[] reservationTypes = this.generateUidTypes();

    // Query
    @SuppressWarnings( "unchecked" )
    List<Reservation> pairs =
      new JdbcTemplate( ds ).query( reservationsQuery,
                                         reservationParams,
                                         reservationTypes,
                                         new ReservationMapper() );
    return pairs;
  }

  public int getResourceId( StudyroomLocation loc, String resourceName )
  {

    // Get the group id
    int[] groupIds = loc.getGroupIds();


    // Get the proper resource id
    try
    {
      PreparedStatement ps;
      ps =
          ds.getConnection().prepareStatement( generateResourceQuery( groupIds.length ) );
      ps.setString( 1, resourceName );

      for ( int i = 0; i < groupIds.length; i++ )
      {
        ps.setInt( 2 + i, groupIds[ i ] );
      }

      ResultSet rs = ps.executeQuery();
      if ( rs.next() )
      {
        return rs.getInt( "res_id" );
      }
      throw new WebApplicationException( ErrorResponse.RESOURCE_DNE );
    }
    catch ( SQLException e )
    {
      throw new WebApplicationException( ErrorResponse.createServerError( e ) );
    }
  }

  public Map<Integer, String> getUdfsForReservationId( int reservationId )
  {
    String query = this.generateUdfByIdQuery();
    Object[] params = new Object[]
      { reservationId };
    int[] types = new int[]
      { java.sql.Types.INTEGER };

    @SuppressWarnings( "unchecked" )
    Map<Integer, Map<Integer, String>> udfs =
      ( Map<Integer, Map<Integer, String>> ) new JdbcTemplate( ds ).query( query,
                                                                                params,
                                                                                types,
                                                                                new UdfsExtractor() );

    if ( udfs.containsKey( reservationId ) )
    {
      return udfs.get( reservationId );
    }
    else
    {
      return new HashMap<Integer, String>();
    }
  }

  private String generateUidQuery()
  {
    try
    {
      StringWriter writer = new StringWriter();
      IOUtils.copy( getClass().getResourceAsStream( "reservationsByUid.sql" ),
                    writer, "UTF-8" );
      return writer.toString();
    }
    catch ( IOException e )
    {
      throw new WebApplicationException( ErrorResponse.createServerError( e ) );
    }
  }

  private Object[] generateUidParams( String uid, Date startTime,
                                      Date endTime )
  {
    String startString =
      DateTimeConverter.createDateTimeFormat().format( startTime );
    String endString =
      DateTimeConverter.createDateTimeFormat().format( endTime );

    return new Object[]
      { startString, endString, startString, endString, startString,
        endString, uid };
  }

  private int[] generateUidTypes()
  {
    return new int[]
      { java.sql.Types.VARCHAR, java.sql.Types.VARCHAR,
        java.sql.Types.VARCHAR, java.sql.Types.VARCHAR,
        java.sql.Types.VARCHAR, java.sql.Types.VARCHAR,
        java.sql.Types.VARCHAR };
  }

  private String generateResourceQuery( int numberOfGroups )
  {
    String resourceQuery =
      "SELECT TOP 1 res_id from RedESoft.dbo.tbl_res WHERE res_hdr = ? AND grp_id IN ??";
    StringBuilder sb = new StringBuilder( resourceQuery );
    int toReplace = sb.indexOf( "??" );
    sb.replace( toReplace, toReplace + 2,
                StringConverter.convertQuestionMarks( numberOfGroups ) );

    return sb.toString();
  }

  private String generateReservationByIdQuery()
  {
    try
    {
      StringWriter writer = new StringWriter();
      IOUtils.copy( getClass().getResourceAsStream( "reservationByReservationId.sql" ),
                    writer, "UTF-8" );
      return writer.toString();
    }
    catch ( IOException e )
    {
      throw new WebApplicationException( ErrorResponse.createServerError( e ) );
    }
  }

  private String generateUdfByIdQuery()
  {
    try
    {
      StringWriter writer = new StringWriter();
      IOUtils.copy( getClass().getResourceAsStream( "udfsByReservationId.sql" ),
                    writer, "UTF-8" );
      return writer.toString();
    }
    catch ( IOException e )
    {
      throw new WebApplicationException( ErrorResponse.createServerError( e ) );
    }
  }

  public void setDbName( String dbName )
  {
    this.dbName = dbName;
  }

  private String getDbName()
  {
    return dbName;
  }

  private void makeConenction()
  {
    ds = DataSourceFactory.createDataSource( getDbName() );
    //ds = DataSourceFactory.createIrmaSource();
  }
}
