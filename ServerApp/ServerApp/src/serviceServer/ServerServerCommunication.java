package serviceServer;

import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import routingTable.DBConnection;
import routingTable.ServiceContainer;
import utils.Constants;
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
	public void broadcastChanges(ArrayList<RoutingTable> missMatch) {

		ArrayList<BroadcastThread> bt = new ArrayList<BroadcastThread>();
		for (String key : ss.keySet()) {
			String address = ss.get(key);
			try {
//				Registry registry = LocateRegistry.getRegistry(address, port);
//				//System.out.println("aftre registry");
//				ServerServerComInterface server = (ServerServerComInterface) registry
//						.lookup("serverServerRMI");
				// server.updateTable(missMatch);
				bt.add(new BroadcastThread(address,missMatch));
				
				// bt.broadcast.join();
			} catch (Exception e) {
				System.out.println("Error Broadcasting Changes...");
				System.out.println("Error connecting to server " + address);
				// System.err.println(e);
				// I/O Error or bad URL
			}
		}
		try {
			for (BroadcastThread t : bt) {
				if (t != null) {
					t.broadcast.join();
				}
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * sync local routing table values with values of other remote routing
	 * tables
	 */
	public void syncRT() {

		ArrayList<SyncThread> st = new ArrayList<SyncThread>();
		for (String key : ss.keySet()) {
			String address = ss.get(key);
			try {
			st.add(new SyncThread(address));

			} catch (Exception e) {
				System.out.println("Error synchronizing RTs...");
				System.out.println("Error connecting to server " + address);

			}

		}
		try {
			for (SyncThread t : st) {
				if (t != null) {
					t.sync.join();
				}
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public ArrayList<RoutingTable> getRoutingDetails() throws RemoteException {
		DBConnection connect = new DBConnection();
		try {
			return connect.getAllFromRoutingTable();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		// TODO Auto-generated method stub

	}

	public ArrayList<RoutingTable> getSharedDetails() throws RemoteException {
		DBConnection connect = new DBConnection();
		try {
			return connect.getAllFromSharingTable();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		// TODO Auto-generated method stub
	}
}
