package edu.ucla.library.libservices.rswrapper.db.mappers;

import edu.ucla.library.libservices.rswrapper.utility.constants.Constants;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

import org.springframework.jdbc.core.RowMapper;

import edu.ucla.library.libservices.rswrapper.beans.Reservation;
import edu.ucla.library.libservices.rswrapper.beans.ResourceAvailableTimes;

public class ResourceAvailableTimeMapper implements RowMapper {
	
	public ResourceAvailableTimeMapper() {
		super();
	}
	
	public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
		ResourceAvailableTimes bean = new ResourceAvailableTimes();
		
		bean.setAvailableTimes(new LinkedList<Reservation>());
		bean.setName(rs.getString( Constants.RESOURCE_NAME));
		
		return bean;
	}
}
