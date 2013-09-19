package edu.ucla.library.libservices.rswrapper.db.mappers;

import edu.ucla.library.libservices.rswrapper.utility.constants.Constants;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import edu.ucla.library.libservices.rswrapper.beans.Reservation;

public class MapOfRoomNameToReservationExtractor
	implements ResultSetExtractor
{
	public MapOfRoomNameToReservationExtractor() {
		super();
	}

	public Object extractData(ResultSet rs) throws SQLException,
			DataAccessException {
		
		Map<String, List<Reservation>> map =
			new HashMap<String, List<Reservation>>();
		
		while (rs.next()) {
			String name = rs.getString( Constants.RESOURCE_NAME);
			// Add key to map if it doesn't exist
			if (!map.containsKey(name)) {
				map.put(name, new LinkedList<Reservation>());
			}
			
			// Retrieve the list corresponding to the resource
			List<Reservation> list = map.get(name);
			
			// Use the logic in ReservationMapper to form the reservation
			Reservation res = (Reservation) new ReservationMapper().mapRow(rs, rs.getRow());
			
			// Add to the list
			list.add(res);
		}
		
		return map;
	}

}
