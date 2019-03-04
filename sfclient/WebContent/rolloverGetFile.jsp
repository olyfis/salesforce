<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@page import="java.io.OutputStream"%>   
<%@ page import="java.io.File"%>
<%@ page import="java.util.ArrayList"%>
 <% 
  	String title =  "Olympus FIS Rollover Database Update"; 	 
%>

<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
 
<title><%=title%></title>
<script type="text/javascript" src="http://cdnjs.cloudflare.com/ajax/libs/jquery/1.9.1/jquery.min.js"></script>
<script type="text/javascript" src="http://cdnjs.cloudflare.com/ajax/libs/jquery.tablesorter/2.9.1/jquery.tablesorter.min.js"></script>
<!--  <script type="text/javascript" src="includes/js/tablesort.js"></script> -->
<style><%@include file="includes/css/reports.css"%></style>
<style><%@include file="includes/css/table.css"%></style>
<style><%@include file="includes/css/header.css"%></style>
<script type="text/javascript" src="includes/js/tableFilter.js"></script>
<link rel="stylesheet" href="includes/css/calendar.css" />

<!-- ********************************************************************************************************************************************************* -->
<script>
  function openWin(myID) {
   myID2 = document.getElementById(b_app).value;

  alert("ID" + myID2);
  //window.open("http://cvyhj3a27:8181/fisAssetServlet/readxml?appID=" + myID2);
	}
	var call = function(id){
		var myID = document.getElementById(id).value;
		//alert("****** myID=" + myID + " ID=" + id);
		//window.open("http://cvyhj3a27:8181/fisAssetServlet/readxml?appID=
		window.open("http://localhost:8181/webreport/getquote?appKey=" + myID);			
	}
	var getComments = function(id){
		var myID = document.getElementById(id).value;
		//alert("****** myID=" + myID + " ID=" + id);
		//alert(myID);
		//window.open("http://cvyhj3a27:8181/fisAssetServlet/readxml?appID=" + myID);
	}
	var getQuote = function(id){
		var myID = document.getElementById(id).value;
		//alert("****** myID=" + myID + " ID=" + id);
		//alert("in Quote" + myID + " --- id=" +id);
		window.open("http://cvyhj3a27:8181/webreport/getquote?appKey=" + myID, 'popUpWindow','height=500,width=800,left=100,top=100,resizable=yes,scrollbars=yes,toolbar=yes,menubar=no,location=no,directories=no, status=yes' );
		//window.open("http://cvyhj3a27:8181/fisAssetServlet/readxml?appID=" + myID);
	}
	var getExcel = function(urlValue){
		var formUrl = document.getElementById(urlValue).value;
		//alert("SD=" + startDate + "****** formUrl=" + formUrl + " \n***** urlValue=" + urlValue);
		//alert("in Quote" + myID + " --- id=" +id);
		window.open( formUrl, 'popUpWindow','height=500,width=800,left=100,top=100,resizable=yes,scrollbars=yes,toolbar=yes,menubar=no,location=no,directories=no, status=yes' );
	}
	var getDetails = function(id){
		var myID = document.getElementById(id).value;
		//alert("****** myID=" + myID + " ID=" + id);
		//alert("in Quote" + myID + " --- id=" +id);
		window.open("http://cvyhj3a27:8181/webreport/getdetails?appKey=" + myID, 'popUpWindow','height=1000,width=1200,left=100,top=100,resizable=yes,scrollbars=yes,toolbar=yes,menubar=no,location=no,directories=no, status=yes' );
		//window.open("http://cvyhj3a27:8181/fisAssetServlet/readxml?appID=" + myID);
	}	
</script>
<script>
$(function() {

  // call the tablesorter plugin
  $("table").tablesorter({
    theme: 'blue',
    // initialize zebra striping of the table
    widgets: ["zebra"],
    // change the default striping class names
    // updated in v2.1 to use widgetOptions.zebra = ["even", "odd"]
    // widgetZebra: { css: [ "normal-row", "alt-row" ] } still works
    widgetOptions : {
      zebra : [ "normal-row", "alt-row" ]
    }
  });

});		
    </script>	
</head>
<!-- ********************************************************************************************************************************************************* -->
<body style="font-family: sans-serif; color: black; font-size: 1;">
 <%@include  file="includes/header.html" %>
<div style="padding-left:20px">
  <h3><%=title%></h3>
</div>

 <form action = "uploaddata.jsp" method = "post" enctype = "multipart/form-data">
<table  border="1" class="tablesorter">
	  <tr bgcolor="#5DADE2"  style="font-family: sans-serif; color: white;" >
	  
	   <th class="a" >Select File</th>  
 <th class="a" >Action</th>

	  </tr>
	   <tr>
  <td bgcolor="#AEB6BF"> <input type = "file" name = "file" size = "50" /></td>
	 <td bgcolor="#AEB6BF"><input type = "submit" value = "Deliver File" /></td>
	 
	   </tr> </table> 
	   </form>
	 
<!-- ********************************************************************************************************************************************************* -->
<%!  
String formUrl = null;

public String TableHeader(){
	String thead = null;
	String color1 = "#5DADE2";
	String style1 = "font-family: sans-serif; color: white;";	  
	//thead = "<tr bgcolor=" + color1 +  "style=" + style1 + ">";
	thead = "<tr class=\"b\" "  +  "style=" + style1 + ">";
	//thead += "<th class=\"b\">App Number</th>";
	
	thead += "<th>Status</th>";
	thead += "<th>Contract ID</th>";
	

	thead += "</tr>";	
	return thead;
} 
%>
<!-- ********************************************************************************************************************************************************* -->	
</body>
</html>