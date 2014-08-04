package serviceServer;


import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;

import authentication.UserInfo;
import RMIInterface.ServerServerComInterface;
import routingTable.DBConnection;

import utils.Constants;

public class PushUserThread implements Runnable {

	private String username;
	private String password;
	private DBConnection connection;
	private ServerServerComInterface server;
	private Registry registry;
	private String address;
	Thread pushUser;
	
	public PushUserThread(String address, String user, String pass) {
		// TODO Auto-generated constructor stub
		this.username = user;
		this.password = pass;
		this.connection = new DBConnection();
		this.address = address;
		pushUser = new Thread(this);
		pushUser.start();
		//System.out.println("new sync thread created");
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub

		try {
			registry = LocateRegistry.getRegistry(address, Constants.SPORT);
			server = (ServerServerComInterface) registry
					.lookup("serverServerRMI");

			System.out.println("Updating UT on server " + address);
			server.updateUserTable(username, password);

//			System.out.println("Updating UT on server " + address);
//			server.updateUserTable(userMissMatch);

		} catch (Exception e) {
			System.out.println("Error Updating tables on server " + address);
			// System.err.println(e);
			// I/O Error or bad URL
		}
	}

}

