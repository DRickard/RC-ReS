package edu.ucla.library.libservices.rswrapper.db.source;

import java.io.IOException;
import java.io.StringWriter;

import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import javax.ws.rs.WebApplicationException;

import org.apache.commons.io.IOUtils;

import org.springframework.jdbc.core.JdbcTemplate;

import edu.ucla.library.libservices.rswrapper.utility.messages.ErrorResponse;
import edu.ucla.library.libservices.rswrapper.utility.Utility;
import edu.ucla.library.libservices.rswrapper.beans.Location;
import edu.ucla.library.libservices.rswrapper.beans.Reservation;
import edu.ucla.library.libservices.rswrapper.db.mappers.StartEndMapper;

public class Magi
{

  private DataSource ds;

  public Magi()
  {
    ds = DataSourceFactory.createDataSource( DBNAME );
  }

  static final String DBNAME = "java:comp/env/jdbc/magi";

  // Returns hours of operation which are between start and end time

  @SuppressWarnings( "unchecked" )
  public List<Reservation> returnHoursOfOperation( Location loc,
                                                   Date start, Date end )
  {

    int locationId = loc.getMagiLocationId();

    String hoursQuery;
    try
    {
      StringWriter writer = new StringWriter();
      IOUtils.copy( getClass().getResourceAsStream( "magihoursByLocationId.sql" ),
                    writer, "UTF-8" );
      hoursQuery = writer.toString();
    }
    catch ( IOException e )
    {
      throw new WebApplicationException( ErrorResponse.createServerError( e ) );
    }

    // Create string representing start and end dates
    String startString = Utility.createDateTimeFormat().format( start );
    String endString = Utility.createDateTimeFormat().format( end );

    // Query
    Object[] hoursParams = new Object[]
      { startString, endString, startString, endString, startString,
        endString, locationId };
    int[] hoursTypes =
    { java.sql.Types.VARCHAR, java.sql.Types.VARCHAR,
      java.sql.Types.VARCHAR, java.sql.Types.VARCHAR,
      java.sql.Types.VARCHAR, java.sql.Types.VARCHAR,
      java.sql.Types.INTEGER };

    return ( List<Reservation> ) new JdbcTemplate( this.ds ).query( hoursQuery,
                                                                    hoursParams,
                                                                    hoursTypes,
                                                                    new StartEndMapper() );
  }
}
