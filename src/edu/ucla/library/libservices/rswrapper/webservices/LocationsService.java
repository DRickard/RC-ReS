package edu.ucla.library.libservices.rswrapper.webservices;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import java.text.ParseException;

import javax.servlet.ServletConfig;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;

import org.codehaus.jackson.map.annotate.JsonView;

//import edu.ucla.library.libservices.rswrapper.utility.Utility;
import edu.ucla.library.libservices.rswrapper.utility.messages.ErrorResponse;

import edu.ucla.library.libservices.rswrapper.beans.ReservationViews;
import edu.ucla.library.libservices.rswrapper.beans.ResourceAvailableTimes;
import edu.ucla.library.libservices.rswrapper.beans.StudyroomLocation;
import edu.ucla.library.libservices.rswrapper.generators.LocationsGeneratorForGroup;
import edu.ucla.library.libservices.rswrapper.utility.converters.DateTimeConverter;

@Path("/locations")
public class LocationsService {
	
	@Context
	ServletConfig config;
	
	public LocationsService() {
		super();
	}
	
	@JsonView(ReservationViews.StartEndPair.class)
	@GET
	@Path("{groupCode}/{date}")
	@Produces("application/json")
	public List<ResourceAvailableTimes> generateTimes(
			@PathParam("groupCode") String groupCode,
			@PathParam("date") String date
	) {
		// Get the location object
		StudyroomLocation loc = StudyroomLocation.getStudyroomLocation(groupCode);
		if (!loc.isValid()) {
			throw new WebApplicationException( ErrorResponse.GROUP_DNE);
		}
		
		// Parse the date parameter
		Date startDate;
		try {
			startDate = DateTimeConverter.createDateFormat().parse(date);
		}
		catch (ParseException e) {
			throw new WebApplicationException();
		}
		
		// Calculate the end date - Add one date
		Calendar cal = Calendar.getInstance();
		cal.setTime(startDate);
		cal.add(Calendar.DATE,1);
		Date endDate = cal.getTime();
		
		// Invoke the generator
		LocationsGeneratorForGroup generator;
		String dbname = config.getServletContext().getInitParameter("datasource.irma");
		generator = new LocationsGeneratorForGroup();
		generator.setDbName(dbname);
		return generator.getRoomAvailableTimes(loc, startDate, endDate);
	}
}
