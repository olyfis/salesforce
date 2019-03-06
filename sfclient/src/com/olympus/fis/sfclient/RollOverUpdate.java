package com.olympus.fis.sfclient;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.*;
import java.util.*;
import java.io.File;
import com.olympus.olyutil.*;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.w3c.dom.NodeList;

public class RollOverUpdate extends HttpServlet {
	
	static String sep = ":";
	//static String sep = "^";
	static Statement stmt = null;
	static private PreparedStatement statement;
	static ResultSet res  = null;
	
	static String sqlFileName =   "C:\\Java_Dev\\props\\sql\\test.sql";
	static String mvUploadDir =   "D:\\Pentaho\\Kettle\\RollOver\\Uploaded\\";
	/******************************************************************************************************************************************************************/	
	public static String baseName(String path) {
		String filename = "";
		System.out.println("PATH=" + path);
		String[] pathparts = path.split("\\\\");
		filename = pathparts[pathparts.length - 1];
		System.out.println("FN=" + filename);
		return filename;
	}
	/******************************************************************************************************************************************************************/	



/******************************************************************************************************************************************************************/	

public String get_query(String sqlFilePath ) throws Exception {
		 String queryStr = "";
		 
			String s = new String();
	        StringBuffer sb = new StringBuffer();
			FileReader fr = new FileReader(new File(sqlFilePath));
					
			// be sure to not have line starting with "--" or "/*" or any other non alphabetical character
			BufferedReader br = new BufferedReader(fr);
			while((s = br.readLine()) != null){
			      sb.append(s);
			}
			br.close();	
			String query = new String();
			query = sb.toString();
			//System.out.println("**** Query: " + query);		 
		 return query;
	 }
	/******************************************************************************************************************************************************************/	
	protected void do_runQuery(Connection con, Olyutil util, String sqlFilePath, String recID) throws Exception {
		 
		ArrayList<String> strArr = new ArrayList<String>();	
		String query = new String();
		query = get_query(sqlFilePath);
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
	}
	/******************************************************************************************************************************************************************/	
	public ArrayList<String> getDBInput(String inputFilePath, Olyutil util) throws Exception {
		ArrayList<String> inputArr = new ArrayList<String>();
		
		String srcFile = util.baseName(inputFilePath);
		String readFilePath = mvUploadDir + srcFile;
		 
		util.moveFile(inputFilePath, readFilePath);
		//inputArr = util.readInputFile(inputFilePath); // Read CSV file
		inputArr = util.readInputFile(readFilePath); // Read CSV file
		
		System.out.println("*** IF: " + readFilePath + " BaseName: " + srcFile);
		util.printStrArray(inputArr, "strArray");
		
		return inputArr;
		
		
 /*
		inputArr = util.readInputFile(inputFilePath); // Read CSV file
		String srcFile = baseName(inputFilePath);
		System.out.println("*** IF: " + inputFilePath + " BaseName: " + srcFile);
		util.printStrArray(inputArr, "strArray");
		moveFile(inputFilePath, mvUploadDir + srcFile);
		return inputArr;
		*/
		
	}
	/******************************************************************************************************************************************************************/	
	public ArrayList<String> getDbInputFileName(String uploadDir) throws Exception {
		ArrayList<String> fileListArr = new ArrayList<String>();
		
		File f = new File(uploadDir); // current directory
		File[] files = f.listFiles();
 
		for (File file : files) {
			if (file.isDirectory()) {
				System.out.print("****^^^^**** directory: ");
			} else {
				//System.out.print("     file: ");	
			}
			 
			fileListArr.add(file.getCanonicalPath());
		}	
		return fileListArr;
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
	public ArrayList<String> readDbInputFile(String inputFilePath, Olyutil util)  throws Exception {
		 ArrayList<String> inputArr = new ArrayList<String>();
		 inputArr = getDBInput(inputFilePath, util);
		//util.printStrArray(inputArr, "strArray");
		return inputArr;	
	}
	/******************************************************************************************************************************************************************/	
	protected Boolean do_checkDbRec(Connection con, Olyutil util, String sqlFilePath, String recID) throws Exception {
		
		Boolean status = false;
		ArrayList<String> strArr = new ArrayList<String>();	
		String query = new String();
		query = get_query(sqlFilePath);
		//System.out.println("**** Query: " + query);
		statement = con.prepareStatement(query);	
		statement.setString(1, recID);
		res = util.getResultSetPS(statement);	 
		strArr = util.resultSetArray(res, sep);			
		//System.out.println("**** arrSize=" + strArr.size()  );
		//System.out.println("**** arr:" + strArr.toString());	
		//result = jutil.displayResults(res);
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
protected Boolean do_InsertDbRec(Connection con, Olyutil util, String sqlFilePath,  String[] data) throws Exception {
		
		Boolean status = false;
		ArrayList<String> strArr = new ArrayList<String>();	
		String insertQuery = new String();
		insertQuery = get_query(sqlFilePath);
		//System.out.println("****^^^^ 1=" + data[0] + " 2=" + data[1] + " 3=" + data[2] + " 3=" + data[3]);
		//System.out.println("****^^^^ 1=" + data[0] + " 2=" + data[1] + " 3=" + data[2]  );
		
		//System.out.println("**** Query: " + insertQuery);
		
		int id1 = Integer.parseInt(data[0]);
		statement = con.prepareStatement(insertQuery);	
		statement.setInt(1, id1);
		statement.setString(2, data[1]);
		statement.setString(3, data[2]);
		statement.executeUpdate();
		
		//res = util.getResultSetPS(statement);	 
		//strArr = util.resultSetArray(res, sep);			
		//System.out.println("**** arrSize=" + strArr.size()  );
		//System.out.println("**** arr:" + strArr.toString());	
		//result = jutil.displayResults(res);
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
	public void doInsert() throws ServletException, IOException {
		Connection conn = null;
		// String sqlFilePath = "C:\\Java_Dev\\props\\sql\\rollover\\test.sql";
		String sqlFilePath = "C:\\Java_Dev\\props\\sql\\rollover\\checkID.sql";
		String InsertSqlFilePath = "C:\\Java_Dev\\props\\sql\\rollover\\insert.sql";
		String propFilePath = "C:\\\\Java_Dev\\\\props\\\\connectionRollOver.prop";
		String inputFileName = "";
		String uploadDir = "D:\\Pentaho\\Kettle\\RollOver\\Upload";
		ArrayList<String> fileListArr = new ArrayList<String>();
		ArrayList<String> inputArr = new ArrayList<String>();
		Olyutil util = new Olyutil();
		String[] splitstr = null;
		String recID = "";
		try {
			fileListArr = getDbInputFileName(uploadDir);
			// System.out.println("**** Return List: " + fileListArr.toString());
			inputFileName = fileListArr.get(0);
			if (inputFileName.length() > 0) {
				inputArr = readDbInputFile(inputFileName, util);
				 //util.printStrArray(inputArr, "INPUT: ");
			}
			conn = do_init(propFilePath);
			if (conn != null) {
				// process input data
				for (int x = 0; x < inputArr.size(); x++) {
					splitstr = inputArr.get(x).split(",");
					recID = splitstr[0];
					//System.out.println("**** Record: " + recID );
					do_runQuery(conn, util, sqlFilePath, recID);
					Boolean stat = do_checkDbRec(conn, util, sqlFilePath, recID);
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
	}

	/******************************************************************************************************************************************************************/	

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
	 
		String paramName = "sqlType";
		String paramValue = request.getParameter(paramName);
		//System.out.println("sqlType=" + paramName + " pVal=" + paramValue + "--");

		if ((paramValue != null && !paramValue.isEmpty()) && paramValue.equals("INS")) {
			doInsert();
		}

	} // End doGet
	
	/******************************************************************************************************************************************************************/	
}
