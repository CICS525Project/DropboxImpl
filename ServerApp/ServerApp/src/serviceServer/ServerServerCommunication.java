package serviceServer;

import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import authentication.UserInfo;
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

		// remove itself
		String myself = null;
		for(String key : ss.keySet())
		{
			if (ss.get(key).equals(Constants.HOST)) {
				myself = key;
			}
		}
		ss.remove(myself);
	}

	// update RT 
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
	// update  ST
	@Override
	public boolean updateShareTable(ArrayList<RoutingTable> missMatch)
			throws RemoteException {
		ServiceContainer serviceContainer = new ServiceContainer();
		
		try {
			serviceContainer.updateSTComplete(missMatch);
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;

	}
	
	
	// update UT
	@Override
	public boolean updateUserTable(String user, String pass)
			throws RemoteException {
		ServiceContainer serviceContainer = new ServiceContainer();
		try {
			serviceContainer.updateUTComplete(user,pass);
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
	public void syncAllTables() {

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

	@Override
	public ArrayList<UserInfo> getUserInfo() throws RemoteException {
		DBConnection connect = new DBConnection();
		try {
			return connect.getUserInfo();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

	public void pushRT(ArrayList<RoutingTable> missMatch) {
		// TODO Auto-generated method stub
		ArrayList<PushThread> pt = new ArrayList<PushThread>();
		for (String key : ss.keySet()) {
			String address = ss.get(key);
			try {
			pt.add(new PushThread(address, missMatch));

			} catch (Exception e) {
				System.out.println("Error pushing RT to server " + address);

			}

		}
		try {
			for (PushThread t : pt) {
				if (t != null) {
					t.push.join();
				}
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void pushUT(String user, String pass ) {
		// TODO Auto-generated method stub
		ArrayList<PushUserThread> put = new ArrayList<PushUserThread>();
		for (String key : ss.keySet()) {
			String address = ss.get(key);
			try {
			put.add(new PushUserThread(address, user, pass));

			} catch (Exception e) {
				System.out.println("Error pushing UT to server " + address);

			}

		}
		try {
			for (PushUserThread t : put) {
				if (t != null) {
					t.pushUser.join();
				}
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void pushST(ArrayList<RoutingTable> sharedMatch ) {
		// TODO Auto-generated method stub
		ArrayList<PushShareThread> st = new ArrayList<PushShareThread>();
		for (String key : ss.keySet()) {
			String address = ss.get(key);
			try {
				/*for(RoutingTable r:sharedMatch){
					System.out.println("Sharing the file by PUSHING INSIDE PUSH ST  ----"+r.getFileName());
				}*/
			st.add(new PushShareThread(address, sharedMatch));

			} catch (Exception e) {
				System.out.println("Error pushing UT to server " + address);

			}

		}
		try {
			for (PushShareThread t : st) {
				if (t != null) {
					t.sharedUser.join();
				}
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	

}
