package RMIInterface;
import java.rmi.*;
import java.sql.SQLException;
import java.util.*;
/**
 * Interface to implement RMI between Client and Service Server
 * @author cics525
 *
 */
public interface ServiceServerInterface extends Remote {
	// Dummy testing methods
//	Date getDate() throws RemoteException;
//	int execute(int i) throws RemoteException;
//	
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
	
	/**
	 * Method that returns all the files associated with a given user
	 * @param user user (owner or shared beneficiary) for whom to look files 
	 * @return Returns a hashmap with the file name and the corresponding version number
	 * @throws RemoteException
	 */
	HashMap<String,Integer> getCurrentFiles(String user)  throws RemoteException; 
	
	/**
	 * Method that allows a remote client to obtain the address of the container associated with the current server
	 * @return Returns a string with the values of the storageConnectionString and container name, separated by comma (,)
	 * @throws RemoteException
	 */
	String getContainer() throws RemoteException;
	
	/**
	 * 
	 * @param fileList list containing the filename and the shared username
	 * @param userName owner of the file
	 * @return return a message indicating success or failure
	 * @throws RemoteException
	 */
	String shareFile(HashMap<String,String> fileList,String userName) throws RemoteException;
	
	/**
	 * method that removes a file from the system by changing the version of the file to -1
	 * @param user user name associated with the file
	 * @param file name of the file to be deleted
	 * @throws RemoteException
	 */
	void deleteFile(String user, String file) throws RemoteException;
	
	/**
	 * method to obtain shared filed of current user
	 * @param userName caller login username
	 * @return arraylist of files shared by that user
	 * @throws RemoteException
	 */
	 HashMap<String, Integer> getAllSharedFilesForUser(String userName)throws RemoteException;
}
