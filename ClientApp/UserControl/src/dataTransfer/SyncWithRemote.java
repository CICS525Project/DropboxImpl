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

	/**
	 * check if a file is shared with user
	 */
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
			HashMap<String, String> localFile = cmd.readHashCode(SessionInfo.getInstance().getWorkFolder());
			for (String key : remoteFileAccess.keySet()) {
				if (!localFile.containsKey(key)) {
					if (remoteFileAccess.get(key) != -1) {
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

	/**
	 * check if a file that is accessable for user is modified by others
	 */
	private void pollSharedFileModify() {
		// get shared file from remote
		// get corresponding files in local
		// check if the version number is different
		try {
			Registry registry = LocateRegistry.getRegistry(SessionInfo
					.getInstance().getRemoteDNS(), SessionInfo.getInstance()
					.getPortNum());
			this.serviceProvider = (ServiceServerInterface) registry
					.lookup("cloudboxRMI");
			HashMap<String, Integer> filesAndVersionShared = serviceProvider
					.getAllSharedFilesForUser(SessionInfo.getInstance()
							.getUsername());
			HashMap<String, String> filesLocal = cmd.readHashCode(SessionInfo.getInstance().getWorkFolder());
			for (String fn : filesLocal.keySet()) {
				
				// if the local file is a shared file
				if (filesAndVersionShared.containsKey(fn)) {
					String localVersion = cmd.readVersionForOne(fn);
					int localVersionInt = Integer.parseInt(localVersion);
					int remoteVersion = filesAndVersionShared.get(fn);
					String filePath = SessionInfo.getInstance().getWorkFolder()
							+ File.separator + fn;
					if (remoteVersion == -1) {
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
						fopt.deleteFileInFoler(filePath);
						// according remote, deleting local and remove local
						// meta data
						cmd.removeOneRecord(SessionInfo.getInstance()
								.getWorkFolder(), fn);
					}
					if (remoteVersion > localVersionInt) {
						// download if remote version is greater than the local
						// version
						if (!OperationQueue.getInstance().getDownloadQueue()
								.contains(fn)) {
							System.out
							.println("Detect file with higher version in remote --- download it.");
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
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	/**
	 * thread starter
	 */
	public void start() {
		System.out.println("Polling thread start...");
		syncer.start();
	}
	/**
	 * thread stopper
	 */
	public static void stop() {
		System.out.println("Stop syncer...");
		syncer = null;
	}
}
