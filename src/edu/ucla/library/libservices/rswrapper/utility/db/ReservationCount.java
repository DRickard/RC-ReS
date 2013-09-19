package edu.ucla.library.libservices.rswrapper.utility.db;

import edu.ucla.library.libservices.rswrapper.db.source.DataSourceFactory;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

public class ReservationCount
{
  private String dbName;
  private String uid;
  private int days;
  private DataSource ds;

  private static final String COUNT_QUERY =
    "SELECT COUNT(srd.[sched_id]) AS reservations FROM " 
    + "[RedESoft].[dbo].[tbl_sched_res_date] srd INNER JOIN " 
    + "[RedESoft].[dbo].[tbl_res] tr ON srd.[res_id] = tr.[res_id] INNER JOIN" 
    + " [RedESoft].[dbo].[tbl_sched_udf_val] suv ON srd.[sched_id] = " 
    + "suv.[sched_id] WHERE suv.[udf_id] = 153 AND LTRIM(RTRIM(suv.[string_value]))" 
    + " = ? AND DATEDIFF(DAY,srd.[mtg_start_date_local], GETDATE()) <= ? AND tr.[grp_id] IN (56,79)";

  public ReservationCount()
  {
    super();
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

  public void setDays( int days )
  {
    this.days = days;
  }

  private int getDays()
  {
    return days;
  }

  private void makeConnection()
  {
    ds = DataSourceFactory.createDataSource( getDbName() );
    //ds = DataSourceFactory.createIrmaSource();
  }

  public int getReservations()
  {
    makeConnection();

    return new JdbcTemplate( ds ).queryForInt( COUNT_QUERY, new Object[]
        { getUid(), getDays() } );
  }
}
