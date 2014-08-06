package serviceServer;


import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;

import RMIInterface.ServerServerComInterface;
import routingTable.DBConnection;
import utils.Constants;
import utils.ServerConnection;
/**
 * ClassName: PushThread
 * Thread to push the changes to other servers for Routing Table changes
 * @author ignacio
 *
 */
public class PushThread implements Runnable {

	ArrayList<RoutingTable> missMatch;
	//	ArrayList<UserInfo> userMissMatch;
	DBConnection connection;
	ServerServerComInterface server;
	Registry registry;
	String address;
	Thread push;
	/**
	 * Method Name: PushThread
	 * Constructor to  initialize the Address and the Files to be updated
	 * @param address
	 * @param shareMatch
	 */
	public PushThread(String address, ArrayList<RoutingTable> missMatch /*, ArrayList<UserInfo> userMissMatch*/) {
		// TODO Auto-generated constructor stub
		this.missMatch = missMatch;
		//		this.userMissMatch = userMissMatch;
		this.connection = new DBConnection();
		this.address = address;
		push = new Thread(this);
		push.start();
		//System.out.println("new sync thread created");
	}
	/**
	 * Method Name: run() 
	 * Thread Implementation Function
	 */
	@Override
	public void run() {

		// test connection to given dns address
		ServerConnection myTestConnection = new ServerConnection();
		if ( !myTestConnection.testConnection(address) ) {
			System.out.println("Could not update RT. Server " + address + " is offline");
			return;
		}

		try {
			registry = LocateRegistry.getRegistry(address, Constants.SPORT);
			server = (ServerServerComInterface) registry
					.lookup("serverServerRMI");

			System.out.println("Updating RT on server " + address);
			server.updateTable(missMatch);
		} catch (Exception e) {
			System.out.println("Error Updating tables on server " + address);
		}
	}

}

