package dataTransfer;

import java.io.File;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.HashMap;

import javax.mail.Session;

import userMetaData.ClientMetaData;
import RMIInterface.ServiceServerInterface;

public class SyncWithRemote implements Runnable {

	private ServiceServerInterface serviceProvider;
	private FileOptHelper fopt;
	private UserOperate uopt;
	private ClientMetaData cmd;
	public SyncWithRemote() {
		fopt = new FileOptHelper();
		uopt = new UserOperate(SessionInfo.getInstance().getRemoteDNS(), SessionInfo.getInstance().getPortNum());
		cmd = new ClientMetaData();
		start();
	}

	private void pollInfo(){
		//get shared file from remote
		//get corresponding files in local
		//check if the version number is different
		try {
			Registry registry = LocateRegistry.getRegistry(SessionInfo
					.getInstance().getRemoteDNS(), SessionInfo.getInstance()
					.getPortNum());
			this.serviceProvider = (ServiceServerInterface) registry
					.lookup("cloudboxRMI");
			HashMap<String, Integer> filesAndVersionShared = serviceProvider
					.getAllSharedFilesForUser(SessionInfo.getInstance()
							.getUsername());
			ArrayList<String> filesLocal = fopt.getFileInFolder(SessionInfo.getInstance().getWorkFolder());
			for(String fn : filesLocal){
				//if the local file is a shared file
				if(filesAndVersionShared.containsKey(fn)){
					String localVersion = cmd.readVersionForOne(fn);
					int localVersionInt = Integer.parseInt(localVersion);
					int remoteVersion = filesAndVersionShared.get(fn);
					String filePath = SessionInfo.getInstance().getWorkFolder() + File.separator + fn;
					if(remoteVersion == -1){
						fopt.deleteFileInFoler(filePath);
						ArrayList<String> f = new ArrayList<String>();
						//according remote, deleting local and remove local meta data
						f.add(fn);
						cmd.removeRecord(filePath, f);
					}
					if(remoteVersion > localVersionInt){
						//download if remote version is greater than the local version
						OperationQueue.getInstance().add(fn, OperationQueue.getInstance().getDownloadQueue());
					}
					//upload will be operated on file creating
				}
			}
			
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(true){
			try {
				pollInfo();
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void start(){
		System.out.println("Polling thread start...");
		Thread t = new Thread(this);
		t.start();
	}
}
