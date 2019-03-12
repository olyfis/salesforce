<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
	<form action="uploaddata.jsp" method="post"
		enctype="multipart/form-data">
		<table border="1">
			<!--  <table  border="1" class="tablesorter"  >     -->
			<tr bgcolor="#5DADE2" style="font-family: sans-serif; color: white;">
				<th class="a">Select File</th>
				<th class="a">Action</th>
			</tr>
			<tr>
				<td bgcolor="#AEB6BF"><input type="file" name="file" size="50" /></td>
				<td bgcolor="#AEB6BF"><input type="submit" value="Deliver File" /></td>
			</tr>
		</table>
	</form>
	<BR>
	<BR>
	<!--    ************************************************************************************************************************ -->
	<form action="/sfclient/readdata" method="get">
		<table border="1" class="tablesorter">
			<tr bgcolor="#5DADE2" style="font-family: sans-serif; color: white;">
				<th class="a">Display Data from Rapport Database</th>
			</tr>
			<tr>
				<td bgcolor="#AEB6BF"><input type="submit" value="Display Data" /></td>
			</tr>
		</table>
		<input type="hidden" name="sqlType" value="DSP" />
	</form>
	</td>
	</tr>
	</table>

</body>
</html>