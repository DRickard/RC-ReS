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
    ds.setUrl( "sqlserver_url" );
    ds.setUsername( "sqlserver_user" );
    ds.setPassword( "sqlserver_pwd" );

    return ds;
  }

  public static DriverManagerDataSource createVgerSource()
  {
    DriverManagerDataSource ds;

    ds = new DriverManagerDataSource();
    ds.setDriverClassName( "oracle.jdbc.OracleDriver" );
    ds.setUrl( "oracle_url" );
    ds.setUsername( "oracle_user" );
    ds.setPassword( "oracle_pwd" );

    return ds;
  }

  public static DriverManagerDataSource createMagiSource()
  {
    DriverManagerDataSource ds;

    ds = new DriverManagerDataSource();
    ds.setDriverClassName( "com.microsoft.sqlserver.jdbc.SQLServerDriver" );
    ds.setUrl( "other_sqlserver_url" );
    ds.setUsername( "other_sqlserver_user" );
    ds.setPassword( "other_sqlserver__pwd" );

    return ds;
  }

  public static DriverManagerDataSource createSchedulerSource()
  {
    DriverManagerDataSource ds;

    ds = new DriverManagerDataSource();
    ds.setDriverClassName( "com.microsoft.sqlserver.jdbc.SQLServerDriver" );
    ds.setUrl( "third_sqlserver_url" );
    ds.setUsername( "third_sqlserver_user" );
    ds.setPassword( "third_sqlserver__pwd" );

    return ds;
  }
}

