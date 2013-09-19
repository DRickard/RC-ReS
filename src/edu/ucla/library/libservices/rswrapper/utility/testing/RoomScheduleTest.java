package edu.ucla.library.libservices.rswrapper.utility.testing;

import edu.ucla.library.libservices.rswrapper.roomsched.AvailableTime;
import edu.ucla.library.libservices.rswrapper.roomsched.OpenRoom;
import edu.ucla.library.libservices.rswrapper.roomsched.RoomSchedule;
import edu.ucla.library.libservices.rswrapper.roomsched.RoomStatus;

import static org.junit.Assert.*;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class RoomScheduleTest {

	@Test
	public void testStudyRoomScheduleDateIntInt() throws Exception {
		int rooms = 5;
		SimpleDateFormat simple = new SimpleDateFormat("MM-dd-yyyy hh:mm a");
		
		Date start, end;
		start = simple.parse("03-27-2012 03:40 PM");
		end = simple.parse("03-28-2012 01:30 AM");

    RoomSchedule s = new RoomSchedule(start, end, rooms);
		assertEquals(simple.parse("03-27-2012 03:30 PM"), s.getBasetime());
		assertEquals(rooms, s.getNumrooms());
		
		// Check the room status for each room in the first half hour
		for (int i = 0; i < s.getNumrooms(); i++) {
			assertEquals(s.getStatus(start,i), RoomStatus.CLOSED);
		}
		
		// Check the boundaries are valid
		assertEquals( RoomStatus.DOESNOTEXIST, s.getStatus(simple.parse("03-27-2012 03:00 PM"),0));
		assertEquals( RoomStatus.CLOSED, s.getStatus(simple.parse("03-27-2012 03:30 PM"),0));
		assertEquals( RoomStatus.CLOSED, s.getStatus(simple.parse("03-28-2012 01:00 AM"),0));
		assertEquals( RoomStatus.DOESNOTEXIST, s.getStatus(simple.parse("03-28-2012 01:30 AM"),0));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testStudyRoomScheduleDateIntIntWithStartGreaterThanDate() throws Exception {
		// Odd parameters
		SimpleDateFormat simple = new SimpleDateFormat("MM-dd-yyyy hh:mm a");
		
		Date start2, end2;
		start2 = simple.parse("03-28-2012 01:30 AM");
		end2 = simple.parse("03-27-2012 03:30 PM");
		new RoomSchedule(start2, end2, 1);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testStudyRoomScheduleDateIntIntWithNumRooms0() throws Exception {
		// Odd parameters
		SimpleDateFormat simple = new SimpleDateFormat("MM-dd-yyyy hh:mm a");
		
		Date start2, end2;
		start2 = simple.parse("03-28-2012 01:30 AM");
		end2 = simple.parse("03-28-2012 03:30 PM");
		new RoomSchedule(start2, end2, 0);
	}
		
	@Test
	public void testGetStatus() throws Exception {
		int numrooms = 5;
		SimpleDateFormat simple = new SimpleDateFormat("MM-dd-yyyy hh:mm a");
		Date start = simple.parse("03-27-2012 02:00 PM");
		Date end = simple.parse("03-28-2012 02:30 AM");

    RoomSchedule s = new RoomSchedule(start, end, numrooms);
		
		// Invalid number of rooms parameter
		assertEquals( RoomStatus.DOESNOTEXIST, s.getStatus(start, -1));
		assertEquals( RoomStatus.DOESNOTEXIST, s.getStatus(start, numrooms));		
	
		// Right before start of schedule
		Date beforeStart = simple.parse("03-27-2012 01:59 AM");
		assertEquals( RoomStatus.DOESNOTEXIST, s.getStatus(beforeStart,0));
		
		// At start of schedule
		assertEquals( RoomStatus.CLOSED, s.getStatus(start,0));
		
		// Right before end of schedule
		Date beforeEnd = simple.parse("03-28-2012 02:29 AM");
		assertEquals( RoomStatus.CLOSED, s.getStatus(beforeEnd,0));
	
		// After end of schedule
		assertEquals( RoomStatus.DOESNOTEXIST, s.getStatus(end,0));
	}

	@Test
	public void testAddHoursOfOperation() throws Exception {
		int numrooms = 5;
		
		SimpleDateFormat simple = new SimpleDateFormat("MM-dd-yyyy hh:mm a");

		// Add normal looking hours
    RoomSchedule s = new RoomSchedule(simple.parse("03-27-2012 05:00 AM"), simple.parse("03-28-2012 07:00 AM"), numrooms);
		Date start = simple.parse("03-27-2012 07:30 AM");
		Date end = simple.parse("03-28-2012 02:00 AM");
		s.addHoursOfOperation(start, end);
		
		assertEquals( RoomStatus.CLOSED, s.getStatus(simple.parse("03-27-2012 05:00 AM"), 0));
		assertEquals( RoomStatus.CLOSED, s.getStatus(simple.parse("03-27-2012 07:29 AM"), 0));
		assertEquals( RoomStatus.OPEN, s.getStatus(simple.parse("03-27-2012 07:30 AM"), 0));
		assertEquals( RoomStatus.OPEN, s.getStatus(simple.parse("03-28-2012 01:59 AM"), 0));
		assertEquals( RoomStatus.CLOSED, s.getStatus(simple.parse("03-28-2012 02:00 AM"), 0));
		
		// Add odd looking hours		
    RoomSchedule s2 = new RoomSchedule(simple.parse("03-27-2012 05:00 AM"), simple.parse("03-28-2012 07:00 AM"), numrooms);
		Date start2 = simple.parse("03-27-2012 06:15 AM");
		Date end2 = simple.parse("03-27-2012 06:25 AM");
		s2.addHoursOfOperation(start2, end2);
		
		assertEquals( RoomStatus.CLOSED, s2.getStatus(simple.parse("03-27-2012 05:29 AM"), 0));
		assertEquals( RoomStatus.OPEN, s2.getStatus(simple.parse("03-27-2012 06:00 AM"), 0));
		assertEquals( RoomStatus.OPEN, s2.getStatus(simple.parse("03-27-2012 06:29 AM"), 0));
		assertEquals( RoomStatus.CLOSED, s2.getStatus(simple.parse("03-27-2012 06:30 AM"), 0));
		
		// Add hours exceeding schedule
    RoomSchedule s3 = new RoomSchedule(simple.parse("03-27-2012 05:00 AM"), simple.parse("03-28-2012 07:00 AM"), numrooms);
		Date start3 = simple.parse("03-27-2012 02:00 AM");
		Date end3 = simple.parse("03-28-2012 09:00 AM");
		s3.addHoursOfOperation(start3, end3);
		assertEquals( RoomStatus.DOESNOTEXIST, s3.getStatus(simple.parse("03-27-2012 04:59 AM"), 0));
		assertEquals( RoomStatus.OPEN, s3.getStatus(simple.parse("03-27-2012 05:00 AM"), 0));
		assertEquals( RoomStatus.OPEN, s3.getStatus(simple.parse("03-28-2012 06:30 AM"), 0));
		assertEquals( RoomStatus.DOESNOTEXIST, s3.getStatus(simple.parse("03-28-2012 07:00 AM"), 0));
		
		// Add hours such that both start and end time are after the schedule
    RoomSchedule s4 = new RoomSchedule(simple.parse("03-27-2012 05:00 AM"), simple.parse("03-28-2012 07:00 AM"), numrooms);
		Date start4 = simple.parse("03-28-2012 07:30 AM");
		Date end4 = simple.parse("03-28-2012 09:00 AM");
		s4.addHoursOfOperation(start4, end4);
		assertEquals( RoomStatus.CLOSED, s4.getStatus(simple.parse("03-28-2012 06:30 AM"), 0));
		assertEquals( RoomStatus.DOESNOTEXIST, s4.getStatus(simple.parse("03-28-2012 07:00 AM"), 0));
		assertEquals( RoomStatus.DOESNOTEXIST, s4.getStatus(simple.parse("03-28-2012 08:00 AM"), 0));
		
		// Add hours that are overlapping
		start4 = simple.parse("03-27-2012 04:30 AM");
		end4 = simple.parse("03-27-2012 06:00 AM");
		s4.addHoursOfOperation(start4, end4);
		assertEquals( RoomStatus.DOESNOTEXIST, s4.getStatus(simple.parse("03-27-2012 04:30 AM"), 0));
		assertEquals( RoomStatus.OPEN, s4.getStatus(simple.parse("03-27-2012 05:00 AM"), 0));
		assertEquals( RoomStatus.CLOSED, s4.getStatus(simple.parse("03-27-2012 06:00 AM"), 0));		
	}

	@Test
	public void testClearHoursOfOperation() throws Exception {
		int numrooms = 5;
		
		SimpleDateFormat simple = new SimpleDateFormat("MM-dd-yyyy hh:mm a");

    RoomSchedule s = new RoomSchedule(simple.parse("03-27-2012 05:00 AM"), simple.parse("03-28-2012 07:00 AM"), numrooms);
		Date start = simple.parse("03-27-2012 07:30 AM");
		Date end = simple.parse("03-28-2012 02:00 AM");
		s.addHoursOfOperation(start, end);
		
		// Check that it is OPEN
		assertEquals( RoomStatus.OPEN, s.getStatus(simple.parse("03-27-2012 07:30 AM"), 0));
		assertEquals( RoomStatus.OPEN, s.getStatus(simple.parse("03-27-2012 02:30 PM"), 0));
		
		// Add to schedule then toggle
		s.addToSchedule(0, simple.parse("03-27-2012 02:30 PM"), simple.parse("03-27-2012 04:30 PM"));
		s.clearHoursOfOperation();
		
		// Check that it is OPEN
		assertEquals( RoomStatus.CLOSED, s.getStatus(simple.parse("03-27-2012 07:30 AM"), 0));
		assertEquals( RoomStatus.RESERVED, s.getStatus(simple.parse("03-27-2012 02:30 PM"), 0));
	}
	
	@Test	
	public void testAddToSchedule() throws Exception {
		// Prep work
		int numrooms = 5;		
		SimpleDateFormat simple = new SimpleDateFormat("MM-dd-yyyy hh:mm a");
    RoomSchedule s = new RoomSchedule(simple.parse("03-27-2012 05:00 AM"), simple.parse("03-28-2012 07:00 AM"), numrooms);
		Date start = simple.parse("03-27-2012 07:30 AM");
		Date end = simple.parse("03-28-2012 02:00 AM");
		s.addHoursOfOperation(start, end);
		
		// Add to schedule, normal case		
		boolean res1 = s.addToSchedule(0, simple.parse("03-27-2012 02:30 PM"), simple.parse("03-27-2012 04:30 PM"));
		assertEquals( RoomStatus.OPEN, s.getStatus(simple.parse("03-27-2012 02:00 PM"), 0));
		assertEquals( RoomStatus.RESERVED, s.getStatus(simple.parse("03-27-2012 02:30 PM"), 0));
		assertEquals( RoomStatus.RESERVED, s.getStatus(simple.parse("03-27-2012 03:00 PM"), 0));
		assertEquals( RoomStatus.RESERVED, s.getStatus(simple.parse("03-27-2012 03:30 PM"), 0));
		assertEquals( RoomStatus.RESERVED, s.getStatus(simple.parse("03-27-2012 04:00 PM"), 0));
		assertEquals( RoomStatus.OPEN, s.getStatus(simple.parse("03-27-2012 04:30 PM"), 0));
		assertEquals(true, res1);
		
		// Add to schedule, odd case		
		boolean res2 = s.addToSchedule(0, simple.parse("03-27-2012 10:45 AM"), simple.parse("03-27-2012 12:01 PM"));
		assertEquals( RoomStatus.OPEN, s.getStatus(simple.parse("03-27-2012 10:00 AM"), 0));
		assertEquals( RoomStatus.RESERVED, s.getStatus(simple.parse("03-27-2012 10:30 AM"), 0));
		assertEquals( RoomStatus.RESERVED, s.getStatus(simple.parse("03-27-2012 12:00 PM"), 0));
		assertEquals( RoomStatus.OPEN, s.getStatus(simple.parse("03-27-2012 12:30 PM"), 0));
		assertEquals(true, res2);		

		// Add to schedule, already exists
		boolean res3 = s.addToSchedule(0, simple.parse("03-27-2012 03:30 PM"), simple.parse("03-27-2012 05:30 PM"));
		assertEquals(false, res3);
		
		// Add to schedule, index out of bounds
		boolean res4 = s.addToSchedule(-1, simple.parse("03-27-2012 05:30 AM"), simple.parse("03-27-2012 06:30 AM"));
		assertEquals(false, res4);
		
		// Add to schedule, index out of bounds
		boolean res5 = s.addToSchedule(5, simple.parse("03-27-2012 05:30 AM"), simple.parse("03-27-2012 06:30 AM"));
		assertEquals(false, res5);
		
		// Add to schedule, start time after end time
		boolean res8 = s.addToSchedule(0, simple.parse("03-28-2012 07:30 AM"), simple.parse("03-28-2012 06:30 AM"));
		assertEquals(false, res8);

    RoomSchedule s2 = new RoomSchedule(start, end, 1);
		s2.addHoursOfOperation(start, end);
		// Add to schedule, before start
		s2.addToSchedule(0, simple.parse("03-27-2012 06:30 AM"), simple.parse("03-27-2012 08:00 AM"));
		assertEquals( RoomStatus.RESERVED, s2.getStatus(simple.parse("03-27-2012 07:30 AM"), 0));
		// Add to schedule, after start
		s2.addToSchedule(0, simple.parse("03-28-2012 01:00 AM"), simple.parse("03-28-2012 05:30 AM"));
		assertEquals( RoomStatus.RESERVED, s2.getStatus(simple.parse("03-28-2012 01:30 AM"), 0));
		
	}
	
	@Test
	public void testFirstTimeRoomIsAvailable() throws Exception {
		int numrooms = 3;
		SimpleDateFormat simple = new SimpleDateFormat("MM-dd-yyyy hh:mm a");		
		// 26 hour schedule
    RoomSchedule s = new RoomSchedule(simple.parse("03-27-2012 12:00 PM"), simple.parse("03-27-2012 06:00 PM"), numrooms);
		
		// Test - everything is null
		assertEquals(null, s.firstTimeRoomIsAvailable());
		
		// Test - add hours of operation
		s.addHoursOfOperation(simple.parse("03-27-2012 02:30 PM"), simple.parse("03-27-2012 05:00 PM"));
		assertEquals(simple.parse("03-27-2012 02:30 PM"), s.firstTimeRoomIsAvailable());
		
		// Test - add some rooms - There is a gap beginning at 2, and a gap in the middle of 1
		s.addToSchedule(0, simple.parse("03-27-2012 02:30 PM"), simple.parse("03-27-2012 05:00 PM"));
		s.addToSchedule(1, simple.parse("03-27-2012 02:30 PM"), simple.parse("03-27-2012 04:00 PM"));
		s.addToSchedule(1, simple.parse("03-27-2012 04:30 PM"), simple.parse("03-27-2012 05:00 PM"));
		s.addToSchedule(2, simple.parse("03-27-2012 03:30 PM"), simple.parse("03-27-2012 05:00 PM"));
		assertEquals(simple.parse("03-27-2012 02:30 PM"), s.firstTimeRoomIsAvailable());
		
		// Test - there is only a gap in 1
		s.addToSchedule(2, simple.parse("03-27-2012 02:30 PM"), simple.parse("03-27-2012 03:30 PM"));
		assertEquals(simple.parse("03-27-2012 04:00 PM"), s.firstTimeRoomIsAvailable());
		
		// Test - Everything is filled
		s.addToSchedule(1, simple.parse("03-27-2012 04:00 PM"), simple.parse("03-27-2012 04:30 PM"));
		assertEquals(null, s.firstTimeRoomIsAvailable());
	}
	
	
	@Test
	public void testGenerateAvailableTimes() throws Exception {
		int numrooms = 3;
		SimpleDateFormat simple = new SimpleDateFormat("MM-dd-yyyy hh:mm a");
    RoomSchedule s = new RoomSchedule(simple.parse("03-27-2012 12:00 PM"), simple.parse("03-27-2012 05:00 PM"), numrooms);
		
		// Add hours of operation
		s.addHoursOfOperation(simple.parse("03-27-2012 01:30 PM"), simple.parse("03-27-2012 05:00 PM"));
		
		List<AvailableTime>[] ar = s.generateAvailableTimes();
		assertEquals(1, ar[0].size());
		assertEquals(1, ar[1].size());
		assertEquals(1, ar[2].size());
		
		s.addToSchedule(0, simple.parse("03-27-2012 02:30 PM"), simple.parse("03-27-2012 04:30 PM"));
		ar = s.generateAvailableTimes();
		assertEquals(2, ar[0].size());
		
		s.addToSchedule(1, simple.parse("03-27-2012 01:30 PM"), simple.parse("03-27-2012 05:00 PM"));
		ar = s.generateAvailableTimes();
		assertEquals(0, ar[1].size());
	}
	
	/*
	 * assertEquals(expected, what it is)
	 */
	@Test
	public void testGenerateOpenStudyRoomsByDuration() throws Exception {
		List<OpenRoom> ar;
		
		int numrooms = 3;
		SimpleDateFormat simple = new SimpleDateFormat("MM-dd-yyyy hh:mm a");		
		// 5 hour schedule
    RoomSchedule s = new RoomSchedule(simple.parse("03-27-2012 12:00 PM"), simple.parse("03-27-2012 05:00 PM"), numrooms);
		
		// Add hours of operation
		s.addHoursOfOperation(simple.parse("03-27-2012 01:30 PM"), simple.parse("03-27-2012 05:00 PM"));
		
		// Full open schedule will have 12 possible sizes
		ar = s.generateOpenStudyRoomsByDuration(4, simple.parse("03-27-2012 12:00 PM"), simple.parse("03-27-2012 05:00 PM"));
		assertEquals(12, ar.size());
		
		// For half hour, 21 slots (1:30, 2:00, 2:30, 3:00, 3:30, 4:00, 4:30)
		ar = s.generateOpenStudyRoomsByDuration(1, simple.parse("03-27-2012 12:00 PM"), simple.parse("03-27-2012 05:00 PM"));
		assertEquals(21, ar.size());
		
		// Add a time such that 0 has no 2 hour rooms
		s.addToSchedule(0, simple.parse("03-27-2012 02:30 PM"), simple.parse("03-27-2012 04:30 PM"));
		ar = s.generateOpenStudyRoomsByDuration(4, simple.parse("03-27-2012 12:00 PM"), simple.parse("03-27-2012 05:00 PM"));
		assertEquals(8, ar.size());
		
		// Add a time such that 1 has only 1 2 hour room
		s.addToSchedule(1, simple.parse("03-27-2012 03:30 PM"), simple.parse("03-27-2012 04:00 PM"));
		ar = s.generateOpenStudyRoomsByDuration(4, simple.parse("03-27-2012 12:00 PM"), simple.parse("03-27-2012 05:00 PM"));
		assertEquals(5, ar.size());
		
		// Add a time such that 2 has only 1 2 hour room
		s.addToSchedule(2, simple.parse("03-27-2012 02:30 PM"), simple.parse("03-27-2012 03:00 PM"));
		ar = s.generateOpenStudyRoomsByDuration(4, simple.parse("03-27-2012 12:00 PM"), simple.parse("03-27-2012 05:00 PM"));
		assertEquals(2, ar.size());
		
		// Test for case where from is later than to : empty list
		ar = s.generateOpenStudyRoomsByDuration(4, simple.parse("03-27-2012 12:00 PM"), simple.parse("03-27-2012 05:00 AM"));
		assertEquals(0, ar.size());
		
		/*
		System.out.println("Sample Print Out");
		for (OpenStudyRoom room : ar ) {
			System.out.println(room);
		}
		*/
	}
	
	@Test
	public void testGenerateAvailableRoomsAtStartTime() throws Exception
	{
		SimpleDateFormat simple = new SimpleDateFormat("MM-dd-yyyy hh:mm a");		
		int numrooms = 4;

    RoomSchedule s = new RoomSchedule(simple.parse("03-27-2012 12:00 PM"), simple.parse("03-27-2012 05:00 PM"), numrooms);
		s.addHoursOfOperation(simple.parse("03-27-2012 12:00 PM"), simple.parse("03-27-2012 05:00 PM"));
		
		s.addToSchedule(0, simple.parse("03-27-2012 02:00 PM"), simple.parse("03-27-2012 05:00 PM")); // should be full time
		// room 1, all free
		s.addToSchedule(2, simple.parse("03-27-2012 12:30 PM"), simple.parse("03-27-2012 05:00 PM")); // room 2, 30 minutes only
		s.addToSchedule(3, simple.parse("03-27-2012 12:00 PM"), simple.parse("03-27-2012 12:30 PM")); // room 3, occupied
		
		List<OpenRoom> list = s.generateAvailableRoomsAtStartTime(4);
		assertEquals(3,list.size());
		assertEquals(4,list.get(0).getNumHalfHours());
		assertEquals(4,list.get(1).getNumHalfHours());
		assertEquals(1,list.get(2).getNumHalfHours());
		
		for ( OpenRoom r : list) {
			assertEquals(r.getTime(), simple.parse("03-27-2012 12:00 PM"));
		}
		
		// Occupy all but
		s.addToSchedule(0, simple.parse("03-27-2012 12:00 PM"), simple.parse("03-27-2012 01:00 PM")); // should be full time
		s.addToSchedule(1, simple.parse("03-27-2012 12:00 PM"), simple.parse("03-27-2012 01:00 PM")); // should be full time
		s.addToSchedule(2, simple.parse("03-27-2012 12:00 PM"), simple.parse("03-27-2012 12:30 PM")); // room 2, 30 minutes only
		List<OpenRoom> list2 = s.generateAvailableRoomsAtStartTime(4);
		assertEquals(0,list2.size());
	}
	
	@Test
	public void testGetBasetime() throws Exception {
		SimpleDateFormat simple = new SimpleDateFormat("MM-dd-yyyy hh:mm a");

    RoomSchedule s = new RoomSchedule(simple.parse("03-27-2012 12:00 PM"), simple.parse("03-27-2012 05:00 PM"), 1);
		assertEquals(simple.parse("03-27-2012 12:00 PM"), s.getBasetime());

    RoomSchedule s2 = new RoomSchedule(simple.parse("03-27-2012 12:05 PM"), simple.parse("03-27-2012 05:00 PM"), 1);
		assertEquals(simple.parse("03-27-2012 12:00 PM"), s2.getBasetime());
	}

	@Test
	public void testGetEndTime() throws Exception {
		SimpleDateFormat simple = new SimpleDateFormat("MM-dd-yyyy hh:mm a");

    RoomSchedule s = new RoomSchedule(simple.parse("03-27-2012 12:00 PM"), simple.parse("03-27-2012 05:00 PM"), 1);
		assertEquals(simple.parse("03-27-2012 05:00 PM"), s.getEndTime());

    RoomSchedule s2 = new RoomSchedule(simple.parse("03-27-2012 12:05 PM"), simple.parse("03-27-2012 05:27 PM"), 1);
		assertEquals(simple.parse("03-27-2012 05:30 PM"), s2.getEndTime());
	}
	
	@Test
	public void testGetNumrooms() throws Exception {
		SimpleDateFormat simple = new SimpleDateFormat("MM-dd-yyyy hh:mm a");
    RoomSchedule s = new RoomSchedule(simple.parse("03-27-2012 12:00 PM"), simple.parse("03-27-2012 05:00 PM"), 17);
		
		assertEquals(17, s.getNumrooms());
	}
}
