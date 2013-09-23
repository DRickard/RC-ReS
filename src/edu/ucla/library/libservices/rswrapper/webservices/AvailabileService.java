package edu.ucla.library.libservices.rswrapper.webservices;

import java.text.ParseException;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletConfig;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;

import org.codehaus.jackson.map.annotate.JsonView;

//import edu.ucla.library.libservices.rswrapper.utility.Utility;
import edu.ucla.library.libservices.rswrapper.utility.messages.ErrorResponse;

import edu.ucla.library.libservices.rswrapper.beans.Availability;
import edu.ucla.library.libservices.rswrapper.beans.ClassroomLocation;
import edu.ucla.library.libservices.rswrapper.beans.ReservationViews;
import edu.ucla.library.libservices.rswrapper.beans.StudyroomLocation;
import edu.ucla.library.libservices.rswrapper.generators.AvailableGeneratorForGroup;
import edu.ucla.library.libservices.rswrapper.generators.AvailableGeneratorForResource;
import edu.ucla.library.libservices.rswrapper.utility.converters.DateTimeConverter;

@Path( "/available" )
public class AvailabileService
{
  @Context
  ServletConfig config;

  private Date start;
  private Date end;

  public AvailabileService()
  {
    super();
  }

  /*@JsonView( ReservationViews.FullReservation.class )
  @GET
  @Path( "resource/{resourceName}" )
  @Produces( "application/json" )
  public Availability getAvailabilityForResource( @PathParam( "resourceName" )
    String resourceName, @DefaultValue( "" )
    @QueryParam( "start" )
    String start, @DefaultValue( "" )
    @QueryParam( "end" )
    String end )
  {
    fillInDates( start, end );

    // Find location object
    ClassroomLocation loc =
      ClassroomLocation.getClassroomLocation( resourceName );
    if ( !loc.isValid() )
    {
      throw new WebApplicationException( ErrorResponse.RESOURCE_DNE );
    }

    // Invoke the generator
    AvailableGeneratorForResource generator;
    String dbname =
      config.getServletContext().getInitParameter( "datasource.irma" );
    generator = new AvailableGeneratorForResource();
    generator.setDbName( dbname );
    return generator.getAvailability( loc, this.start, this.end );
  }

  @JsonView( ReservationViews.FullReservation.class )
  @GET
  @Path( "group/{groupCode}" )
  @Produces( "application/json" )
  public Availability getAvailabilityForGroup( @PathParam( "groupCode" )
    String groupCode, @DefaultValue( "" )
    @QueryParam( "start" )
    String start, @DefaultValue( "" )
    @QueryParam( "end" )
    String end )
  {
    fillInDates( start, end );

    // Get the location object
    StudyroomLocation loc =
      StudyroomLocation.getStudyroomLocation( groupCode );
    if ( !loc.isValid() )
    {
      throw new WebApplicationException( ErrorResponse.GROUP_DNE );
    }

    // Invoke the generator
    AvailableGeneratorForGroup generator;
    String dbname =
      config.getServletContext().getInitParameter( "datasource.irma" );
    generator = new AvailableGeneratorForGroup();
    generator.setDbName( dbname );
    return generator.getGroupAvailability( loc, this.start, this.end );
  }

  @JsonView( ReservationViews.FullReservation.class )
  @GET
  @Path( "group/{groupCode}/list" )
  @Produces( "application/json" )
  public List<Availability> getListOfAvailabilityForEachRoomInGroup( @PathParam( "groupCode" )
    String groupCode, @DefaultValue( "" )
    @QueryParam( "start" )
    String start, @DefaultValue( "" )
    @QueryParam( "end" )
    String end )
  {
    fillInDates( start, end );

    // Get the location object
    StudyroomLocation loc =
      StudyroomLocation.getStudyroomLocation( groupCode );
    if ( !loc.isValid() )
    {
      throw new WebApplicationException( ErrorResponse.GROUP_DNE );
    }

    // Invoke the generator
    AvailableGeneratorForGroup generator;
    String dbname =
      config.getServletContext().getInitParameter( "datasource.irma" );
    generator = new AvailableGeneratorForGroup();
    generator.setDbName( dbname );
    return generator.getGroupAvailabilitiesPerResource( loc, this.start,
                                                        this.end );
  }

  private void fillInDates( String start, String end )
  {
    Calendar cal = Calendar.getInstance();

    if ( start.equals( "" ) )
    {
      this.start = new Date();
    }
    else
    {
      try
      {
        this.start = DateTimeConverter.createDateTimeFormat().parse( start );
      }
      catch ( ParseException e )
      {
        throw new WebApplicationException( ErrorResponse.INVALID_DATETIME_FORMAT );
      }
    }

    if ( end.equals( "" ) )
    {
      cal.setTime( this.start );
      cal.add( Calendar.DATE, 1 );
      this.end = cal.getTime();
    }
    else
    {
      try
      {
        this.end = DateTimeConverter.createDateTimeFormat().parse( end );
      }
      catch ( ParseException e )
      {
        throw new WebApplicationException( ErrorResponse.INVALID_DATETIME_FORMAT );
      }
    }
  }*/
}
