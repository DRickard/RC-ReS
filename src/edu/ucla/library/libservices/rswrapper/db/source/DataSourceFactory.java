package edu.ucla.library.libservices.rswrapper.db.source;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.DriverManagerDataSource;

public class DataSourceFactory
{
  public static DataSource createDataSource( String name )
  {
    InitialContext context;
    DataSource connection;

    try
    {
      context = new InitialContext();
      connection = ( DataSource ) context.lookup( name );
    }
    catch ( NamingException e )
    {
      e.printStackTrace();
      connection = null;
    }

    return connection;
  }

  // This method is only available for testing

  public static DriverManagerDataSource createIrmaSource()
  {
    DriverManagerDataSource ds = new DriverManagerDataSource();

    ds.setDriverClassName( "com.microsoft.sqlserver.jdbc.SQLServerDriver" );
    ds.setUrl( "jdbc:sqlserver://db-mrm.library.ucla.edu:1433" );
    ds.setUsername( "irma_rpt" );
    ds.setPassword( "irma_rpt_pwd" );

    return ds;
  }

  public static DriverManagerDataSource createVgerSource()
  {
    DriverManagerDataSource ds;

    ds = new DriverManagerDataSource();
    ds.setDriverClassName( "oracle.jdbc.OracleDriver" );
    ds.setUrl( "jdbc:oracle:thin:@ils-db-prod.library.ucla.edu:1521:VGER" );
    ds.setUsername( "vger_support" );
    ds.setPassword( "vger_support_pwd" );

    return ds;
  }

  public static DriverManagerDataSource createMagiSource()
  {
    DriverManagerDataSource ds;

    ds = new DriverManagerDataSource();
    ds.setDriverClassName( "com.microsoft.sqlserver.jdbc.SQLServerDriver" );
    ds.setUrl( "jdbc:sqlserver://sqlprod.library.ucla.edu:1433" );
    ds.setUsername( "magi_web" );
    ds.setPassword( "magi_web_pwd" );

    return ds;
  }

  public static DriverManagerDataSource createSchedulerSource()
  {
    DriverManagerDataSource ds;

    ds = new DriverManagerDataSource();
    ds.setDriverClassName( "com.microsoft.sqlserver.jdbc.SQLServerDriver" );
    ds.setUrl( "jdbc:sqlserver://db-libraryweb.library.ucla.edu:1433" );
    ds.setUsername( "Hours_Update" );
    ds.setPassword( "Hours_Update_pwd" );

    return ds;
  }
}

