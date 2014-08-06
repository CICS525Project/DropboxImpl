
package utils;

/**
 * ClassName: ContainerUtility
 * To call the getContainerConnection
 * @author Jitin
 *
 */
public class ContainerUtility {
	
	public static final String storageConnectionString = Constants.STORAGECONNECTIONSTRING;
	public String getContainerConnection(){
		return storageConnectionString;
	}
}
