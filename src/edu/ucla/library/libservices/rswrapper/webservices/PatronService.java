package edu.ucla.library.libservices.rswrapper.webservices;

import edu.ucla.library.libservices.rswrapper.generators.PatronGenerator;

import javax.servlet.ServletConfig;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

@Path( "/patron/" )
public class PatronService
{
  @Context
  ServletConfig config;

  public PatronService()
  {
    super();
  }

  @GET
  @Produces( "application/json, text/xml" )
  @Path( "group/{id}" )
  public Response getGroup( @PathParam( "id" )
    String uid )
  {
    PatronGenerator maker;
    maker = new PatronGenerator();
    
    maker.setDbName( config.getServletContext().getInitParameter( "datasource.oracle" ) );
    maker.setUid( uid );
    
    return Response.ok().entity( maker.getGroup() ).build();
  }
}
