package serviceServer;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;

import utils.Constants;
import RMIInterface.ServerServerComInterface;

public class BroadcastThread implements Runnable {

	ArrayList<RoutingTable> missMatch;
	Thread broadcast;
	String address;
	
	public BroadcastThread(String address,ArrayList<RoutingTable> missMatch) {
		// TODO Auto-generated constructor stub
	this.address = address;
	this.missMatch = missMatch;
	broadcast = new Thread(this);
	System.out.println("broadcast thread created");
	broadcast.start();
	}
	
	
	@Override
	public void run() {

		
		// TODO Auto-generated method stub
		try {
			Registry registry = LocateRegistry.getRegistry(address, Constants.SPORT);
			ServerServerComInterface server = (ServerServerComInterface) registry.lookup("serverServerRMI");
			// server.updateTable(missMatch);
			server.updateTable(this.missMatch);
		} catch (Exception e) {
			System.out.println("Error modifying routing table in remote server " + address);
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}


	}

}
