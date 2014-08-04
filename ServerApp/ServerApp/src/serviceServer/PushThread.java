package serviceServer;


import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;

import authentication.UserInfo;
import RMIInterface.ServerServerComInterface;
import routingTable.DBConnection;

import utils.Constants;

public class PushThread implements Runnable {

	ArrayList<RoutingTable> missMatch;
//	ArrayList<UserInfo> userMissMatch;
	DBConnection connection;
	ServerServerComInterface server;
	Registry registry;
	String address;
	Thread push;
	
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
	
	@Override
	public void run() {
		// TODO Auto-generated method stub

		try {
			registry = LocateRegistry.getRegistry(address, Constants.SPORT);
			server = (ServerServerComInterface) registry
					.lookup("serverServerRMI");

			System.out.println("Updating RT/ST on server " + address);
			server.updateTable(missMatch);

//			System.out.println("Updating UT on server " + address);
//			server.updateUserTable(userMissMatch);

		} catch (Exception e) {
			System.out.println("Error Updating tables on server " + address);
			// System.err.println(e);
			// I/O Error or bad URL
		}
	}

}

