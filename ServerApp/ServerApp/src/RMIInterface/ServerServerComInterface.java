package RMIInterface;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

import authentication.UserInfo;
import serviceServer.RoutingTable;
/**
 * Interface for Server to Server communication
 * @author Ignacio
 *
 */
public interface ServerServerComInterface extends Remote{

	/**
	 * This method is used to modify the routing table of other service server. Is called as RMI and invoked remotely on other servers
	 * @return returns true if successful or false in case of errors
	 * @throws RemoteException
	 */
	boolean updateTable(ArrayList<RoutingTable> missMatch) throws RemoteException;
	/**
	 * Method that modifies remotely the contents of user info table
	 * @param missMatch parameter containing the user information entries to be updated remotely
	 * @return returns true or false depending on the result of the operation
	 * @throws RemoteException
	 */
	boolean updateUserTable(String user, String pass) throws RemoteException;
	
	/**
	 * Used to updateShare Table when the User is sharing the files
	 * @param missMatch
	 * @return
	 * @throws RemoteException
	 */
	boolean updateShareTable(ArrayList<RoutingTable> missMatch) throws RemoteException;
	
	/**
	 * Method to obtain routing table values of the current machine
	 * @return arraylist of routing table values
	 * @throws RemoteException
	 */
	ArrayList<RoutingTable> getRoutingDetails() throws RemoteException;
	/**
	 * Method to obtain the Shared Details of the current machine
	 * @return ArrayList of Routing Tables
	 * @throws RemoteException
	 */
	ArrayList<RoutingTable> getSharedDetails() throws RemoteException;
	/**
	 * Method to obtain the User Details of the current machine
	 * @return ArrayList of Routing Tables
	 * @throws RemoteException
	 */
	ArrayList<UserInfo> getUserInfo()throws RemoteException;
	
}
