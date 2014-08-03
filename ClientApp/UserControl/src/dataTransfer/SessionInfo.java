package dataTransfer;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.HashMap;

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
		/**testing if current linked server is alive if not, change to another server**/
		ArrayList<String> rDNS = new ArrayList<String>();
		rDNS.add(ConfigurationData.SERVICE_S1);
		rDNS.add(ConfigurationData.SERVICE_S2);
		rDNS.add(ConfigurationData.SERVICE_S3);
		rDNS.add(ConfigurationData.SERVICE_S4);
		rDNS.add(ConfigurationData.SERVICE_B1);
		rDNS.add(ConfigurationData.SERVICE_B2);
		rDNS.remove(remoteDNS);
		try {
			Registry registry = LocateRegistry.getRegistry(remoteDNS, portNum);
			ServiceServerInterface ssi = (ServiceServerInterface)registry.lookup("cloudboxRMI");
			if(ssi == null){
				for(String backUpDNS : rDNS){
					registry = LocateRegistry.getRegistry(backUpDNS, portNum);
					ssi = (ServiceServerInterface)registry.lookup("cloudboxRMI");
					if(ssi != null){
						remoteDNS = backUpDNS;
						return remoteDNS;
					}
				}
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
