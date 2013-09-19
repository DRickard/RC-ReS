package edu.ucla.library.libservices.rswrapper.generators;

import java.rmi.RemoteException;

import javax.ws.rs.WebApplicationException;

import webservice.resourcescheduler.peoplecube.ResourceSchedulerServiceStub.GetReservation;
import webservice.resourcescheduler.peoplecube.ResourceSchedulerServiceStub.GetReservationResponse;
import webservice.resourcescheduler.peoplecube.ResourceSchedulerServiceStub.ReservationData;
import webservice.resourcescheduler.peoplecube.ResourceSchedulerServiceStub.ReservationRequest;
import webservice.resourcescheduler.peoplecube.ResourceSchedulerServiceStub.SubmitReservation;
import webservice.resourcescheduler.peoplecube.ResourceSchedulerServiceStub.SubmitReservationResponse;

import edu.ucla.library.libservices.rswrapper.utility.messages.ErrorResponse;
import edu.ucla.library.libservices.rswrapper.beans.Reservation;
import edu.ucla.library.libservices.rswrapper.db.source.ResourceScheduler;

import webservice.resourcescheduler.peoplecube.ResourceSchedulerServiceStub;

public class UpdateReservationGenerator
  extends SoapGenerator
{

  public UpdateReservationGenerator()
  {
    super();
  }

  public Reservation update( Reservation submitted, int resID )
  {
    try
    {
      // Get the reservation
      int reservationId;
      GetReservation getter;
      GetReservationResponse response;
      Reservation reservation;
      ReservationData result;
      ReservationData submitResult;
      ReservationRequest request;
      ResourceScheduler scheduler;
      SubmitReservation submitter;
      SubmitReservationResponse submitResponse;

      request = new ReservationRequest();
      getter = new GetReservation();

      request.setReservationId( resID );
      getter.setRequest( request );

      response = this.stub.getReservation( getter, this.credentials );
      result = response.getGetReservationResult();
      if ( !result.getIsValid() )
      { // If the id # does not exist, it'll go here
        throw new WebApplicationException( ErrorResponse.createSoapError( result.getAllChildBrokenBusinessRules() ) );
      }

      // Update the fields
      reservation = new Reservation( result );
      reservation.updateFields( submitted );

      // Submit the reservation
      submitter = new SubmitReservation();
      submitter.setData( reservation.getReservationData() );
      submitResponse = this.stub.submitReservation( submitter, this.credentials );
      submitResult = submitResponse.getSubmitReservationResult();
      if ( !submitResult.getIsValid() )
      { // If invalid, error out
        throw new WebApplicationException( ErrorResponse.createSoapError( submitResult.getAllChildBrokenBusinessRules() ) );
      }

      // Return the updated reservation
      reservationId = submitResult.getReservationBaseData().getId();
      scheduler = new ResourceScheduler();
      scheduler.setDbName( getIrmaDb() );
      return scheduler.getReservation( reservationId );
    }
    catch ( RemoteException e )
    {
      throw new WebApplicationException( ErrorResponse.createServerError( e ) );
    }
  }

}
