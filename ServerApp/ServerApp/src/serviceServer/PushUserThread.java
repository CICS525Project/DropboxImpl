package serviceServer;


import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import RMIInterface.ServerServerComInterface;
import utils.Constants;
import utils.ServerConnection;

public class PushUserThread implements Runnable {

	private String username;
	private String password;
	private ServerServerComInterface server;
	private Registry registry;
	private String address;
	Thread pushUser;
	
	public PushUserThread(String address, String user, String pass) {
		// TODO Auto-generated constructor stub
		this.username = user;
		this.password = pass;
		this.address = address;
		pushUser = new Thread(this);
		pushUser.start();
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub

		// test connection to given dns address
		ServerConnection myTestConnection = new ServerConnection();
		if (!myTestConnection.testConnection(address)) {
			System.out.println("Could not update UT. Server " + address
					+ " is offline");
			return;
		}
		
		try {
			registry = LocateRegistry.getRegistry(address, Constants.SPORT);
			server = (ServerServerComInterface) registry
					.lookup("serverServerRMI");

			System.out.println("Updating UT on server " + address);
			server.updateUserTable(username, password);
		} catch (Exception e) {
			System.out.println("Error Updating UT on server " + address);
		}
	}

}

