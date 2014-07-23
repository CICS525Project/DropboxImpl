package RMIInterface;
import java.rmi.*;
import java.util.*;
/**
 * Interface to implement RMI between Client and Service Server
 * @author cics525
 *
 */
public interface ServiceServerInterface extends Remote {
	// Dummy testing methods
	Date getDate() throws RemoteException;
	int execute(int i) throws RemoteException;
	
	/**
	 * login method for local user authentication
	 * @param username username provided by user in the GUI
	 * @param password password password provided by user in GUI
	 * @return true or false depending on provided credentials
	 * @throws RemoteException
	 */
	boolean login(String username, String password) throws RemoteException;
	
	/** method to obtain server addresses for a given list of files
	 * 
	 * @param files array list with files in user's local cloudbox directory
	 * @param user username of current user
	 * @return hashmap with every filename and its corresponding service server address
	 */
	HashMap<String,String> getAddress(ArrayList<String> files, String user) throws RemoteException;
	
	/**
	 * Method that attempts to create a new account in the system
	 * @param username new user name
	 * @param password new password
	 * @return returns true if the new account is created successfully or false otherwise
	 * @throws RemoteException
	 */
	boolean signIn(String username, String password) throws RemoteException;
	
	
	HashMap<String,Integer> getCurrentFiles(String user)  throws RemoteException; 
	
}
