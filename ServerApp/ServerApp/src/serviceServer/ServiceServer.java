package serviceServer;

import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.util.*;
import utils.Constants;
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
	private ServerClientCommunication mySCCom;
	private ServerBackupCommunication mySBCom;

	public boolean login(String username, String password)
			throws RemoteException {
		// TODO Auto-generated method stub
		Authentication auth = new Authentication();
		return auth.validUser(username, password);
	}

	public HashMap<String, String> getAddress(ArrayList<String> files,
			String user) throws RemoteException {
		// TODO Auto-generated method stub
		DBConnection connection = new DBConnection();
		HashMap<String, String> result = new HashMap<String, String>();
		try {
			result = connection.searchForServerName(files, user);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;

	}

	public boolean signIn(String username, String password)
			throws RemoteException {
		// TODO Auto-generated method stub
		Authentication auth = new Authentication();
		return auth.createUser(username, password);
	}

	@Override
	public HashMap<String, Integer> getCurrentFiles(String user)
			throws RemoteException {
		// TODO Auto-generated method stub
		DBConnection connection = new DBConnection();
		HashMap<String, Integer> result = new HashMap<String, Integer>();
		try {
			result = connection.searchForFiles(user);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public String getContainer() throws RemoteException {
		// TODO Auto-generated method stub
		return (Constants.STORAGECONNECTIONSTRING + "," + Constants.CONTAINER);
	}

	public ServiceServer() throws RemoteException {

		// create instances of communication classes
		mySSCom = new ServerServerCommunication();
		mySCCom = new ServerClientCommunication();
		mySBCom = new ServerBackupCommunication();
		

	}

	public String shareFile(HashMap<String,String> fileList,String userName) throws RemoteException {
		DBConnection connection = new DBConnection();
		String result=null;
		try {
			result = connection.insertRecordforShare(fileList, userName);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
		
	}
	
	/**
	 * This method is called from the ServiceServer instance to refresh the
	 * content of the routing table based on the contents of the container. Once
	 * the changes have been identified, it modifies the routing table and makes
	 * changes on other service servers RT and finally notify user(s) of the
	 * changes so the they can update their local copy of files
	 */
	public void refreshRT(int port) {
		// Call Jitin's method to obtain information from the container

		ServiceContainer serviceContainer = new ServiceContainer();
		ArrayList<RoutingTable> missMatch = new ArrayList<RoutingTable>();

		missMatch = serviceContainer.checkContainerWithRoutingTable(Constants.CONTAINER,
				Constants.HOST);

//		 temporary code to test update with the current routing table.
		
		try {
			if (!missMatch.isEmpty()) {
				System.out.println("Miss Match "+missMatch.toString());
				serviceContainer.updateRTComplete(missMatch);
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Based on the results obtained from the poll method, execute the
		// following methods:
		// myServerServerCommunication.broadcastChanges();
		// to notify other service servers
		// myServerClientCommunication.sendNotification(user, message);
		// to notify the user of recent changes.
		if (!missMatch.isEmpty()){
			// Update routing table
			mySSCom.broadcastChanges(port, missMatch);
			// backing up files
			mySBCom.downloadMissMatch(missMatch);
			mySBCom.uploadBackup(missMatch);
			mySBCom.cleanTemp(missMatch);
			
		}
		
		// sync local routing table values with values of other remote routing tables
		mySSCom.syncRT();
		// mySCCom.sendNotification("jitin", "upload,file1"); // repeat this
		// notification for every user related to file1

	}

}
