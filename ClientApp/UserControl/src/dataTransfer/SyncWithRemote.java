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

	private void pollSharedFileInit(){
		System.out.println("Function polling shared called");
		try {
			Registry registry = LocateRegistry.getRegistry(SessionInfo
					.getInstance().getRemoteDNS(), SessionInfo.getInstance()
					.getPortNum());
			this.serviceProvider = (ServiceServerInterface) registry
					.lookup("cloudboxRMI");
			HashMap<String, Integer> remoteFileAccess = serviceProvider.getCurrentFiles(SessionInfo.getInstance().getUsername());
			ArrayList<String> remoteFile = (ArrayList<String>) remoteFileAccess.keySet();
			ArrayList<String> localFile = fopt.getFileInFolder(SessionInfo.getInstance().getWorkFolder());
			for(String file : remoteFile){
				//if local file does not have file in remote
				//means this file is just shared from others
				System.out.println("File: " + file + " is shared to me.");
				if(!localFile.contains(file)){
					OperationQueue.getInstance().add(file, OperationQueue.getInstance().getDownloadQueue());
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
	
	private void pollSharedFileModify(){
		//get shared file from remote
		//get corresponding files in local
		//check if the version number is different
		System.out.println("Function polling modify called");
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
					System.out.println("Detect in polling, shared file " + fn);
					String localVersion = cmd.readVersionForOne(fn);
					int localVersionInt = Integer.parseInt(localVersion);
					int remoteVersion = filesAndVersionShared.get(fn);
					String filePath = SessionInfo.getInstance().getWorkFolder() + File.separator + fn;
					if(remoteVersion == -1){
						System.out.println("Detect file deleted.");
						fopt.deleteFileInFoler(filePath);
						ArrayList<String> f = new ArrayList<String>();
						//according remote, deleting local and remove local meta data
						f.add(fn);
						cmd.removeRecord(filePath, f);
					}
					if(remoteVersion > localVersionInt){
						System.out.println("Detect file changed.");
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
				pollSharedFileInit();
				pollSharedFileModify();
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
