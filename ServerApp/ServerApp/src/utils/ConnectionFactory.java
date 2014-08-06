package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * ClassName: ConnectionFactory
 * ManageConnection with the SQL DB in Azure
 * @author Jitin
 * @version 1.0, 19/July/2014
 */
public class ConnectionFactory {

	//static reference to DBS3
	//static reference to itself  

	
	private static Connection myConnection;	
	
	public static final String connectionUrl = Constants.URL +"databaseName="+Constants.DBNAME+";user="+Constants.USER+"@"+Constants.SQLSERVER+";"
		      		+ "password="+Constants.PASSWORD+"";
	/**
	 * Private Constructor
	 */
	static {
		try{
			Class.forName(Constants.DRIVER);
		}
		catch(ClassNotFoundException classNotFoundException){
			classNotFoundException.printStackTrace();
		}
	}
	/**
	 * Private Constructor
	 */
	private ConnectionFactory() {}
	/**
	 * MethodName: createConnection
	 * Create the Connection to the DB
	 * @return
	 * @throws SQLException
	 */
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
	/**
	 * MethodName: getConnection
	 * Calls the createConnection method
	 * @return
	 * @throws SQLException
	 */
	public static Connection getConnection() throws SQLException
	{
			myConnection = createConnection();
			myConnection.setAutoCommit(true);
		if (myConnection == null) {
			throw new SQLException("Could not connect to DB");
		}
		return myConnection;
	}
}
