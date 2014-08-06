package serviceServer;

import java.rmi.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

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

	/**
	 * MethodName: updateTable
	 * Used to call the updateRTComplete in the ServiceContainer
	 * @param missMatch
	 * @return boolean
	 */
	public boolean updateTable(ArrayList<RoutingTable> missMatch)
			throws RemoteException {

		ServiceContainer serviceContainer = new ServiceContainer();
		try {
			serviceContainer.updateRTComplete(missMatch);

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	/**
	 * MethodName: updateShareTable
	 * Used to call the updateSTComplete in the ServiceContainer
	 * @param missMatch
	 * @return boolean
	 */
	@Override
	public boolean updateShareTable(ArrayList<RoutingTable> missMatch)
			throws RemoteException {
		ServiceContainer serviceContainer = new ServiceContainer();

		try {
			serviceContainer.updateSTComplete(missMatch);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;

	}
	/**
	 * MethodName: updateUserTable
	 * Used to call the updateUTComplete in the ServiceContainer
	 * @param user
	 * @param pass
	 * @return boolean
	 */
	@Override
	public boolean updateUserTable(String user, String pass)
			throws RemoteException {
		ServiceContainer serviceContainer = new ServiceContainer();
		try {
			serviceContainer.updateUTComplete(user,pass);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * MethodName:syncAllTables 
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
			e.printStackTrace();
		}
	}

	@Override
	/**
	 * MethodName: getRoutingDetails
	 * Used to call the getAllFromRoutingTable from the DBConnection class
	 */
	public ArrayList<RoutingTable> getRoutingDetails() throws RemoteException {
		DBConnection connect = new DBConnection();
		try {
			return connect.getAllFromRoutingTable();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;

	}
	/**
	 * MethodName: getSharedDetails
	 * Used to call the getAllFromSharingTable from the DBConnection class
	 */
	public ArrayList<RoutingTable> getSharedDetails() throws RemoteException {
		DBConnection connect = new DBConnection();
		try {
			return connect.getAllFromSharingTable();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * MethodName: getUserInfo
	 * Used to call the getUserInfo from the DBConnection class
	 */
	@Override
	public ArrayList<UserInfo> getUserInfo() throws RemoteException {
		DBConnection connect = new DBConnection();
		try {
			return connect.getUserInfo();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;

	}
	/**
	 * MethodName: pushRT
	 * Used to call the PushThread Class
	 * @param missMatch
	 */
	public void pushRT(ArrayList<RoutingTable> missMatch) {
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
			e.printStackTrace();
		}
	}
	/**
	 * MethodName: pushUT
	 * Used to call the PushUserThread Class
	 * @param user
	 * @param pass
	 */
	public void pushUT(String user, String pass ) {
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
			e.printStackTrace();
		}
	}
	/**
	 * MethodName: pushST
	 * Used to call the PushShareThread Class
	 * @param sharedMatch
	 */
	public void pushST(ArrayList<RoutingTable> sharedMatch ) {
		ArrayList<PushShareThread> st = new ArrayList<PushShareThread>();
		for (String key : ss.keySet()) {
			String address = ss.get(key);
			try {
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
			e.printStackTrace();
		}
	}

}
