package edu.ucla.library.libservices.rswrapper.generators;

import java.util.Date;
import java.util.List;

import edu.ucla.library.libservices.rswrapper.beans.ClassroomLocation;
import edu.ucla.library.libservices.rswrapper.beans.Reservation;

public class ScheduleGeneratorForResource
  extends ReservationsGeneratorForResource
{
  public List<Reservation> getListOfReservations( ClassroomLocation loc,
                                                  Date startTime,
                                                  Date endTime )
  {
    return this.getReservations( loc, startTime, endTime );
  }
}
