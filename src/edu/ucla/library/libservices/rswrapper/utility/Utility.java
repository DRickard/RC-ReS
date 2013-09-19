package edu.ucla.library.libservices.rswrapper.utility;

import java.text.SimpleDateFormat;

public class Utility
{
	/*
	static public int groupIdToLocationId(int groupId)
	{
		switch (groupId) {
		case 56: return 19;
		case 58: return 1;
		case 79: return 19;
		default: return 0;
		}
	}
	*/
	
	/*	
	static public int getGroupId(String groupCode) {
		if (groupCode.equals("rcstudyrooms"))
			return 56;
		if (groupCode.equals("powellstudyrooms"))
			return 58;
		if (groupCode.equals("pods"))
			return 79;
		
		return 0;
	}
	
	static public int resourceIdToMagiLocationId(int resourceId)
	{
		switch (resourceId) {
		case 65: return 9;
		case 66: return 9;
		case 67: return 9;
		case 208: return 19;
		default: return 0;
		}
	}
	
	static public int getResourceId(String resourceCode)
	{
		if (resourceCode.equals("a"))
			return 65;
		if (resourceCode.equals("b"))
			return 66;
		if (resourceCode.equals("c"))
			return 67;
		if (resourceCode.equals("rc"))
			return 208;
		
		return 0;
	}
	*/
	
	/*
	static public int[] getGroupsByLocationCode(String locationCode)
	{
		if (locationCode.equals("powell"))
			return new int[] { 58 };
		if (locationCode.equals("yrl"))
			return new int[] { 79, 56 };
		
		return null;
	}
	*/
	
	
	// Below are helpers to generate prepared statements for the various SQL queries required
	static public String implodeQuestionMarks(int length)
	{
		// Should look like (?,?,?)
		StringBuilder groupIdString = new StringBuilder("(");
		String delim = "";
		for (int i = 0; i < length; i++) {
			groupIdString.append(delim);
			groupIdString.append('?');
			delim = ",";
		}
		groupIdString.append(")");
		
		return groupIdString.toString();
	}
	
	static public SimpleDateFormat createTimeFormat() 
	{
		return new SimpleDateFormat("hh:mm a");
	}
	
	static public SimpleDateFormat createDateFormat() 
	{
		return new SimpleDateFormat("yyyy-MM-dd");
	}
	
	static public SimpleDateFormat createDateTimeFormat() 
	{
		return new SimpleDateFormat("yyyy-MM-dd hh:mm a");
	}	
	
	static public SimpleDateFormat createRSDateTimeFormat() 
	{
		return new SimpleDateFormat("yyyy-MM-dd HH:mm");
	}	
}
