package RMIInterface;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.ArrayList;

import authentication.UserInfo;
import serviceServer.RoutingTable;
/**
 * Interface for Server to Server communication
 * @author cics525
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
	
	boolean updateShareTable(ArrayList<RoutingTable> missMatch) throws RemoteException;
	
	/**
	 * Method to obtain routing table values of the current machine
	 * @return arraylist of routing table values
	 * @throws RemoteException
	 */
	ArrayList<RoutingTable> getRoutingDetails() throws RemoteException;
	/**
	 * 
	 * @return
	 * @throws RemoteException
	 */
	ArrayList<RoutingTable> getSharedDetails() throws RemoteException;
	/**
	 * 
	 * @return
	 * @throws RemoteException
	 */
	ArrayList<UserInfo> getUserInfo()throws RemoteException;
	
}
