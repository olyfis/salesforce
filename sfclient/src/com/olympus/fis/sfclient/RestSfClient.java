package com.olympus.fis.sfclient;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import com.sun.jersey.api.client.*;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.core.util.MultivaluedMapImpl;

 

/**
 * Servlet implementation class RestSfClient
 */
@WebServlet("/RestSfClient")
public class RestSfClient extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RestSfClient() {
        super();
        // TODO Auto-generated constructor stub
    }
    public static String runURI() {
    	String uri = "";
     
    	String appKey = "";
    	String jsonStr = "[{\"Status\" : \"Failed\" }] ";
    	String acct = "Test Rest";
    	 //String acct = "Test";
		//String uri = "http://cvyhj3a27:8181/webreport/getchart?cType=SBS";
    	try {
		    uri = "https://olympus--fis.cs40.my.salesforce.com/services/data/v42.0/query/?q=SELECT+id,Name+from+Account+where+Name+=+'" + URLEncoder.encode(acct, "UTF-8") + "'";  
    	} catch (UnsupportedEncodingException e) {
    		 
    	}

		//String uri = "https://olympus--fis.cs40.my.salesforce.com/services/data/v42.0/query/?q=SELECT+id,Name+from+Account";
		System.out.println("Run:" + uri );
		
		
		//Client client = ClientBuilder.newClient();
		//System.out.println("Run:" + uri );
		/*
		WebTarget target = client.target(uri);
		System.out.println( target.request(MediaType.TEXT_XML).get(String.class) );
		*/
		appKey = "00D540000000fMN!ARgAQLuB5pEMHt8KDJYMV3jJgiSdKt92eOgSC3xMwrwMlH9mOCnjv8mopMu.hcdRnfbLsOx1yBI__IMcCUKM3De7Vkei_OQZ";
		Client client = Client.create();
		WebResource webresrc = client.resource(uri);
		appKey = "Bearer " + appKey; // appKey is unique number
		MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl();
 
	 
		ClientResponse response = null;
		response = webresrc.queryParams(queryParams)
		                        .header("Content-Type", "application/x-www-form-urlencoded")
		    .header("Authorization", appKey)
		    .get(ClientResponse.class);
		if (response.getStatus() == 200) {
			 jsonStr = response.getEntity(String.class);
		}
		
		return jsonStr;
		
     
    }
    
    
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String retValue = "";
		//response.getWriter().append("Served at: ").append(request.getContextPath());
		PrintWriter out = response.getWriter();
	
		retValue = runURI();
		//out.write("<br> in doGet()-- Returned: " + retValue);
		out.write(retValue);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
 
	}
		
		


}
