package dataTransfer;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JOptionPane;

import userMetaData.ServerConnectionTest;
import RMIInterface.ServiceServerInterface;

public class SessionInfo {
	
	private String username;
	private String userPwd;
	private HashMap<String, String> fileLocations;
	private String workFolder;
	private String remoteDNS;
	private int portNum;
	private static SessionInfo singleton = null;
	
	public static SessionInfo getInstance(){
		if(singleton == null){
			singleton = new SessionInfo();
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
		ServerConnectionTest scTest = new ServerConnectionTest();
		if(!scTest.testConnection(remoteDNS)){
			String originDNS = remoteDNS;
			remoteDNS = scTest.testDNSCtrl();
			JOptionPane.showMessageDialog(null,
					"Server" +originDNS +" is down!! Now change to " + remoteDNS + " for service!!");
		}
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
