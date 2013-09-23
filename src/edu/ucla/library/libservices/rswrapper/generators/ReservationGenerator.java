package edu.ucla.library.libservices.rswrapper.generators;

import edu.ucla.library.libservices.rswrapper.beans.SimpleReservation;
import edu.ucla.library.libservices.rswrapper.db.mappers.SimpleReservationMapper;
import edu.ucla.library.libservices.rswrapper.db.source.DataSourceFactory;

import java.util.List;

import javax.sql.DataSource;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.springframework.jdbc.core.JdbcTemplate;

@XmlRootElement( name = "schedules" )
public class ReservationGenerator
{
  private static final String RES_BY_UID =
    "SELECT * FROM RedESoft.dbo.vw_rc_res_by_udf WHERE udf_val = ? AND startDate >= (GETDATE() - 1)ORDER BY startDate, display_order";
    //"SELECT * FROM RedESoft.dbo.vw_rc_res_by_udf_test WHERE udf_val = ? AND startDate >= (GETDATE() - 1) ORDER BY startDate, display_order";

  private DataSource ds;
  private String dbName;
  @XmlElement( name = "reservation" )
  private List<SimpleReservation> simpleReservation;

  public ReservationGenerator()
  {
    super();
  }

  public List<SimpleReservation> getReservationsByUID( String uid )
  {
    makeConenction();

    simpleReservation =
        new JdbcTemplate( this.ds ).query( RES_BY_UID, new Object[]
          { uid }, new SimpleReservationMapper() );

    return simpleReservation;
  }

  public void setDbName( String dbName )
  {
    this.dbName = dbName;
  }

  private String getDbName()
  {
    return dbName;
  }

  private void makeConenction()
  {
    ds = DataSourceFactory.createDataSource( getDbName() );
    //ds = DataSourceFactory.createIrmaSource();
  }
}
