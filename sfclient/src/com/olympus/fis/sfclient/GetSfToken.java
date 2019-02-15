package com.olympus.fis.sfclient;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Path;
import javax.ws.rs.core.MultivaluedMap;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;

 


public class GetSfToken  extends HttpServlet { 
	static String USERNAME = null;
	static String PASSWORD = null;
	static String LOGINURL = null;
	static String GRANTSERVICE = null;
	static String CLIENTID = null;
	static String CLIENTSECRET = null;
	static String REST_ENDPOINT = null;
	static String API_VERSION = null;
	static String baseUri = null;
	static Header oauthHeader = null;
	static Header prettyPrintHeader = new BasicHeader("X-PrettyPrint", "1");
	
 
 
	public GetSfToken() {
		super();
		// TODO Auto-generated constructor stub

	}
	/******************************************************************************************************************************************************************/	
	public static void getProperties(String propFile) {
		FileInputStream fis = null;
		Properties keyProps = new Properties();

		try {
			fis = new FileInputStream(propFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		try {
			keyProps.load(fis);
		} catch (IOException e) {
			e.printStackTrace();
		}
		USERNAME = (String) keyProps.get("USERNAME");
		PASSWORD = (String) keyProps.get("PASSWORD");
		CLIENTID = (String) keyProps.get("CLIENTID");
		CLIENTSECRET = (String) keyProps.get("CLIENTSECRET");
		GRANTSERVICE = (String) keyProps.get("GRANTSERVICE");
		LOGINURL = (String) keyProps.get("LOGINURL");
		REST_ENDPOINT = (String) keyProps.get("REST_ENDPOINT");
		API_VERSION = (String) keyProps.get("API_VERSION");
	}
	/******************************************************************************************************************************************************************/	
	/*****************************************************************************************************/
	// Get Access oAuth2 token
	/**********************************************************************************************************/
	public static String getAccessToken(String loginURL) {
		String loginAccessToken = null;
		String loginInstanceUrl = null;
		
		HttpClient httpclient = HttpClients.createDefault();
		// Login requests must be POSTs
		HttpPost httpPost = new HttpPost(loginURL);
		HttpResponse response = null;

		try {
			// Execute the login POST request
			response = httpclient.execute(httpPost);
		} catch (ClientProtocolException cpException) {
			// Handle protocol exception
		} catch (IOException ioException) {
			// Handle system IO exception
		}

		// verify response is HTTP OK
		final int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode != HttpStatus.SC_OK) {
			System.out.println("Error authenticating to Force.com: " + statusCode);
			// Error is in EntityUtils.toString(response.getEntity())
			return "No Connection";
		}
		String getResult = null;
		
		try {
			getResult = EntityUtils.toString(response.getEntity());
		} catch (IOException ioException) {
			// Handle system IO exception
		}
		
	 
		JSONObject jsonObject = null;
		

		try {
			jsonObject = (JSONObject) new JSONTokener(getResult).nextValue();
			loginAccessToken = jsonObject.getString("access_token");
			loginInstanceUrl = jsonObject.getString("instance_url");
		} catch (JSONException jsonException) {
			// Handle JSON exception
		}
		/*
		 * 
		System.out.println(response.getStatusLine());
		System.out.println("Successful login");
		System.out.println("  instance URL: " + loginInstanceUrl);
		System.out.println("  access token/session ID: " + loginAccessToken);
	*/	
		// New code
		baseUri = loginInstanceUrl + REST_ENDPOINT + API_VERSION;
		oauthHeader = new BasicHeader("Authorization", "OAuth " + loginAccessToken);
		/*
		 * System.out.println("oauthHeader1: " + oauthHeader); System.out.println("\n" +
		 * response.getStatusLine()); System.out.println("Successful login");
		 * System.out.println("instance URL: " + loginInstanceUrl);
		 */
		// System.out.println("access token/session ID: " + loginAccessToken);
		//System.out.println("***** baseUri: " + baseUri);
		// release connection

		httpPost.releaseConnection();

		return loginAccessToken;
	}
	
	/******************************************************************************************************************************************************************/	

    public static String runURI(String token) {
    	String uri = "";
     
    	 
    	String jsonStr = "[{\"Status\" : \"Failed\" }] ";
    	String acct = "Test Rest";
    	//String appKey = "00D540000000fMN!ARgAQLuB5pEMHt8KDJYMV3jJgiSdKt92eOgSC3xMwrwMlH9mOCnjv8mopMu.hcdRnfbLsOx1yBI__IMcCUKM3De7Vkei_OQZ";
    	String appKey = "";
    	appKey = token;
    	try {
		    uri = "https://olympus--fis.cs40.my.salesforce.com/services/data/v42.0/query/?q=SELECT+id,Name+from+Account+where+Name+=+'" 
		    		+ URLEncoder.encode(acct, "UTF-8") + "'";  
    	} catch (UnsupportedEncodingException e) {
    		 e.printStackTrace();
    	}

		//String uri = "https://olympus--fis.cs40.my.salesforce.com/services/data/v42.0/query/?q=SELECT+id,Name+from+Account";
		System.out.println("Run:      " + uri );
		System.out.println(appKey );
		
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
    /******************************************************************************************************************************************************************/	
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String retValue = "";
		//response.getWriter().append("Served at: ").append(request.getContextPath());
		PrintWriter out = response.getWriter();
		String uri = new String();
		String loginURL = new String();
		String token = new String();
		String propFile = "C:\\Java_Dev\\props\\salesforce\\key.properties";
		// System.out.println("\n -------");
		getProperties(propFile);
		// Assemble the login request URL
				loginURL = LOGINURL + GRANTSERVICE + "&client_id=" + CLIENTID + "&client_secret=" + CLIENTSECRET + "&username="
						+ USERNAME + "&password=" + PASSWORD;
				// System.out.println(loginURL);
				token = getAccessToken(loginURL);
				System.out.println("Access token: " + token);
				// end getting oAuth token
				
		//retValue = runURI(token);
		//out.write("<br> in doGet()-- Returned: " + retValue);
		out.write(token);
	}
    /******************************************************************************************************************************************************************/	

 
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
 
	}
    
    /******************************************************************************************************************************************************************/	
    /******************************************************************************************************************************************************************/	
    /******************************************************************************************************************************************************************/	
    /******************************************************************************************************************************************************************/	

}



