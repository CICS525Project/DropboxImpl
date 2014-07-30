package serviceServer;

import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import routingTable.ServiceContainer;
import RMIInterface.ServerServerComInterface;

public class ServerServerCommunication implements ServerServerComInterface {

	// hashmap with the addresses of every service server on the system
	HashMap<String, String> ss;

	ServerServerCommunication() {
		ss = new HashMap<String, String>();
		ss.put("SS1", "cics525group6S1.cloudapp.net");
		ss.put("SS2", "cics525group6S2.cloudapp.net");
		ss.put("SS3", "cics525group6S3.cloudapp.net");
		ss.put("SS4", "cics525group6s4.cloudapp.net");
		ss.put("BS1", "cics525group6.cloudapp.net");
		ss.put("BS2", "cics525group6b2.cloudapp.net");
	}

	// Method implementation to update information of other service servers
	// routing table
	public boolean updateTable(ArrayList<RoutingTable> missMatch)
			throws RemoteException {

		ServiceContainer serviceContainer = new ServiceContainer();
		try {
			serviceContainer.updateRTComplete(missMatch);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * This method is called from the ServerServerComunication instance to
	 * notify all the service servers of changes in the routing table
	 * 
	 * @return returns true when successful or false otherwise
	 */
	public void broadcastChanges(int port, ArrayList<RoutingTable> missMatch) {

		// RMI methods are called inside this method
		// Add code to establish communication and execute RMI with each service
		// server
		try {

			for (String key : ss.keySet()) {
				String address = ss.get(key);
				Registry registry = LocateRegistry.getRegistry(address, port);
				ServerServerComInterface server = (ServerServerComInterface) registry
						.lookup("serverServerRMI");
				server.updateTable(missMatch);
			}

		} catch (java.io.IOException e) {
			System.err.println(e);
			// I/O Error or bad URL
		} catch (NotBoundException e) {
			// NiftyServer isn't registered
			System.err.println(e);
		}

	}

}
