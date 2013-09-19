package edu.ucla.library.libservices.rswrapper.beans;

abstract public class Location
{

  public Location()
  {
    super();
  }

  protected String name;
  protected int magiLocationId;

  public int getMagiLocationId()
  {
    return magiLocationId;
  }

  public void setMagiLocationId( int magiLocationId )
  {
    this.magiLocationId = magiLocationId;
  }

  public String getName()
  {
    return name;
  }

  public void setName( String name )
  {
    this.name = name;
  }

  abstract boolean isValid();

  abstract public BusinessRules generate( String uid );
}
