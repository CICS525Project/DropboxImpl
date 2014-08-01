package dataTransfer;

import java.util.HashMap;

public class sessionInfo {
	
	private String username;
	private String userPwd;
	private HashMap<String, String> fileLocations;
	private String workFolder;
	private String remoteDNS;
	private int portNum;
	private static sessionInfo singleton = null;
	
	public static sessionInfo getInstance(){
		if(singleton == null){
			singleton = new sessionInfo();
		}
		return singleton;
	}
	
	
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getUserPwd() {
		return userPwd;
	}
	public void setUserPwd(String userPwd) {
		this.userPwd = userPwd;
	}
	public HashMap<String, String> getFileLocations() {
		return fileLocations;
	}
	public void setFileLocations(HashMap<String, String> fileLocations) {
		this.fileLocations = fileLocations;
	}
	public String getWorkFolder() {
		return workFolder;
	}
	public void setWorkFolder(String workFolder) {
		this.workFolder = workFolder;
	}
	public String getRemoteDNS() {
		return remoteDNS;
	}
	public void setRemoteDNS(String remoteDNS) {
		this.remoteDNS = remoteDNS;
	}
	public int getPortNum() {
		return portNum;
	}
	public void setPortNum(int portNum) {
		this.portNum = portNum;
	}
}
