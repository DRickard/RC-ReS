package edu.ucla.library.libservices.rswrapper.generators;

import java.rmi.RemoteException;

import javax.ws.rs.WebApplicationException;

import javax.xml.bind.annotation.XmlElement;

import edu.ucla.library.libservices.rswrapper.utility.messages.ErrorResponse;
import edu.ucla.library.libservices.rswrapper.beans.Resource;

import webservice.resourcescheduler.peoplecube.ResourceSchedulerServiceStub.GetResourceResponse;
import webservice.resourcescheduler.peoplecube.ResourceSchedulerServiceStub.GetResource;
import webservice.resourcescheduler.peoplecube.ResourceSchedulerServiceStub.ResourceData;

public class GetResourceGenerator
  extends SoapGenerator
{

  @XmlElement( name = "resource" )
  private Resource resource;

  public void generateResource( int resourceId )
    throws WebApplicationException
  {

    try
    {
      GetResource res = new GetResource();
      res.setResourceId( resourceId );

      GetResourceResponse response =
        this.stub.getResource( res, this.credentials );

      ResourceData data = response.getGetResourceResult();

      if ( data.getIsValid() )
      {
        this.resource = new Resource();
        this.resource.setId( data.getId() );
        this.resource.setGroupId( data.getGroupId() );
        this.resource.setResourceTypeId( data.getResourceTypeId() );
        this.resource.setDescription( data.getDescription() );
        this.resource.setHeaderTitle( data.getHeaderTitle() );
        this.resource.setCapacity( data.getCapacity() );
      }
      else
      {
        // Resource probably does not exist
        throw new WebApplicationException( ErrorResponse.createSoapError( data.getAllChildBrokenBusinessRules() ) );
      }
    }
    catch ( RemoteException e )
    {
      throw new WebApplicationException( ErrorResponse.createServerError( e ) );
    }
  }

  public Resource getResource()
  {
    return this.resource;
  }

}
