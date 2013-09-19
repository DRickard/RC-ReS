package edu.ucla.library.libservices.rswrapper.utility.messages;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import webservice.resourcescheduler.peoplecube.ResourceSchedulerServiceStub.ArrayOfBrokenRuleData;
import webservice.resourcescheduler.peoplecube.ResourceSchedulerServiceStub.BrokenRuleData;

public class ErrorResponse {

	static public Response createErrorResponse(String message, Status status)
	{
		ResponseBuilder builder = Response.status(status);
		builder.type("text/plain");
		builder.entity(message);
		return builder.build();
	}
	
	static public Response createServerError(Exception e) {
		return ErrorResponse.createErrorResponse(e.getMessage(), Status.INTERNAL_SERVER_ERROR);
	}
	
	static public Response createSoapError(ArrayOfBrokenRuleData data) {
		
		BrokenRuleData[] brokenRuleDataArr = data.getBrokenRuleData();
		String message = "";

		for (int i = 0; i < brokenRuleDataArr.length; i++) {
			BrokenRuleData brokenRuleData = brokenRuleDataArr[i];
			message += brokenRuleData.getDescription() + " ";
		}
		
		return ErrorResponse.createErrorResponse(message, Status.BAD_REQUEST);
	}
	
	static public final Response RESERVATION_DNE = ErrorResponse.createErrorResponse("Reservation does not exist.", Status.NOT_FOUND);
	
	static public final Response RESOURCE_DNE = ErrorResponse.createErrorResponse("Resource does not exist.", Status.NOT_FOUND);
	
	static public final Response GROUP_DNE = ErrorResponse.createErrorResponse("Group does not exist.", Status.NOT_FOUND);
	
	static public final Response LOCATION_DNE = ErrorResponse.createErrorResponse("Location does not exist.", Status.NOT_FOUND);
	
	static public final Response INVALID_DATE_FORMAT = ErrorResponse.createErrorResponse("Invalid datetime format. Format should be YYYY-MM-DD", Status.BAD_REQUEST);
	
	static public final Response INVALID_DATETIME_FORMAT = ErrorResponse.createErrorResponse("Invalid datetime format. Format should be YYYY-MM-DD HH:MM:SS XM", Status.BAD_REQUEST);
	
	static public final Response ID_EXISTS = ErrorResponse.createErrorResponse("The parameter id should not be specified in the payload.", Status.BAD_REQUEST);
	
}
