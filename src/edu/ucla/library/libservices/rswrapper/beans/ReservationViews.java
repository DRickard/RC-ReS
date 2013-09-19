package edu.ucla.library.libservices.rswrapper.beans;

public class ReservationViews
{
  public static class StartEndPair
  {
  } // start and end only

  public static class AvailableSlot
    extends StartEndPair
  {
  } // + minimal resource information (capacity, resourceId)

  public static class BasicReservation
    extends AvailableSlot
  {
  } // + basic reservation information

  public static class FullReservation
    extends BasicReservation
  {
  } // + full reservation information
}
