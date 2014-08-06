package serviceServer;

import java.rmi.*;
import java.sql.SQLException;
import java.util.*;

import com.microsoft.windowsazure.services.blob.client.CloudBlobClient;
import com.microsoft.windowsazure.services.blob.client.CloudBlobContainer;
import com.microsoft.windowsazure.services.blob.client.CloudBlockBlob;
import com.microsoft.windowsazure.services.core.storage.CloudStorageAccount;

import utils.Constants;
import utils.ServerConnection;
import routingTable.DBConnection;
import routingTable.ServiceContainer;
import authentication.Authentication;
import RMIInterface.ServiceServerInterface;

/**
 * container class for main service server functionality.
 * 
 * @author ignacio
 *
 */
public class ServiceServer implements ServiceServerInterface {

	
	private ServerServerCommunication mySSCom;
	private ServerBackupCommunication mySBCom;
/**
 * MethodName: login
 * Used to login the user and checks the validity of the credentials
 * @param username
 * @param password
 */
	public boolean login(String username, String password)
			throws RemoteException {
		Authentication auth = new Authentication();
		System.out.println("User "+ username + " attemping to log in");
		return auth.validUser(username, password);
	}
	/**
	 * MethodName: getAddress
	 * Used to get the Address of the Files for the User
	 * @param ArrayList<String> files
	 * @param user
	 * @return result
	 */
	public HashMap<String, String> getAddress(ArrayList<String> files,
			String user) throws RemoteException {
		DBConnection connection = new DBConnection();
		HashMap<String, String> result = new HashMap<String, String>();
		ServerConnection myTest = new ServerConnection();
		try {
			result = connection.searchForServerName(files, user);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		// code that checks the availability of servers specified for each file
		
		for(String key: result.keySet()){
            String address = result.get(key);
            if (!myTest.testConnection(address)) {
            	System.out.println("File: " + key + " - Server " + address + " is down");
            	
            	if (myTest.testConnection(Constants.B1HOST)) {
            		System.out.println("File "+ key +" available on backup server 1");
            		result.put(key,Constants.B1HOST);
             	}
            	else if (myTest.testConnection(Constants.B2HOST)) {
            		System.out.println("File "+ key +" available on backup server 2");
            		result.put(key,Constants.B2HOST);
            	}
            	else {
            		System.out.println("File " + key + " not available. All servers down.");
            	}
            	
            }
			
        }		
		// returns final value
		return result;
	}
	/**
	 * MethodName: signIn
	 * Used to create an account for the User
	 * @param username
	 * @param password
	 * @return boolean
	 */
	public boolean signIn(String username, String password)
			throws RemoteException {
		Authentication auth = new Authentication();
		auth.createUser(username, password);
		System.out.println("Account for user: "+ username + " created");
		mySSCom.pushUT(username, password);
		return true;
	}
	/**
	 * MethodName: getCurrentFiles
	 * Used to get the Current Files for the User
	 * @param user
	 * @return HashMap<String, Integer> result
	 */
	@Override
	public HashMap<String, Integer> getCurrentFiles(String user)
			throws RemoteException {
		DBConnection connection = new DBConnection();
		HashMap<String, Integer> result = new HashMap<String, Integer>();
		try {
			result = connection.searchForFiles(user);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	/**
	 * MethodName: getContainer
	 * Used to get the Container for the User
	 * @return String
	 */
	@Override
	public String getContainer() throws RemoteException {
		return (Constants.STORAGECONNECTIONSTRING + "," + Constants.CONTAINER);
	}
	/**
	 * MethodName: ServiceServer
	 * Constructor for the Class
	 * Initialize the objects
	 */
	public ServiceServer() throws RemoteException {

		// create instances of communication classes
		mySSCom = new ServerServerCommunication();
		mySBCom = new ServerBackupCommunication();
	}

	/**
	 * MethodName: shareFile
	 * Used to share the File with other Users
	 * @param fileList
	 * @param userName
	 * @return String
	 */
	public String shareFile(HashMap<String,String> fileList,String userName) throws RemoteException {
		DBConnection connection = new DBConnection();
		String result=null;
		try {
			result = connection.insertRecordforShare(fileList, userName);
			ArrayList<RoutingTable> shared = new ArrayList<RoutingTable>();
			for (String key : fileList.keySet()) {
				RoutingTable routingTable=new RoutingTable();
				routingTable.setUserName(userName);
				routingTable.setFileName(key);
				routingTable.setSharedUserName(fileList.get(key));
				shared.add(routingTable);
			}
			mySSCom.pushST(shared);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
		
	}
	
	/**
	 * Method Name:refreshRT 
	 * This method is called from the ServiceServer instance to refresh the
	 * content of the routing table based on the contents of the container. Once
	 * the changes have been identified, it modifies the routing table and makes
	 * changes on other service servers RT and finally notify user(s) of the
	 * changes so the they can update their local copy of files
	 * @param port
	 */
	public void refreshRT(int port) {
		// Call Jitin's method to obtain information from the container
		
		ServiceContainer serviceContainer = new ServiceContainer();
		ArrayList<RoutingTable> missMatch = new ArrayList<RoutingTable>();

		missMatch = serviceContainer.checkContainerWithRoutingTable(Constants.CONTAINER,
				Constants.HOST);

		
		try {
			if (!missMatch.isEmpty()) {
				
				for (RoutingTable r : missMatch) {
					System.out.println("************Missmatch element*********** " + r.getFileName() + "version " + r.getVersion() + "owner " + r.getUserName());
				}
//				System.out.println("Miss Match "+missMatch.get(0).getVersion());
				serviceContainer.updateRTComplete(missMatch);
				System.out.println("New files added/modified in container " + Constants.CONTAINER);
				mySSCom.pushRT(missMatch);
				mySBCom.downloadMissMatch(missMatch);
				mySBCom.uploadBackup(missMatch);
				mySBCom.cleanTemp(missMatch);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	/**
	 * MethodName: deleteFile
	 * Used to Delete the Files
	 * @param user
	 * @param file
	 * @return String
	 */
	@Override
	public void deleteFile(String user, String file) throws RemoteException {
		
		ServiceContainer serviceContainer = new ServiceContainer();
		try {
			// delete file from backup containers
			// Retrieve service storage account
			CloudStorageAccount storageAccount1 = CloudStorageAccount
					.parse(Constants.backup1StorageConnectionString);
			CloudStorageAccount storageAccount2 = CloudStorageAccount
					.parse(Constants.backup2StorageConnectionString);

			// Create the blob client.
			CloudBlobClient blobClient1 = storageAccount1
					.createCloudBlobClient();
			CloudBlobClient blobClient2 = storageAccount2
					.createCloudBlobClient();

			// Get a reference to a container.
			// The container name must be lower case
			CloudBlobContainer container1 = blobClient1
					.getContainerReference("backup1");
			CloudBlobContainer container2 = blobClient2
					.getContainerReference("backup2");

			// deleting blob1
			CloudBlockBlob toRemove = container1.getBlockBlobReference(file);
			toRemove.downloadAttributes();
			if (toRemove.getMetadata().get("name").equals(user)) {
				toRemove.deleteIfExists();
				System.out.println("File " + toRemove.getName()
						+ " deleted from " + container1.getName());
			}
			toRemove = null;
			// deleting blob2
			toRemove = container2.getBlockBlobReference(file);
			toRemove.downloadAttributes();
			if (toRemove.getMetadata().get("name").equals(user)) {
				toRemove.deleteIfExists();
				System.out.println("File " + toRemove.getName()
						+ " deleted from " + container2.getName());
			}
			serviceContainer.updateVersionForDelete(user,file);
			System.out.println("User " + user + " deleted file: " + file);
			System.out.println("Done updating RT with version to -1 in file " + file + " ,user " + user);
			// broadcasting -1 version
			
			ArrayList<RoutingTable> missMatch = new ArrayList<RoutingTable>();
			RoutingTable routingTable = new RoutingTable();
			
			routingTable.setFileName(file);
			routingTable.setUserName(user);
			routingTable.setVersion(-1);
			missMatch.add(routingTable);

			mySSCom.pushRT(missMatch);
			
		} catch (Exception e) {
			System.out.println("Error deleting entry. Failed to update version as -1");
		}
		
	}
	/**
	 * MethodName: getAllSharedFilesForUser
	 * Used to Share the Files
	 * @param userName
	 * @return HashMap<String, Integer>
	 */
	@Override
	public HashMap<String, Integer> getAllSharedFilesForUser(String userName)
			throws RemoteException {
		ServiceContainer serviceContainer = new ServiceContainer();
		try {
			return serviceContainer.getAllSharedFilesForUser(userName);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

}
