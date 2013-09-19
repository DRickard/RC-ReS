package edu.ucla.library.libservices.rswrapper.webservices;

import edu.ucla.library.libservices.rswrapper.beans.Reservation;
import edu.ucla.library.libservices.rswrapper.beans.ReservationViews;
import edu.ucla.library.libservices.rswrapper.generators.AvailableRoomsInLocationGenerator;
import edu.ucla.library.libservices.rswrapper.utility.converters.DateTimeConverter;
import edu.ucla.library.libservices.rswrapper.utility.messages.ErrorResponse;

import java.io.UnsupportedEncodingException;

import java.net.URLDecoder;

import java.text.ParseException;

import java.util.Date;
import java.util.List;

import javax.servlet.ServletConfig;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;

import org.codehaus.jackson.map.annotate.JsonView;

@Path( "/availablerooms" )
public class AvailableReservationsService
{

  @Context
  ServletConfig config;

  private Date start;

  public AvailableReservationsService()
  {
    super();
  }

  @JsonView( ReservationViews.AvailableSlot.class )
  @GET
  @Path( "complete/startingat/{time}/length/{numHalfHours}" )
  @Produces( "application/json" )
  public List<Reservation> getOpenRoomsAtTimeMaxLength( @PathParam( "time" )
    String startParam, @PathParam( "numHalfHours" )
    int numHalfHours )
  {
    AvailableRoomsInLocationGenerator generator;

    try
    {
      start =
          DateTimeConverter.createDateTimeFormat().parse( URLDecoder.decode( startParam,
                                                                             "utf-8" ) );
    }
    catch ( ParseException e )
    {
      throw new WebApplicationException( ErrorResponse.INVALID_DATETIME_FORMAT );
    }
    catch ( UnsupportedEncodingException e )
    {
      throw new WebApplicationException( ErrorResponse.createServerError( e ) );
    }

    // Invoke the generator
    generator = new AvailableRoomsInLocationGenerator();
    generator.setDbName( config.getServletContext().getInitParameter( "datasource.irma" ) );
    generator.setMagidb( config.getServletContext().getInitParameter( "datasource.magi" ) );
    return generator.getAvailableRoomsInLocationAtStartTimeMaxTime( start,
                                                                    numHalfHours );
  }

  @JsonView( ReservationViews.AvailableSlot.class )
  @GET
  @Path( "startingat/{time}/length/{numHalfHours}" )
  @Produces( "application/json" )
  public List<Reservation> getOpenRoomsAtTime( @PathParam( "time" )
    String startParam, @PathParam( "numHalfHours" )
    int numHalfHours )
  {
    AvailableRoomsInLocationGenerator generator;

    try
    {
      start =
          DateTimeConverter.createDateTimeFormat().parse( URLDecoder.decode( startParam,
                                                                             "utf-8" ) );
    }
    catch ( ParseException e )
    {
      throw new WebApplicationException( ErrorResponse.INVALID_DATETIME_FORMAT );
    }
    catch ( UnsupportedEncodingException e )
    {
      throw new WebApplicationException( ErrorResponse.createServerError( e ) );
    }

    // Invoke the generator
    generator = new AvailableRoomsInLocationGenerator();
    generator.setDbName( config.getServletContext().getInitParameter( "datasource.irma" ) );
    generator.setMagidb( config.getServletContext().getInitParameter( "datasource.magi" ) );
    return generator.getAvailableRoomsInLocationAtStartTime( start,
                                                             numHalfHours );
  }

  /*

  @Path( "before/{time}/length/{numHalfHours}" )
  @Produces( "application/json" )
  public List<Reservation> getOpenRoomsBeforeTime(
    @PathParam( "time" )
    String startParam, @PathParam( "numHalfHours" )
    int numHalfHours )
 */

  @JsonView( ReservationViews.AvailableSlot.class )
  @GET
  @Path( "after/{time}/until/{spanLength}/length/{meetLength}" )
  @Produces( "application/json" )
  public List<Reservation> getOpenRoomsAfterTime( @PathParam( "time" )
    String startParam, @PathParam( "spanLength" )
    int spanLength, @PathParam( "meetLength" )
    int meetLength )
  {
    AvailableRoomsInLocationGenerator generator;

    try
    {
      start =
          DateTimeConverter.createDateTimeFormat().parse( URLDecoder.decode( startParam,
                                                                             "utf-8" ) );
    }
    catch ( ParseException e )
    {
      throw new WebApplicationException( ErrorResponse.INVALID_DATETIME_FORMAT );
    }
    catch ( UnsupportedEncodingException e )
    {
      throw new WebApplicationException( ErrorResponse.createServerError( e ) );
    }

    // Invoke the generator
    generator = new AvailableRoomsInLocationGenerator();
    generator.setDbName( config.getServletContext().getInitParameter( "datasource.irma" ) );
    generator.setMagidb( config.getServletContext().getInitParameter( "datasource.magi" ) );
    return generator.getAvailableRoomsInLocationAfterStart( start,
                                                            meetLength,
                                                            spanLength );
  }

  @JsonView( ReservationViews.AvailableSlot.class )
  @GET
  @Path( "before/{time}/length/{numHalfHours}" )
  @Produces( "application/json" )
  public List<Reservation> getOpenRoomsBeforeTime( @PathParam( "time" )
    String startParam, @PathParam( "numHalfHours" )
    int numHalfHours )
  {
    AvailableRoomsInLocationGenerator generator;

    try
    {
      start =
          DateTimeConverter.createDateTimeFormat().parse( URLDecoder.decode( startParam,
                                                                             "utf-8" ) );
    }
    catch ( ParseException e )
    {
      throw new WebApplicationException( ErrorResponse.INVALID_DATETIME_FORMAT );
    }
    catch ( UnsupportedEncodingException e )
    {
      throw new WebApplicationException( ErrorResponse.createServerError( e ) );
    }

    // Invoke the generator
    generator = new AvailableRoomsInLocationGenerator();
    generator.setDbName( config.getServletContext().getInitParameter( "datasource.irma" ) );
    generator.setMagidb( config.getServletContext().getInitParameter( "datasource.magi" ) );
    return generator.getAvailableRoomsInLocationBeforeStart( start,
                                                             numHalfHours );
  }
}
    //,@DefaultValue( "" ) @QueryParam( "uid" ) String uid )
    //, @DefaultValue( "" ) @QueryParam( "time" ) String start 
/*
      if ( start.equals( "" ) )
      {
        this.start = new Date();
      }
      else
      {
    }
 */
    // Get the location object
    /*StudyroomLocation loc = StudyroomLocation.getStudyroomLocation( "yrl" );
    if ( !loc.isValid() )
    {
      throw new WebApplicationException( ErrorResponse.GROUP_DNE );
    }*/

    //BusinessRules br = loc.generate( uid );
    //String dbname = config.getServletContext().getInitParameter( "datasource.irma" );
    //import javax.ws.rs.DefaultValue;
    //import javax.ws.rs.QueryParam;
    //import edu.ucla.library.libservices.rswrapper.utility.Utility;
    //import edu.ucla.library.libservices.rswrapper.beans.BusinessRules;
    //import edu.ucla.library.libservices.rswrapper.beans.StudyroomLocation;
