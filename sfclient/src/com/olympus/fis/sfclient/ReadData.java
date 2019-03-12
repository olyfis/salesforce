package com.olympus.fis.sfclient;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.sql.Date;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.olympus.olyutil.Olyutil;

public class ReadData extends HttpServlet {
	static String sep = ":";
	//static String sep = "^";
	static Statement stmt = null;
	static private PreparedStatement statement;
	static ResultSet res  = null;
	static String mvUploadDir =   "D:\\Pentaho\\Kettle\\RollOver\\Uploaded\\";
	static String uploadDir = "D:\\Pentaho\\Kettle\\RollOver\\Upload";
	/******************************************************************************************************************************************************************/
	/******************************************************************************************************************************************************************/
	public ArrayList<String> doInsert() throws ServletException, IOException {
		Connection conn = null;
		String query = new String();
		String sep = ",";
		String sqlFilePath = "C:\\Java_Dev\\props\\sql\\rollover\\checkID.sql";
		String InsertSqlFilePath = "C:\\Java_Dev\\props\\sql\\rollover\\insert.sql";
		String propFilePath = "C:\\\\Java_Dev\\\\props\\\\connectionRollOver.prop";
		String inputFileName = "";
		 
		ArrayList<String> fileListArr = new ArrayList<String>();
		ArrayList<String> inputArr = new ArrayList<String>();
		
		Olyutil util = new Olyutil();
		String[] splitstr = null;
		String recID = "";
		try {
			fileListArr = util.getFilesFromDir(uploadDir);
			// System.out.println("**** Return List: " + fileListArr.toString());
			inputFileName = fileListArr.get(0);
			if (inputFileName.length() > 0) {
				inputArr = getDBInput(inputFileName, util);
				 //util.printStrArray(inputArr, "INPUT: ");
			}
			conn = do_init(propFilePath);
			if (conn != null) {
				// process input data
				for (int x = 0; x < inputArr.size(); x++) {
					splitstr = inputArr.get(x).split(",");
					recID = splitstr[0];
					//System.out.println("**** Record: " + recID );
					query = util.do_getQuery(sqlFilePath);
					//System.out.println("^^^ Query: " + query);		
					do_runQuery(conn, query, sep, recID);
					Boolean stat = util.do_checkDbRec(conn, recID, query, sep);
					if (stat.equals(false)) {
						//System.out.println("INSERT Record: " + recID + " Status=" + stat + " Arr= " + inputArr.get(x));
						do_InsertDbRec(conn, util, InsertSqlFilePath, splitstr);
					}
				}
			} else {
				System.out.println("Database connection: Failed");
			}
			if (conn != null) {
				conn.close();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return inputArr;
	}
	/******************************************************************************************************************************************************************/
		public ArrayList<String> do_runQuery(Connection con, String query, String sep, String recID) throws Exception { // 

		Olyutil util = new Olyutil();
		ArrayList<String> strArr = new ArrayList<String>();		 
		//System.out.println("**** Query: " + query);
		statement = con.prepareStatement(query);	
		statement.setString(1, recID);
		res = util.getResultSetPS(statement);	 
		strArr = util.resultSetArray(res, sep);			
		//System.out.println("**** arrSize=" + strArr.size()  );
		//System.out.println("**** arr:" + strArr.toString());	
		//result = jutil.displayResults(res);
		try {
			if (stmt != null) {
				stmt.close();
			}
		} catch (SQLException se) {
			se.printStackTrace();
		} // end finally try
		return strArr;
	}
	/******************************************************************************************************************************************************************/	
	public ArrayList<String> getDBInput(String inputFilePath, Olyutil util) throws Exception {
		ArrayList<String> inputArr = new ArrayList<String>();
		String srcFile = util.baseName(inputFilePath);
		String readFilePath = mvUploadDir + srcFile;	 
		util.moveFile(inputFilePath, readFilePath);
		inputArr = util.readInputFile(readFilePath); // Read CSV file	
		//System.out.println("*** IF: " + readFilePath + " BaseName: " + srcFile);
		//util.printStrArray(inputArr, "strArray");	
		return inputArr;
	}
	/******************************************************************************************************************************************************************/
	public Connection do_init(String propFilePath )  throws Exception {
		 Olyutil util = new Olyutil();
		 Connection conn = null;		 
		try {
			FileInputStream fis = new FileInputStream(propFilePath);
	 		Properties connectionProps = new Properties();
	 		connectionProps.load(fis); 		 		
			conn = util.getConnection(connectionProps);		
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return conn;
	 }
	/******************************************************************************************************************************************************************/
	public Boolean do_InsertDbRec(Connection con, Olyutil util, String sqlFilePath,  String[] data) throws Exception {
		
		Boolean status = false;
		ArrayList<String> strArr = new ArrayList<String>();	
		String insertQuery = new String();
		insertQuery = util.do_getQuery(sqlFilePath);
		System.out.println("****^^^^ 1=" + data[0] + " 2=" + data[1] + " 3=" + data[2] + " 4=" + data[3] + " 5=" + data[4] );	
		System.out.println("**** Query: " + insertQuery);		
		int id1 = Integer.parseInt(data[0]);
		int int1 = Integer.parseInt(data[4]);
		java.sql.Date date;
		date = new java.sql.Date(new SimpleDateFormat("yyyy-MM-dd").parse(data[3].substring(0, 10)).getTime());
		statement = con.prepareStatement(insertQuery);
		statement.setInt(1, id1);
		statement.setString(2, data[1]);
		statement.setString(3, data[2]);
		statement.setDate(4, date);
		statement.setInt(5, int1);
		statement.executeUpdate();

		// res = util.getResultSetPS(statement);
		// strArr = util.resultSetArray(res, sep);
		// System.out.println("**** arrSize=" + strArr.size() );
		// System.out.println("**** arr:" + strArr.toString());
		// result = jutil.displayResults(res);
		if (strArr.size() > 0) {
			status = true;
		}
		try {
			if (stmt != null) {
				stmt.close();
			}
		} catch (SQLException se) {
			se.printStackTrace();
		} // end finally try

		return status;
	}
	/******************************************************************************************************************************************************************/	
	/******************************************************************************************************************************************************************/	
	/******************************************************************************************************************************************************************/	
	public void testMove() throws ServletException, IOException {	
		ArrayList<String> fileListArr = new ArrayList<String>();
		ArrayList<String> inputArr = new ArrayList<String>();
		Olyutil util = new Olyutil();
		String[] splitstr = null;
		String recID = "";
		String inputFileName = "";
		try {
			fileListArr = util.getFilesFromDir(uploadDir);
			// System.out.println("**** Return List: " + fileListArr.toString());
			inputFileName = fileListArr.get(0);
			if (inputFileName.length() > 0) {
				//inputArr = getDBInput(inputFileName, util);
				// util.printStrArray(inputArr, "INPUT: ");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		String srcFile = util.baseName(inputFileName);
		System.out.println("*** IF: " + inputFileName + " BaseName: " + srcFile);
		//util.printStrArray(inputArr, "strArray");
		  //util.moveFile(inputFileName, mvUploadDir + srcFile);
		util.moveFile(inputFileName, mvUploadDir + srcFile);
	}
	/******************************************************************************************************************************************************************/
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		ArrayList<String> inputArr = new ArrayList<String>();
		String dispatchJSP = null;
		String paramName = "sqlType";
		String paramValue = request.getParameter(paramName);
		//System.out.println("sqlType=" + paramName + " pVal=" + paramValue + "--");

		if ((paramValue != null && !paramValue.isEmpty()) && paramValue.equals("INS")) {
			inputArr = doInsert();
			//System.out.println("sqlType=" + paramName + " pVal=" + paramValue + "--");
			dispatchJSP = "/uploadresult.jsp";
			//testMove();
		} else if ((paramValue != null && !paramValue.isEmpty()) && paramValue.equals("DSP")) {
			dispatchJSP = "/displaytable.jsp";
		}
		//strArr = getData(startDateValue, endDateValue, connProp, sqlFile, sep);
	 
 		request.getSession().setAttribute("strArr", inputArr);
		// req.getSession().setAttribute(paramName, paramValue);
		request.getRequestDispatcher(dispatchJSP).forward(request, response);
		// System.out.println("Exit Servlet " + this.getServletName() + " in doGet() ");
	 
		
		
	} // End doGet

	/******************************************************************************************************************************************************************/
}
