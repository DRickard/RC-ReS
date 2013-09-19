package edu.ucla.library.libservices.rswrapper.webservices;

import java.text.ParseException;

import java.util.Calendar;
import java.util.Date;
//import java.util.List;

import javax.servlet.ServletConfig;

import javax.ws.rs.GET;

//import javax.ws.rs.DefaultValue;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
//import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;

//import org.codehaus.jackson.map.annotate.JsonView;

import edu.ucla.library.libservices.rswrapper.utility.messages.ErrorResponse;
//import edu.ucla.library.libservices.rswrapper.utility.Utility;
//import edu.ucla.library.libservices.rswrapper.beans.Reservation;
//import edu.ucla.library.libservices.rswrapper.beans.ReservationViews;
import edu.ucla.library.libservices.rswrapper.db.source.ResourceScheduler;
import edu.ucla.library.libservices.rswrapper.utility.converters.DateTimeConverter;

import javax.ws.rs.core.Response;


@Path( "/reservations" )
public class ReservationsByUidService
{

  @Context
  ServletConfig config;

  private Date start;
  private Date end;

  /*@JsonView( ReservationViews.BasicReservation.class )
  @GET
  @Path( "{uid}" )
  @Produces( "application/json" )
  public List<Reservation> get( @PathParam( "uid" )
    String uid, @DefaultValue( "" )
    @QueryParam( "start" )
    String start, @DefaultValue( "" )
    @QueryParam( "end" )
    String end )
  {
    this.fillInDates( start, end );

    ResourceScheduler rs = new ResourceScheduler();
    return rs.getListOfStartEndPairsWithUidAndTime( uid, this.start,
                                                    this.end );
  }*/
  
  @GET
  @Path( "current/{uid}" )
  @Produces( "application/json" )
  public Response reservationsByUID(@PathParam( "uid" )
    String uid)
  {
    ResourceScheduler rs = new ResourceScheduler();
    
    rs.setDbName( config.getServletContext().getInitParameter( "datasource.irma" ) );
    rs.getReservationsByUID( uid );
    
    return Response.ok().entity( rs ).build();
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
        this.start =
            DateTimeConverter.createDateTimeFormat().parse( start );
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
  }
}
