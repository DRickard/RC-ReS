package edu.ucla.library.libservices.rswrapper.generators;

import edu.ucla.library.libservices.rswrapper.utility.messages.ErrorResponse;

import javax.ws.rs.WebApplicationException;

import org.apache.axis2.AxisFault;
import org.apache.axis2.client.Options;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.axis2.transport.http.HttpTransportProperties;

import webservice.resourcescheduler.peoplecube.ResourceSchedulerServiceStub;
import webservice.resourcescheduler.peoplecube.ResourceSchedulerServiceStub.RSCredentials;
import webservice.resourcescheduler.peoplecube.ResourceSchedulerServiceStub.RSCredentialsE;

public class SoapGenerator
  extends Generator
{

  protected ResourceSchedulerServiceStub stub;
  protected RSCredentialsE credentials;

  public void setup( String authuser, String authpwd, String rsuser )
  {
    try
    {
      this.stub = new ResourceSchedulerServiceStub();
    }
    catch ( AxisFault e )
    {
      throw new WebApplicationException( ErrorResponse.createServerError( e ) );
    }

    /*
		 * Source:
		 * http://stackoverflow.com/questions/1528089/how-to-do-basic-authentication
		 * -with-an-axis2-adb-client
		 */
    HttpTransportProperties.Authenticator auth =
      new HttpTransportProperties.Authenticator();
    auth.setUsername( authuser );
    auth.setPassword( authpwd );
    auth.setPreemptiveAuthentication( true );
    final Options clientOptions =
      this.stub._getServiceClient().getOptions();
    clientOptions.setProperty( HTTPConstants.AUTHENTICATE, auth );

    RSCredentials cred = new RSCredentials();
    cred.setUsername( rsuser );

    this.credentials = new RSCredentialsE();
    this.credentials.setRSCredentials( cred );
  }
}
