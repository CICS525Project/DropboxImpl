package serviceServer;

import java.rmi.AlreadyBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import RMIInterface.ServiceServerInterface;

/* Block of arguments to be passed to java VM when running the program. Not used in this version of the code
 * -cp C:\Users\DBAdmin\Documents\RMIBookExample\src;C:\Users\DBAdmin\Documents\RMIBookExample\bin\ServerRemote.class
-Djava.rmi.server.codebase=file:/C:/Users/DBAdmin/Documents/RMIBookExample/bin/ServerRemote.class
-Djava.rmi.server.hostname=cics525group6S3.cloudapp.net
-Djava.security.policy=C:/Users/DBAdmin/Documents/RMIBookExample/server.policy
 */

public class StartServer {

	// Constant definitions
	public static final String HOST 		 = "cics525group6S3.cloudapp.net";
	public static final int PORT			 = 12345;
	public static final String DB			 = "cics525group6DB3";
		
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		try {
			Registry registry = LocateRegistry.createRegistry(PORT);
			System.setProperty("java.rmi.server.hostname", HOST);
			ServiceServerInterface server = new ServiceServer();
			ServiceServerInterface stub = (ServiceServerInterface) UnicastRemoteObject.exportObject(server, PORT);  
			 
            registry = LocateRegistry.getRegistry(PORT);
//            System.out.println(stub.toString());
            registry.bind("cloudboxRMI", stub);
            System.out.println("Server running...");
			
		} catch (java.io.IOException e) {
			System.err.println(e);
			// problem registering server
		} catch (AlreadyBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
