/**
 * 
 */
package routingTable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import authentication.UserInfo;

import com.microsoft.windowsazure.services.core.storage.Constants;

import serviceServer.RoutingTable;
import utils.ConnectionFactory;

/**
 * ClassName: DBConnection
 * This class is used to Store the File Details in the Routing Table and the Shared Table
 * @author Jitin
 * @version 2.0, 22/July/2014
 */
public class DBConnection {
	/**
	 * Method Name: insertRecordforUpload
	 * This method will receive a Hashmap containing FileName and Version which is saved
	 * into the Routing Table in the Server
	 * @param fileList
	 * @param userName
	 * @param serverName
	 * @return result
	 * @throws SQLException
	 */
	public String insertRecordforUpload(HashMap<String,Integer> fileList,String userName,String serverName) throws SQLException{
		String result=null;
		Connection con = null;
		PreparedStatement ps=null;
		try{
			con=ConnectionFactory.getConnection();
			String query="INSERT INTO [routingTable] VALUES (?,?,?,?)";
			ps=con.prepareStatement(query);
			Set set=fileList.entrySet();
			Iterator i=set.iterator();
			while(i.hasNext()){
				Map.Entry me = (Map.Entry)i.next();
				ps.setString(1, userName);
				ps.setString(2, (String)me.getKey());
				ps.setString(3, serverName);
				ps.setInt(4, (int)me.getValue());
				ps.addBatch();
			}
			ps.executeBatch();
			result="success";
		}
		catch(Exception e){
			result="failure";
			throw new SQLException();
		}
		finally {
			if (ps != null) try { ps.close(); } catch(Exception e) {}
			if (con != null) try { con.close(); } catch(Exception e) {}
		}
		return result;
	}
/**
 * Method Name: updateVersionForFile
 * This method will receive a Hashmap containing FileName and Version for which 
 * the Version number are updated in case of new Version Numbers
 * @param fileList
 * @param userName
 * @param serverName
 * @return result
 * @throws SQLException
 */
	public String updateVersionForFile(HashMap<String,Integer> fileList,String userName,String serverName) throws SQLException {
		String result=null;
		Connection con = null;
		PreparedStatement ps=null;
		try{
			con=ConnectionFactory.getConnection();
			String query="UPDATE [routingTable] SET version= ? WHERE userName =? AND fileName=? AND serverName=?";
			ps=con.prepareStatement(query);
			Set set=fileList.entrySet();
			Iterator i=set.iterator();
			while(i.hasNext()){
				Map.Entry me = (Map.Entry)i.next();
				ps.setInt(1, (int)me.getValue());
				ps.setString(2, userName);
				ps.setString(3, (String)me.getKey());
				ps.setString(4, serverName);
				ps.addBatch();
			}
			ps.executeBatch();
			result="success";
		}
		catch(Exception e){
			result="failure";
			throw new SQLException();
		}
		finally {
			if (ps != null) try { ps.close(); } catch(Exception e) {}
			if (con != null) try { con.close(); } catch(Exception e) {}
		}
		return result;

	}
/**
 * Method Name: insertRecordforShare
 * This method is used to insert the records to the sharedTable 
 * when the given user shares files with other users
 * @param fileList
 * @param userName
 * @return
 * @throws SQLException
 */
	public String insertRecordforShare(HashMap<String,String> fileList,String userName) throws SQLException{
		String result=null;
		Connection con = null;
		PreparedStatement ps=null;
		try{
			con=ConnectionFactory.getConnection();
			String query="INSERT INTO [sharedTable] VALUES (?,?,?)";
			ps=con.prepareStatement(query);
			Set set=fileList.entrySet();
			Iterator i=set.iterator();
			while(i.hasNext()){
				Map.Entry me = (Map.Entry)i.next();
				ps.setString(1, userName);
				ps.setString(2, (String)me.getKey());
				ps.setString(3, (String)me.getValue());
				ps.addBatch();
			}
			ps.executeBatch();
			result="success";
		}
		catch(Exception e){
			result="failure";
			throw new SQLException();
		}
		finally {
			if (ps != null) try { ps.close(); } catch(Exception e) {}
			if (con != null) try { con.close(); } catch(Exception e) {}
		}
		return result;
	}
/**
 * Method Name: searchForServerName
 * This method is used to search for the Server DNS Names of the Given FileNames for
 * a particular User. This includes both Owned Files and Shared Files for the User
 * @param fileList
 * @param userName
 * @return
 * @throws SQLException
 */
	public HashMap<String, String> searchForServerName(ArrayList<String> fileList,String userName)throws SQLException{
		Connection con = null;
		Connection con1 = null;
		PreparedStatement ps=null;
		PreparedStatement ps1=null;
		ResultSet rs=null;
		ResultSet rs1=null;
		HashMap<String, String> result =new HashMap<String, String>();
		ArrayList<String> userShareList=new ArrayList<String>();
		try{
			con=ConnectionFactory.getConnection();
			for(String file: fileList){
				String query="SELECT fileName,serverName FROM [routingTable] WHERE userName=? AND fileName=?";
				ps=con.prepareStatement(query);
				ps.setString(1, userName);
				ps.setString(2, file);
				rs=ps.executeQuery();
				while(rs.next()){
					result.put(rs.getString(1), rs.getString(2));
				}
			}
			userShareList=searchForFileinSharedTable(fileList,userName);
			con1=ConnectionFactory.getConnection();
			for(String user:userShareList){
				for(String file: fileList){
					String query="SELECT fileName,serverName FROM [routingTable] WHERE userName=? AND fileName=?";
					ps1=con1.prepareStatement(query);
					ps1.setString(1, user);
					ps1.setString(2, file);
					rs1=ps1.executeQuery();
					while(rs1.next()){
						result.put(rs1.getString(1), rs1.getString(2));
					}
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
			throw new SQLException();
		}
		finally {
			if (ps != null) try { ps.close(); } catch(Exception e) {}
			if (ps1 != null) try { ps1.close(); } catch(Exception e) {}
			if (con != null) try { con.close(); } catch(Exception e) {}
			if (con1 != null) try { con1.close(); } catch(Exception e) {}
			if (rs != null) try { rs.close(); } catch(Exception e) {}
			if (rs1 != null) try { rs1.close(); } catch(Exception e) {}
		}
		return result;
	}
	/**
	 * Method Name: searchForFileinSharedTable
	 * Searches the Owner Name of the Files who shared with the Given UserName
	 * @param fileList
	 * @param userName
	 * @return
	 */
	public ArrayList<String> searchForFileinSharedTable(ArrayList<String> fileList,String userName){
		Connection con = null;
		PreparedStatement ps=null;
		ResultSet rs=null;
		ArrayList<String> result=new ArrayList<String>();
		try{
			for(String file: fileList){
				con=ConnectionFactory.getConnection();
				String query="SELECT userName FROM [sharedTable] WHERE sharedUserName=? AND fileName=?";
				ps=con.prepareStatement(query);
				ps.setString(1, userName);
				ps.setString(2, file);
				rs=ps.executeQuery();
				while(rs.next()){
					result.add(rs.getString(1));
				}
			}
		}
		catch(Exception e){
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
	 * Method Name: searchForFileinSharedTable
	 * Searches the Owner Name of the Files who shared with the Given UserName
	 * @param userName
	 * @return
	 */
	public ArrayList<String> searchForFileinSharedTable(String userName){
		Connection con = null;
		PreparedStatement ps=null;
		ResultSet rs=null;
		ArrayList<String> result=new ArrayList<String>();
		try{
				con=ConnectionFactory.getConnection();
				String query="SELECT userName FROM [sharedTable] WHERE sharedUserName=?";
				ps=con.prepareStatement(query);
				ps.setString(1, userName);
				rs=ps.executeQuery();
				while(rs.next()){
					result.add(rs.getString(1));
				}
		}
		catch(Exception e){
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
	 * Method Name: searchForVersionNumber
	 * This method is used to return the FileNumber and their Version Numbers
	 * for a given List of Files
	 * @param fileList
	 * @param userName
	 * @return result
	 * @throws SQLException
	 */
	public HashMap<String, Integer> searchForVersionNumber(ArrayList<String> fileList,String userName)throws SQLException{
		Connection con = null;
		PreparedStatement ps=null;
		ResultSet rs=null;
		HashMap<String, Integer>  result=new HashMap<String, Integer> ();
		try{
			con=ConnectionFactory.getConnection();
			for(String file: fileList){
				String query="SELECT version FROM [routingTable] WHERE userName=? AND fileName=?";
				ps=con.prepareStatement(query);
				ps.setString(1, userName);
				ps.setString(2, file);
				rs=ps.executeQuery();
				while(rs.next()){
					result.put(file,rs.getInt(1));
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
			throw new SQLException();
		}
		finally {
			if (rs != null) try { rs.close(); } catch(Exception e) {}
			if (ps != null) try { ps.close(); } catch(Exception e) {}
			if (con != null) try { con.close(); } catch(Exception e) {}
		}
		return result;
	}
	/**
	 * Method Name: searchForFiles
	 * Used to retrieve the List of Files associated with a particular UserName
	 * Includes both the Owned Files and Shared by files
	 * @param userName
	 * @return result
	 * @throws SQLException
	 */
		public HashMap<String, Integer> searchForFiles(String userName)throws SQLException{
			Connection con = null;
			Connection con1 = null;
			PreparedStatement ps=null;
			PreparedStatement ps1=null;
			ResultSet rs=null;
			ResultSet rs1=null;
			HashMap<String, Integer> result =new HashMap<String, Integer>();
			ArrayList<String> userShareList=new ArrayList<String>();
			try{
				con=ConnectionFactory.getConnection();
					String query="SELECT fileName,version FROM [routingTable] WHERE userName=?";
					ps=con.prepareStatement(query);
					ps.setString(1, userName);
					rs=ps.executeQuery();
					while(rs.next()){
						result.put(rs.getString(1), rs.getInt(2));
					}
				userShareList=searchForFileinSharedTable(userName);
				con1=ConnectionFactory.getConnection();
				for(String user:userShareList){
						String query1="SELECT fileName,version FROM [routingTable] WHERE userName=?";
						ps1=con1.prepareStatement(query1);
						ps1.setString(1, user);
						rs1=ps1.executeQuery();
						while(rs1.next()){
							result.put(rs1.getString(1), rs1.getInt(2));
						}
						
				}
			}
			catch(Exception e){
				e.printStackTrace();
				throw new SQLException();
			}
			finally {
				
				if (ps != null) try { ps.close(); } catch(Exception e) {}
				if (ps1 != null) try { ps1.close(); } catch(Exception e) {}
				if (con != null) try { con.close(); } catch(Exception e) {}
				if (con1 != null) try { con1.close(); } catch(Exception e) {}
				if (rs != null) try { rs.close(); } catch(Exception e) {}
				if (rs1 != null) try { rs1.close(); } catch(Exception e) {}
			}
			return result;
		}
		
		/**
		 * Method Name: getAllFromRoutingTable
		 * Used to retrieve the List of Files associated with a particular UserName
		 * Includes both the Owned Files and Shared by files
		 * @param userName
		 * @return result
		 * @throws SQLException
		 */
			public ArrayList<RoutingTable> getAllFromRoutingTable(String s)throws SQLException{
				Connection con = null;
				Connection con1 = null;
				PreparedStatement ps=null;
				PreparedStatement ps1=null;
				ResultSet rs=null;
				ResultSet rs1=null;
				ArrayList<RoutingTable> result=new ArrayList<RoutingTable>();
				try{
					con=ConnectionFactory.getConnection();
						String query="SELECT userName,fileName,serverName,version FROM [routingTable] WHERE serverName=?";
						ps=con.prepareStatement(query);
						ps.setString(1, s);
						rs=ps.executeQuery();
						while(rs.next()){
							RoutingTable routingTable=new RoutingTable();
							routingTable.setUserName(rs.getString(1));
							routingTable.setFileName(rs.getString(2));
							routingTable.setServerName(rs.getString(3));
							routingTable.setVersion(rs.getInt(4));
							result.add(routingTable);
						}
				}
				catch(Exception e){
					e.printStackTrace();
					throw new SQLException();
				}
				finally {
					
					if (ps != null) try { ps.close(); } catch(Exception e) {}
					if (con != null) try { con.close(); } catch(Exception e) {}
					if (rs != null) try { rs.close(); } catch(Exception e) {}
				}
				return result;
			}
			/**
			 * Method Name: getAllFromRoutingTable
			 * Used to retrieve the List of Files associated with a particular UserName
			 * Includes both the Owned Files and Shared by files
			 * @return result
			 * @throws SQLException
			 */
				public ArrayList<RoutingTable> getAllFromRoutingTable()throws SQLException{
					Connection con = null;
					Connection con1 = null;
					PreparedStatement ps=null;
					PreparedStatement ps1=null;
					ResultSet rs=null;
					ResultSet rs1=null;
					ArrayList<RoutingTable> result=new ArrayList<RoutingTable>();
					try{
							con=ConnectionFactory.getConnection();
							String query="SELECT userName,fileName,serverName,version FROM [routingTable]";
							ps=con.prepareStatement(query);
							rs=ps.executeQuery();
							while(rs.next()){
								RoutingTable routingTable=new RoutingTable();
								routingTable.setUserName(rs.getString(1));
								routingTable.setFileName(rs.getString(2));
								routingTable.setServerName(rs.getString(3));
								routingTable.setVersion(rs.getInt(4));
								result.add(routingTable);
							}
					}
					catch(Exception e){
						e.printStackTrace();
						throw new SQLException();
					}
					finally {
						
						if (ps != null) try { ps.close(); } catch(Exception e) {}
						if (con != null) try { con.close(); } catch(Exception e) {}
						if (rs != null) try { rs.close(); } catch(Exception e) {}
					}
					return result;
				}
				/**
				 * Method Name: getAllFromSharingTable
				 * Used to retrieve the List of Files associated with a particular UserName
				 * Includes both the Owned Files and Shared by files
				 * @param userName
				 * @return result
				 * @throws SQLException
				 */
				public ArrayList<RoutingTable> getAllFromSharingTable()throws SQLException{
					Connection con = null;
					PreparedStatement ps=null;
					ResultSet rs=null;
					ArrayList<RoutingTable> result=new ArrayList<RoutingTable>();
					try{
						con=ConnectionFactory.getConnection();
						String query="SELECT userName,fileName,sharedUserName FROM [sharedTable]";
						ps=con.prepareStatement(query);
						rs=ps.executeQuery();
						while(rs.next()){
							RoutingTable routingTable=new RoutingTable();
							routingTable.setUserName(rs.getString(1));
							routingTable.setFileName(rs.getString(2));
							routingTable.setSharedUserName(rs.getString(3));
							result.add(routingTable);
						}
					}
					catch(Exception e){
						e.printStackTrace();
						throw new SQLException();
					}
					finally {

						if (ps != null) try { ps.close(); } catch(Exception e) {}
						if (con != null) try { con.close(); } catch(Exception e) {}
						if (rs != null) try { rs.close(); } catch(Exception e) {}
					}
					return result;
				}
				
				/**
				 * Method Name: getUserInfo
				 * Get the user credentials from the DB
				 * @return result
				 * @throws SQLException
				 */
				public ArrayList<UserInfo> getUserInfo()throws SQLException{
					Connection con = null;
					PreparedStatement ps=null;
					ResultSet rs=null;
					ArrayList<UserInfo> result=new ArrayList<UserInfo>();
					try{
						con=ConnectionFactory.getConnection();
						String query="SELECT userName,password FROM [user]";
						ps=con.prepareStatement(query);
						rs=ps.executeQuery();
						while(rs.next()){
							UserInfo info=new UserInfo();
							info.setUserName(rs.getString("userName"));
							info.setPassword(rs.getString("password"));
							result.add(info);
						}
					}
					catch(Exception e){
						e.printStackTrace();
						throw new SQLException();
					}
					finally {

						if (ps != null) try { ps.close(); } catch(Exception e) {}
						if (con != null) try { con.close(); } catch(Exception e) {}
						if (rs != null) try { rs.close(); } catch(Exception e) {}
					}
					return result;
				}
				
				
}