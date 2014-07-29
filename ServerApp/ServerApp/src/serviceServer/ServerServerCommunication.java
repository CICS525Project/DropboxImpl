package serviceServer;

import java.rmi.RemoteException;

import RMIInterface.ServerServerComInterface;

public class ServerServerCommunication implements ServerServerComInterface {

	// Method implementation to update information of other service servers
	// routing table
	public boolean updateTable() throws RemoteException {
		// This method should use the SQL methods defined by Jitin to update
		// routing table information of this server's routing table
		// The method will be called from other Service Servers as RMI
		// code to be added here...
		// code to be added here...
		return false;
	}

	/**
	 * This method is called from the ServerServerComunication instance to
	 * notify all the service servers of changes in the routing table
	 * 
	 * @return returns true when successful or false otherwise
	 */
	public boolean broadcastChanges() {
		
		// RMI methods are called inside this method
		// Add code to establish communication and execute RMI with each service server
		
		// SS1.updateTable();
		// SS2.updateTable();
		// SS3.updateTable();
		// SS4.updateTable();
		
		
		return false;

	}

}
