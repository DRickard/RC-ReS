package edu.ucla.library.libservices.rswrapper.generators;

import javax.sql.DataSource;

import edu.ucla.library.libservices.rswrapper.db.source.DataSourceFactory;

public class Generator
{
  protected DataSource ds;
  protected String dbName;
  protected String irmaDb;
  protected String magidb;

  public void setDbName( String dbName )
  {
    this.dbName = dbName;
  }

  private String getDbName()
  {
    return dbName;
  }

  protected void makeDBConnection()
  {
    ds = DataSourceFactory.createDataSource( getDbName() );
    //ds = DataSourceFactory.createIrmaSource();
  }

  public void setIrmaDb( String irmaDb )
  {
    this.irmaDb = irmaDb;
  }

  public String getIrmaDb()
  {
    return irmaDb;
  }

  public void setMagidb( String magidb )
  {
    this.magidb = magidb;
  }

  public String getMagidb()
  {
    return magidb;
  }
}
