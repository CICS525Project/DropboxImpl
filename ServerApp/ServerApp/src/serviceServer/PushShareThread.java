package serviceServer;


import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;

import authentication.UserInfo;
import RMIInterface.ServerServerComInterface;
import routingTable.DBConnection;
import utils.Constants;
import utils.ServerConnection;

public class PushShareThread implements Runnable {

	ArrayList<RoutingTable> shareMissMatch;
	DBConnection connection;
	ServerServerComInterface server;
	Registry registry;
	String address;
	Thread sharedUser;
	
	public PushShareThread(String address, ArrayList<RoutingTable> shareMatch) {
		// TODO Auto-generated constructor stub
		this.shareMissMatch = shareMatch;
		this.connection = new DBConnection();
		this.address = address;
		sharedUser = new Thread(this);
		sharedUser.start();
		//System.out.println("new sync thread created");
	}
	
	@Override
	public void run() {
		// test connection to given dns address
		ServerConnection myTestConnection = new ServerConnection();
		if (!myTestConnection.testConnection(address)) {
			System.out.println("Could not update ST. Server " + address
					+ " is offline");
			return;
		}

		try {
			registry = LocateRegistry.getRegistry(address, Constants.SPORT);
			server = (ServerServerComInterface) registry
					.lookup("serverServerRMI");

			System.out.println("Updating ST on server " + address);
			server.updateShareTable(shareMissMatch);

//			System.out.println("Updating UT on server " + address);
//			server.updateUserTable(userMissMatch);

		} catch (Exception e) {
			System.out.println("Error Updating ST on server " + address);
			// System.err.println(e);
			// I/O Error or bad URL
		}
	}

}



