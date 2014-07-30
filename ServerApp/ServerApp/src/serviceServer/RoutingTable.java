package serviceServer;

import java.io.Serializable;

/**
 * @author Jitin
 *
 */
public class RoutingTable implements Serializable {
	
	private String userName;
	private String fileName;
	private String serverName;
	private int version;
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getServerName() {
		return serverName;
	}
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}
	
}