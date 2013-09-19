package edu.ucla.library.libservices.rswrapper.db.mappers;

import edu.ucla.library.libservices.rswrapper.utility.constants.Constants;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import edu.ucla.library.libservices.rswrapper.beans.Reservation;
import edu.ucla.library.libservices.rswrapper.beans.Resource;

public class ReservationMapper
  implements RowMapper
{

  public ReservationMapper()
  {
    super();
  }

  public Object mapRow( ResultSet rs, int rowNum )
    throws SQLException
  {

    Reservation res = new Reservation();
    res.setId( rs.getInt( Constants.RESERVATION_ID ) );
    res.setStart( rs.getTimestamp( Constants.START ) );
    res.setEnd( rs.getTimestamp( Constants.END ) );
    res.setDescription( rs.getString( Constants.RESERVATION_NAME ) );
    res.addResource( new Resource( rs.getInt( Constants.RESOURCE_ID ),
                                   rs.getInt( Constants.GROUP_ID ),
                                   rs.getInt( Constants.RESOURCE_TYPE_ID ),
                                   rs.getInt( Constants.RESOURCE_CAP ),
                                   rs.getString( Constants.RESOURCE_NAME ),
                                   rs.getString( Constants.RESOURCE_TITLE ) ) );
    res.setNumberOfAttendees( rs.getInt( Constants.NUM_ATTENDEES ) );

    return res;
  }
}
