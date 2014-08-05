package dataTransfer;

import java.io.File;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.HashMap;
import userUtil.ClientMetaData;
import RMIInterface.ServiceServerInterface;

public class SyncWithRemote implements Runnable {

	private ServiceServerInterface serviceProvider;
	private FileOptHelper fopt;
	private UserOperate uopt;
	private ClientMetaData cmd;
	public volatile static Thread syncer;

	public SyncWithRemote() {
		fopt = new FileOptHelper();
		uopt = new UserOperate(SessionInfo.getInstance().getRemoteDNS(),
				SessionInfo.getInstance().getPortNum());
		cmd = new ClientMetaData();
		syncer = new Thread(this);
		start();
	}

	private void pollSharedFileInit() {
		// System.out.println("Function polling shared called");
		try {
			Registry registry = LocateRegistry.getRegistry(SessionInfo
					.getInstance().getRemoteDNS(), SessionInfo.getInstance()
					.getPortNum());
			this.serviceProvider = (ServiceServerInterface) registry
					.lookup("cloudboxRMI");
			HashMap<String, Integer> remoteFileAccess = serviceProvider
					.getCurrentFiles(SessionInfo.getInstance().getUsername());
			ArrayList<String> localFile = fopt.getFileInFolder(SessionInfo
					.getInstance().getWorkFolder());
			for (String key : remoteFileAccess.keySet()) {
				if (!localFile.contains(key)) {
					if (remoteFileAccess.get(key) != -1) {
						System.out.println("File: " + key
								+ " is accessable to me.");
						if (!OperationQueue.getInstance().getDownloadQueue()
								.contains(key)) {
							fopt.downLoadFileControl(key);
						}
					}
				}
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			SessionInfo.getInstance().getRemoteDNS();
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void pollSharedFileModify() {
		// get shared file from remote
		// get corresponding files in local
		// check if the version number is different
		// System.out.println("Function polling modify called");
		try {
			Registry registry = LocateRegistry.getRegistry(SessionInfo
					.getInstance().getRemoteDNS(), SessionInfo.getInstance()
					.getPortNum());
			this.serviceProvider = (ServiceServerInterface) registry
					.lookup("cloudboxRMI");
			HashMap<String, Integer> filesAndVersionShared = serviceProvider
					.getAllSharedFilesForUser(SessionInfo.getInstance()
							.getUsername());
			ArrayList<String> filesLocal = fopt.getFileInFolder(SessionInfo
					.getInstance().getWorkFolder());
			System.out.println("Size of shared Version hashmap is " + filesAndVersionShared.size());
			for (String fn : filesLocal) {
				
				// if the local file is a shared file
				System.out.println("File " + fn + " have remote version " + filesAndVersionShared.get(fn));
				if (filesAndVersionShared.containsKey(fn)) {
					System.out.println("Detect in polling, shared file " + fn);
					String localVersion = cmd.readVersionForOne(fn);
					int localVersionInt = Integer.parseInt(localVersion);
					int remoteVersion = filesAndVersionShared.get(fn);
					String filePath = SessionInfo.getInstance().getWorkFolder()
							+ File.separator + fn;
					if (remoteVersion == -1) {
						System.out.println("Detect file deleted.");
						if (OperationQueue.getInstance().getDownloadQueue()
								.contains(fn)) {
							OperationQueue.getInstance().getDownloadQueue()
									.remove(fn);
						}
						if (OperationQueue.getInstance().getUploadQueue()
								.contains(fn)) {
							OperationQueue.getInstance().getUploadQueue()
									.remove(fn);
						}
						/**
						 * check if local has file, if yes , delete, if not
						 * nothing
						 **/
						fopt.deleteFileInFoler(filePath);
						// according remote, deleting local and remove local
						// meta data
						cmd.removeOneRecord(SessionInfo.getInstance()
								.getWorkFolder(), fn);
					}
					if (remoteVersion > localVersionInt) {
						System.out
								.println("Detect file with higher version in remote --- download it.");
						// download if remote version is greater than the local
						// version
						if (!OperationQueue.getInstance().getDownloadQueue()
								.contains(fn)) {
							fopt.downLoadFileControl(fn);
						}
					}
					// upload will be operated on file creating
				}
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			SessionInfo.getInstance().getRemoteDNS();
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

		Thread thisThread = Thread.currentThread();
		while (syncer == thisThread) {
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

	public void start() {
		System.out.println("Polling thread start...");
		syncer.start();
	}

	public static void stop() {
		System.out.println("Stop syncer...");
		syncer = null;
	}
}
