package com.cloudbox.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * 
 * @author Jitin
 * @version 1.0, 19/July/2014
 */
public class ConnectionFactory {
	
	//static reference to itself	
	public static final String URL 		 = "jdbc:sqlserver://r8n9umoc58.database.windows.net;";
	public static final String USER 	 = "cics525";
	public static final String PASSWORD  = "MSSgroup6";
	public static final String DRIVER	 = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
	public static final String SQLSERVER = "r8n9umoc58";
	public static final String DBNAME    = "cics525group6DBS1";
	
	private static Connection myConnection;	
	
	public static final String connectionUrl = URL +"databaseName="+DBNAME+";user="+USER+"@"+SQLSERVER+";"
		      		+ "password="+PASSWORD+"";
	//private constructor
	static {
		try{
			Class.forName(DRIVER);
		}
		catch(ClassNotFoundException classNotFoundException){
			classNotFoundException.printStackTrace();
		}
	}
	
	private ConnectionFactory() {}
	
	public static Connection createConnection() throws SQLException
	{
		Connection connection=null;
		try{
			connection=DriverManager.getConnection(connectionUrl);
			return connection;
		}
		catch(SQLException exception){
			exception.printStackTrace();
			throw exception;
		}
	}
	
	public static Connection getConnection() throws SQLException
	{
		if (myConnection == null || myConnection.isClosed() ||
				! myConnection.isValid(0)) 
		{
			myConnection = createConnection();
			myConnection.setAutoCommit(true);
		}
		if (myConnection == null) {
			throw new SQLException("Could not connect to DB");
		}
		return myConnection;
	}
}
