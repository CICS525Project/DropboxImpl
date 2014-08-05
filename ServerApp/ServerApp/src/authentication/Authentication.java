package authentication;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import utils.ConnectionFactory;

/**
 * Class Name: Authentication
 * Used to Validate the User Login and Create New Account
 * @author Jitin
 *
 */

public class Authentication {

/**
 * To check for Valid User
 * @param userName
 * @param password
 * @return result 
 */
	public boolean validUser(String userName, String password){
		boolean result=false;
		Connection con = null;
		PreparedStatement ps=null;
		ResultSet rs=null;
		try {
			// Establish the connection.
			con=ConnectionFactory.getConnection();
			// Create and execute an SQL statement that returns some data.
			String query = "SELECT * FROM [User] WHERE username=? AND password =?";
			ps=con.prepareStatement(query);
			ps.setString(1, userName);
			ps.setString(2, password);
			rs=ps.executeQuery();
			while(rs.next()){
				result=true;
			}
		}
		catch (SQLException se) {
			result=false;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			if (rs != null) try { rs.close(); } catch(Exception e) {}
			if (ps != null) try { ps.close(); } catch(Exception e) {}
			if (con != null) try { con.close(); } catch(Exception e) {}
		}
		return result;
	}
	/**
	 * Used to create a new account for User
	 * @param userName
	 * @param password
	 * @return result
	 */

	public boolean createUser(String userName, String password){
		boolean result=false;
		Connection con = null;
		PreparedStatement ps=null;

		try {
			// Establish the connection.
			con=ConnectionFactory.getConnection();
			// Create and execute an SQL statement that returns some data.
			String query = "INSERT INTO [User] VALUES (?,?)";
			ps=con.prepareStatement(query);
			ps.setString(1, userName);
			ps.setString(2, password);
			ps.executeUpdate();
			result=true;
		}
		// Handle any errors that may have occurred.
		catch(SQLException se){
			result=false;
			System.out.println(se.getLocalizedMessage());
		}

		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			if (ps != null) try { ps.close(); } catch(Exception e) {}
			if (con != null) try { con.close(); } catch(Exception e) {}
		}
		return result;
	}


}
