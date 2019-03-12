<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@page import="java.io.OutputStream"%>   
<%@ page import="java.io.File"%>
<%@ page import="com.olympus.olyutil.Olyutil"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import = "java.io.*,java.util.*, javax.servlet.*" %>
<%@ page import = "javax.servlet.http.*" %>
<%@ page import = "org.apache.commons.fileupload.*" %>
<%@ page import = "org.apache.commons.fileupload.disk.*" %>
<%@ page import = "org.apache.commons.fileupload.servlet.*" %>
<%@ page import = "org.apache.commons.io.output.*" %>
<%@ page import="java.sql.*"%>


<% 
  	 String title =  "Olympus FIS Rollover Database Update Results"; 	 
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>   <%=title%> </title>
<script type="text/javascript" src="http://cdnjs.cloudflare.com/ajax/libs/jquery/1.9.1/jquery.min.js"></script>
<style><%@include file="includes/css/table.css"%></style>
<style><%@include file="includes/css/header.css"%></style>
 
</head>
<!-- ********************************************************************************************************************************************************* -->
   <body style="font-family: sans-serif; color: black; font-size: 1;">   
<!-- ********************************************************************************************************************************************************* -->
<%!/*******************************************************************************************************************************************************************/
public ResultSet queryDB(Connection conn, String query) {
	
	
 
	String sep = ",";
	Olyutil util = new Olyutil();
	ArrayList<String> strArr = new ArrayList<String>();		 
	Statement stmt = null;
	PreparedStatement statement;
	ResultSet res  = null;

	try {
		if (conn != null) {
			statement = conn.prepareStatement(query);
			res = util.getResultSetPS(statement);
			//strArr = util.resultSetArray(res, sep);			
			//System.out.println("**** arrSize=" + strArr.size()  );
			//System.out.println("**** arr:" + strArr.toString());	
			//result = jutil.displayResults(res);

		} else {
			System.out.println("**** Connection to databases failed!" );
		}
	} catch (Exception ex) {
		ex.printStackTrace();
	}
	return res;
}

/*******************************************************************************************************************************************************************/


public ArrayList<String> readFileData(String filePath){
	 ArrayList<String> arr = new ArrayList<String>();
	 
     try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
         String sCurrentLine;
         while ((sCurrentLine = br.readLine()) != null) {
             arr.add(sCurrentLine);
         }
     } catch (IOException e) {
         e.printStackTrace();
     } 
	return arr;
}
/*******************************************************************************************************************************************************************/
public static String basename(String path) {
		String filename = "";
		//System.out.println("PATH=" + path);
		String[] pathparts = path.split("\\\\");
		filename = pathparts[pathparts.length - 1];
		//System.out.println("FN=" + filename);
		return filename;
	}
/*******************************************************************************************************************************************************************/
public static Connection  getDbConnection() {
	Connection connection = null;
	
	String driverName = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
	String connectionUrl = "jdbc:sqlserver://cvyhj1d10.oai.olympusglobal.com:1433;databaseName=RollOverTest;";	 
	String dbName = "RollOverTest";
	String userId = "rapport";
	String password = "rapport";
		//System.out.println("**** Load Driver");
		try {
			Class.forName(driverName);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		//Connection connection = null;	
		//System.out.println("**** Try Connection");
		try {
			//connection = DriverManager.getConnection(connectionUrl + dbName, userId, password);
			connection = DriverManager.getConnection(connectionUrl, userId, password);
			//System.out.println("RS=" + resultSet.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	return connection;
}
/*************************************************************************************************************************************************************/
public String TableHeader(ResultSet resultSet) throws Exception {
	Olyutil util = new Olyutil();
	ArrayList<String> hdrArr = new ArrayList<String>();	
	String thead = null;
	String color1 = "#5DADE2";
	String style1 = "font-family: sans-serif; color: white;";	
	hdrArr = util.getColNames(resultSet);
	//thead = "<tr bgcolor=" + color1 +  "style=" + style1 + ">";
	thead = "<tr class=\"b3\" "  +  "style=" + style1 + ">";
	//thead += "<th class=\"b\">App Number</th>";
	
	for (int m = 0; m < hdrArr.size(); m++) {
	thead += "<th> " + hdrArr.get(m) + "</th>";
	
	}
	thead += "</tr>";	
	return thead;
} 
/*************************************************************************************************************************************************************/

/*************************************************************************************************************************************************************/

%>

<%@include  file="includes/header.html" %>
<div style="padding-left:20px" >
<h3><%=title%></h3>
</div>
<!--   /*******************************************************************************************************************************************************************/ -->
<%
	/*******************************************************************************************************************************************************************/  
	// Display Data on page
	Connection conn = null;
	String sep = ",";
	ArrayList<String> list = new ArrayList<String>();
	ArrayList<String> qRtnArr = new ArrayList<String>();
	ResultSet resSet  = null;
	String splitItem = null;
	String cells = null;
	//list = (ArrayList<String>) session.getAttribute("strArr");
	Olyutil util = new Olyutil();
	conn = getDbConnection();
	String query = "select * from dbo.RollOverTest2";
	resSet = queryDB(conn, query);
	qRtnArr = util.resultSetArray(resSet, sep);	
	if (qRtnArr.size() > 0) {
		out.println("Display Data" + "<br>");
		out.println("<table class=\"tablesorter\" border=\"1\"> <thead> <tr>");
		out.println(TableHeader(resSet));
		out.println("</tr></thead>");
		out.println("<tbody id=\"report\"><tr>");
		for (int k = 0; k < qRtnArr.size(); k++) {
			splitItem = qRtnArr.get(k);
			String token_list1[] = splitItem.split(sep);
			for (int q = 0; q < token_list1.length; q++) {
				out.println("<td class=\"odd\">" + token_list1[q] + "</td>");			
			}
			cells = "";
			out.println("</tr>");
		}
		out.println("</tbody></table>"); // Close Table
	} else {
		out.println("No data to display." + "<br>");
	}
	 
	if (conn != null) {
		conn.close();
	}
 
	/*******************************************************************************************************************************************************************/

	/*******************************************************************************************************************************************************************/
%>


</body>
</html>
