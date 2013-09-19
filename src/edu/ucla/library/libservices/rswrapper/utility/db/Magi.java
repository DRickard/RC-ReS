package edu.ucla.library.libservices.rswrapper.utility.db;

import edu.ucla.library.libservices.rswrapper.db.source.DataSourceFactory;

//import java.io.IOException;
//import java.io.StringWriter;

import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

//import javax.ws.rs.WebApplicationException;

//import org.apache.commons.io.IOUtils;

import org.springframework.jdbc.core.JdbcTemplate;

//import edu.ucla.library.libservices.rswrapper.utility.messages.ErrorResponse;
//import edu.ucla.library.libservices.rswrapper.utility.Utility;
//import edu.ucla.library.libservices.rswrapper.beans.Location;
import edu.ucla.library.libservices.rswrapper.beans.Reservation;
import edu.ucla.library.libservices.rswrapper.db.mappers.StartEndMapper;
import edu.ucla.library.libservices.rswrapper.utility.converters.DateTimeConverter;

public class Magi
{
  private DataSource ds;
  private String dbName; // = "java:comp/env/jdbc/magi";
  /*private static final String MAGI_HOURS =
    "SELECT OpenDateTime as startdate, CloseDateTime as enddate FROM " +
    "MAGI.dbo.view_scheduler_latestschedule WHERE ( (OpenDateTime >= ? " +
    "AND OpenDateTime <= ?) OR (CloseDateTime >= ? AND CloseDateTime <= ?) " +
    "OR (OpenDateTime <= ? AND CloseDateTime >= ?) ) AND LocationID = ? AND " +
    "OpenDateTime != CloseDateTime ORDER BY OpenDateTime";*/
  private static final String RC_SCHEDULE =
    "EXEC Library_Web.dbo.uspGetOpenCloseTimes ?, ?, ?";

  public Magi()
  {
    //ds = DataSourceFactory.createDataSource( dbName );
  }

  // Returns hours of operation which are between start and end time

  @SuppressWarnings( "unchecked" )
  public List<Reservation> returnHoursOfOperation( //Location loc,
    int localID, Date start, Date end )
  {
    makeConenction();

    //int locationId = loc.getMagiLocationId();

    //System.out.println( "in Magi, start param = " + start + ", end param = " + end + ", locale = " + localID );
    // Create string representing start and end dates
    String startString =
      DateTimeConverter.createDateTimeFormat().format( start );
    String endString =
      DateTimeConverter.createDateTimeFormat().format( end );
    //System.out.println( "in Magi, start = " + startString + ", end = " + endString );

    // Query
    Object[] hoursParams = new Object[]
      { startString, endString, //startString, endString, startString, endString, 
        localID };
    int[] hoursTypes =
    { java.sql.Types.VARCHAR, java.sql.Types.VARCHAR,
      //java.sql.Types.VARCHAR, java.sql.Types.VARCHAR,
      //java.sql.Types.VARCHAR, java.sql.Types.VARCHAR,
      java.sql.Types.INTEGER };

    return new JdbcTemplate( this.ds ).query( RC_SCHEDULE, //MAGI_HOURS, 
                                              hoursParams,
                                              hoursTypes,
                                              new StartEndMapper() );
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
    //ds = DataSourceFactory.createSchedulerSource(); //.createMagiSource();
  }
}
    /*String hoursQuery;
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
    }*/
