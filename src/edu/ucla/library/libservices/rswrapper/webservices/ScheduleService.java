package edu.ucla.library.libservices.rswrapper.webservices;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;

import org.codehaus.jackson.map.annotate.JsonView;

//import edu.ucla.library.libservices.rswrapper.utility.Utility;
import edu.ucla.library.libservices.rswrapper.utility.messages.ErrorResponse;
import edu.ucla.library.libservices.rswrapper.beans.ClassroomLocation;
import edu.ucla.library.libservices.rswrapper.beans.Reservation;
import edu.ucla.library.libservices.rswrapper.beans.ReservationViews;
import edu.ucla.library.libservices.rswrapper.beans.ResourceSchedule;
import edu.ucla.library.libservices.rswrapper.beans.StudyroomLocation;
import edu.ucla.library.libservices.rswrapper.generators.ScheduleGeneratorForGroup;
import edu.ucla.library.libservices.rswrapper.generators.ScheduleGeneratorForResource;
import edu.ucla.library.libservices.rswrapper.utility.converters.DateTimeConverter;

@Path("/schedule")
public class ScheduleService {
	
	@Context
	ServletConfig config;
	
	private Date start;
	private Date end;
	
	public ScheduleService()
	{
		super();
	}
	
	@JsonView(ReservationViews.BasicReservation.class)
	@GET
	@Path("group/{groupCode}")
	@Produces("application/json")
	public List<ResourceSchedule> getGroupReservations(
			@PathParam("groupCode") String groupCode,
			@DefaultValue("")@QueryParam("start") String start,
			@DefaultValue("")@QueryParam("end") String end
	)
	{
		this.fillInDates(start, end);		
		ScheduleGeneratorForGroup generator = new ScheduleGeneratorForGroup();
		generator.setDbName(
			config.getServletContext().getInitParameter("datasource.irma")
		);
		
		// Get the location object
		StudyroomLocation loc = StudyroomLocation.getStudyroomLocation(groupCode);
		if (!loc.isValid()) {
			throw new WebApplicationException( ErrorResponse.GROUP_DNE);
		}
		
		return generator.getResourceSchedules(loc, this.start, this.end);
	}
	
	@JsonView(ReservationViews.BasicReservation.class)
	@GET
	@Path("resource/{roomName}")
	@Produces("application/json")
	public List<Reservation> getRoomReservations(
			@PathParam("roomName") String roomName,
			@DefaultValue("")@QueryParam("start") String start,
			@DefaultValue("")@QueryParam("end") String end			
	)
	{
		this.fillInDates(start, end);
		ScheduleGeneratorForResource generator = new ScheduleGeneratorForResource();
		generator.setDbName(
			config.getServletContext().getInitParameter("datasource.irma")
		);
		
		// Find location
		ClassroomLocation loc = ClassroomLocation.getClassroomLocation(roomName);
		if (!loc.isValid()) {
			throw new WebApplicationException( ErrorResponse.RESOURCE_DNE);
		}
		
		return generator.getListOfReservations(loc, this.start, this.end);
	}
	
	private void fillInDates(String start, String end) {
		Calendar cal = Calendar.getInstance();
	
		if (start.equals("")) {
			this.start = new Date();
		}
		else {
			try {
				this.start = DateTimeConverter.createDateTimeFormat().parse(start);
			}
			catch (ParseException e) {
				throw new WebApplicationException( ErrorResponse.INVALID_DATETIME_FORMAT);
			}
		}

		if (end.equals("")) {
			cal.setTime(this.start);
			cal.add(Calendar.DATE, 1);
			this.end = cal.getTime();
		}
		else {
			try {
				this.end = DateTimeConverter.createDateTimeFormat().parse(end);
			}
			catch (ParseException e) {
				throw new WebApplicationException( ErrorResponse.INVALID_DATETIME_FORMAT);
			}
		}
	}
}