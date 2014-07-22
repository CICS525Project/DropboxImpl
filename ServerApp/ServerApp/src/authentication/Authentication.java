package authentication;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class Authentication {


	public boolean validUser(String userName, String password){
		boolean result=false;
		// Create a variable for the connection string.
		Utils util=new Utils();
		String connectionUrl = util.sqlServer +
				"databaseName="+util.dbName+";user="+util.userName+"@"+util.serverName+";"
				+ "password="+util.password+"";
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;

		try {
			// Establish the connection.
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			con = DriverManager.getConnection(connectionUrl);
			// Create and execute an SQL statement that returns some data.
			String SQL = "SELECT * FROM [User] WHERE username="+"'"+userName+"'"+" AND password ="+"'"+password+"'";
			System.out.println(SQL);
			stmt = con.createStatement();
			rs = stmt.executeQuery(SQL);
			// Iterate through the data in the result set and display it.
			while (rs.next()) {
				result=true;
			}
		}
		// Handle any errors that may have occurred.
		catch (Exception e) {
			System.out.println(e.getLocalizedMessage());
			e.printStackTrace();
		}
		finally {
			if (rs != null) try { rs.close(); } catch(Exception e) {}
			if (stmt != null) try { stmt.close(); } catch(Exception e) {}
			if (con != null) try { con.close(); } catch(Exception e) {}
		}
		return result;
	}


	public boolean createUser(String userName, String password){
		boolean result=false;
		// Create a variable for the connection string.
		Utils util=new Utils();
		String connectionUrl = util.sqlServer +
				"databaseName="+util.dbName+";user="+util.userName+"@"+util.serverName+";"
				+ "password="+util.password+"";
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;

		try {
			// Establish the connection.
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			con = DriverManager.getConnection(connectionUrl);
			// Create and execute an SQL statement that returns some data.
			String SQL = "INSERT INTO [User] VALUES ('"+userName+"','"+password+"')";
			stmt = con.createStatement();
			stmt.executeUpdate(SQL);
			result=true;
		}
		// Handle any errors that may have occurred.
		catch(SQLException se){
			result=false;
			System.out.println(se.getLocalizedMessage());
		}

		catch (Exception e) {
			System.out.println(e.getLocalizedMessage());
			e.printStackTrace();
		}
		finally {
			if (rs != null) try { rs.close(); } catch(Exception e) {}
			if (stmt != null) try { stmt.close(); } catch(Exception e) {}
			if (con != null) try { con.close(); } catch(Exception e) {}
		}
		return result;
	}

}