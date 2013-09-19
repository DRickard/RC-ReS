package edu.ucla.library.libservices.rswrapper.db.mappers;

import edu.ucla.library.libservices.rswrapper.utility.constants.Constants;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import edu.ucla.library.libservices.rswrapper.beans.Reservation;

public class StartEndMapper implements RowMapper {

	public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
		
		Reservation res = new Reservation();
		res.setStart(rs.getTimestamp( Constants.START));
		res.setEnd(rs.getTimestamp( Constants.END));
		return res;
	}	
}
