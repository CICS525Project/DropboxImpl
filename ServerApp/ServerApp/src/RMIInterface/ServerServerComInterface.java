package RMIInterface;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

import serviceServer.RoutingTable;
/**
 * Interface for Server to Server communication
 * @author cics525
 *
 */
public interface ServerServerComInterface extends Remote{

	/**
	 * This method is used to modify the routing table the service server. Is called as RMI from another service server
	 * @return returns true if successful or false in case of errors
	 * @throws RemoteException
	 */
	boolean updateTable(ArrayList<RoutingTable> missMatch) throws RemoteException;
	
	/**
	 * Method to obtain routing table values of the current machine
	 * @return arraylist of routing table values
	 * @throws RemoteException
	 */
	ArrayList<RoutingTable> getRoutingDetails() throws RemoteException;
	
	ArrayList<RoutingTable> getSharedDetails() throws RemoteException;
	
}
