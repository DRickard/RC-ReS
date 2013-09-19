package edu.ucla.library.libservices.rswrapper.generators;

import java.io.IOException;
import java.io.StringWriter;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ws.rs.WebApplicationException;

import org.apache.commons.io.IOUtils;

import org.springframework.jdbc.core.JdbcTemplate;

//import edu.ucla.library.libservices.rswrapper.utility.Utility;
import edu.ucla.library.libservices.rswrapper.utility.messages.ErrorResponse;
import edu.ucla.library.libservices.rswrapper.beans.ClassroomLocation;
import edu.ucla.library.libservices.rswrapper.beans.Reservation;
import edu.ucla.library.libservices.rswrapper.db.mappers.ReservationMapper;
import edu.ucla.library.libservices.rswrapper.db.mappers.UdfsExtractor;
import edu.ucla.library.libservices.rswrapper.utility.converters.DateTimeConverter;

public class ReservationsGeneratorForResource
  extends Generator
{

  @SuppressWarnings( "unchecked" )
  protected List<Reservation> getReservations( ClassroomLocation loc,
                                               Date startTime,
                                               Date endTime )
  {
    makeDBConnection();

    int resourceId = loc.getResourceId();

    String query;
    try
    {
      StringWriter writer = new StringWriter();
      IOUtils.copy( getClass().getResourceAsStream( "reservationsByResourceAndTime.sql" ),
                    writer, "UTF-8" );
      query = writer.toString();
    }
    catch ( IOException e )
    {
      throw new WebApplicationException( ErrorResponse.createServerError( e ) );
    }

    String startString =
      DateTimeConverter.createDateTimeFormat().format( startTime );
    String endString =
      DateTimeConverter.createDateTimeFormat().format( endTime );

    Object[] params = new Object[]
      { startString, endString, startString, endString, startString,
        endString, resourceId };
    int[] types =
    { java.sql.Types.VARCHAR, java.sql.Types.VARCHAR,
      java.sql.Types.VARCHAR, java.sql.Types.VARCHAR,
      java.sql.Types.VARCHAR, java.sql.Types.VARCHAR,
      java.sql.Types.INTEGER };

    List<Reservation> reservations =
      new JdbcTemplate( this.ds ).query( query, params, types,
                                         new ReservationMapper() );

    Map<Integer, Map<Integer, String>> udfs =
      this.getUdfs( resourceId, startTime, endTime );
    for ( Reservation r: reservations )
    {
      // Retrieve user defined fields belonging to that reservation Id and
      /// store it in the reservation object
      r.setUdfs( udfs.get( r.getId() ) );
    }
    return reservations;
  }

  @SuppressWarnings(
    { "unchecked" } )
  private Map<Integer, Map<Integer, String>> getUdfs( int resourceId,
                                                      Date startTime,
                                                      Date endTime )
  {
    makeDBConnection();

    String query;
    try
    {
      StringWriter writer = new StringWriter();
      IOUtils.copy( getClass().getResourceAsStream( "udfsByResource.sql" ),
                    writer, "UTF-8" );
      query = writer.toString();
    }
    catch ( IOException e )
    {
      throw new WebApplicationException( ErrorResponse.createServerError( e ) );
    }

    String startString =
      DateTimeConverter.createDateTimeFormat().format( startTime );
    String endString =
      DateTimeConverter.createDateTimeFormat().format( endTime );

    Object[] params = new Object[]
      { startString, endString, startString, endString, startString,
        endString, resourceId };
    int[] types =
    { java.sql.Types.VARCHAR, java.sql.Types.VARCHAR,
      java.sql.Types.VARCHAR, java.sql.Types.VARCHAR,
      java.sql.Types.VARCHAR, java.sql.Types.VARCHAR,
      java.sql.Types.INTEGER };

    return ( Map<Integer, Map<Integer, String>> ) new JdbcTemplate( this.ds ).query( query,
                                                                                     params,
                                                                                     types,
                                                                                     new UdfsExtractor() );
  }
}
