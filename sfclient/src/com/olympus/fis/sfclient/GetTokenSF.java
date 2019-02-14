package com.olympus.fis.sfclient;

 

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import org.apache.http.client.methods.HttpPost;
 
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.Header;
 
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.util.EntityUtils;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.json.JSONException;
 
import org.json.JSONArray;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class GetTokenSF  extends HttpServlet{
	
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
	
	static String leadId;
	static String leadFirstName;
	static String leadLastName;
	static String leadCompany;
	static String leadEmail;
	static String leadPhone;
	static String leadPostal;

/**********************************************************************************************************/  
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
	
/*****************************************************************************************************/
// getBody method
/*****************************************************************************************************/
	private static String getBody(InputStream inputStream) {
	    String result = "";
	    try {
	        BufferedReader in = new BufferedReader(
	                new InputStreamReader(inputStream)
	        );
	        String inputLine;
	        while ( (inputLine = in.readLine() ) != null ) {
	            result += inputLine;
	            result += "\n";
	        }
	        in.close();
	    } catch (IOException ioe) {
	        ioe.printStackTrace();
	    }
	    return result;
	}
/*********************************************************************************************************************************/	
	
	public static void displayJsonArray(JSONArray jsonArr) {

		Iterator<Object> iterator = jsonArr.iterator();
		// Set<String> keys = jsonObject.keySet();
		while (iterator.hasNext()) {

			Object obj = iterator.next();
			if (obj instanceof JSONObject) {
				Set<String> keys = ((JSONObject) obj).keySet();
				// System.out.println("%%%%%%%%% KEYS %%%%%%%%" + keys.toString() + "keyNum=" +
				// keys.size());
				for (String key : keys) {
					// System.out.println(key + ":" + jsonObject.get(key));
					System.out.println("*******Key: " + key + " -> " + ((JSONObject) obj).get(key));
				}
				// System.out.println("*******" + ((JSONObject) obj).get("CUST_ID"));
			}
		}
	}
/*********************************************************************************************************************************/	
/*********************************************************************************************************************************/
//method to print array
/*********************************************************************************************************************************/
	public static void printStrArray(ArrayList<String> strArr, String tag) {
		for (String str : strArr) { // iterating ArrayList
			System.out.println(tag + str);
		}
		// System.out.println(names[index]);
	}

	/*****************************************************************************************************/
	// Get Access oAuth2 token
	/**********************************************************************************************************/
	public static String getAccessToken(String loginURL) {
		String loginAccessToken = null;

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
		String loginInstanceUrl = null;

		try {
			jsonObject = (JSONObject) new JSONTokener(getResult).nextValue();
			loginAccessToken = jsonObject.getString("access_token");
			loginInstanceUrl = jsonObject.getString("instance_url");
		} catch (JSONException jsonException) {
			// Handle JSON exception
		}
		/*
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
	
 /*************************************************************************************************************************************************************/
 // displayLeads --  display leads from Salesforce
 /*************************************************************************************************************************************************************/
	public static void displayLeads(JSONObject json) {
		JSONArray j = json.getJSONArray("records");
		
		for (int i = 0; i < j.length(); i++){
	        leadId = json.getJSONArray("records").getJSONObject(i).getString("Id");
	        leadFirstName = json.getJSONArray("records").getJSONObject(i).getString("FirstName");
	        leadLastName = json.getJSONArray("records").getJSONObject(i).getString("LastName");
	        leadCompany = json.getJSONArray("records").getJSONObject(i).getString("Company");
	        //leadEmail = json.getJSONArray("records").getJSONObject(i).getString("Email");
	        //leadPhone = json.getJSONArray("records").getJSONObject(i).getString("Phone");
	        //leadPostal = json.getJSONArray("records").getJSONObject(i).getString("PostalCode");
	       // System.out.println("*************** Lead record is: " + i + ". " + leadId + " " + leadPhone + " " + 
	        		 //leadFirstName + " " +  leadLastName +   " " + leadEmail +  "(" + leadCompany + ")");
	        //System.out.println("***ID:" + leadId);
	    }	
	}		
/*************************************************************************************************************************************************************/
// gwrLeadID -- get the lead by ID number	
/*************************************************************************************************************************************************************/
	
	public static ArrayList<String> getLeadID(JSONObject json) throws IOException {
		ArrayList<String> arrStr = new ArrayList<String>();

		JSONArray j = json.getJSONArray("records");
		
		for (int i = 0; i < j.length(); i++){
	        leadId = json.getJSONArray("records").getJSONObject(i).getString("Id");
	        //System.out.println("***ID:" + leadId);
	        arrStr.add(leadId);
	    }
		return arrStr;
	}		
	
/*************************************************************************************************************************************************************/
// getResponse --  get the response object
/*************************************************************************************************************************************************************/
	public static HttpResponse getResponse(String uri) throws IOException {
		HttpResponse response = null;

		//String uri = baseUri + "/query?q=Select+Id,FirstName,LastName,Company,Email,Phone,PostalCode+From+Lead+Limit+10";
		try {
	        //Set up the HTTP objects needed to make the request.
	        HttpClient httpClient = HttpClientBuilder.create().build();   
	        //System.out.println("Query URL: " + uri);
	        HttpGet httpGet = new HttpGet(uri);
	        //System.out.println("oauthHeader2: " + oauthHeader);
	        httpGet.addHeader(oauthHeader);
	        httpGet.addHeader(prettyPrintHeader);
	        // Make the request.
	       response = httpClient.execute(httpGet);     
		} catch (IOException ioe) {
	        ioe.printStackTrace();
	    } catch (NullPointerException npe) {
	        npe.printStackTrace();
	    }
		return response;
	}	
	
/*************************************************************************************************************************************************************/
// processResponse -- process the response object	
/*************************************************************************************************************************************************************/
	public static ArrayList<String> processResponse(HttpResponse response) throws IOException { // Process the result

		ArrayList<String> idArr = new ArrayList<String>();

		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode == 200) {
			String response_string = EntityUtils.toString(response.getEntity());
			try {
				JSONObject json = new JSONObject(response_string);
				// System.out.println("JSON result of Query:\n" + json.toString(1));
				displayLeads(json);
				idArr = getLeadID(json);
				// printStrArray(idArr, "***^ ID:");

			} catch (JSONException je) {
				je.printStackTrace();
			}
		} else {
			System.out.println("Query was unsuccessful. Status code returned is " + statusCode);
			System.out.println("An error has occured. Http status: " + response.getStatusLine().getStatusCode());
			System.out.println(getBody(response.getEntity().getContent()));
			System.exit(-1);
		}

		return idArr;
	}
	
/*************************************************************************************************************************************************************/
// queryLeads -- run query against Salesforce	
/*************************************************************************************************************************************************************/
	
	public static ArrayList<String> queryLeads(String uri) { // Query Leads using REST HttpGet
		HttpResponse response = null;
		ArrayList<String> resArr = null;
		
		try {
			response = getResponse(uri);
			resArr = processResponse(response);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (NullPointerException npe) {
			npe.printStackTrace();
		}
		
		return resArr;
	}

	/*****************************************************************************************************/
// Service method
	/*****************************************************************************************************/
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		String uri = new String();
		String loginURL = new String();
		String token = new String();
		ArrayList<String> leadArr = null;

		String propFile = "C:\\Java_Dev\\props\\salesforce\\key.properties";
		// System.out.println("\n_______________ Lead QUERY _______________");
		getProperties(propFile);
		/*
		System.out.println("**** CLIENTID=" + CLIENTID);
		System.out.println("**** CLIENTSECRET=" + CLIENTSECRET);
		System.out.println("**** USERNAME=" + USERNAME);
		System.out.println("**** PASSWORD=" + PASSWORD);
		System.out.println("**** LOGINURL=" + LOGINURL);
		System.out.println("**** GRANTSERVICE=" + GRANTSERVICE);
		System.out.println("**** REST_ENDPOINT=" + REST_ENDPOINT);
		System.out.println("**** API_VERSION=" + API_VERSION);
		 */
		// Assemble the login request URL
		loginURL = LOGINURL + GRANTSERVICE + "&client_id=" + CLIENTID + "&client_secret=" + CLIENTSECRET + "&username="
				+ USERNAME + "&password=" + PASSWORD;
		// System.out.println(loginURL);
		token = getAccessToken(loginURL);
		System.out.println("Access token: " + token);
		// end getting oAuth token
 		
		/*--------------------------------------------------------------------------------------------------------------------------------------*/
		uri = baseUri + "/query?q=Select+Id,FirstName,LastName,Company,Email,Phone,PostalCode+From+Lead";	
		//System.out.println("URI:" + uri);
		leadArr = queryLeads(uri);
		 //printStrArray(leadArr, "^^***^ ID:");
		 PrintWriter out = res.getWriter();
			res.setContentType("text/plain");
			//out.write(" in Service method doGet() ");
			out.write(token);
			String paramName = "id";
			String paramValue = req.getParameter(paramName);
			//System.out.println("pName=" + paramName + " pVal=" + paramValue + "--");
			req.getSession().setAttribute("strArrID", leadArr);
			req.getSession().setAttribute(paramName, paramValue);
			//System.out.println("----- IDs=" + leadArr.toString());
			
			//req.getSession().setAttribute(paramName, paramValue);
	       // req.getRequestDispatcher("/ajax.jsp").forward(req, res);
		 
		 
		 
		/*--------------------------------------------------------------------------------------------------------------------------------------*/

		 /*
			ArrayList<String> strArr = new ArrayList<String>();
		   
			PrintWriter out = res.getWriter();
			res.setContentType("text/plain");
			//out.write(" in Service method doGet() ");
		 
		 
			String paramName = "date";
			String paramValue = req.getParameter(paramName);
			//System.out.println("pName=" + paramName + " pVal=" + paramValue + "--");
			strArr = getBookingData(paramValue);
			//JUtils jutil = new JUtils();
			jutil.printStrArray(strArr);
			
			req.getSession().setAttribute("strArr", strArr);
			req.getSession().setAttribute(paramName, paramValue);
	        req.getRequestDispatcher("/ilbooking.jsp").forward(req, res);	 
			 //System.out.println("Exit Servlet " + this.getServletName() + " in doGet() ");
			 
			  */
		}
/**********************************************************************************************************/  
	 
	@Override
	public void init() throws ServletException {
		//System.out.println("Servlet " + this.getServletName() + " has started");
	}
/**********************************************************************************************************/  

	@Override
	public void destroy() {
		//System.out.println("Servlet " + this.getServletName() + " has stopped");
	}
}
