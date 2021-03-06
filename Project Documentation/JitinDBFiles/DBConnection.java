/**
 * 
 */
package com.cloudbox.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author Jitin
 * @version 1.0, 19/July/2014
 */
public class DBConnection {

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
		}
		finally {
			if (ps != null) try { ps.close(); } catch(Exception e) {}
			if (con != null) try { con.close(); } catch(Exception e) {}
		}
		return result;
	}

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
		}
		finally {
			if (ps != null) try { ps.close(); } catch(Exception e) {}
			if (con != null) try { con.close(); } catch(Exception e) {}
		}
		return result;

	}

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
		}
		finally {
			if (ps != null) try { ps.close(); } catch(Exception e) {}
			if (con != null) try { con.close(); } catch(Exception e) {}
		}
		return result;
	}

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
	public ArrayList<String> searchForFileinSharedTable(ArrayList<String> fileList,String userName){
		Connection con = null;
		PreparedStatement ps=null;
		ResultSet rs=null;
		ArrayList<String> result=new ArrayList<String>();
		try{
			con=ConnectionFactory.getConnection();
			for(String file: fileList){
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
		}
		finally {
			if (ps != null) try { ps.close(); } catch(Exception e) {}
			if (con != null) try { con.close(); } catch(Exception e) {}
		}
		return result;
	}
}
