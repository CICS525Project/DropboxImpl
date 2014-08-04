package serviceServer;


import java.io.File;
import java.rmi.AlreadyBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;



import utils.Constants;
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


	public static void main(String[] args) {
		// TODO Auto-generated method stub

//		Constants localConstants = new Constants();
//		localConstants.setConstants();
		
		// cleaning temp dir before running the server again
		File file = new File("C:\\cloudboxTemp");        
        String[] myFiles;      
            if(file.isDirectory()){  
                myFiles = file.list();  
                for (int i=0; i<myFiles.length; i++) {  
                    File myFile = new File(file, myFiles[i]);   
                    myFile.delete();  
                }  
             }
		
		try {
			// Starting the RMI interface to communicate with Client machine
			Registry clientRegistry = LocateRegistry
					.createRegistry(Constants.CPORT);
			System.setProperty("java.rmi.server.hostname", Constants.HOST);
			ServiceServerInterface server = new ServiceServer();
			ServiceServerInterface stub = (ServiceServerInterface) UnicastRemoteObject
					.exportObject(server, Constants.CPORT);

			clientRegistry = LocateRegistry.getRegistry(Constants.CPORT);

			clientRegistry.bind("cloudboxRMI", stub);
			System.out.println("Client-Server RMI running...");

			// Starting the RMI interface to communicate with other service
			// Servers
			Registry serverRegistry = LocateRegistry.createRegistry(Constants.SPORT);
			// System.setProperty("java.rmi.server.hostname", HOST);
			ServerServerComInterface ssInt = new ServerServerCommunication();
			ServerServerComInterface serverStub = (ServerServerComInterface) UnicastRemoteObject
					.exportObject(ssInt, Constants.SPORT);
			serverRegistry = LocateRegistry.getRegistry(Constants.SPORT);
			serverRegistry.bind("serverServerRMI", serverStub);
			System.out.println("Server-Server RMI running...");

			ServiceServer updateInstance = new ServiceServer();
			System.out.println("Entering infinite refresh loop... ");
			
			// initially synchronize tables
			ServerServerCommunication mySSCom = new ServerServerCommunication();
			mySSCom.syncAllTables();
			
			// monitor the container permanently
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
