/**
 * 
 */
package routingTable;


import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import com.microsoft.windowsazure.services.core.storage.*;
import com.microsoft.windowsazure.services.blob.client.*;

import serviceServer.RoutingTable;
import utils.ConnectionFactory;
import utils.ContainerUtility;

/**
 * ClassName: ServiceContainer
 * This class is used to provide connection to Container and Routing Table
 * @author Jitin
 * @version 1.0, 25/July/2014
 */
public class ServiceContainer {

	/**
	 * Method Name: uploadBlobIntoContainer
	 * This is to upload the Given File in the Path into the Given Container
	 * @param filePath
	 * @param containerName
	 * @return String
	 */
	public String uploadBlobIntoContainer(String filePath,String containerName){
		String result="failure";
		try
		{
			CloudStorageAccount storageAccount = CloudStorageAccount.parse(ContainerUtility.storageConnectionString);
			CloudBlobClient blobClient = storageAccount.createCloudBlobClient();
			CloudBlobContainer container = blobClient.getContainerReference(containerName);

			// Define the path to a local file.
			File f = new File(filePath);
			System.out.println(f.getName().toString());

			// Create or overwrite the "myimage.jpg" blob with contents from a local file.
			CloudBlockBlob blob = container.getBlockBlobReference(f.getName().toString());

			File source = new File(filePath);
			blob.upload(new FileInputStream(source), source.length());

			result="success";
		}
		catch (Exception e)
		{
			// Output the stack trace.
			e.printStackTrace();
		}

		return result;

	}
	/**
	 * Method Name: checkContainerWithRoutingTable
	 * This is used to Compare the Routing Table with a Given Container
	 * @param containerName
	 * @param serverName
	 */
	public ArrayList<RoutingTable> checkContainerWithRoutingTable(String containerName,String serverName){
		ArrayList<RoutingTable> missingInRoutingTable=new ArrayList<RoutingTable>();
		//ArrayList<RoutingTable> missingInContainer=new ArrayList<RoutingTable>();
		try
		{
			ArrayList<RoutingTable> completeListFromRoutingTable=new ArrayList<RoutingTable>();
			ArrayList<RoutingTable> containerList=new ArrayList<RoutingTable>();

			CloudStorageAccount storageAccount = CloudStorageAccount.parse(ContainerUtility.storageConnectionString);
			CloudBlobClient blobClient = storageAccount.createCloudBlobClient();
			CloudBlobContainer container = blobClient.getContainerReference(containerName);

			/*
			 * This loop is used to check with the List in the Routing Table
			 * and map them to Routing Table Objects and add to the completeListFromRoutingTable List
			 */
			DBConnection connection=new DBConnection();
			completeListFromRoutingTable=connection.getAllFromRoutingTable(serverName);
			/*
			 * This loop is used to check with the List in the Container
			 * and map them to Routing Table Objects and add to the Container List
			 */
			for (ListBlobItem blobItem : container.listBlobs()) {
				String userName=null;
				int version=0;
				String filename = container.getBlockBlobReference(blobItem.getUri().toString()).getName();
				CloudBlob blockBlob = container.getBlockBlobReference(filename);
				blockBlob.downloadAttributes();
				HashMap<String, String> metaValues=new HashMap<>();
				metaValues=blockBlob.getMetadata();
				RoutingTable routingTable=new RoutingTable();
				if(metaValues.get("name")!=null && metaValues.get("version") !=null){
					userName=metaValues.get("name");
					version=Integer.parseInt(metaValues.get("version"));
					routingTable.setUserName(userName);
					routingTable.setFileName(filename);
					routingTable.setServerName(serverName);
					routingTable.setVersion(version);
				}
				//System.out.println("File Details Service 3:"+filename+" "+userName+" "+version+" "+serverName);

				containerList.add(routingTable);
			}
			
			/*
			 * This is used to Compare the List in the Routing Table with the Container list
			 * The FileNames which are not present in the Container and which is
			 * present in the Routing Table are added to the missingInContainer List
			 */
			/*for(RoutingTable routingTable:completeListFromRoutingTable){
				boolean flag=false;
				for(RoutingTable routingTable2:containerList){
					if(routingTable2.getFileName().equalsIgnoreCase(routingTable.getFileName()) && routingTable2.getServerName().equalsIgnoreCase(routingTable.getServerName()) 
							&& routingTable2.getUserName().equalsIgnoreCase(routingTable.getUserName()) && routingTable2.getVersion()==routingTable.getVersion()){
						flag=true;
						break;
					}

				}
				if(!flag){
					missingInContainer.add(routingTable);
				}
			}*/
			/*
			 * This is used to Compare the Container list with the Routing Table List
			 * The FileNames which are not present in the Routing Table and which is
			 * present in the Container are added to the missingInRoutingTable List
			 */
			for(RoutingTable routingTable:containerList){
				boolean flag=false;
				for(RoutingTable routingTable2:completeListFromRoutingTable){
					if(routingTable2.getFileName().equalsIgnoreCase(routingTable.getFileName()) && routingTable2.getServerName().equalsIgnoreCase(routingTable.getServerName()) 
							&& routingTable2.getUserName().equalsIgnoreCase(routingTable.getUserName()) && routingTable2.getVersion()==routingTable.getVersion()){
						flag=true;
						break;
					}

				}
				if(!flag){
					missingInRoutingTable.add(routingTable);
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return missingInRoutingTable;

	}
	/**
	 * Method Name: insertMissingInRoutingTable
	 * This is used to insert the Missing Values in the Routing Table
	 * @param missingList
	 * @throws SQLException
	 */
	public void insertMissingInRoutingTable(ArrayList<RoutingTable> missingList) throws SQLException{
		Connection con = null;
		PreparedStatement ps=null;
		try{
			con=ConnectionFactory.getConnection();
			String query="INSERT INTO [routingTable] VALUES (?,?,?,?)";
			ps=con.prepareStatement(query);
			for(RoutingTable i:missingList){
				if(!(i.getUserName()==null || i.getFileName()==null || i.getServerName()==null ||i.getVersion()==0)){
				ps.setString(1, i.getUserName());
				ps.setString(2, i.getFileName());
				ps.setString(3, i.getServerName());
				ps.setInt(4, i.getVersion());
				ps.addBatch();
				}
			}
			ps.executeBatch();
		}
		catch(Exception e){
			e.printStackTrace();
			throw new SQLException();
		}
		finally {
			if (ps != null) try { ps.close(); } catch(Exception e) {}
			if (con != null) try { con.close(); } catch(Exception e) {}
		}
	}
	
	/**
	 * Method Name: updateRoutingTableVersion
	 * This is used to update the Version Number in the Routing Table
	 * @param missingList
	 * @throws SQLException
	 */
	public void updateRoutingTableVersion(RoutingTable missingList) throws SQLException{
		Connection con = null;
		PreparedStatement ps=null;
		try{
			con=ConnectionFactory.getConnection();
			String query="UPDATE [routingTable] SET version=? WHERE fileName=? AND userName=? AND serverName=?";
			ps=con.prepareStatement(query);
			ps.setInt(1, missingList.getVersion());
			ps.setString(2, missingList.getFileName());
			ps.setString(3, missingList.getUserName());
			ps.setString(4, missingList.getServerName());
			ps.executeUpdate();
		}
		catch(Exception e){
			throw new SQLException();
		}
		finally {
			if (ps != null) try { ps.close(); } catch(Exception e) {}
			if (con != null) try { con.close(); } catch(Exception e) {}
		}
	}
	/**
	 * Method Name: updateRTComplete
	 * Used to Update the Routing Table completely with both Insertion of new Records and Updation 
	 * of Existing Records
	 * @param missingList
	 * @throws SQLException
	 */
	public void updateRTComplete(ArrayList<RoutingTable> missingList) throws SQLException{
		Connection con = null;
		PreparedStatement ps=null;
		ResultSet rs=null;
		ArrayList<RoutingTable> missingInRoutingTable=new ArrayList<RoutingTable>();
		try{
			for(RoutingTable i:missingList){
				con=ConnectionFactory.getConnection();
				String query="SELECT userName,fileName,serverName,version FROM [routingTable] WHERE fileName=? AND userName=? AND serverName=?";
				ps=con.prepareStatement(query);
				ps.setString(1, i.getFileName());
				ps.setString(2, i.getUserName());
				ps.setString(3, i.getServerName());
				rs=ps.executeQuery();
				if(rs.next()){
					updateRoutingTableVersion(i);
				}
				else{
					missingInRoutingTable.add(i);
				}
			}
			if(!missingInRoutingTable.isEmpty()){
			insertMissingInRoutingTable(missingInRoutingTable);
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
	}
	/**
	 * Method Name: compareRT
	 * Used to compare the current Server RoutingTable with the other RoutingTable (Given)
	 * It fetches the details of new data in the other Table
	 * @param firstList
	 * @param secondList
	 * @return
	 */
	public ArrayList<RoutingTable> compareRT(ArrayList<RoutingTable> firstList,ArrayList<RoutingTable> secondList){
		ArrayList<RoutingTable> result=new ArrayList<RoutingTable>();
		for(RoutingTable routingTable:secondList){
			boolean flag=false;
			for(RoutingTable routingTable2:firstList){
				if(routingTable2.getFileName().equalsIgnoreCase(routingTable.getFileName()) && routingTable2.getServerName().equalsIgnoreCase(routingTable.getServerName()) 
						&& routingTable2.getUserName().equalsIgnoreCase(routingTable.getUserName()) ){
					if(routingTable2.getVersion()==routingTable.getVersion() || (routingTable2.getVersion()==-1 && routingTable.getVersion()>0) ||(routingTable.getVersion()!=-1 && routingTable2.getVersion()>routingTable.getVersion())){
					flag=true;
					break;
					}
				}

			}
			if(!flag){
				result.add(routingTable);
			}
		}
		return result;
	}
	/**
	 * Method Name: compareST
	 * Used to compare the current Server SharedTable with the other SharedTable (Given)
	 * It fetches the details of new data in the other Table
	 * @param firstList
	 * @param secondList
	 * @return ArrayList<RoutingTable> result
	 */
	public ArrayList<RoutingTable> compareST(ArrayList<RoutingTable> firstList,ArrayList<RoutingTable> secondList){
		ArrayList<RoutingTable> result=new ArrayList<RoutingTable>();
		for(RoutingTable routingTable:secondList){
			boolean flag=false;
			for(RoutingTable routingTable2:firstList){
				if(routingTable2.getFileName().equalsIgnoreCase(routingTable.getFileName()) && routingTable2.getSharedUserName().equalsIgnoreCase(routingTable.getSharedUserName()) 
						&& routingTable2.getUserName().equalsIgnoreCase(routingTable.getUserName()) ){
					flag=true;
					break;
				}

			}
			if(!flag){
				result.add(routingTable);
			}
		}
		return result;
	}
	/**
	 * Method Name: insertMissingInSharedTable
	 * This is used to insert the Missing Values in the Shared Table
	 * @param missingList
	 * @throws SQLException
	 */
	public void insertMissingInSharedTable(ArrayList<RoutingTable> missingList) throws SQLException{
		Connection con = null;
		PreparedStatement ps=null;
		try{
			con=ConnectionFactory.getConnection();
			String query="INSERT INTO [sharedTable] VALUES (?,?,?)";
			ps=con.prepareStatement(query);
			for(RoutingTable i:missingList){
				if(!(i.getFileName()==null || i.getSharedUserName()==null || i.getUserName()==null)){
					ps.setString(1, i.getUserName());
					ps.setString(2, i.getFileName());
					ps.setString(3, i.getSharedUserName());
					ps.addBatch();
				}
			}
			ps.executeBatch();
		}
		catch(Exception e){
			e.printStackTrace();
			throw new SQLException();
		}
		finally {
			if (ps != null) try { ps.close(); } catch(Exception e) {}
			if (con != null) try { con.close(); } catch(Exception e) {}
		}
	}
	/**
	 * Method Name: updateVersionForDelete
	 * Method is used to when a delete operation happens. It actually makes the version 
	 * of the fileName to -1
	 * @param userName
	 * @param fileName
	 * @throws SQLException
	 */
		public void updateVersionForDelete(String userName,String fileName) throws SQLException {
			Connection con = null;
			Connection con1 = null;
			Connection con2 = null;
			Connection con3 = null;
			PreparedStatement ps=null;
			ResultSet rs=null;
			PreparedStatement ps1=null;
			ResultSet rs1=null;
			PreparedStatement ps2=null;
			ResultSet rs2=null;
			PreparedStatement ps3=null;
			ResultSet rs3=null;
			try{
				con=ConnectionFactory.getConnection();
				String query="SELECT version FROM [routingTable] WHERE userName =? AND fileName=? ";
				ps=con.prepareStatement(query);
				ps.setString(1, userName);
				ps.setString(2, fileName);
				rs=ps.executeQuery();
				if(rs.next()){
					con1=ConnectionFactory.getConnection();
					String query1="UPDATE [routingTable] SET version= ? WHERE userName =? AND fileName=?";
					ps1=con1.prepareStatement(query1);
					ps1.setInt(1, -1);
					ps1.setString(2, userName);
					ps1.setString(3, fileName);
					ps1.executeUpdate();
				}
				else{
					con2=ConnectionFactory.getConnection();
					String query2="SELECT userName FROM [sharedTable] WHERE sharedUserName =? AND fileName=? ";
					ps2=con2.prepareStatement(query2);
					ps2.setString(1, userName);
					ps2.setString(2, fileName);
					rs2=ps2.executeQuery();
					if(rs2.next()){
						con3=ConnectionFactory.getConnection();
						String query3="UPDATE [routingTable] SET version= ? WHERE userName =? AND fileName=?";
						ps3=con3.prepareStatement(query3);
						ps3.setInt(1, -1);
						ps3.setString(2, rs2.getString("userName"));
						ps3.setString(3, fileName);
						ps3.executeUpdate();
					}
				}
				
			}
			catch(Exception e){
				throw new SQLException();
			}
			finally {

				if (rs != null) try { rs.close(); } catch(Exception e) {}
				if (ps != null) try { ps.close(); } catch(Exception e) {}
				if (con != null) try { con.close(); } catch(Exception e) {}
				if (rs1 != null) try { rs1.close(); } catch(Exception e) {}
				if (ps1 != null) try { ps1.close(); } catch(Exception e) {}
				if (con1 != null) try { con1.close(); } catch(Exception e) {}
				if (rs2 != null) try { rs2.close(); } catch(Exception e) {}
				if (ps2 != null) try { ps2.close(); } catch(Exception e) {}
				if (con2 != null) try { con2.close(); } catch(Exception e) {}
				if (rs3 != null) try { rs3.close(); } catch(Exception e) {}
				if (ps3 != null) try { ps3.close(); } catch(Exception e) {}
				if (con3 != null) try { con3.close(); } catch(Exception e) {}
			}
		}
		/**
		 * Method Name: getAllSharedFilesForUser
		 * Used to retrieve the List of Files associated with a particular UserName
		 * Includes both the Owned Files and Shared by files
		 * @param userName
		 * @return result
		 * @throws SQLException
		 */
		public ArrayList<String> getAllSharedFilesForUser(String userName)throws SQLException{
			Connection con = null;
			PreparedStatement ps=null;
			ResultSet rs=null;
			ArrayList<String> result=new ArrayList<String>();
			try{
				con=ConnectionFactory.getConnection();
				String query="SELECT fileName FROM [sharedTable] WHERE sharedUserName=? OR userName=?";
				ps=con.prepareStatement(query);
				ps.setString(1, userName);
				ps.setString(2, userName);
				rs=ps.executeQuery();
				while(rs.next()){
					result.add(rs.getString("fileName"));
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

