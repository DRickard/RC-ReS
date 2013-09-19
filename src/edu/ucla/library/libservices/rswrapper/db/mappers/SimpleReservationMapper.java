package edu.ucla.library.libservices.rswrapper.db.mappers;

import edu.ucla.library.libservices.rswrapper.beans.SimpleReservation;

import edu.ucla.library.libservices.rswrapper.utility.converters.DateTimeConverter;

import java.sql.ResultSet;
import java.sql.SQLException;

import java.text.SimpleDateFormat;

import java.util.Date;

import org.springframework.jdbc.core.RowMapper;

public class SimpleReservationMapper
  implements RowMapper
{
  public SimpleReservationMapper()
  {
    super();
  }

  public Object mapRow( ResultSet rs, int rowNum )
    throws SQLException
  {
    SimpleReservation bean;
    
    bean = new SimpleReservation();
    bean.setEndDate( rs.getString( "endDate" ) );
    bean.setRoomName( rs.getString( "roomName" ) );
    bean.setSchedID( rs.getInt( "sched_id" ) );
    bean.setStartDate( rs.getString( "startDate" ) );
    bean.setTitle( rs.getString( "title" ) );
    
    return bean;
  }
}