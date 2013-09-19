package edu.ucla.library.libservices.rswrapper.db.mappers;

import edu.ucla.library.libservices.rswrapper.utility.constants.Constants;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import edu.ucla.library.libservices.rswrapper.beans.Resource;

public class ResourceMapper
  implements RowMapper
{
  public ResourceMapper()
  {
    super();
  }

  public Object mapRow( ResultSet rs, int rowNum )
    throws SQLException
  {
    Resource res;

    res = new Resource();
    res.setBigImage( rs.getString( Constants.BIG_IMAGE ) );
    res.setCapacity( rs.getInt( Constants.RESOURCE_CAP ) );
    res.setDescription( rs.getString( Constants.RESOURCE_NAME ) );
    res.setGroupId( rs.getInt( Constants.GROUP_ID ) );
    res.setHeaderTitle( rs.getString( Constants.RESOURCE_TITLE ) );
    res.setId( rs.getInt( Constants.RESOURCE_ID ) );
    res.setResourceTypeId( rs.getInt( Constants.RESOURCE_TYPE_ID ) );
    res.setSmallImage( rs.getString( Constants.SMALL_IMAGE ) );

    return res;
  }
}
