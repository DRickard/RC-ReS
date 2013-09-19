package edu.ucla.library.libservices.rswrapper.db.mappers;

import edu.ucla.library.libservices.rswrapper.utility.constants.Constants;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

public class UdfsExtractor implements ResultSetExtractor
{
	public UdfsExtractor() {
		super();
	}
	
	public Object extractData(ResultSet rs) throws SQLException, DataAccessException
	{
		// The outer map maps ReservationId => an inner map
		// The inner map maps UserDefinedFieldIds => User defined field values
		Map<Integer, Map<Integer, String>> mapOfResToUdfs
			= new HashMap<Integer, Map<Integer, String>>();
		
		while (rs.next()) {
			int reservationId = rs.getInt( Constants.RESERVATION_ID);
			
			// Add to map of udfs if this reservationId is encountered for
			/// the first time
			if (!mapOfResToUdfs.containsKey(reservationId)) {
				mapOfResToUdfs.put(reservationId, new HashMap<Integer,String>());
			}
			
			Map<Integer, String> udfs = mapOfResToUdfs.get(reservationId);
			udfs.put(rs.getInt( Constants.UDF_ID), rs.getString( Constants.UDF_VALUE));
		}
		
		return mapOfResToUdfs;
	}
}
