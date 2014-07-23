package serviceServer;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
/*
 * -cp C:\Users\DBAdmin\Documents\RMIBookExample\src;C:\Users\DBAdmin\Documents\RMIBookExample\bin\ServerRemote.class
-Djava.rmi.server.codebase=file:/C:/Users/DBAdmin/Documents/RMIBookExample/bin/ServerRemote.class
-Djava.rmi.server.hostname=cics525group6S3.cloudapp.net
-Djava.security.policy=C:/Users/DBAdmin/Documents/RMIBookExample/server.policy
 */
public class ServiceServer implements
		ServiceServerInterface {


	public ServiceServer() throws RemoteException {
	}

	// implement the ServerRemote interface
	public Date getDate() throws RemoteException {
		return new Date();
	}

	public int execute(int i) throws RemoteException {
		return i*i*i*i;
	}

	public static void main(String args[]) throws RemoteException{
		int port = 12345;
		Registry registry = LocateRegistry.createRegistry(port);
		try {
			System.setProperty("java.rmi.server.hostname", "cics525group6S3.cloudapp.net");
			ServiceServerInterface server = new ServiceServer();
			ServiceServerInterface stub = (ServiceServerInterface) UnicastRemoteObject.exportObject(server, port);  
			 
            registry = LocateRegistry.getRegistry(port);
//            System.out.println(stub.toString());
            registry.bind("NiftyServer", stub);
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
