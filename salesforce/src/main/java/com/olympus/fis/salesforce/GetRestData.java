package com.olympus.fis.salesforce;

 
import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;


public class GetRestData {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String uri = "http://cvyhj3a27:8181/webreport/getchart?cType=SBS";

		Client client = ClientBuilder.newClient();
		
/*	
		WebTarget target = client.target(uri);
		System.out.println( target.request(MediaType.TEXT_XML).get(String.class) );
*/
		//********************************************************************************/
		/*
				WebResource webresrc = client.resource(uri);
				ClientResponse resp =  WebResource.get(ClientResponse.class);
				if (resp.getStatus() == 200) {
					resp.getEntity();
				}
			}
		*/
		
	}

}
