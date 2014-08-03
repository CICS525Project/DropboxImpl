package serviceServer;


import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;

import authentication.UserInfo;
import RMIInterface.ServerServerComInterface;
import routingTable.DBConnection;
import routingTable.ServiceContainer;
import utils.Constants;

public class SyncThread implements Runnable {

	ArrayList<RoutingTable> selfRT;
	ArrayList<RoutingTable> selfST;
	ArrayList<UserInfo> selfUT;
	DBConnection connection;
	ServerServerComInterface server;
	Registry registry;
	String address;
	Thread sync ;
	
	public SyncThread(String address) {
		// TODO Auto-generated constructor stub
		this.selfRT = new ArrayList<RoutingTable>();
		this.selfST = new ArrayList<RoutingTable>();
		this.selfUT = new ArrayList<UserInfo>();
		this.connection = new DBConnection();
		this.address = address;
		sync = new Thread(this);
		sync.start();
		//System.out.println("new sync thread created");
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		ArrayList<RoutingTable> result = new ArrayList<RoutingTable>();
		ArrayList<RoutingTable> sharedresult = new ArrayList<RoutingTable>();
		ArrayList<RoutingTable> missMatch = new ArrayList<RoutingTable>();
		ArrayList<RoutingTable> sharedmissMatch = new ArrayList<RoutingTable>();
		ArrayList<UserInfo> userResult = new ArrayList<UserInfo>();
		ArrayList<UserInfo> userMissMatch = new ArrayList<UserInfo>();
		try {
			registry = LocateRegistry.getRegistry(address, Constants.SPORT);
			server = (ServerServerComInterface) registry.lookup("serverServerRMI");
			this.selfRT = connection.getAllFromRoutingTable();
			this.selfST = connection.getAllFromSharingTable();
			this.selfUT = connection.getUserInfo();
			
			result = server.getRoutingDetails();
			sharedresult= server.getSharedDetails();
			userResult = server.getUserInfo();
			
			ServiceContainer container=new ServiceContainer();
			
			missMatch = container.compareRT(this.selfRT, result);
			sharedmissMatch = container.compareST(this.selfST, sharedresult);
			userMissMatch = container.compareUserInfo(this.selfUT, userResult);
			
			System.out.println("Comparing routing table with data from server " + address);
			if(!missMatch.isEmpty()){
				//container.insertMissingInRoutingTable(missMatch);
				container.updateRTComplete(missMatch);
				System.out.println("RT updated with data from " + address);
			}
			if(!sharedmissMatch.isEmpty()){
				container.insertMissingInSharedTable(sharedmissMatch);
				System.out.println("ST updated with data from " + address);
			}
			if(!userMissMatch.isEmpty()){
				container.insertMissingInUserTable(userMissMatch);
				System.out.println("UT updated with data from " + address);
			}
			
			
		}catch (Exception e) {
			System.out.println("Error synchronizing RT/STs...");
			System.out.println("Error connecting to server " + address);
			// System.err.println(e);
			// I/O Error or bad URL
		}
	}

}
