package edu.ucla.library.libservices.rswrapper.generators;

import java.rmi.RemoteException;

import javax.ws.rs.WebApplicationException;

import webservice.resourcescheduler.peoplecube.ResourceSchedulerServiceStub.DeleteReservation;
import webservice.resourcescheduler.peoplecube.ResourceSchedulerServiceStub.DeleteReservationResponse;
import webservice.resourcescheduler.peoplecube.ResourceSchedulerServiceStub.ReservationData;

import edu.ucla.library.libservices.rswrapper.utility.messages.ErrorResponse;
import edu.ucla.library.libservices.rswrapper.beans.Reservation;
import edu.ucla.library.libservices.rswrapper.db.source.ResourceScheduler;

import webservice.resourcescheduler.peoplecube.ResourceSchedulerServiceStub;

public class DeleteReservationGenerator
  extends SoapGenerator
{
  public DeleteReservationGenerator()
  {
    super();
  }

  public Reservation delete( int resID )
  {
    Reservation reservation;
    ReservationData rData;
    ResourceScheduler scheduler;

    scheduler = new ResourceScheduler();
    scheduler.setDbName( getIrmaDb() );
    reservation = scheduler.getReservation( resID );

    if ( reservation == null )
    {
      throw new WebApplicationException( ErrorResponse.RESERVATION_DNE );
    }

    rData = new ReservationData();
    // For deletion, second param does not matter
    rData = reservation.getReservationData();

    try
    {
      DeleteReservation deleter;
      DeleteReservationResponse response;
      ReservationData result;

      deleter = new DeleteReservation();
      deleter.setData( rData );
      response = this.stub.deleteReservation( deleter, this.credentials );
      result = response.getDeleteReservationResult();

      if ( !result.getIsValid() )
      {
        throw new WebApplicationException( ErrorResponse.createSoapError( result.getAllChildBrokenBusinessRules() ) );
      }

      // Return what you deleted
      return reservation;
    }
    // If the id number does not exist, it seems it just triggers a
    // Soap Exception
    catch ( RemoteException e )
    {
      throw new WebApplicationException( ErrorResponse.createServerError( e ) );
    }

  }
}
