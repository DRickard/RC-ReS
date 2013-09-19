package edu.ucla.library.libservices.rswrapper.webservices;

import javax.servlet.ServletConfig;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;

import org.codehaus.jackson.map.annotate.JsonView;

import edu.ucla.library.libservices.rswrapper.utility.messages.ErrorResponse;
import edu.ucla.library.libservices.rswrapper.beans.ClassroomLocation;
import edu.ucla.library.libservices.rswrapper.beans.Reservation;
import edu.ucla.library.libservices.rswrapper.beans.ReservationViews;
import edu.ucla.library.libservices.rswrapper.beans.StudyroomLocation;
import edu.ucla.library.libservices.rswrapper.generators.DeleteReservationGenerator;
import edu.ucla.library.libservices.rswrapper.generators.GetReservationGenerator;
import edu.ucla.library.libservices.rswrapper.generators.SubmitReservationGenerator;
import edu.ucla.library.libservices.rswrapper.generators.UpdateReservationGenerator;

@Path( "/reservation" )
public class ReservationService
{

  @Context
  ServletConfig config;

  private void errorOutIfIdIsSpecifedInReservation( Reservation r )
    throws WebApplicationException
  {

    // If the id is not 0, someone supplied a reservationId
    if ( r.getId() != 0 )
    {
      throw new WebApplicationException( ErrorResponse.ID_EXISTS );
    }
  }

  @JsonView( ReservationViews.FullReservation.class )
  @GET
  @Path( "{reservationId}" )
  @Produces( "application/json" )
  public Reservation get( @PathParam( "reservationId" )
    int reservationId )
  {
    GetReservationGenerator generator = new GetReservationGenerator();
    generator.setDbName( config.getServletContext().getInitParameter( "datasource.irma" ) );
    String userauth =
      config.getServletContext().getInitParameter( "user.auth" );
    String passauth =
      config.getServletContext().getInitParameter( "pass.auth" );
    String userrs =
      config.getServletContext().getInitParameter( "user.rs" );
    generator.setup( userauth, passauth, userrs );
    return generator.get( reservationId );
  }

  @JsonView( ReservationViews.FullReservation.class )
  @POST
  @Path( "group/{groupCode}/{resourceName}" )
  @Consumes( "application/json" )
  @Produces( "application/json" )
  public Reservation submitWithGroupCodeAndResourceName( Reservation r,
                                                         @PathParam( "groupCode" )
    String groupCode, @PathParam( "resourceName" )
    String resourceName )
  {
    StudyroomLocation loc =
      StudyroomLocation.getStudyroomLocation( groupCode );
    if ( !loc.isValid() )
    {
      throw new WebApplicationException( ErrorResponse.RESOURCE_DNE );
    }

    this.errorOutIfIdIsSpecifedInReservation( r );
    SubmitReservationGenerator generator =
      new SubmitReservationGenerator();
    generator.setDbName( config.getServletContext().getInitParameter( "datasource.irma" ) );
    String userauth =
      config.getServletContext().getInitParameter( "user.auth" );
    String passauth =
      config.getServletContext().getInitParameter( "pass.auth" );
    String userrs =
      config.getServletContext().getInitParameter( "user.rs" );
    generator.setup( userauth, passauth, userrs );
    return generator.submitWithGroupCodeAndResourceName( r, loc,
                                                         resourceName );
  }

  @JsonView( ReservationViews.FullReservation.class )
  @POST
  @Path( "reserveroom/{roomID}" )
  @Consumes( "application/json" )
  @Produces( "application/json" )
  public Reservation submitWithRoomID( Reservation theReservation,
                                       @PathParam( "roomID" )
    int roomID )
  {
    this.errorOutIfIdIsSpecifedInReservation( theReservation );
    SubmitReservationGenerator generator =
      new SubmitReservationGenerator();
    generator.setDbName( config.getServletContext().getInitParameter( "datasource.irma" ) );
    generator.setIrmaDb( config.getServletContext().getInitParameter( "datasource.irma" ) );
    String userauth =
      config.getServletContext().getInitParameter( "user.auth" );
    String passauth =
      config.getServletContext().getInitParameter( "pass.auth" );
    String userrs =
      config.getServletContext().getInitParameter( "user.rs" );
    generator.setup( userauth, passauth, userrs );
    return generator.submitByRoomID( theReservation, roomID );
  }

  @JsonView( ReservationViews.FullReservation.class )
  @POST
  @Path( "resource/{resourceCode}" )
  @Consumes( "application/json" )
  @Produces( "application/json" )
  public Reservation submitWithResourceCode( Reservation r,
                                             @PathParam( "resourceCode" )
    String rc )
  {
    ClassroomLocation loc = ClassroomLocation.getClassroomLocation( rc );
    if ( !loc.isValid() )
    {
      throw new WebApplicationException( ErrorResponse.RESOURCE_DNE );
    }

    this.errorOutIfIdIsSpecifedInReservation( r );
    SubmitReservationGenerator generator =
      new SubmitReservationGenerator();
    generator.setDbName( config.getServletContext().getInitParameter( "datasource.irma" ) );
    String userauth =
      config.getServletContext().getInitParameter( "user.auth" );
    String passauth =
      config.getServletContext().getInitParameter( "pass.auth" );
    String userrs =
      config.getServletContext().getInitParameter( "user.rs" );
    generator.setup( userauth, passauth, userrs );
    return generator.submitWithResourceCode( r, loc );
  }

  @JsonView( ReservationViews.FullReservation.class )
  @PUT
  @Path( "confirm/{reservationId}" )
  @Consumes( "application/json" )
  @Produces( "application/json" )
  public Reservation update( Reservation reservation, @PathParam( "reservationId" )
    int reservationId )
  {
    this.errorOutIfIdIsSpecifedInReservation( reservation );
    UpdateReservationGenerator generator =
      new UpdateReservationGenerator();
    generator.setDbName( config.getServletContext().getInitParameter( "datasource.irma" ) );
    generator.setIrmaDb( config.getServletContext().getInitParameter( "datasource.irma" ) );
    String userauth =
      config.getServletContext().getInitParameter( "user.auth" );
    String passauth =
      config.getServletContext().getInitParameter( "pass.auth" );
    String userrs =
      config.getServletContext().getInitParameter( "user.rs" );
    generator.setup( userauth, passauth, userrs );
    return generator.update( reservation, reservationId );
  }

  @JsonView( ReservationViews.FullReservation.class )
  @DELETE
  @Path( "release/{reservationId}" )
  @Produces( "application/json" )
  public Reservation releaseReservation( @PathParam( "reservationId" )
    int reservationId )
  {
    DeleteReservationGenerator generator =
      new DeleteReservationGenerator();
    generator.setDbName( config.getServletContext().getInitParameter( "datasource.irma" ) );
    generator.setIrmaDb( config.getServletContext().getInitParameter( "datasource.irma" ) );
    String userauth =
      config.getServletContext().getInitParameter( "user.auth" );
    String passauth =
      config.getServletContext().getInitParameter( "pass.auth" );
    String userrs =
      config.getServletContext().getInitParameter( "user.rs" );
    generator.setup( userauth, passauth, userrs );
    return generator.delete( reservationId );
  }
}
/*
    StudyroomLocation loc =
      StudyroomLocation.getStudyroomLocation( groupCode );
    if ( !loc.isValid() )
    {
      throw new WebApplicationException( ErrorResponse.RESOURCE_DNE );
    }
 */