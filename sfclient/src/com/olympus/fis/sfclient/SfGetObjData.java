package com.olympus.fis.sfclient;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
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
//import org.apache.jasper.tagplugins.jstl.core.Set;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;

public class SfGetObjData  extends HttpServlet { 
	
	public static final String USERID = "uuid"; // http://localhost:8181/sfclient/getdata?qt=uuid&id=Test+Rest
	public static final String USERALL = "uall"; // http://localhost:8181/sfclient/getdata?qt=uall
	public static final String ACCTDET = "actd"; // http://localhost:8181/sfclient/getdata?qt=actd
	
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
	
	
	
	/******************************************************************************************************************************************************************/	

	public SfGetObjData() {
		super();
	}
	/******************************************************************************************************************************************************************/	
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
	/**********************************************************************************************************/
	// Get Access oAuth2 token
	/**********************************************************************************************************/
	public static String getAccessToken() {
		String loginAccessToken = null;
		String loginInstanceUrl = null;
		
		HttpClient httpclient = HttpClients.createDefault();
 
		String loginURL = new String();
		
		String propFile = "C:\\Java_Dev\\props\\salesforce\\key.properties";
		getProperties(propFile);
		// Assemble the login request URL
		loginURL = LOGINURL + GRANTSERVICE + "&client_id=" + CLIENTID + "&client_secret=" + CLIENTSECRET + "&username="
				+ USERNAME + "&password=" + PASSWORD;
		// System.out.println(loginURL);
		
		
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
	public static String runURI(String token, String uri) {
    	//String uri = "";	 
    	String jsonStr = "[{\"Status\" : \"Failed\" }] ";
    	String appKey = "";
    	appKey = token;

		
		//System.out.println("Run:      " + uri );
		//System.out.println(appKey );
		
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
    public static void getJsonData(String responseValue, String srch) {
    	JSONObject myResponse = null;
    	
    	if (! responseValue.isEmpty()) {
    		myResponse = new JSONObject(responseValue);
    	} else {
    		return ;
    	}
		String name = "";
		String id = "";
		Iterator iterator = myResponse.keys();
		String key = null;
		while (iterator.hasNext()) {
			key = (String) iterator.next();
			System.out.println("**** Key:" + key + "--" + "Value:" + key.valueOf(key));
		}
		JSONArray recsArr = (JSONArray) myResponse.getJSONArray(srch);
		//System.out.println("**** JSON_Array Length:" + recsArr.length());
		//System.out.println("**** JSON_Array:" + recsArr);

		if (recsArr.length() > 0) {
			JSONObject recs = recsArr.getJSONObject(0);
			name = recs.getString("Name");
			id = recs.getString("Id");
			System.out.println("**** JSON_OBJ -> (Name):" + name + "--"  + " (ID):"  + id + "--");
		}
    	// Add values to a HashMap
    }
    /******************************************************************************************************************************************************************/	
    public static String getQueryType(String token, String qType, HttpServletRequest request) {
		String uriQuery = "";
		String acct = "";
		String retVal = "";
		String op = "";
		String runOK = "true";
		
		//System.out.println("qType=" + qType + " Access token: " + token);
		if ( qType != null) {
			op = qType.toLowerCase();
		} else  {
			op = "";
			runOK = "false";
		}
		try {
			 //System.out.println("****^^^^^***** qType=" + qType + "-- OP=" + op + "--" );
			if (op.matches(USERALL)) {
				//System.out.println("****^^^^^***** qType=" + qType );
				uriQuery = "https://olympus--fis.cs40.my.salesforce.com/services/data/v42.0/query/?q=SELECT+id,Name+from+Account";
				
				
			} else if (op.matches(USERID)) {
				String idParam = "id";
				String idValue = request.getParameter(idParam);
				acct = idValue;
				 System.out.println("****^^^^^***** ID=" + idValue );
				uriQuery = "https://olympus--fis.cs40.my.salesforce.com/services/data/v42.0/query/?q=SELECT+id,Name+from+Account+where+Name+=+'" 
		    		+ URLEncoder.encode(acct, "UTF-8") + "'";  
				//System.out.println(uriQuery);
						
			} else if (op.matches(ACCTDET)) {	
				System.out.println("******^^^^^ OP=" +op);
				uriQuery = "https://olympus--fis.cs40.my.salesforce.com/services/data/v20.0/sobjects/Account/0015400000FHdeCAAT";
				String idParam = "id";
				String idValue = request.getParameter(idParam);
				//System.out.println(uriQuery);
				
			} else {
				runOK = "false";
			}
    	} catch (UnsupportedEncodingException e) {
    		 e.printStackTrace();
    	}	
		
		if (runOK.equals("true")) {
			retVal = runURI(token, uriQuery);
			System.out.println("Return Value: " + retVal);
    	}
		return(retVal);
    	
    }
    /******************************************************************************************************************************************************************/	

    public static boolean isJSONValid(String test) {
        try {
            new JSONObject(test);
        } catch (JSONException ex) {
            // e.g. in case JSONArray is valid as well...
            try {
                new JSONArray(test);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }
    /******************************************************************************************************************************************************************/	
    public static void idJsonObjType(String jsonStr) {
    	JSONObject jsonObj = null;  
		// String response = "above is my response";
    	Boolean arrStatus = false;
    	
		if (jsonStr != null && isJSONValid(jsonStr)) { 
			jsonObj = new JSONObject(jsonStr);
			Iterator iterator = jsonObj.keys();
			String key = null;
			while (iterator.hasNext()) {
				key = (String) iterator.next();
				//System.out.println("****^^^^^^^**** Key:" + key + "--" + "Value:" + jsonObj.get(key));
				Object Obj = jsonObj.get(key);
				arrStatus = isJsonObjArray(jsonObj, key);
				if (arrStatus) {
					System.out.println("****^^^^^^^**** Key:" + key + "--" + "is a JSONArray");
				}
				
				/*
				if (Obj instanceof String) {
					System.out.println("Key=" + key + " ********** Value is String...........");
				} else if (Obj instanceof Integer) {
					System.out.println("Key=" + key + " ********** Value is Integer...........");
				} else if (Obj instanceof JSONArray) {
					System.out.println("Key=" + key + " ********** Value is JSONArray...........");
				} else if (Obj instanceof Boolean) {
					System.out.println("Key=" + key + " ********** Value is Boolean...........");
				}
				*/
			}
		}
    }
    /******************************************************************************************************************************************************************/	
    public static Boolean isJsonObjArray(JSONObject jsonObj, String key) {
    	
    	//System.out.println("****^^^^^^^**** Key:" + key + "--" + "Value:" + jsonObj.get(key));
    	Object Obj = jsonObj.get(key);
    	if (Obj instanceof JSONArray) {
			System.out.println("Key=" + key + " ********** Value is Array...........");
			return true;
		}
    	return false; 	
    }
    /******************************************************************************************************************************************************************/	
    public static void parseJsonData(String jsonStr) {
    	JSONObject jsonObj = null;   
 
    	System.out.println("***********************************************************************************");
    	if ( jsonStr != null && isJSONValid(jsonStr)) {
    		jsonObj = new JSONObject(jsonStr);
    	} else {
    		System.out.println("JSON string is not valid JSON");
    		return ;
    	}
    	idJsonObjType(jsonStr);
    	
    	/*
    	//System.out.println("JO=" + jsonObj);
    	Iterator iterator = jsonObj.keys();
		String key = null;	

		while (iterator.hasNext()) {
			key = (String) iterator.next();
			 System.out.println("**** Key:" + key + "--" + "Value:" + jsonObj.get(key));
		}
	
    	*/
    }
    
    /******************************************************************************************************************************************************************/	
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		 
    	// response.getWriter().append("Served at: ").append(request.getContextPath());
    	String jsonStr = "[{\"Status\" : \"Failed\" }] ";
    	PrintWriter out = response.getWriter();
    	JSONArray jsonArr = new JSONArray();
    	String qType = "qt";
		String qTypeValue = request.getParameter(qType); 
		//System.out.println("^^^^^^^ qTypeValue=" + qTypeValue);
    	String token = new String();
		String retValue = "";
		token = getAccessToken();
		retValue = getQueryType(token, qTypeValue, request);

		
	 /*
		if (retValue.equals("failed")) {
			retValue = jsonStr;
		} else {
			//getJsonData(retValue, "records");
			
			
		}
	 */
		out.write(retValue);
		parseJsonData(retValue);
			    
	}
    /******************************************************************************************************************************************************************/	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
 
	}
	/******************************************************************************************************************************************************************/	

	

}
