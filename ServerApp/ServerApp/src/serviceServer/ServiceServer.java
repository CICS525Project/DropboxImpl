package serviceServer;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.util.*;

import routingTable.DBConnection;
import authentication.Authentication;
import RMIInterface.ServiceServerInterface;
/*
 * -cp C:\Users\DBAdmin\Documents\RMIBookExample\src;C:\Users\DBAdmin\Documents\RMIBookExample\bin\ServerRemote.class
-Djava.rmi.server.codebase=file:/C:/Users/DBAdmin/Documents/RMIBookExample/bin/ServerRemote.class
-Djava.rmi.server.hostname=cics525group6S3.cloudapp.net
-Djava.security.policy=C:/Users/DBAdmin/Documents/RMIBookExample/server.policy
 */
public class ServiceServer implements ServiceServerInterface {

	public static final String HOST 		 = "cics525group6S3.cloudapp.net";
	public static final int PORT			 = 12345;
	public static final String DB			 = "cics525group6DB3";
	
	
	public boolean login(String username, String password)
			throws RemoteException {
		// TODO Auto-generated method stub
		Authentication auth = new Authentication();
		return auth.validUser(username, password);
	}

	
	public HashMap<String, String> getAddress(ArrayList<String> files,
			String user) throws RemoteException {
		// TODO Auto-generated method stub
		DBConnection connection = new DBConnection();
		HashMap<String,String> result = new HashMap<String,String>();
		try {
			result = connection.searchForServerName(files,user);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;

//		HashMap<String,String> result = new HashMap<String,String>();
//		
//		for(String name : files) {
//			result.put(name, "cics525group6S3.cloudapp.net");
//		}
//		
//
//		return result;
	}
	
	
	public boolean signIn(String username, String password)
			throws RemoteException {
		// TODO Auto-generated method stub
		Authentication auth = new Authentication();
		return auth.createUser(username, password);
	}


	@Override
	public HashMap<String, String> getCurrentFiles() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

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
		
		Registry registry = LocateRegistry.createRegistry(PORT);
		try {
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
