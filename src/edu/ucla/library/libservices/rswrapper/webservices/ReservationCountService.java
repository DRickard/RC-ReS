package edu.ucla.library.libservices.rswrapper.webservices;

import edu.ucla.library.libservices.rswrapper.utility.db.ReservationCount;

import javax.servlet.ServletConfig;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

@Path("/count/")
public class ReservationCountService
{
  @Context
  ServletConfig config;

  public ReservationCountService()
  {
    super();
  }
  
  @GET
  @Produces( "text/plain" )
  @Path( "{uid}/{days}" )
  public Response getResCount(@PathParam( "uid" )
    String uid, @PathParam( "days" )
    int days)
  {
    ReservationCount counter;

    counter = new ReservationCount();
    counter.setDays( days );
    counter.setDbName( config.getServletContext().getInitParameter( "datasource.irma" ) );
    counter.setUid( uid );
    
    try
    {
      return Response.ok().entity( String.valueOf( counter.getReservations() ) ).build();
    }
    catch ( Exception e )
    {
      return Response.serverError().entity( "search failed: " + e.getMessage() ).build();
    }
  }
}
