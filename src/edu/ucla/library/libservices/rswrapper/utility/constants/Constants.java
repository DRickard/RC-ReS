package edu.ucla.library.libservices.rswrapper.utility.constants;

public class Constants
{
  //field names from IRMA db
  static public final String GROUP_ID = "grp_id";

  static public final String RESOURCE_ID = "res_id";
  static public final String RESOURCE_NAME = "roomname";
  static public final String RESOURCE_CAP = "capacity";
  static public final String RESOURCE_TITLE = "title";
  static public final String RESOURCE_TYPE_ID = "res_type_id";

  static public final String SMALL_IMAGE = "smallimage";
  static public final String BIG_IMAGE = "bigimage";

  static public final String RESERVATION_ID = "sched_id";
  static public final String RESERVATION_NAME = "reservationname";

  static public final String UDF_ID = "udf_id";
  static public final String UDF_VALUE = "udf_string";

  static public final String START = "startdate";
  static public final String END = "enddate";

  static public final String NUM_ATTENDEES = "num_attendees";
  
  //time values
  public static final int HALF_HOUR = 30;
  public static final int MIN_MEET = 2;
  
  //other
  //RC = Research Commons
  //SR = Powell Study Rooms
  public static final int DATE_PARAM_COUNT = 6;
  public static final int[] RC_GROUP_IDS = {56,79}; //{82,83}
  public static final int[] SR_GROUP_IDS = {58}; //{56,79}
  public static final int MAGI_RC_ID = 19;
  public static final int SETHOURS_RC_ID = 48;
  public static final int SETHOURS_SR_ID = 54;
}
