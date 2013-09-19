package edu.ucla.library.libservices.rswrapper.webservices;

import edu.ucla.library.libservices.rswrapper.generators.ReservationGenerator;

import javax.servlet.ServletConfig;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

@Path( "/currentreservations" )
public class CurrentReservations
{
  @Context
  ServletConfig config;

  public CurrentReservations()
  {
    super();
  }

  @GET
  @Path( "for/{uid}" )
  @Produces( "application/json" )
  public Response reservationsByUID(@PathParam( "uid" )
    String uid)
  {
    ReservationGenerator rs;

    rs = new ReservationGenerator();
    rs.setDbName( config.getServletContext().getInitParameter( "datasource.irma" ) );
    rs.getReservationsByUID( uid );
    
    return Response.ok().entity( rs ).build();
  }
}
