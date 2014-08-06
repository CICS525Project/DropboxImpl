package serviceServer;

import java.io.Serializable;

/**
 * ClassName: RoutingTable
 * Object Class to hold the values of the Routing Table and also the SharedUserName
 * @author Jitin
 *
 */
public class RoutingTable implements Serializable {
	
	/**
	 * Serialiazable ID
	 */
	private static final long serialVersionUID = 8279777402450098974L;
	private String userName;
	private String fileName;
	private String serverName;
	private int version;
	private String sharedUserName;
	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}
	/**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}
	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}
	/**
	 * @param fileName the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	/**
	 * @return the serverName
	 */
	public String getServerName() {
		return serverName;
	}
	/**
	 * @param serverName the serverName to set
	 */
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
	/**
	 * @return the version
	 */
	public int getVersion() {
		return version;
	}
	/**
	 * @param version the version to set
	 */
	public void setVersion(int version) {
		this.version = version;
	}
	/**
	 * @return the sharedUserName
	 */
	public String getSharedUserName() {
		return sharedUserName;
	}
	/**
	 * @param sharedUserName the sharedUserName to set
	 */
	public void setSharedUserName(String sharedUserName) {
		this.sharedUserName = sharedUserName;
	}
	
	
}