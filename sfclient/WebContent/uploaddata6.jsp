<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@page import="java.io.OutputStream"%>   
<%@ page import="java.io.File"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import = "java.io.*,java.util.*, javax.servlet.*" %>
<%@ page import = "javax.servlet.http.*" %>
<%@ page import = "org.apache.commons.fileupload.*" %>
<%@ page import = "org.apache.commons.fileupload.disk.*" %>
<%@ page import = "org.apache.commons.fileupload.servlet.*" %>
<%@ page import = "org.apache.commons.io.output.*" %>
<%@ page import="java.sql.*"%>


<% 
  	 String title =  "Olympus FIS Rollover Database Update"; 	 
%>

<%

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
public ArrayList<String> readFileData(String filePath){
	 ArrayList<String> arr = new ArrayList<String>();
	 
     try (BufferedReader br = new BufferedReader(new FileReader(filePath)))
     {

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
		System.out.println("PATH=" + path);
		String[] pathparts = path.split("\\\\");
		filename = pathparts[pathparts.length - 1];
		System.out.println("FN=" + filename);
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

		System.out.println("**** Load Driver");
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


/*******************************************************************************************************************************************************************/
public static void  checkRecExists(Connection connection, String ID ) throws Exception {
	Statement statement = null;
	ResultSet resultSet = null;
	
	try {	
		if (connection != null) {
			statement = connection.createStatement();	
			String sql = "SELECT  [ContractID] FROM [RollOverTest].[dbo].[RollOverTest2] where [ContractID] =  " + ID + "";
			System.out.println("**** Query: " + sql);
			resultSet = statement.executeQuery(sql);
		} else {
			System.out.println("**** Connection is null! ");
		}
		
		//System.out.println("RS=" + resultSet.toString());
	} catch (Exception e) {
		e.printStackTrace();
	}	 
	while(resultSet.next()){		
		System.out.println("****^^^^**** RS=" + resultSet.getString("ContractID"));
	}
 
}
/*******************************************************************************************************************************************************************/

	%>
  
 <%@include  file="includes/header.html" %>
<div style="padding-left:20px" >
  <h3><%=title%></h3>
</div>

<%

	Connection conn = null;

	ArrayList<String> dataArr = new ArrayList<String>();
   File file ;
   int maxFileSize = 15000 * 1024;
   int maxMemSize = 15000 * 1024;
   ServletContext context = pageContext.getServletContext();
   String filePath = context.getInitParameter("file-upload");
   String xDataItem = null;
   String rowEven = "#D7DBDD";
   String rowOdd = "AEB6BF";
   String rowColor = "";
   // Verify the content type
   String contentType = request.getContentType();
   
   if ((contentType.indexOf("multipart/form-data") >= 0)) {
      DiskFileItemFactory factory = new DiskFileItemFactory();
      // maximum size that will be stored in memory
      factory.setSizeThreshold(maxMemSize);
      
      // Location to save data that is larger than maxMemSize.
      factory.setRepository(new File("c:\\temp"));

      // Create a new file upload handler
      ServletFileUpload upload = new ServletFileUpload(factory);
      
      // maximum file size to be uploaded.
      upload.setSizeMax( maxFileSize );
      
      try { 
         // Parse the request to get file items.
         List fileItems = upload.parseRequest(request);

         // Process the uploaded file items
         Iterator i = fileItems.iterator();
/*
         out.println("<html>");
         out.println("<head>");
         out.println("<title>JSP File upload</title>");  
         out.println("</head>");
         out.println("<body>");
        */
         while ( i.hasNext () ) {
            FileItem fi = (FileItem)i.next();
            if ( !fi.isFormField () ) {
               // Get the uploaded file parameters
               String fieldName = fi.getFieldName();
               String fileName = fi.getName();
               boolean isInMemory = fi.isInMemory();
               long sizeInBytes = fi.getSize();
            
               // Write the file
               if( fileName.lastIndexOf("\\") >= 0 ) {
                  file = new File( filePath + 
                  fileName.substring( fileName.lastIndexOf("\\"))) ;
               } else {
                  file = new File( filePath + 
                  fileName.substring(fileName.lastIndexOf("\\")+1)) ;
               }
               fi.write( file ) ;
              // out.println("Uploaded Filename: " + filePath +  fileName + "<br>");
               
               out.println("<h5>Uploaded Filename: " + fileName + "</h5>");
               //out.println("<h5>Uploaded to Directory: " + filePath + "</h5><br>");
               String srcFile = filePath + "\\" + basename(fileName);
               //out.println("<h5>srcFile: " + srcFile + "</h5><br>");
               dataArr = readFileData(srcFile);
              // out.println("dataArr size=" + dataArr.size() + "--");
               
              
              conn = getDbConnection();
             
               
               out.println("<table  border=\"1\" >  <tr bgcolor=\"#5DADE2\"  style=\"font-family: sans-serif; color: white;\" >");
               out.println("<th class=\"a\" >DB Status </th>");
               out.println("<th class=\"a\" >Column 1 </th>");
               out.println("<th class=\"a\" >Column 2 </th>");
               out.println("<th class=\"a\" >Column 3 </th>");
               out.println("</tr>");
               
               for (int j = 0; j < dataArr.size(); j++) {
          			 rowColor = (j % 2 == 0) ? rowEven : rowOdd;
          			 out.println("<tr bgcolor=" + rowColor + ">");
          	
          			xDataItem = dataArr.get(j);
          		 
          			String token_list[] = xDataItem.split(",");
          			String idVal = token_list[0];
          			 checkRecExists(conn, idVal);
          			
               
             
              // out.println("<tr>");
               out.println("<td> Insert</td>");
               for (int x = 0; x < token_list.length; x++) {
            	  
     				out.println("<td>" + token_list[x] +"</td>");
     			}
 
               out.println("</tr>");
              
               
               
               } // end outter loop
               out.println("</table> <BR>");
               %>
               <form action = "/sfclient/rollover"  method = "get"  >
               <table  border="1" >
               	  <tr bgcolor="#5DADE2"  style="font-family: sans-serif; color: white;" >
               	  
               	  
                <th class="a" >Insert Data into Rapport </th>

               	  </tr>
               	   <tr>
                 
               	 <td bgcolor="#AEB6BF"><input type = "submit" value = "Upload Data" /></td>
               	 
               	   </tr> </table> 
               	   <input type = "hidden" name="sqlType" value = "INS" />
               	   </form>
              <%  
               
            }
         }
         //out.println("</body>");
        // out.println("</html>");
      } catch(Exception ex) {
         System.out.println(ex);
      }
   } else {
	   /*
      out.println("<html>");
      out.println("<head>");
      out.println("<title>Servlet upload</title>");  
      out.println("</head>");
      out.println("<body>");
      */
      out.println("<p>No file uploaded</p>"); 
     // out.println("</body>");
      //out.println("</html>");
   }
%>
</body>
</html>