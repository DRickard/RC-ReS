package edu.ucla.library.libservices.rswrapper.generators;

import edu.ucla.library.libservices.rswrapper.db.source.DataSourceFactory;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

public class PatronGenerator
{
  private DataSource ds;
  private String dbName;
  private String uid;
  private static final String GROUP_QUERY =
    "SELECT vger_support.get_simple_group(pb.patron_group_id) AS patron_group" 
    + " FROM ucladb.patron p INNER JOIN ucladb.patron_barcode pb ON " 
    + "p.patron_id = pb.patron_id WHERE p.institution_id = ? AND " 
    + "pb.patron_barcode_id = ucladb.getFirstPatronBarcodeID( p.patron_id )";

  public PatronGenerator()
  {
    super();
  }

  private void makeConnection()
  {
    ds = DataSourceFactory.createDataSource( getDbName() );
    //ds = DataSourceFactory.createVgerSource();
  }

  public void setDbName( String dbName )
  {
    this.dbName = dbName;
  }

  private String getDbName()
  {
    return dbName;
  }

  public void setUid( String uid )
  {
    this.uid = uid;
  }

  private String getUid()
  {
    return uid;
  }

  public String getGroup()
  {
    makeConnection();

    try
    {
      return new JdbcTemplate( ds ).queryForObject( GROUP_QUERY,
                                                    new Object[]
          { getUid() }, String.class ).toString();
    }
    catch ( Exception e )
    {
      e.printStackTrace();
      return "n/a";
    }
  }
}
