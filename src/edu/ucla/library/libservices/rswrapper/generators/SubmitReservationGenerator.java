package edu.ucla.library.libservices.rswrapper.generators;

import edu.ucla.library.libservices.rswrapper.beans.ClassroomLocation;
import edu.ucla.library.libservices.rswrapper.beans.Reservation;
import edu.ucla.library.libservices.rswrapper.beans.Resource;
import edu.ucla.library.libservices.rswrapper.beans.StudyroomLocation;
import edu.ucla.library.libservices.rswrapper.db.source.ResourceScheduler;
import edu.ucla.library.libservices.rswrapper.utility.messages.ErrorResponse;

import java.rmi.RemoteException;

import javax.ws.rs.WebApplicationException;

import webservice.resourcescheduler.peoplecube.ResourceSchedulerServiceStub.ReservationData;
import webservice.resourcescheduler.peoplecube.ResourceSchedulerServiceStub.SubmitReservation;
import webservice.resourcescheduler.peoplecube.ResourceSchedulerServiceStub.SubmitReservationResponse;

public class SubmitReservationGenerator
  extends SoapGenerator
{
  private ResourceScheduler rs;

  public SubmitReservationGenerator()
  {
    super();
    rs = new ResourceScheduler();
  }

  public Reservation submitByRoomID( Reservation submitted, int roomID )
  {
    int reservationId;
    ResourceScheduler scheduler;

    reservationId = submitReservation( submitted, roomID );

    scheduler = new ResourceScheduler();
    scheduler.setDbName( getIrmaDb() );
    return scheduler.getReservation( reservationId );
  }

  public Reservation submitWithGroupCodeAndResourceName( Reservation r,
                                                         StudyroomLocation loc,
                                                         String resourceName )
  {
    ResourceScheduler rs = new ResourceScheduler();
    int resourceId = rs.getResourceId( loc, resourceName );

    int reservationId = this.submitReservation( r, resourceId );
    return rs.getReservation( reservationId );
  }

  public Reservation submitWithResourceCode( Reservation r,
                                             ClassroomLocation loc )
  {
    // Find resource id
    int resourceId = loc.getResourceId();
    if ( resourceId == 0 )
    {
      throw new WebApplicationException( ErrorResponse.RESOURCE_DNE );
    }

    int reservationId = this.submitReservation( r, resourceId );
    return rs.getReservation( reservationId );
  }

  private int submitReservation( Reservation r, int resourceId )
  {
    r.addResource( new Resource( resourceId,
                                 "" ) ); // Note: safe to pass in empty string b/c it will be replaced anyways
    SubmitReservation sr = new SubmitReservation();
    sr.setData( r.getReservationData() );

    try
    {
      SubmitReservationResponse response =
        this.stub.submitReservation( sr, this.credentials );
      ReservationData result = response.getSubmitReservationResult();
      // If invalid, error out
      if ( !result.getIsValid() )
      {
        throw new WebApplicationException( ErrorResponse.createSoapError( result.getAllChildBrokenBusinessRules() ) );
      }

      // Process the result
      return result.getReservationBaseData().getId();
    }
    catch ( RemoteException e )
    {
      throw new WebApplicationException( ErrorResponse.createServerError( e ) );
    }
  }
}
