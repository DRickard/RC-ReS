package edu.ucla.library.libservices.rswrapper.db.mappers;

import edu.ucla.library.libservices.rswrapper.utility.constants.Constants;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

import org.springframework.jdbc.core.RowMapper;

import edu.ucla.library.libservices.rswrapper.beans.Reservation;
import edu.ucla.library.libservices.rswrapper.beans.ResourceSchedule;

public class ResourceScheduleMapper implements RowMapper {
	
	public ResourceScheduleMapper() {
		super();
	}
	
	public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
		ResourceSchedule bean = new ResourceSchedule();
		
		bean.setSchedule(new LinkedList<Reservation>());
		bean.setName(rs.getString( Constants.RESOURCE_NAME));
		
		return bean;
	}
}
