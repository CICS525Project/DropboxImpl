package serviceServer;


import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import RMIInterface.ServerServerComInterface;
import utils.Constants;
import utils.ServerConnection;
/**
 * ClassName: PushUserThread
 * Thread to push the changes to other servers for User Table changes
 * @author ignacio
 *
 */
public class PushUserThread implements Runnable {

	private String username;
	private String password;
	private ServerServerComInterface server;
	private Registry registry;
	private String address;
	Thread pushUser;
	/**
	 * Method Name: PushUserThread
	 * Constructor to  initialize the Address and the Files to be updated
	 * @param address
	 * @param shareMatch
	 */
	public PushUserThread(String address, String user, String pass) {
		// TODO Auto-generated constructor stub
		this.username = user;
		this.password = pass;
		this.address = address;
		pushUser = new Thread(this);
		pushUser.start();
	}
	/**
	 * Method Name: run() 
	 * Thread Implementation Function
	 */
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

