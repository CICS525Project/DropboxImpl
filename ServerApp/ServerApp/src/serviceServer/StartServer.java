package serviceServer;

import java.rmi.AlreadyBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import RMIInterface.ServerServerComInterface;
import RMIInterface.ServiceServerInterface;

/* Block of arguments to be passed to java VM when running the program. Not used in this version of the code
 * -cp C:\Users\DBAdmin\Documents\RMIBookExample\src;C:\Users\DBAdmin\Documents\RMIBookExample\bin\ServerRemote.class
-Djava.rmi.server.codebase=file:/C:/Users/DBAdmin/Documents/RMIBookExample/bin/ServerRemote.class
-Djava.rmi.server.hostname=cics525group6S3.cloudapp.net
-Djava.security.policy=C:/Users/DBAdmin/Documents/RMIBookExample/server.policy
 */

/**
 * Runner method to start the service server
 * @author ignacio
 *
 */
public class StartServer {

	// Constant definitions
	public static final String HOST 		 = "cics525group6S3.cloudapp.net";
	public static final int CPORT			 = 12345; 	// port for RMI with client
	public static final int SPORT			 = 9999;	// port for RMI with other service servers
	public static final String DB			 = "cics525group6DB3";
		
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		try {
			// Starting the RMI interface to communicate with Client machine
			Registry clientRegistry = LocateRegistry.createRegistry(CPORT);
			System.setProperty("java.rmi.server.hostname", HOST);
			ServiceServerInterface server = new ServiceServer();
			ServiceServerInterface stub = (ServiceServerInterface) UnicastRemoteObject.exportObject(server, CPORT);  
			 
			clientRegistry = LocateRegistry.getRegistry(CPORT);

			clientRegistry.bind("cloudboxRMI", stub);
            System.out.println("Client-Server RMI running...");
			
            // Starting the RMI interface to communicate with other service Servers
            Registry serverRegistry = LocateRegistry.createRegistry(SPORT);
//          System.setProperty("java.rmi.server.hostname", HOST);
            ServerServerComInterface ssInt = new ServerServerCommunication();
            ServerServerComInterface serverStub = (ServerServerComInterface) UnicastRemoteObject.exportObject(ssInt, SPORT);
            serverRegistry = LocateRegistry.getRegistry(SPORT);
            serverRegistry.bind("serverServerRMI", serverStub);
            System.out.println("Server-Server RMI running...");
            
            
            ServiceServer updateInstance = new ServiceServer();
            System.out.println("Entering infinite refresh loop... ");
            while (true) {
                updateInstance.refreshRT(9999);
            }

            
		} catch (java.io.IOException e) {
			System.err.println(e);
			// problem registering server
		} catch (AlreadyBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
