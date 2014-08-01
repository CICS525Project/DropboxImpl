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
				if(metaValues.get("name")!=null && metaValues.get("version") !=null){
					userName=metaValues.get("name");
					version=Integer.parseInt(metaValues.get("version"));
				}
				//System.out.println("File Details :"+filename+" "+userName+" "+version+" "+serverName);
				RoutingTable routingTable=new RoutingTable();
				routingTable.setUserName(userName);
				routingTable.setFileName(filename);
				routingTable.setServerName(serverName);
				routingTable.setVersion(version);
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
				ps.setString(1, i.getUserName());
				ps.setString(2, i.getFileName());
				ps.setString(3, i.getServerName());
				ps.setInt(4, i.getVersion());
				ps.addBatch();
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
}

